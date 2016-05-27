/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.internal.stream.SocketInputStream;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.core.api.MuleContext;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpSocketDelegate implements SocketDelegate
{

    private final TcpProtocol protocol;
    private final MuleContext muleContext;
    private final InputStream inputStream;
    private final SocketAttributes attributes;
    private final Socket socket;

    public TcpSocketDelegate(Socket socket, TcpProtocol protocol, MuleContext muleContext)
    {
        this.socket = socket;
        this.protocol = protocol;
        this.muleContext = muleContext;
        attributes = new ImmutableSocketAttributes(socket);
        inputStream = getInputStream(socket);
    }

    private InputStream getInputStream(Socket socket)
    {
        DataInputStream underlyingIs = new DataInputStream(new BufferedInputStream(new SocketInputStream(socket)));
        return new StreamingSocketInputStream(underlyingIs);
    }

    public InputStream getSocketInputStream() throws IOException
    {
        return protocol.read(inputStream);

    }

    public void close()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public MuleMessage<InputStream, SocketAttributes> getMuleMessage() throws IOException
    {
        try
        {
            return SocketUtils.createMuleMessage(getSocketInputStream(), attributes, muleContext);
        }
        catch (IOException e)
        {
            if (protocol.getRethrowExceptionOnRead())
            {
                throw e;
            }

            return SocketUtils.createMuleMessageWithNullPayload(attributes, muleContext);
        }
    }
}
