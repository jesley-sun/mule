/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.config.dsl.config;

import static java.util.Optional.empty;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.extension.api.introspection.config.RuntimeConfigurationModel;
import org.mule.runtime.extension.api.runtime.ConfigurationProvider;
import org.mule.runtime.module.extension.internal.config.dsl.AbstractExtensionObjectFactory;
import org.mule.runtime.module.extension.internal.runtime.DynamicConfigPolicy;
import org.mule.runtime.module.extension.internal.runtime.config.ConfigurationProviderFactory;
import org.mule.runtime.module.extension.internal.runtime.config.DefaultConfigurationProviderFactory;
import org.mule.runtime.module.extension.internal.runtime.resolver.ResolverSet;
import org.mule.runtime.module.extension.internal.runtime.resolver.ValueResolver;

import java.util.Optional;

final class ConfigurationProviderObjectFactory extends AbstractExtensionObjectFactory<ConfigurationProvider<Object>>
{

    private final String name;
    private final RuntimeConfigurationModel configurationModel;
    private final ConfigurationProviderFactory configurationProviderFactory = new DefaultConfigurationProviderFactory();
    private final MuleContext muleContext;

    private DynamicConfigPolicy dynamicConfigPolicy;
    private Optional<ValueResolver<ConnectionProvider>> connectionProviderResolver = empty();

    ConfigurationProviderObjectFactory(String name,
                                       RuntimeConfigurationModel configurationModel,
                                       MuleContext muleContext)
    {
        this.name = name;
        this.configurationModel = configurationModel;
        this.muleContext = muleContext;
    }

    @Override
    public ConfigurationProvider<Object> getObject() throws Exception
    {

        ResolverSet resolverSet = getParametersAsResolverSet();
        ConfigurationProvider<Object> configurationProvider;
        try
        {
            if (resolverSet.isDynamic() /*|| connectionProviderResolver.isDynamic()*/)
            {
                configurationProvider = configurationProviderFactory.createDynamicConfigurationProvider(
                        name,
                        configurationModel,
                        resolverSet,
                        connectionProviderResolver,
                        dynamicConfigPolicy);
            }
            else
            {
                configurationProvider = configurationProviderFactory.createStaticConfigurationProvider(
                        name,
                        configurationModel,
                        resolverSet,
                        connectionProviderResolver,
                        muleContext);
            }

            muleContext.getInjector().inject(configurationProvider);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return configurationProvider;

    }

    public void setDynamicConfigPolicy(DynamicConfigPolicy dynamicConfigPolicy)
    {
        this.dynamicConfigPolicy = dynamicConfigPolicy;
    }

    public void setConnectionProviderResolver(Optional<ValueResolver<ConnectionProvider>> connectionProviderResolver)
    {
        this.connectionProviderResolver = connectionProviderResolver;
    }
}
