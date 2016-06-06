/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.worker.tcp;

import static org.mule.module.socket.internal.SocketUtils.createMuleMessage;
import org.mule.module.socket.api.connection.udp.UdpRequesterConnection;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.api.worker.SocketWorker;
import org.mule.module.socket.internal.TcpInputStream;
import org.mule.runtime.api.execution.CompletionHandler;
import org.mule.runtime.api.execution.ExceptionCallback;
import org.mule.runtime.api.message.MuleEvent;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.extension.api.runtime.MessageHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpWorker implements SocketWorker
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpRequesterConnection.class);
    private final Socket socket;
    private final TcpInputStream dataIn;
    private final OutputStream dataOut;
    private final InputStream underlyingIn;
    private final TcpProtocol protocol;
    private boolean dataInWorkFinished = false;
    private Object notify = new Object();
    private boolean moreMessages = true;


    public TcpWorker(Socket socket, TcpProtocol protocol) throws IOException
    {
        this.socket = socket;
        this.protocol = protocol;

        underlyingIn = new BufferedInputStream(socket.getInputStream());
        dataOut = new BufferedOutputStream(socket.getOutputStream());
        dataIn = new TcpInputStream(underlyingIn)
        {
            @Override
            public void close() throws IOException
            {
                // Don't actually close the stream, we just want to know if the
                // we want to stop receiving messages on this sockete.
                // The Protocol is responsible for closing this.
                dataInWorkFinished = true;
                moreMessages = false;

                synchronized (notify)
                {
                    notify.notifyAll();
                }
            }
        };
    }

    private void waitForStreams()
    {
        // The Message with the InputStream as a payload can be dispatched
        // into a different thread, in which case we need to wait for it to
        // finish streaming
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
                    }
                }
            }
        }
    }

    private InputStream getNextMessage() throws IOException
    {
        InputStream readMsg = null;
        try
        {
            readMsg = protocol.read(dataIn);

            if (dataIn.isStreaming())
            {
                moreMessages = false;
            }

            return readMsg;
        }
        finally
        {
            if (readMsg == null)
            {
                dataIn.close();
            }
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

    private boolean hasMoreMessages()
    {
        return !socket.isClosed() && !dataInWorkFinished && moreMessages;
    }

    @Override
    public void stop()
    {
        waitForStreams();
        try
        {
            shutdownSocket();
            socket.close();
        }
        catch (IOException e)
        {
            LOGGER.warn("Socket close failed with: " + e);
        }

    }

    @Override
    public void run(MuleContext muleContext, MessageHandler<InputStream, SocketAttributes> messageHandler)
    {
        while (hasMoreMessages())
        {
            InputStream inputStream = null;
            try
            {
                inputStream = getNextMessage();
            }
            catch (IOException e)
            {
                LOGGER.error(e.getMessage());
                break;
            }

            SocketAttributes attributes = new ImmutableSocketAttributes(socket);
            messageHandler.handle(createMuleMessage(inputStream, attributes, muleContext), new CompletionHandler<MuleEvent, Exception, MuleEvent>()
            {
                @Override
                public void onCompletion(MuleEvent muleEvent, ExceptionCallback<MuleEvent, Exception> exceptionCallback)
                {
                    try
                    {
                        protocol.write(dataOut, muleEvent.getMessage().getPayload());
                        dataOut.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e)
                {
                    LOGGER.error(e.getMessage());
                    moreMessages = false;
                }
            });
        }
    }
}
