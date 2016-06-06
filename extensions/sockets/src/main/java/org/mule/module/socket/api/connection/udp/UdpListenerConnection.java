/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import static org.mule.module.socket.internal.SocketUtils.configureConnection;
import static org.mule.module.socket.internal.SocketUtils.createPacket;
import static org.mule.module.socket.internal.SocketUtils.newSocket;
import org.mule.module.socket.api.connection.ListenerConnection;
import org.mule.module.socket.api.exceptions.ReadingTimeoutException;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.api.worker.SocketWorker;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpListenerConnection extends AbstractUdpConnection implements ListenerConnection
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpListenerConnection.class);


    public UdpListenerConnection(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);
    }

    @Override
    public void connect() throws ConnectionException
    {
        socket = newSocket(host, port);
        configureConnection(socket, socketProperties);
    }

    @Override
    public SocketWorker listen() throws IOException, ConnectionException
    {
        DatagramPacket packet = createPacket(socketProperties.getReceiveBufferSize());

        try
        {
            socket.receive(packet);
        }
        catch (SocketTimeoutException e)
        {
            throw new ReadingTimeoutException("UDP Source timed out while awaiting for new packages", e);
        }
        catch (IOException e)
        {
            if (socket.isClosed())
            {
                LOGGER.debug("UDP listener socket has been closed");
                throw new ConnectionException("An error occurred while listening for new UDP packets", e);
            }

            throw e;
        }

        DatagramSocket newConnection = new DatagramSocket();
        configureConnection(newConnection, socketProperties);
        //todo  fix udp
        return null;//new UdpSourceClient(newConnection, packet, socketProperties, objectSerializer);
    }

}
