/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection.tcp;

import org.mule.module.socket.api.connection.AbstractSocketConnection;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.runtime.core.api.lifecycle.Initialisable;

abstract class AbstractTcpConnection extends AbstractSocketConnection implements Initialisable
{
    protected final TcpProtocol protocol;

    public AbstractTcpConnection(String host, int port, TcpProtocol protocol)
    {
        super(host, port);
        this.protocol = protocol;
    }

    @Override
    public void initialise()
    {
        protocol.setObjectSerializer(objectSerializer);
    }
}
