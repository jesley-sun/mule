/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import static org.mule.module.socket.internal.SocketUtils.configureConnection;
import static org.mule.module.socket.internal.SocketUtils.newSocket;
import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.client.UdpOperationClient;
import org.mule.module.socket.api.connection.RequesterConnection;
import org.mule.module.socket.internal.DefaultUdpRequestingSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpRequesterConnection extends AbstractUdpConnection implements RequesterConnection
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpRequesterConnection.class);
    private final DefaultUdpRequestingSocketProperties requestingSocketProperties;

    public UdpRequesterConnection(DefaultUdpRequestingSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);
        requestingSocketProperties = socketProperties;
    }

    @Override
    public void connect() throws ConnectionException
    {
        socket = newSocket(requestingSocketProperties.getBindingHost(), requestingSocketProperties.getLocalPort());
        configureConnection(socket, requestingSocketProperties);
    }

    @Override
    public SocketClient getClient() throws ConnectionException
    {
        return new UdpOperationClient(socket, host, port, requestingSocketProperties, objectSerializer);
    }

}
