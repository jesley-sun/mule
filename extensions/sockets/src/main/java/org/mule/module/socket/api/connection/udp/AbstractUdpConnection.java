/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.udp;

import org.mule.module.socket.api.connection.AbstractSocketConnection;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.core.api.serialization.DefaultObjectSerializer;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.IOException;
import java.net.DatagramSocket;

import javax.inject.Inject;

public abstract class AbstractUdpConnection extends AbstractSocketConnection
{

    protected DatagramSocket socket;
    protected final UdpSocketProperties socketProperties;

    @DefaultObjectSerializer
    @Inject
    protected ObjectSerializer objectSerializer;

    public AbstractUdpConnection(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
    {
        super(host, port);
        this.socketProperties = socketProperties;
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
