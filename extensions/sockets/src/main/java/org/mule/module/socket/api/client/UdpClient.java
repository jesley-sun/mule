/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import static java.util.Arrays.copyOf;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
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

    private final DatagramSocket socket;
    private final UdpSocketProperties socketProperties;
    private final ObjectSerializer objectSerializer;

    public UdpClient(DatagramSocket socket, UdpSocketProperties socketProperties, ObjectSerializer objectSerializer)
    {
        this.socket = socket;
        this.socketProperties = socketProperties;
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void send(Object data) throws ConnectionException
    {
        try
        {
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
    public InputStream receive() throws IOException
    {
        DatagramPacket packet = createPacket();
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

    /**
     * Creates a {@link DatagramPacket} with the size of the content, addressed to
     * the port and address of the client.
     *
     * @param content that is going to be sent inside the packet
     * @return a packet ready to be sent
     * @throws UnresolvableHostException
     */
    private DatagramPacket createPacket(byte[] content) throws UnresolvableHostException
    {
        DatagramPacket packet = new DatagramPacket(content, content.length);
        return packet;
    }

    /**
     * @return a packet configured to be used for receiving purposes
     * @throws UnresolvableHostException
     */
    private DatagramPacket createPacket() throws UnresolvableHostException
    {
        int bufferSize = socketProperties.getReceiveBufferSize();
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        return packet;
    }
}
