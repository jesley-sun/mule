/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.protocol;

import org.mule.module.socket.internal.DelegatingInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayProtocolWrapper extends DelegatingInputStream
{

    private final SocketInputStreamConsumer consumer;
    private final InputStream socketIs;
    private InputStream delegate;

    public ByteArrayProtocolWrapper(InputStream socketIs, SocketInputStreamConsumer consumer)
    {
        this.consumer = consumer;
        this.socketIs = socketIs;
    }

    @Override
    protected InputStream getDelegate() throws IOException
    {
        //todo sync
        if (delegate == null)
        {
            delegate = new ByteArrayInputStream(consumer.consume(socketIs));
        }

        return delegate;
    }
}
