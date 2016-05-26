/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api;

import org.mule.module.socket.api.client.RequesterSocket;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;

/**
 * Basic set of operations for extensions which send content
 * across a generic socket
 *
 * @since 4.0
 */
public class SocketOperations
{

    /**
     * Sends the data through the client.
     *
     * @param data that will be serialized and sent through the socket.
     * @throws ConnectionException if the connection couldn't be established, if the remote host was unavailable.
     */
    public void send(@Connection RequesterSocket client,
                     @Optional(defaultValue = "#[payload]") Object data,
                     @Optional(defaultValue = "UTF-8") String encoding) throws ConnectionException
    {
        client.send(data);
    }
}
