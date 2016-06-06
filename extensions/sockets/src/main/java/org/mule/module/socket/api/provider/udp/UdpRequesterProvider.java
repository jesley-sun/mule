/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.provider.udp;

import org.mule.module.socket.api.config.RequesterConfig;
import org.mule.module.socket.api.connection.udp.UdpRequesterConnection;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.internal.ConnectionSettings;
import org.mule.module.socket.internal.DefaultUdpRequestingSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandlingStrategy;
import org.mule.runtime.api.connection.ConnectionHandlingStrategyFactory;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Optional;

@Alias("udp-requester")
public class UdpRequesterProvider implements ConnectionProvider<RequesterConfig, UdpRequesterConnection>
{

    @ParameterGroup
    ConnectionSettings settings;

    @Parameter
    @Optional
    DefaultUdpRequestingSocketProperties udpRequestingSocketProperties = new DefaultUdpRequestingSocketProperties();

    @Override
    public UdpRequesterConnection connect(RequesterConfig udpConfig) throws ConnectionException, UnresolvableHostException
    {
        UdpRequesterConnection connection = new UdpRequesterConnection(udpRequestingSocketProperties, settings.getHost(), settings.getPort());
        connection.connect();
        return connection;
    }

    @Override
    public void disconnect(UdpRequesterConnection connection)
    {
        connection.disconnect();
    }

    @Override
    public ConnectionValidationResult validate(UdpRequesterConnection connection)
    {
        return SocketUtils.validate(connection);
    }

    @Override
    public ConnectionHandlingStrategy<UdpRequesterConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory<RequesterConfig, UdpRequesterConnection> handlingStrategyFactory)
    {
        return handlingStrategyFactory.supportsPooling();
    }
}

