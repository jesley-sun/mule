/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection;

import org.mule.module.socket.api.client.SocketClient;
import org.mule.module.socket.api.worker.SocketWorker;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.IOException;

public interface ListenerConnection extends SocketConnection
{
    SocketWorker listen() throws IOException, ConnectionException;
}
