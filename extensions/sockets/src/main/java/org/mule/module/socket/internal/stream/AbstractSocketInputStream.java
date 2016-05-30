/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @since 4.0
 */
public abstract class AbstractSocketInputStream extends AutoCloseInputStream
{

    private static InputStream createLazyStream(LazyStreamSupplier streamFactory)
    {
        return (InputStream) Enhancer.create(InputStream.class, (MethodInterceptor) (proxy, method, arguments, methodProxy)
                -> methodProxy.invoke(streamFactory.get(), arguments));
    }

    private final LazyStreamSupplier streamSupplier;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public AbstractSocketInputStream(LazyStreamSupplier streamSupplier)
    {
        super(createLazyStream(streamSupplier));
        this.streamSupplier = streamSupplier;
    }

    @Override
    public final synchronized void close() throws IOException
    {
        if (closed.compareAndSet(false, true) && streamSupplier.isSupplied())
        {
            doClose();
        }
    }

    protected void doClose() throws IOException
    {
        super.close();
    }

}
