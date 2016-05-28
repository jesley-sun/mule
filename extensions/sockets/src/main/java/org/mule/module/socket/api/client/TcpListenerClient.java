/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.tcp.TcpServerSocketProperties;
import org.mule.module.socket.internal.SocketDelegate;
import org.mule.module.socket.internal.TcpSocketDelegate;
import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpListenerClient extends AbstractTcpClient implements ListenerSocket
{

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpListenerClient.class);

    private ServerSocket socket;
    private TcpServerSocketProperties socketProperties;

    public TcpListenerClient(TcpServerSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
    {
        super(protocol, host, port);
        this.socketProperties = socketProperties;
        initialise();
    }

    public void initialise() throws ConnectionException, UnresolvableHostException
    {
        try
        {
            this.socket = new ServerSocket();

            socket.setSoTimeout(socketProperties.getTimeout());
            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            socket.setReuseAddress(socketProperties.getReuseAddress());
        }
        catch (Exception e)
        {
            throw new ConnectionException("Could not create TCP listener socket");
        }

        InetSocketAddress address = SocketUtils.getSocketAddress(host, port, socketProperties.getFailOnUnresolvedHost());

        try
        {
            socket.bind(address, socketProperties.getReceiveBacklog());
        }
        catch (IOException e)
        {
            throw new ConnectionException(String.format("Could not bind socket to host '%s' and port '%d'", host, port));
        }
    }

    // todo this shouldnt be optional
    private Optional<Socket> listen() throws IOException, ConnectionException
    {
        try
        {
            Socket newConnection = socket.accept();
            configureIncomingConnection(newConnection);
            return Optional.ofNullable(newConnection);
        }
        catch (Exception e)
        {
            if (socket.isClosed())
            {
                LOGGER.debug("TCP listener socket has been closed");
                return Optional.empty();
            }
            else
            {
                throw new ConnectionException("An error occurred while listening for new TCP connections", e);
            }
        }
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
            throw new IOException("Could not configure incoming TCP connection");
        }
    }

    @Override
    public Optional<SocketDelegate> receive() throws ConnectionException, IOException
    {

        Optional<Socket> incomingConnection = listen();

        if (!incomingConnection.isPresent())
        {
            return Optional.empty();
        }

        Socket socket = incomingConnection.get();
        return Optional.of(new TcpSocketDelegate(socket, protocol, muleContext));
    }

    public synchronized void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            LOGGER.error("An error occurred when closing TCP listener socket");
        }
    }

    @Override
    public void validate() throws ConnectionException
    {
        if (socket.isClosed() || !socket.isBound())
        {
            throw new ConnectionException("Listener TCP socket is invalid");
        }
    }
}
