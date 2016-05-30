/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.runtime.core.api.MuleContext;

/**
 *
 */
public abstract class AbstractSocketClient implements SocketClient
{

    protected String host;
    protected int port;
    protected MuleContext muleContext;

    public AbstractSocketClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

}
