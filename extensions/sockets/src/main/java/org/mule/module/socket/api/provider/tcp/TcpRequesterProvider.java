/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.provider.tcp;

import org.mule.module.socket.api.config.RequesterConfig;
import org.mule.module.socket.api.connection.tcp.TcpRequesterConnection;
import org.mule.module.socket.api.protocol.SafeProtocol;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.tcp.TcpClientSocketProperties;
import org.mule.module.socket.internal.ConnectionSettings;
import org.mule.module.socket.internal.DefaultTcpClientSocketProperties;
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

@Alias("tcp-requester")
public class TcpRequesterProvider implements ConnectionProvider<RequesterConfig, TcpRequesterConnection>
{

    @ParameterGroup
    ConnectionSettings settings;

    @Parameter
    @Optional
    TcpClientSocketProperties tcpClientSocketProperties = new DefaultTcpClientSocketProperties();

    @Parameter
    @Optional
    TcpProtocol protocol = new SafeProtocol();

    @Override
    public TcpRequesterConnection connect(RequesterConfig requesterConfig) throws ConnectionException
    {
        TcpRequesterConnection connection = new TcpRequesterConnection(tcpClientSocketProperties, protocol, settings.getHost(), settings.getPort());
        connection.connect();
        return connection;
    }

    @Override
    public void disconnect(TcpRequesterConnection connection)
    {
        connection.disconnect();
    }

    @Override
    public ConnectionValidationResult validate(TcpRequesterConnection connection)
    {
        return SocketUtils.validate(connection);
    }

    @Override
    public ConnectionHandlingStrategy<TcpRequesterConnection> getHandlingStrategy(ConnectionHandlingStrategyFactory<RequesterConfig, TcpRequesterConnection> handlingStrategyFactory)
    {
        return handlingStrategyFactory.supportsPooling();
    }
}
