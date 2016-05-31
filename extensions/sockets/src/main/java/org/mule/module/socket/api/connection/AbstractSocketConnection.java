/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection;

import org.mule.runtime.core.api.serialization.DefaultObjectSerializer;
import org.mule.runtime.core.api.serialization.ObjectSerializer;

import javax.inject.Inject;

/**
 *
 */
public abstract class AbstractSocketConnection implements SocketConnection
{

    @DefaultObjectSerializer
    @Inject
    protected ObjectSerializer objectSerializer;

    protected String host;
    protected int port;

    protected AbstractSocketConnection(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

}
