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
import org.mule.module.socket.api.client.TcpListenerClient;
import org.mule.module.socket.api.config.ListenerConfig;
import org.mule.module.socket.api.protocol.SafeProtocol;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.internal.ConnectionEvent;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.api.message.NullPayload;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.DefaultMuleMessage;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.construct.FlowConstructAware;
import org.mule.runtime.core.transformer.types.DataTypeFactory;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.UseConfig;
import org.mule.runtime.extension.api.runtime.source.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alias("tcp-listener")
public class TcpListener extends Source<Object, ImmutableSocketAttributes> implements FlowConstructAware
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpListener.class);
    private ExecutorService executorService;
    private FlowConstruct flowConstruct;


    @UseConfig
    private ListenerConfig config;

    @Inject
    private MuleContext muleContext;

    @Parameter
    @Optional
    private TcpProtocol protocol = new SafeProtocol();

    @Connection
    private TcpListenerClient client;

    private AtomicBoolean stopRequested = new AtomicBoolean(false);

    @Override
    public void start() throws Exception
    {
        executorService = newSingleThreadExecutor(r -> new Thread(r, format("%s%s.tcp.listener", getPrefix(muleContext), flowConstruct.getName())));
        executorService.execute(this::listen);
    }

    private void listen()
    {
        LOGGER.debug("Started listener");
        for (; ; )
        {
            if (isRequestedToStop())
            {
                return;
            }

            try
            {
                java.util.Optional<ConnectionEvent> event = client.receive();
                if (event.isPresent())
                {
                    processNewConnection(event.get());
                }
            }
            catch (ConnectionException e)
            {
                e.printStackTrace();
                sourceContext.getExceptionCallback().onException(e);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private MuleMessage<Object, ImmutableSocketAttributes> createMessage(ConnectionEvent event)
    {
        DataType dataType = DataTypeFactory.create(InputStream.class);
        // todo encoding
        return (MuleMessage) new DefaultMuleMessage(event.getContent() == null ? NullPayload.getInstance() : event.getContent()
                , dataType, event.getAttributes(), muleContext);
    }

    private void processNewConnection(ConnectionEvent event)
    {

        if (isRequestedToStop())
        {
            return;
        }

        sourceContext.getMessageHandler().handle(createMessage(event));

    }

    @Override
    public void stop()
    {
        // todo this is kinda defensive right?
        if (client != null)
        {
            client.disconnect();
        }

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
