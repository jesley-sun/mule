/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api;

import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.connection.RequesterConnection;
import org.mule.module.socket.internal.metadata.SocketMetadataResolver;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic set of operations for extensions which write content
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
    @MetadataScope(outputResolver = SocketMetadataResolver.class)
    public InputStream send(@Connection RequesterConnection connection,
                            @Optional(defaultValue = "#[payload]") Object data,
                            @Optional(defaultValue = "UTF-8") String encoding) throws ConnectionException, IOException
    {
        SocketClient client = connection.getClient();
        //try
        //{
            client.write(data);
        System.out.println("WRITE DATA");
        InputStream read = client.read();
        System.out.println("READ DATA");
        return read;
        //}
        //catch (IOException e)
        //{
        //
        //}
    }
}
