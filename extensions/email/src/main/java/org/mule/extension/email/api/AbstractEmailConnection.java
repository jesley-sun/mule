/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.email.api;

import org.mule.extension.email.internal.PasswordAuthenticator;
import org.mule.runtime.api.connection.ConnectionValidationResult;

import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.net.SocketFactory;

/**
 * Generic implementation for an email connection of a connector which operates
 * over the SMTP, IMAP or POP3 protocols.
 * <p>
 * Performs the creation of a persistent set of properties that are used
 * to configure the {@link Session} instance.
 * <p>
 * The full list of properties available to configure can be found at:
 * <ul>
 * <li>for POP3 <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/pop3/package-summary.html#properties</a></li>
 * <li>for SMTP <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html#properties</a></li>
 * <li>for IMAP <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/package-summary.html#properties</a></li>
 * <ul/>
 *
 * @since 4/0
 */
public abstract class AbstractEmailConnection
{

    /**
     * The host name of the mail server.
     */
    private static final String HOST_PROPERTY_MASK = "mail.%s.host";

    /**
     * The port number of the mail server.
     */
    private static final String PORT_PROPERTY_MASK = "mail.%s.port";

    /**
     * Socket connection timeout value in milliseconds. Default is infinite timeout.
     */
    private static final String CONNECTION_TIMEOUT_PROPERTY_MASK = "mail.%s.connectiontimeout";

    /**
     * Socket write timeout value in milliseconds. Default is infinite timeout.
     */
    private static final String WRITE_TIMEOUT_PROPERTY_MASK = "mail.%s.writetimeout";

    /**
     * Indicates if the STARTTLS command shall be used to initiate a TLS-secured connection.
     */
    private static final String START_TLS_PROPERTY_MASK = "mail.%s.starttls.enable";

    /**
     * Specifies the {@link SocketFactory} class to create smtp sockets.
     */
    private static final String SOCKET_FACTORY_CLASS_PROPERTY_MASK = "mail.%s.socketFactory.class";

    /**
     * Whether to use {@link Socket} as a fallback if the initial connection fails or not.
     */
    private static final String SOCKET_FACTORY_FALLBACK_PROPERTY_MASK = "mail.%s.socketFactory.fallback";

    /**
     * Specifies the port to connect to when using a socket factory.
     */
    private static final String SOCKET_FACTORY_PORT_MASK = "mail.%s.socketFactory.port";

    /**
     * Specifies the default transport protocol.
     */
    private static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";

    /**
     * Socket read timeout value in milliseconds. This timeout is implemented by {@link Socket}. Default is infinite timeout.
     */
    private static final String TIMEOUT_PROPERTY_MASK = "mail.%s.timeout";

    /**
     * Defines the default mime charset to use when none has been specified for the message.
     */
    private static final String MAIL_MIME_CHARSET = "mail.mime.charset";

    /**
     * Default socket read, connection and write timeout. See JavaMail session properties.
     */
    private static final long DEFAULT_TIMEOUT = 15000L;

    private final String protocol;
    protected final Session session;


    /**
     * Base constructor for {@link AbstractEmailConnection} implementations.
     *
     * @param protocol          the protocol used to send mails.
     * @param user              the username to establish connection with the mail server.
     * @param password          the password corresponding to the {@code username}
     * @param host              the host name of the mail server.
     * @param port              the port number of the mail server.
     * @param connectionTimeout the socket connection timeout
     * @param readTimeout       the socket read timeout
     * @param writeTimeout      the socket write timeout
     * @param properties        the custom properties added to configure the session.
     */
    public AbstractEmailConnection(String protocol,
                                   String user,
                                   String password,
                                   String host,
                                   String port,
                                   long connectionTimeout,
                                   long readTimeout,
                                   long writeTimeout,
                                   Map<String, String> properties)
    {
        this.protocol = protocol;

        Properties sessionProperties = buildSessionProperties(host, port, connectionTimeout, readTimeout, writeTimeout);

        if (properties != null)
        {
            sessionProperties.putAll(properties);
        }

        PasswordAuthenticator authenticator = null;
        if (user != null && password != null)
        {
            authenticator = new PasswordAuthenticator(user, password);
        }

        session = Session.getInstance(sessionProperties, authenticator);
    }

    /**
     * Creates a new instance and set all the properties required by the specified {@code protocol}.
     */
    private Properties buildSessionProperties(String host, String port, long connectionTimeout, long readTimeout, long writeTimeout)
    {
        Properties properties = new Properties();
        set(properties, PORT_PROPERTY_MASK, port);
        set(properties, HOST_PROPERTY_MASK, host);

        if (isSecure())
        {
            set(properties, START_TLS_PROPERTY_MASK, "true");
            set(properties, SOCKET_FACTORY_FALLBACK_PROPERTY_MASK, "false");

            //TODO: keeping this for secure connection implementations
            //set(SOCKET_FACTORY_CLASS_PROPERTY_MASK, "className");
        }

        set(properties, CONNECTION_TIMEOUT_PROPERTY_MASK, Long.toString(connectionTimeout <= 0L ? DEFAULT_TIMEOUT : connectionTimeout));
        set(properties, TIMEOUT_PROPERTY_MASK, Long.toString(readTimeout <= 0L ? DEFAULT_TIMEOUT : readTimeout));

        // Note: "mail." + protocol + ".writetimeout" breaks TLS/SSL Dummy Socket and makes tests run 6x slower!!!
        if (writeTimeout > 0L)
        {
            set(properties, WRITE_TIMEOUT_PROPERTY_MASK, Long.toString(writeTimeout));
        }

        set(properties, TRANSPORT_PROTOCOL, protocol);
        return properties;
    }

    /**
     * Sets a value to the specified property.
     *
     * @param property the property to be set.
     * @param value    the corresponding value for the specified {@code property}
     */
    private void set(Properties properties, String property, String value)
    {
        properties.setProperty(String.format(property, protocol), value);
    }

    /**
     * @return true if the protocol is secure, false otherwise.
     */
    private boolean isSecure()
    {
        return protocol.endsWith("s");
    }

    /**
     * @return the email {@link Session} used by the connection.
     */
    public Session getSession()
    {
       return session;
    }

    /**
     * disconnects the internal client used by the {@link AbstractEmailConnection} instance.
     */
    public abstract void disconnect();

    /**
     * Checks if the current {@link AbstractEmailConnection} instance is valid or not.
     *
     * @return a {@link ConnectionValidationResult} indicating if the connection
     * is valid or not.
     */
    public abstract ConnectionValidationResult validate();

}
