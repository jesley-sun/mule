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
    private final SocketAttributes attributes;
    private final Socket socket;
//public TcpRequesterClient(TcpClientSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
    public TcpSocketDelegate(Socket socket, TcpProtocol protocol, MuleContext muleContext)
    {
        this.socket = socket;
        this.protocol = protocol;
        this.muleContext = muleContext;
        attributes = new ImmutableSocketAttributes(socket);
    }

    private InputStream getInputStream(Socket socket) throws IOException
    {
        //FIXME same code as tcp requester
        DataInputStream underlyingIs = new DataInputStream(new BufferedInputStream(new SocketInputStream(socket.getInputStream())));
        return new TcpInputStream(underlyingIs);
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
        return SocketUtils.createMuleMessage(protocol.read(getInputStream(socket)), attributes, muleContext);
    }
}
