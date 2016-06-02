/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.source;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mule.module.socket.internal.SocketUtils.createMuleMessage;
import static org.mule.runtime.core.util.concurrent.ThreadNameHelper.getPrefix;
import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.connection.ListenerConnection;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.execution.CompletionHandler;
import org.mule.runtime.api.execution.ExceptionCallback;
import org.mule.runtime.api.message.MuleEvent;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.runtime.source.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for socket connections of the given protocol in the configured host and port
 *
 * @since 4.0
 */
public class SocketListener extends Source<InputStream, SocketAttributes> implements FlowConstructAware
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketListener.class);
    private ExecutorService executorService;
    private FlowConstruct flowConstruct;

    @Inject
    private MuleContext muleContext;

    @Connection
    private ListenerConnection connection;

    private AtomicBoolean stopRequested = new AtomicBoolean(false);

    @Override
    public void start() throws Exception
    {
        executorService = newSingleThreadExecutor(r -> new Thread(r, format("%s%s.socket.listener", getPrefix(muleContext), flowConstruct.getName())));
        executorService.execute(this::listen);
    }

    private void listen()
    {

        for (; ; )
        {
            if (isRequestedToStop())
            {
                return;
            }

            try
            {

                SocketClient client = connection.listen();

                if (isRequestedToStop())
                {
                    client.close();
                    return;
                }

                MuleMessage<InputStream, SocketAttributes> muleMessage = createMuleMessage(client.read(),
                                                                                           client.getAttributes(),
                                                                                           muleContext);

                sourceContext.getMessageHandler().handle(muleMessage, new CompletionHandler<MuleEvent, Exception, MuleEvent>()
                {
                    @Override
                    public void onCompletion(MuleEvent muleEvent, ExceptionCallback<MuleEvent, Exception> exceptionCallback)
                    {
                        if (isRequestedToStop())
                        {
                            try
                            {
                                client.close();
                            }
                            catch (IOException e)
                            {
                                LOGGER.error(e.getMessage());
                            }
                        }
                        try
                        {
                            client.write(muleEvent.getMessage().getPayload());
                        }
                        catch (IOException e)
                        {
                            exceptionCallback.onException(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e)
                    {
                        LOGGER.error(e.getMessage());
                        try
                        {
                            client.close();
                        }
                        catch (IOException e1)
                        {
                            //fixme
                            LOGGER.error(e1.getMessage());
                        }
                    }
                });
            }
            catch (ConnectionException e)
            {
                if (!isRequestedToStop())
                {
                    sourceContext.getExceptionCallback().onException(e);
                }
            }
            catch (Exception e)
            {
                // keep listening
                e.printStackTrace();
                LOGGER.debug(e.getMessage());
            }
        }
    }


    @Override
    public void stop()
    {
        stopRequested.set(true);
        shutdownExecutor();
    }

    private boolean isRequestedToStop()
    {
        return stopRequested.get() || Thread.currentThread().isInterrupted();
    }


    private void shutdownExecutor()
    {
        if (executorService == null)
        {
            return;
        }

        executorService.shutdownNow();
        try
        {
            if (!executorService.awaitTermination(muleContext.getConfiguration().getShutdownTimeout(), MILLISECONDS))
            {
                if (LOGGER.isWarnEnabled())
                {
                    LOGGER.warn("Could not properly terminate pending events for socket listener on flow " + flowConstruct.getName());
                }
            }
        }
        catch (InterruptedException e)
        {
            if (LOGGER.isWarnEnabled())
            {
                LOGGER.warn("Got interrupted while trying to terminate pending events for socket listener on flow " + flowConstruct.getName());
            }
        }
    }

    @Override
    public void setFlowConstruct(FlowConstruct flowConstruct)
    {
        this.flowConstruct = flowConstruct;
    }

}
