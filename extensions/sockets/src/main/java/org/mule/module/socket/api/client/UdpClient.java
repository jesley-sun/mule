/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import static java.util.Arrays.copyOf;
import static org.mule.module.socket.internal.SocketUtils.connectSocket;
import static org.mule.module.socket.internal.SocketUtils.createPacket;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpClient implements SocketClient
{

    private DatagramSocket socket;
    private DatagramPacket packet;
    private final UdpSocketProperties socketProperties;
    private final ObjectSerializer objectSerializer;

    // used in the operation
    public UdpClient(DatagramSocket socket, UdpSocketProperties socketProperties, ObjectSerializer objectSerializer)
    {
        this.socket = socket;

        this.socketProperties = socketProperties;
        this.objectSerializer = objectSerializer;
    }

    // used in the source
    public UdpClient(DatagramPacket packet, UdpSocketProperties socketProperties, ObjectSerializer objectSerializer)
    {
        this.packet = packet;

        this.socketProperties = socketProperties;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void write(Object data) throws ConnectionException
    {
        try
        {
            if (socket == null)
            {
                socket = connectSocket(new DatagramSocket(), packet.getAddress(), packet.getPort());
                SocketUtils.configureConnection(socket, socketProperties);
            }

            byte[] byteArray = SocketUtils.getByteArray(data, true, false, objectSerializer);
            DatagramPacket sendPacket = createPacket(byteArray);

            socket.send(sendPacket);
        }
        catch (IOException e)
        {
            throw new ConnectionException("An error occurred while trying to write into the UDP socket", e);
        }
    }

    @Override
    public InputStream read() throws IOException
    {
        if (packet != null)
        {
            return new ByteArrayInputStream(copyOf(packet.getData(), packet.getLength()));
        }

        DatagramPacket packet = SocketUtils.createPacket(socketProperties.getReceiveBufferSize());
        socket.receive(packet);
        return new ByteArrayInputStream(copyOf(packet.getData(), packet.getLength()));
    }

    @Override
    public void close() throws IOException
    {
        SocketUtils.closeSocket(socket);
    }

    @Override
    public SocketAttributes getAttributes()
    {
        return new ImmutableSocketAttributes(socket);
    }
}
