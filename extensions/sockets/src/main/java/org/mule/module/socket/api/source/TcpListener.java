/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.source;

import org.mule.module.socket.api.client.TcpListenerClient;
import org.mule.module.socket.api.protocol.SafeProtocol;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;

import javax.inject.Inject;

@Alias("tcp-listener")
public class TcpListener extends AbstractSocketListener
{
    @Inject
    private MuleContext muleContext;

    @Parameter
    @Optional
    private TcpProtocol protocol = new SafeProtocol();

    @Connection
    private TcpListenerClient client;
}
