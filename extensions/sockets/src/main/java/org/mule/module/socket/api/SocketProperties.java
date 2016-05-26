/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.socket.api;

/**
 * Interface for objects that provide common configuration for generic sockets.
 * <p>
 * {@code null} values can be returned by any of the methods, meaning that there is no value defined for the property.
 *
 * @since 4.0
 */
public interface SocketProperties
{

    /**
     * The size of the buffer (in bytes) used when sending data, set on the socket itself.
     */
    int getSendBufferSize();

    /**
     * The size of the buffer (in bytes) used when receiving data, set on the socket itself.
     */
    int getReceiveBufferSize();

    /**
     * This sets the SO_TIMEOUT value on the sockets
     * On client sockets: Reading from the socket will block for up to this long (in milliseconds) before the read fails.
     * On server sockets:
     * <p>
     * A value of 0 (the default) causes the read to wait indefinitely (if no data arrives).
     */
    int getTimeout();


    /**
     * Enabling SO_REUSEADDR prior to binding the socket using bind(SocketAddress)
     * allows the socket to be bound even though a previous connection is in a timeout state.

     * For UDP sockets it may be necessary to bind more than one
     * socket to the same socket address. This is typically for the
     * purpose of receiving multicast packets
     */
    boolean getReuseAddress();
}
