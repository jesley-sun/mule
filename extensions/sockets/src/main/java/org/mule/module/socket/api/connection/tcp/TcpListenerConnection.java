/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.tcp;

import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.client.TcpClient;
import org.mule.module.socket.api.connection.ListenerConnection;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.tcp.TcpServerSocketProperties;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TcpListenerConnection extends AbstractTcpConnection implements ListenerConnection
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpListenerConnection.class);

    private ServerSocket serverSocket;
    private TcpServerSocketProperties socketProperties;

    public TcpListenerConnection(TcpServerSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
    {
        super(host, port, protocol);
        this.socketProperties = socketProperties;
    }

    private void configureIncomingConnection(Socket newConnection) throws IOException
    {
        try
        {
            // todo same code as configuring a requester socket
            newConnection.setSoTimeout(socketProperties.getTimeout());
            newConnection.setTcpNoDelay(true);
            newConnection.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            newConnection.setSendBufferSize(socketProperties.getSendBufferSize());

            if (socketProperties.getKeepAlive() != null)
            {
                newConnection.setKeepAlive(socketProperties.getKeepAlive());
            }

            if (socketProperties.getLinger() != null)
            {
                newConnection.setSoLinger(true, socketProperties.getLinger());
            }
        }
        catch (SocketException e)
        {
            throw new IOException("Could not configure incoming TCP connection", e);
        }
    }

    private Socket acceptConnection() throws ConnectionException, IOException
    {
        try
        {
            return serverSocket.accept();
        }
        catch (IOException e)
        {
            LOGGER.debug(e.getMessage());
            if (serverSocket.isClosed())
            {
                LOGGER.debug("TCP listener socket has been closed");
                throw new ConnectionException("An error occurred while listening for new TCP connections", e);
            }
            throw e;
        }
    }

    @Override
    public SocketClient listen() throws IOException, ConnectionException
    {
        Socket newConnection = acceptConnection();
        configureIncomingConnection(newConnection);
        return new TcpClient(newConnection, protocol);
    }

    @Override
    public void disconnect()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            LOGGER.error("An error occurred when closing TCP listener socket");
        }
    }

    @Override
    public void connect() throws ConnectionException
    {
        try
        {
            serverSocket = new ServerSocket();

            serverSocket.setSoTimeout(socketProperties.getTimeout());
            serverSocket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            serverSocket.setReuseAddress(socketProperties.getReuseAddress());
        }
        catch (Exception e)
        {
            throw new ConnectionException("Could not create TCP listener socket", e);
        }

        InetSocketAddress address = SocketUtils.getSocketAddress(host, port, socketProperties.getFailOnUnresolvedHost());

        try
        {
            serverSocket.bind(address, socketProperties.getReceiveBacklog());
        }
        catch (IOException e)
        {
            throw new ConnectionException(String.format("Could not bind socket to host '%s' and port '%d'", host, port), e);
        }
    }

    @Override
    public void validate() throws ConnectionException
    {
        if (serverSocket.isClosed() || !serverSocket.isBound())
        {
            throw new ConnectionException("Listener TCP socket is invalid");
        }
    }
}
