/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
///*
// * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
// * The software in this package is published under the terms of the CPAL v1.0
// * license, a copy of which has been included with this distribution in the
// * LICENSE.txt file.
// */
//package org.mule.module.socket.api.client;
//
//public class TcpListenerClient extends AbstractTcpConnection implements ListenerSocket
//{
//
//    //private static final Logger LOGGER = LoggerFactory.getLogger(TcpListenerClient.class);
//    //
//    //private ServerSocket socket;
//    //private TcpServerSocketProperties socketProperties;
//    //
//    //public TcpListenerClient(TcpServerSocketProperties socketProperties, TcpProtocol protocol, String host, Integer port) throws ConnectionException
//    //{
//    //    super(protocol, host, port);
//    //    this.socketProperties = socketProperties;
//    //    initialise();
//    //}
//    //
//    //public void initialise() throws ConnectionException, UnresolvableHostException
//    //{
//    //    try
//    //    {
//    //        this.socket = new ServerSocket();
//    //
//    //        // todo socket timeout must be always 0
//    //        //socket.setSoTimeout(socketProperties.getTimeout());
//    //        socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
//    //        socket.setReuseAddress(socketProperties.getReuseAddress());
//    //    }
//    //    catch (Exception e)
//    //    {
//    //        throw new ConnectionException("Could not create TCP listener socket", e);
//    //    }
//    //
//    //    InetSocketAddress address = SocketUtils.getSocketAddress(host, port, socketProperties.getFailOnUnresolvedHost());
//    //
//    //    try
//    //    {
//    //        socket.bind(address, socketProperties.getReceiveBacklog());
//    //    }
//    //    catch (IOException e)
//    //    {
//    //        throw new ConnectionException(String.format("Could not bind socket to host '%s' and port '%d'", host, port), e);
//    //    }
//    //}
//    //
//    //private Socket acceptConnection() throws ConnectionException, IOException
//    //{
//    //    try
//    //    {
//    //        return socket.accept();
//    //    }
//    //    catch (IOException e)
//    //    {
//    //        LOGGER.debug(e.getMessage());
//    //        if (socket.isClosed())
//    //        {
//    //            LOGGER.debug("TCP listener socket has been closed");
//    //            throw new ConnectionException("An error occurred while listening for new TCP connections", e);
//    //        }
//    //        throw e;
//    //    }
//    //}
//    //
//    //private TcpClient getClient() throws IOException, ConnectionException
//    //{
//    //    Socket newConnection = acceptConnection();
//    //    configureIncomingConnection(newConnection);
//    //    return new TcpClient(newConnection, protocol);
//    //}
//    //
//    //private void configureIncomingConnection(Socket newConnection) throws IOException
//    //{
//    //    try
//    //    {
//    //        // todo same code as configuring a requester socket
//    //        newConnection.setSoTimeout(socketProperties.getTimeout());
//    //        newConnection.setTcpNoDelay(true);
//    //        newConnection.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
//    //        newConnection.setSendBufferSize(socketProperties.getSendBufferSize());
//    //
//    //        if (socketProperties.getKeepAlive() != null)
//    //        {
//    //            newConnection.setKeepAlive(socketProperties.getKeepAlive());
//    //        }
//    //
//    //        if (socketProperties.getLinger() != null)
//    //        {
//    //            newConnection.setSoLinger(true, socketProperties.getLinger());
//    //        }
//    //    }
//    //    catch (SocketException e)
//    //    {
//    //        throw new IOException("Could not configure incoming TCP connection", e);
//    //    }
//    //}
//    //
//    //public synchronized void disconnect()
//    //{
//    //    try
//    //    {
//    //        socket.close();
//    //    }
//    //    catch (IOException e)
//    //    {
//    //        LOGGER.error("An error occurred when closing TCP listener socket");
//    //    }
//    //}
//    //
//    //@Override
//    //public void validate() throws ConnectionException
//    //{
//    //    if (socket.isClosed() || !socket.isBound())
//    //    {
//    //        throw new ConnectionException("Listener TCP socket is invalid");
//    //    }
//    //}
//}
