/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import static java.util.Arrays.copyOf;
import static org.mule.module.socket.internal.SocketUtils.createPacket;
import static org.mule.module.socket.internal.SocketUtils.getAddress;
import org.mule.module.socket.api.exceptions.ReadingTimeoutException;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.internal.DefaultUdpRequestingSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class UdpOperationClient extends AbstractUdpClient implements SocketClient
{

    private SocketAddress socketAddress;
    private final DefaultUdpRequestingSocketProperties socketProperties;

    public UdpOperationClient(DatagramSocket socket, String host, int port, DefaultUdpRequestingSocketProperties socketProperties, ObjectSerializer objectSerializer)
    {
        super(objectSerializer);
        this.socketProperties = socketProperties;
        this.socket = socket;
        this.socketAddress = getAddress(host, port);
    }

    @Override
    public void write(Object data) throws IOException
    {
        byte[] byteArray = SocketUtils.getByteArray(data, true, objectSerializer);
        DatagramPacket sendPacket = createPacket(byteArray);
        sendPacket.setSocketAddress(socketAddress);
        socket.send(sendPacket);
    }

    @Override
    public InputStream read() throws IOException
    {
        DatagramPacket receivedPacket = createPacket(socketProperties.getReceiveBufferSize());
        receivedPacket.setSocketAddress(socketAddress);
        try
        {
            socket.receive(receivedPacket);
            return new ByteArrayInputStream(copyOf(receivedPacket.getData(), receivedPacket.getLength()));
        }
        catch (SocketTimeoutException e)
        {
            throw new ReadingTimeoutException("UDP socket timed out while waiting for a response", e);
        }
    }

    @Override
    public SocketAttributes getAttributes()
    {
        return new ImmutableSocketAttributes(socket);

    }
}
