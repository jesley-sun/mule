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
import org.mule.module.socket.api.connection.ListenerConnection;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import java.net.SocketException;

public class UdpListenerConnection extends AbstractUdpConnection implements ListenerConnection
{

    public UdpListenerConnection(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);
    }

    @Override
    public void connect() throws ConnectionException
    {
        configureConnection();

        try
        {
            socket.bind(getAddress(host, port));
        }
        catch (SocketException e)
        {
            throw new ConnectionException(String.format("Could not bind UDP socket to host '%s' on port '%d'", host, port), e);
        }
    }

    @Override
    public SocketClient listen()
    {
        return new UdpClient(socket, socketProperties, objectSerializer);
    }
}
