/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.metadata.extension.resolver;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.MetadataContentResolver;
import org.mule.runtime.api.metadata.resolving.MetadataKeysResolver;
import org.mule.runtime.api.metadata.resolving.MetadataOutputResolver;
import org.mule.runtime.core.util.ValueHolder;

import java.util.List;

public class TestThreadContextClassLoaderResolver implements MetadataKeysResolver, MetadataContentResolver<String>, MetadataOutputResolver<String>
{
    private static ValueHolder<ClassLoader> contextClassLoader = new ValueHolder<>();

    public static void reset()
    {
        contextClassLoader.set(null);
    }

    public static ClassLoader getCurrentState()
    {
        return contextClassLoader.get();
    }

    @Override
    public List<MetadataKey> getMetadataKeys(MetadataContext context) throws MetadataResolvingException, ConnectionException
    {
        contextClassLoader.set(Thread.currentThread().getContextClassLoader());
        return TestMetadataResolverUtils.getKeys(context);
    }

    @Override
    public MetadataType getContentMetadata(MetadataContext context, String key) throws MetadataResolvingException
    {
        contextClassLoader.set(Thread.currentThread().getContextClassLoader());
        return TestMetadataResolverUtils.getMetadata(key);
    }

    @Override
    public MetadataType getOutputMetadata(MetadataContext context, String key) throws MetadataResolvingException
    {
        contextClassLoader.set(Thread.currentThread().getContextClassLoader());
        return TestMetadataResolverUtils.getMetadata(key);
    }
}
