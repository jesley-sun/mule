/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.core.api.MuleContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpSocketDelegate implements SocketDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UdpSocketDelegate.class);
    private final DatagramPacket packet;
    private final MuleContext muleContext;
    private final SocketAttributes attributes;

    public UdpSocketDelegate(DatagramPacket packet, ImmutableSocketAttributes attributes, MuleContext muleContext)
    {
        this.packet = packet;
        this.attributes = attributes;
        this.muleContext = muleContext;
    }

    @Override
    public MuleMessage<InputStream, SocketAttributes> getMuleMessage()
    {
        if (packet.getLength() > 0)
        {
            return SocketUtils.createMuleMessage(new ByteArrayInputStream(Arrays.copyOf(packet.getData(), packet.getLength())), attributes, muleContext);
        }
        else
        {
            LOGGER.debug("Received UDP packet without content");
            return SocketUtils.createMuleMessageWithNullPayload(attributes, muleContext);
        }
    }

    @Override
    public void close()
    {
        // UDP sockets are stateless
    }
}
