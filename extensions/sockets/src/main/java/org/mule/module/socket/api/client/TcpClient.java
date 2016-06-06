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
    protected boolean dataInWorkFinished = false;
    protected Object notify = new Object();

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
        TcpInputStream inputStream = new TcpInputStream(new DataInputStream(new BufferedInputStream(socket.getInputStream())))
        {
            @Override
            public void close() throws IOException
            {
                // Don't actually close the stream, we just want to know if the
                // we want to stop receiving messages on this socket.
                // The Protocol is responsible for closing this.
                dataInWorkFinished = true;

                synchronized (notify)
                {
                    notify.notifyAll();
                }

                getDelegate().close();
            }
        };

        return protocol.read(inputStream);
    }

    @Override
    public void close() throws IOException
    {
        // TODO BEWARE, WE ARE NOT CLOSING THE SOCKET THE STREAM IT IS CLOSED FROM THE OUTSIDE
        // WHAT HAPPENS IF NO ONE CLOSES THAT STREAM?
        if (!dataInWorkFinished)
        {
            synchronized (notify)
            {
                if (!dataInWorkFinished)
                {
                    try
                    {
                        notify.wait();
                    }
                    catch (InterruptedException e)
                    {
                        LOGGER.error("INTERRUPTED");
                    }
                }
            }
        }

        shutdownSocket();
        socket.close();
    }

    public void run()
    {
        try
        {
            boolean hasMoreMessages = true;
            TcpInputStream inputStream = new TcpInputStream(new DataInputStream(new BufferedInputStream(socket.getInputStream())))
            {
                @Override
                public void close() throws IOException
                {
                    // Don't actually close the stream, we just want to know if the
                    // we want to stop receiving messages on this socket.
                    // The Protocol is responsible for closing this.
                    dataInWorkFinished = true;

                    synchronized (notify)
                    {
                        notify.notifyAll();
                    }

                    getDelegate().close();
                }
            };

            while (hasMoreMessages)
            {

                if (inputStream == null)
                {
                    inputStream.close();
                    hasMoreMessages = false;
                }

                if (inputStream.isStreaming())
                {
                    hasMoreMessages = false;
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void shutdownSocket() throws IOException
    {
        try
        {
            socket.shutdownOutput();
        }
        catch (UnsupportedOperationException e)
        {
            //Ignore, not supported by ssl sockets
        }
    }

    @Override
    public SocketAttributes getAttributes()
    {
        return new ImmutableSocketAttributes(socket);
    }
}
