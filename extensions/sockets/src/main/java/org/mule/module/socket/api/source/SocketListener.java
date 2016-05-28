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
import static org.mule.runtime.core.util.concurrent.ThreadNameHelper.getPrefix;
import org.mule.module.socket.api.client.ListenerSocket;
import org.mule.module.socket.internal.SocketDelegate;
import org.mule.module.socket.internal.UdpSocketDelegate;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.runtime.source.Source;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketListener extends Source<InputStream, SocketAttributes> implements FlowConstructAware
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpSocketDelegate.class);
    private ExecutorService executorService;
    private FlowConstruct flowConstruct;

    @Inject
    private MuleContext muleContext;

    @Connection
    private ListenerSocket client;

    private AtomicBoolean stopRequested = new AtomicBoolean(false);

    @Override
    public void start() throws Exception
    {
        client.setMuleContext(muleContext);
        executorService = newSingleThreadExecutor(r -> new Thread(r, format("%s%s.udp.listener", getPrefix(muleContext), flowConstruct.getName())));
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
                Optional<SocketDelegate> delegate = client.receive();

                // An error receiving a connection, just wait for another one
                if (!delegate.isPresent())
                {
                    continue;
                }

                if (isRequestedToStop())
                {
                    delegate.get().close();
                    return;
                }

                sourceContext.getMessageHandler().handle(delegate.get().getMuleMessage());
            }
            catch (Exception e)
            {
                sourceContext.getExceptionCallback().onException(e);
            }
        }
    }


    @Override
    public void stop()
    {
        client.disconnect();
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
                    LOGGER.warn("Could not properly terminate pending events for directory listener on flow " + flowConstruct.getName());
                }
            }
        }
        catch (InterruptedException e)
        {
            if (LOGGER.isWarnEnabled())
            {
                LOGGER.warn("Got interrupted while trying to terminate pending events for directory listener on flow " + flowConstruct.getName());
            }
        }
    }

    @Override
    public void setFlowConstruct(FlowConstruct flowConstruct)
    {
        this.flowConstruct = flowConstruct;
    }

}
