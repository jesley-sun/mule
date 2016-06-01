/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import static org.mule.module.socket.internal.SocketUtils.createPacket;
import static org.mule.module.socket.internal.SocketUtils.getAddress;
import static org.mule.module.socket.internal.SocketUtils.newSocket;
import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.client.UdpClient;
import org.mule.module.socket.api.connection.ListenerConnection;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.DatagramPacket;

public class UdpListenerConnection extends AbstractUdpConnection implements ListenerConnection
{

    public UdpListenerConnection(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);
    }

    @Override
    public void connect() throws ConnectionException
    {
        socket = newSocket(host, port);
        configureConnection();
    }

    @Override
    public SocketClient listen() throws IOException, ConnectionException
    {
        DatagramPacket packet = createPacket(socketProperties.getReceiveBufferSize());
        if (!socket.isBound())
        {
            socket.bind(getAddress(host, port));
        }
        socket.receive(packet);
        return new UdpClient(packet, socketProperties, objectSerializer);
    }

}
