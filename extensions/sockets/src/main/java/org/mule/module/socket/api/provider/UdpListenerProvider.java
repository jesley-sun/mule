/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.provider;

import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.client.UdpListenerClient;
import org.mule.module.socket.api.config.AbstractSocketConfig;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.ConnectionSettings;
import org.mule.module.socket.internal.DefaultUdpSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandlingStrategy;
import org.mule.runtime.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.core.api.serialization.DefaultObjectSerializer;
import org.mule.runtime.core.api.serialization.ObjectSerializer;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Optional;

import javax.inject.Inject;

@Alias("udp-listener")
public class UdpListenerProvider implements ConnectionProvider<AbstractSocketConfig, SocketClient>
{

    @ParameterGroup
    ConnectionSettings settings;

    @Parameter
    @Optional
    UdpSocketProperties udpSocketProperties = new DefaultUdpSocketProperties();

    @DefaultObjectSerializer
    @Inject
    ObjectSerializer objectSerializer;

    @Override
    public SocketClient connect(AbstractSocketConfig udpConfig) throws ConnectionException, UnresolvableHostException
    {
        UdpListenerClient client = new UdpListenerClient(udpSocketProperties, settings.getHost(), settings.getPort());
        client.setObjectSerializer(objectSerializer);
        return client;
    }

    @Override
    public void disconnect(SocketClient client)
    {
        client.disconnect();
    }

    @Override
    public ConnectionValidationResult validate(SocketClient udpClient)
    {
        return SocketUtils.validate(udpClient);
    }

    @Override
    public ConnectionHandlingStrategy<SocketClient> getHandlingStrategy(ConnectionHandlingStrategyFactory<AbstractSocketConfig, SocketClient> handlingStrategyFactory)
    {
        return handlingStrategyFactory.cached();
    }
}
