/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import static org.mule.module.socket.internal.SocketUtils.getAddress;
import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.client.UdpClient;
import org.mule.module.socket.api.connection.RequesterConnection;
import org.mule.module.socket.internal.DefaultUdpRequestingSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import java.net.InetSocketAddress;

public class UdpRequesterConnection extends AbstractUdpConnection implements RequesterConnection
{
    private final DefaultUdpRequestingSocketProperties requestingSocketProperties;

    public UdpRequesterConnection(DefaultUdpRequestingSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);
        requestingSocketProperties = socketProperties;
    }

    @Override
    public void connect() throws ConnectionException
    {
        configureConnection();

        try
        {
            InetSocketAddress receivingAddress = getAddress(requestingSocketProperties.getBindingHost(), requestingSocketProperties.getLocalPort());
            socket.bind(receivingAddress);

            socket.connect(address, port);
        }
        catch (Exception e)
        {
            throw new ConnectionException(String.format("Could not connect UDP socket to host '%s' on port '%d'", host, port), e);
        }
    }

    @Override
    public SocketClient getClient()
    {
        return new UdpClient(socket, socketProperties, objectSerializer);
    }
}
