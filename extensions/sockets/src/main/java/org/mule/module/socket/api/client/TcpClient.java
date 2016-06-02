/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.module.socket.internal.TcpInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClient implements SocketClient
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);

    public TcpClient(Socket socket, TcpProtocol protocol)
    {
        this.socket = socket;
        this.protocol = protocol;
    }

    private final Socket socket;
    private final TcpProtocol protocol;

    @Override
    public void write(Object data) throws IOException
    {
        OutputStream socketOutputStream;
        try
        {
            socketOutputStream = socket.getOutputStream();
        }
        catch (IOException e)
        {
            throw e;
            //throw new ConnectionException("An error occurred while trying to write into the socket", e);
        }

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socketOutputStream);
        protocol.write(bufferedOutputStream, data);
        bufferedOutputStream.flush();

    }

    @Override
    public InputStream read() throws IOException
    {
        TcpInputStream inputStream = new TcpInputStream(new DataInputStream(new BufferedInputStream(socket.getInputStream())));
        return protocol.read(inputStream);
    }

    @Override
    public void close() throws IOException
    {
        SocketUtils.closeSocket(socket);
    }

    @Override
    public SocketAttributes getAttributes()
    {
        return new ImmutableSocketAttributes(socket);
    }
}
