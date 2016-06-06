/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import static java.util.Arrays.copyOf;
import static org.mule.module.socket.internal.SocketUtils.createPacket;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpSourceClient extends AbstractUdpClient implements SocketClient
{

    private final DatagramPacket packet;
    private final UdpSocketProperties socketProperties;

    public UdpSourceClient(DatagramSocket socket, DatagramPacket packet, UdpSocketProperties socketProperties, ObjectSerializer objectSerializer)
    {
        super(objectSerializer);
        this.packet = packet;
        this.socket = socket;
        this.socketProperties = socketProperties;
    }

    @Override
    public void write(Object data) throws IOException
    {
        byte[] byteArray = SocketUtils.getByteArray(data, true, objectSerializer);
        DatagramPacket sendPacket = createPacket(byteArray);
        sendPacket.setSocketAddress(packet.getSocketAddress());
        socket.send(sendPacket);
    }

    @Override
    public InputStream read() throws IOException
    {
        return new ByteArrayInputStream(copyOf(packet.getData(), packet.getLength()));
    }

    @Override
    public SocketAttributes getAttributes()
    {
        return new ImmutableSocketAttributes(packet);
    }
}
