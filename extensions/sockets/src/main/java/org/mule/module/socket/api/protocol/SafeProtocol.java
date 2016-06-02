/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.protocol;

import org.mule.runtime.core.api.serialization.ObjectSerializer;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This precedes every message with a cookie.
 * It should probably not be used in production.
 * We use ths protocol as the default because previously people tended to use DefaultProtocol without considering packet fragmentation etc.
 * You should probably change to LengthProtocol.
 * Remember - both sender and receiver must use the same protocol.
 */
public class SafeProtocol extends AbstractByteProtocol
{

    public static final String COOKIE = "You are using SafeProtocol";

    private final TcpProtocol cookieProtocol = new LengthProtocol(COOKIE.length());
    private TcpProtocol delegate;

    @Parameter
    @Optional(defaultValue = "-1")
    private int maxMessageLeght = NO_MAX_LENGTH;

    @Parameter
    @Optional(defaultValue = "true")
    private boolean payloadOnly = true;

    public SafeProtocol()
    {
        super(false);
        LengthProtocol protocol = new LengthProtocol();
        protocol.setPayloadOnly(payloadOnly);
        delegate = protocol;
    }

    public InputStream read(InputStream is) throws IOException
    {
        if (assertSiblingSafe(is))
        {
            InputStream result = delegate.read(is);
            if (null == result)
            {
                // EOF after cookie but before data
                helpUser();
            }
            return result;
        }
        else
        {
            return null;
        }
    }

    public void write(OutputStream os, Object data) throws IOException
    {
        assureSibling(os);
        delegate.write(os, data);
    }

    private void assureSibling(OutputStream os) throws IOException
    {
        cookieProtocol.write(os, COOKIE);
    }

    /**
     * @param is Stream to read data from
     * @return true if further data are available; false if EOF
     * @throws IOException
     */
    private boolean assertSiblingSafe(InputStream is) throws IOException
    {
        Object cookie = null;
        try
        {
            cookie = cookieProtocol.read(is);
        }
        catch (Exception e)
        {
            helpUser(e);
        }
        if (null != cookie)
        {
            String parsedCookie = IOUtils.toString((InputStream) cookie);
            if (parsedCookie.length() != COOKIE.length() || !COOKIE.equals(parsedCookie))
            {
                helpUser();
            }
            else
            {
                return true;
            }
        }
        return false; // eof
    }

    private void helpUser() throws IOException
    {
        throw new IOException("You are not using a consistent protocol on your TCP transport. "
                              + "Please read the documentation for the TCP transport, "
                              + "paying particular attention to the protocol parameter.");
    }

    private void helpUser(Exception e) throws IOException
    {
        throw (IOException) new IOException("An error occurred while verifying your connection.  "
                                            + "You may not be using a consistent protocol on your TCP transport. "
                                            + "Please read the documentation for the TCP transport, "
                                            + "paying particular attention to the protocol parameter.").initCause(e);
    }

    @Override
    public void setObjectSerializer(ObjectSerializer objectSerializer)
    {
        this.objectSerializer = objectSerializer;
        delegate.setObjectSerializer(objectSerializer);
        cookieProtocol.setObjectSerializer(objectSerializer);
    }
}
