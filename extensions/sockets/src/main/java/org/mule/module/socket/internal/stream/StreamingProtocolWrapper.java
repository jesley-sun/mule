/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal.stream;

import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.internal.DelegatingInputStream;

import java.io.InputStream;

public class StreamingProtocolWrapper extends DelegatingInputStream
{
    private final InputStream inputStream;
    private final TcpProtocol protocol;


    public StreamingProtocolWrapper(TcpProtocol protocol, InputStream inputStream)
    {
        this.protocol = protocol;
        this.inputStream = inputStream;
    }

    @Override
    protected InputStream getDelegate()
    {
        return (InputStream) protocol.read(inputStream);
    }
}
