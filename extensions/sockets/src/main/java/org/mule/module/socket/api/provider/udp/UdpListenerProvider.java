/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.provider.udp;

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
import org.mule.module.socket.api.connection.udp.UdpListenerConnection;

import javax.inject.Inject;

@Alias("udp-listener")
public class UdpListenerProvider implements ConnectionProvider<AbstractSocketConfig, UdpListenerConnection>
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
    public UdpListenerConnection connect(AbstractSocketConfig udpConfig) throws ConnectionException, UnresolvableHostException
    {
        UdpListenerConnection connection = new UdpListenerConnection(udpSocketProperties, settings.getHost(), settings.getPort());
        connection.connect();
        return connection;
    }

    @Override
    public void disconnect(UdpListenerConnection connection)
    {
        connection.disconnect();
    }

    @Override
    public ConnectionValidationResult validate(UdpListenerConnection connection)
    {
        return SocketUtils.validate(connection);
    }

    @Override
    public ConnectionHandlingStrategy<UdpListenerConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory<AbstractSocketConfig, UdpListenerConnection> handlingStrategyFactory)
    {
        return handlingStrategyFactory.cached();
    }
}
