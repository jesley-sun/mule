/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.tcp.TcpServerSocketProperties;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

/**
 * Default mutable implementation of the {@code TcpServerSocketProperties} interface.
 *
 * @since 4.0
 */
public class DefaultTcpServerSocketProperties extends AbstractTcpSocketProperties implements TcpServerSocketProperties
{
    /**
     * {@inheritDoc}
     */
    @Parameter
    @Optional(defaultValue = "50")
    private int receiveBacklog = 50;

    public int getReceiveBacklog()
    {
        return receiveBacklog;
    }
}
