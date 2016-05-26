/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import static java.lang.String.*;
import static org.mule.module.socket.internal.SocketUtils.getSocketAddress;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.tcp.TcpClientSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpRequesterClient extends AbstractSocketClient implements RequesterSocket
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpRequesterClient.class);

    private final Socket socket;
    private final TcpClientSocketProperties socketProperties;
    private final TcpProtocol protocol;
    private final InetSocketAddress address;

    public TcpRequesterClient(TcpClientSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
    {
        super(host, port);
        socket = new Socket();
        address = getSocketAddress(host, port, socketProperties.getFailOnUnresolvedHost());
        this.protocol = protocol;
        this.socketProperties = socketProperties;
        initialise();
    }

    private void initialise() throws ConnectionException, UnresolvableHostException
    {
        try
        {
            socket.setSendBufferSize(socketProperties.getSendBufferSize());
            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            socket.setTcpNoDelay(socketProperties.getSendTcpNoDelay());
            socket.setSoTimeout(socketProperties.getTimeout());

            if (socketProperties.getKeepAlive() != null)
            {
                socket.setKeepAlive(socketProperties.getKeepAlive());
            }

            if (socketProperties.getLinger() != null)
            {
                socket.setSoLinger(true, socketProperties.getLinger());
            }


            InetSocketAddress address = getSocketAddress(socketProperties.getBindingHost(), socketProperties.getLocalPort(), socketProperties.getFailOnUnresolvedHost());
            socket.bind(address);
        }
        catch (Exception e)
        {
            throw new ConnectionException("Could not create TCP requester socket", e);
        }
    }


    public void connect() throws ConnectionException
    {
        try
        {
            socket.connect(address, socketProperties.getConnectionTimeout());
        }
        catch (IOException e)
        {
            throw new ConnectionException(format("Could not connect TCP requester socket to host '%s' on port '%d'", host, port), e);
        }
    }

    public void send(Object data) throws ConnectionException
    {
        try (BufferedOutputStream socketStream = new BufferedOutputStream(socket.getOutputStream()))
        {
            protocol.write(socketStream, data);
            socketStream.flush();
        }
        catch (IOException e)
        {
            throw new ConnectionException("An error occurred while trying to write into the socket", e);
        }
    }

    public void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            LOGGER.error("An error occurred when closing TCP requester socket");
        }
    }

    @Override
    public void validate() throws ConnectionException, UnresolvableHostException
    {
        if (!socket.isConnected())
        {
            throw new ConnectionException("Requester TCP socket is not connected");
        }
    }
}
