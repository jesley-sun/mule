/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.socket.api.source;

import java.io.Serializable;

/**
 * Canonical representation of a socket's metadata attributes.
 * <p>
 * It contains information such as a the port from which the
 * responses are being received, the name of the remote host and its address.
 *
 * @since 4.0
 */
public interface SocketAttributes extends Serializable
{

    int getRemotePort();

    String getRemoteHostAddress();

    String getRemoteHostName();
}
