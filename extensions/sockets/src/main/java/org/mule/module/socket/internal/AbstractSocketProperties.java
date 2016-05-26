/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.SocketProperties;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.ConfigName;
import org.mule.runtime.extension.api.annotation.param.Optional;

public abstract class AbstractSocketProperties implements SocketProperties
{

    public static final int DEFAULT_BUFFER_SIZE =  1024 * 16;
    public static final String DEFAULT_VALUE_BUFFER_SIZE =  "16384";

    /**
     * The name of this config object, so that it can be referenced by config elements.
     */
    @ConfigName
    protected String name;

    /**
     * The size of the buffer (in bytes) used when sending data, set on the socket itself.
     */
    @Parameter
    @Optional(defaultValue = DEFAULT_VALUE_BUFFER_SIZE)
    protected int sendBufferSize = DEFAULT_BUFFER_SIZE;

    /**
     * The size of the buffer (in bytes) used when receiving data, set on the socket itself.
     */
    @Parameter
    @Optional(defaultValue = DEFAULT_VALUE_BUFFER_SIZE)
    protected int receiveBufferSize = DEFAULT_BUFFER_SIZE;

    /**
     * This sets the SO_TIMEOUT value on sockets. Indicates the amount of time (in milliseconds)
     * that the socket will wait in a blocking operation before failing.
     * <p>
     * A value of 0 (the default) means waiting indefinitely.
     */
    @Parameter
    @Optional(defaultValue = "0")
    protected int timeout = 0;

    /**
     * If set (the default), SO_REUSEADDRESS is set on the sockets before binding.
     * This helps reduce "address already in use" errors when a socket is re-used.
     */
    @Parameter
    @Optional(defaultValue = "true")
    private boolean reuseAddress = true;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSendBufferSize()
    {
        return sendBufferSize;
    }

    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }


    public boolean getReuseAddress()
    {
        return reuseAddress;
    }
}
