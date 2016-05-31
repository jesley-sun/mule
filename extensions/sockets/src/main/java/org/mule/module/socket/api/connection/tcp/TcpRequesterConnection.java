/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.tcp;

import static org.mule.module.socket.internal.SocketUtils.getSocketAddress;
import org.mule.module.socket.api.client.TcpClient;
import org.mule.module.socket.api.connection.RequesterConnection;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.tcp.TcpClientSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpRequesterConnection extends AbstractTcpConnection implements RequesterConnection
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpRequesterConnection.class);
    private final Socket socket;
    private final TcpClientSocketProperties socketProperties;
    private final InetSocketAddress address;

    public TcpRequesterConnection(TcpClientSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
    {
        super(host, port, protocol);
        socket = new Socket();
        address = getSocketAddress(host, port, socketProperties.getFailOnUnresolvedHost());
        this.socketProperties = socketProperties;
    }

    @Override
    public TcpClient getClient()
    {
        return new TcpClient(socket, protocol);
    }

    @Override
    public void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            LOGGER.error("An error occurred when  closing TCP requester socket");
        }
    }

    @Override
    public void connect() throws ConnectionException
    {

        try
        {
            socket.setSendBufferSize(socketProperties.getSendBufferSize());
            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            socket.setTcpNoDelay(socketProperties.getSendTcpNoDelay());
            socket.setSoTimeout(socketProperties.getTimeout());

            if (socketProperties.getKeepAlive() != null)
            {
                socket.setKeepAlive(socketProperties.getKeepAlive());
            }

            if (socketProperties.getLinger() != null)
            {
                socket.setSoLinger(true, socketProperties.getLinger());
            }


            InetSocketAddress localAddress = getSocketAddress(socketProperties.getBindingHost(), socketProperties.getLocalPort(), socketProperties.getFailOnUnresolvedHost());
            socket.bind(localAddress);

            socket.connect(address, socketProperties.getConnectionTimeout());
        }
        catch (Exception e)
        {
            throw new ConnectionException(String.format("Could not connect TCP requester socket to host '%s' on port '%d'", host, port), e);
        }

    }

    @Override
    public void validate() throws ConnectionException, UnresolvableHostException
    {
        if (!socket.isConnected() || socket.isClosed())
        {
            throw new ConnectionException("Requester TCP socket is not connected");
        }
    }


}
