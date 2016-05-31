/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api;

public interface RequestingSocketProperties
{

    /**
     * Specifies a certain port to be used by the socket for receiving purposes. If it was not set, the system will
     * pick up an ephemeral port to bind the socket with.
     */
    Integer getLocalPort();


    /**
     * Specifies a certain host to be used by the socket for receiving purposes. If it was not set, the system will
     * pick up a host name to bind the socket with.
     */
    String getBindingHost();
}
