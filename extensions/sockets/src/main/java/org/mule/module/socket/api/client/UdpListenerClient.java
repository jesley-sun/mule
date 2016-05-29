/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.SocketDelegate;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.module.socket.internal.UdpSocketDelegate;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpListenerClient extends AbstractUdpClient implements ListenerSocket
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpListenerClient.class);


    public UdpListenerClient(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(socketProperties, host, port);

        try
        {
            socket = new DatagramSocket(port);
        }
        catch (SocketException e)
        {
            throw new ConnectionException(String.format("Could not create listener UDP socket on port '%d'", port));
        }

        initialise();
    }

    public Optional<SocketDelegate> receive() throws ConnectionException, UnresolvableHostException
    {

        DatagramPacket packet = createPacket();
        try
        {
            socket.receive(packet);
            return Optional.of(new UdpSocketDelegate(packet, new ImmutableSocketAttributes(socket), muleContext));
        }
        catch (IOException e)
        {
            LOGGER.error(String.format("An error occurred when receiving from UDP socket listening in host '%s' port '%d'", host, port));
            return Optional.empty();
        }
    }

    protected DatagramPacket createPacket() throws UnresolvableHostException
    {
        int bufferSize = socketProperties.getReceiveBufferSize();
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        packet.setAddress(address);
        packet.setPort(port);
        return packet;
    }


    public void disconnect()
    {
        SocketUtils.closeSocket(socket);
    }

    @Override
    public void validate() throws ConnectionException, UnresolvableHostException
    {
        if (socket.isClosed() || !socket.isBound())
        {
            throw new ConnectionException("UDP listener socket is not connected");
        }
    }
}
