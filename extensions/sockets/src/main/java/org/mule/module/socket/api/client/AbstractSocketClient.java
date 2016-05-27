/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.api.message.NullPayload;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.DefaultMuleMessage;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.transformer.types.DataTypeFactory;

import java.io.InputStream;

/**
 *
 */
public abstract class AbstractSocketClient implements SocketClient, MuleContextAware
{

    protected String host;
    protected int port;
    private MuleContext muleContext;

    public AbstractSocketClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    protected MuleMessage<InputStream, SocketAttributes> createMuleMessage(InputStream content, SocketAttributes attributes)
    {
        DataType dataType = DataTypeFactory.create(InputStream.class);
        Object payload = NullPayload.getInstance();
        MuleMessage<InputStream, SocketAttributes> message;

        if (content != null)
        {
            payload = content;
        }

        message = (MuleMessage) new DefaultMuleMessage(payload, dataType, attributes, muleContext);
        return message;
    }

    protected MuleMessage<InputStream, SocketAttributes> createMuleMessageWithNullPayload(SocketAttributes attributes)
    {
        Object payload = NullPayload.getInstance();
        DataType dataType = DataTypeFactory.create(NullPayload.class);
        return (MuleMessage) new DefaultMuleMessage(payload, dataType, attributes, muleContext);
    }
}
