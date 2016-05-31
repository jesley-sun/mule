/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import static org.mule.module.socket.internal.SocketUtils.getSocketAddressbyName;
import org.mule.module.socket.api.connection.AbstractSocketConnection;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class AbstractUdpConnection extends AbstractSocketConnection
{

    protected final DatagramSocket socket;
    protected final UdpSocketProperties socketProperties;
    protected InetAddress address;

    public AbstractUdpConnection(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(host, port);
        this.socketProperties = socketProperties;
        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            throw new ConnectionException("Could not create requester UDP socket");
        }
    }

    protected void configureConnection() throws ConnectionException
    {
        try
        {
            socket.setSendBufferSize(socketProperties.getSendBufferSize());
            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            socket.setBroadcast(socketProperties.getBroadcast());
            socket.setSoTimeout(socketProperties.getTimeout());
            socket.setReuseAddress(socketProperties.getReuseAddress());
            address = getSocketAddressbyName(host);
        }
        catch (Exception e)
        {
            throw new ConnectionException("UDP Socket could not be created", e);
        }
    }

    @Override
    public void disconnect()
    {
        try
        {
            SocketUtils.closeSocket(socket);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            //TODO
        }
    }

    @Override
    public void validate() throws ConnectionException
    {
        if (!socket.isClosed())
        {
            throw new ConnectionException("Requester UDP socket is closed");
        }
    }
}
