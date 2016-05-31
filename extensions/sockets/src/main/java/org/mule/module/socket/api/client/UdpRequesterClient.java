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
//import org.mule.module.socket.api.exceptions.UnresolvableHostException;
//import org.mule.module.socket.api.udp.UdpSocketProperties;
//import org.mule.module.socket.internal.SocketUtils;
//import org.mule.runtime.api.connection.ConnectionException;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.SocketException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class UdpRequesterClient extends AbstractUdpClient implements RequesterSocket
//{
//    private static final Logger LOGGER = LoggerFactory.getLogger(UdpRequesterClient.class);
//
//    public UdpRequesterClient(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
//    {
//        super(socketProperties, host, port);
//
//        try
//        {
//            socket = new DatagramSocket();
//        }
//        catch (SocketException e)
//        {
//            throw new ConnectionException("Could not create requester UDP socket");
//        }
//        initialise();
//    }
//
//    @Override
//    public void send(Object data) throws ConnectionException
//    {
//        try
//        {
//            byte[] byteArray = SocketUtils.getByteArray(data, true, false, objectSerializer);
//            DatagramPacket sendPacket = createPacket(byteArray);
//            socket.send(sendPacket);
//        }
//        catch (IOException e)
//        {
//            throw new ConnectionException("An error occurred while trying to write into the UDP socket", e);
//        }
//    }
//
//    @Override
//    public InputStream receive() throws IOException
//    {
//        DatagramPacket packet = createPacket();
//        try
//        {
//            socket.receive(packet);
//            return new ByteArrayInputStream(packet.getData());
//        }
//        catch (IOException e)
//        {
//            LOGGER.error("An error occurred when receiving response from UDP requester socket");
//            throw e;
//        }
//    }
//
//    @Override
//    public void connect() throws ConnectionException, UnresolvableHostException
//    {
//         //TODO
//    }
//
//    @Override
//    public void disconnect()
//    {
//        SocketUtils.closeSocket(socket);
//    }
//
//    @Override
//    public void validate() throws ConnectionException
//    {
//        if (!socket.isConnected())
//        {
//            throw new ConnectionException("Requester UDP socket is not connected");
//        }
//    }
//}
