/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.internal.SocketUtils;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.IOException;
import java.net.DatagramSocket;

public class AbstractUdpClient
{

    protected final ObjectSerializer objectSerializer;
    protected DatagramSocket socket;

    public AbstractUdpClient(ObjectSerializer objectSerializer)
    {
        this.objectSerializer = objectSerializer;
    }

    public void close() throws IOException
    {
        SocketUtils.closeSocket(socket);
    }
}
