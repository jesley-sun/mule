/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import static org.apache.commons.lang3.StringUtils.isBlank;
import org.mule.module.socket.api.connection.AbstractSocketConnection;
import org.mule.module.socket.api.exceptions.UnresolvableHostException;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.module.socket.api.udp.UdpSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionExceptionCode;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.api.message.NullPayload;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.DefaultMuleMessage;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.serialization.ObjectSerializer;
import org.mule.runtime.core.transformer.types.DataTypeFactory;
import org.mule.runtime.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketUtils
{

    private static final int PORT_CHOSEN_BY_SYSTEM = 0;

    public static void write(OutputStream os, Object data, boolean payloadOnly, ObjectSerializer objectSerializer) throws IOException
    {
        writeByteArray(os, getByteArray(data, payloadOnly, objectSerializer));
    }

    protected static void writeByteArray(OutputStream os, byte[] data) throws IOException
    {
        os.write(data);
    }

    public static byte[] getByteArray(Object data, boolean payloadOnly, ObjectSerializer objectSerializer) throws IOException
    {
        if (data instanceof InputStream)
        {
            return IOUtils.toByteArray((InputStream) data);
        }
        else if (data instanceof MuleMessage)
        {
            if (payloadOnly)
            {
                return getByteArray(((MuleMessage) data).getPayload(), payloadOnly, objectSerializer);
            }
            else
            {
                return objectSerializer.serialize(data);
            }
        }
        else if (data instanceof byte[])
        {
            return (byte[]) data;
        }
        else if (data instanceof String)
        {
            // todo encoding
            return ((String) data).getBytes();
        }
        else if (data instanceof Serializable)
        {
            return objectSerializer.serialize(data);
        }

        throw new IllegalArgumentException(String.format("Cannot serialize data: '%s'", data));
    }

    public static ConnectionValidationResult validate(AbstractSocketConnection connection)
    {
        try
        {
            connection.validate();
            return ConnectionValidationResult.success();
        }
        catch (UnresolvableHostException e)
        {
            return ConnectionValidationResult.failure(e.getMessage(), ConnectionExceptionCode.UNKNOWN_HOST, e);
        }
        catch (ConnectionException e)
        {
            return ConnectionValidationResult.failure(e.getMessage(), ConnectionExceptionCode.UNKNOWN, e);
        }
    }

    public static void closeSocket(DatagramSocket socket) throws IOException
    {
        socket.close();
    }

    public static void closeSocket(Socket socket) throws IOException
    {
        socket.close();
    }

    public static InetSocketAddress getAddress(String host, Integer port)
    {
        if (port == null && isBlank(host))
        {
            return new InetSocketAddress(PORT_CHOSEN_BY_SYSTEM);
        }
        else if (port == null && !isBlank(host))
        {
            return new InetSocketAddress(host, PORT_CHOSEN_BY_SYSTEM);
        }
        else if (port != null && isBlank(host))
        {
            return new InetSocketAddress(port);
        }

        return new InetSocketAddress(host, port);
    }

    public static InetSocketAddress getSocketAddress(String host, Integer port, boolean failOnUnresolvedHost)
    {

        InetSocketAddress address = getAddress(host, port);
        if (address.isUnresolved() && failOnUnresolvedHost)
        {
            throw new UnresolvableHostException(String.format("Host '%s' could not be resolved", host));
        }
        return address;
    }

    public static InetAddress getSocketAddressbyName(String host)
            throws UnresolvableHostException
    {
        try
        {
            return InetAddress.getByName(host);
        }
        catch (UnknownHostException e)
        {
            throw new UnresolvableHostException(String.format("Host name '%s' could not be resolved", host));
        }
    }

    public static MuleMessage<InputStream, SocketAttributes> createMuleMessage(InputStream content, SocketAttributes attributes, MuleContext muleContext)
    {
        DataType dataType = DataTypeFactory.create(InputStream.class);
        Object payload = NullPayload.getInstance();
        MuleMessage<InputStream, SocketAttributes> message;

        if (content != null)
        {
            payload = content;
        }

        message = (MuleMessage) new DefaultMuleMessage(payload, dataType, attributes, muleContext);
        return message;
    }

    public static MuleMessage<InputStream, SocketAttributes> createMuleMessageWithNullPayload(SocketAttributes attributes, MuleContext muleContext)
    {
        Object payload = NullPayload.getInstance();
        DataType dataType = DataTypeFactory.create(NullPayload.class);
        return (MuleMessage) new DefaultMuleMessage(payload, dataType, attributes, muleContext);
    }

    /**
     * Creates a {@link DatagramPacket} with the size of the content, addressed to
     * the port and address of the client.
     *
     * @param content that is going to be sent inside the packet
     * @return a packet ready to be sent
     * @throws UnresolvableHostException
     */
    public static DatagramPacket createPacket(byte[] content) throws UnresolvableHostException
    {
        return new DatagramPacket(content, content.length);
    }

    /**
     * @return a packet configured to be used for receiving purposes
     * @throws UnresolvableHostException
     */
    public static DatagramPacket createPacket(int bufferSize) throws UnresolvableHostException
    {
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        return packet;
    }


    public static DatagramSocket newSocket(String host, Integer port) throws ConnectionException
    {
        try
        {
            return new DatagramSocket(getAddress(host, port));
        }
        catch (Exception e)
        {
            throw new ConnectionException("Could not bind UDP Socket", e);
        }
    }

    public static void configureConnection(DatagramSocket socket, UdpSocketProperties socketProperties) throws ConnectionException
    {
        if (socket == null)
        {
            throw new IllegalStateException("UDP Socket must be created before being configured");
        }

        try
        {
            socket.setSendBufferSize(socketProperties.getSendBufferSize());
            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
            socket.setBroadcast(socketProperties.getBroadcast());
            socket.setSoTimeout(socketProperties.getTimeout());
            socket.setReuseAddress(socketProperties.getReuseAddress());
        }
        catch (Exception e)
        {
            throw new ConnectionException("UDP Socket could not be created", e);
        }
    }
}
