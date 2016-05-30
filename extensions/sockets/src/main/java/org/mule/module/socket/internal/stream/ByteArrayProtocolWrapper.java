/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal.stream;

import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.internal.DelegatingInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayProtocolWrapper extends DelegatingInputStream
{

    private final TcpProtocol protocol;
    private final InputStream inputStream;
    private DelegatingInputStream delegate;

    public ByteArrayProtocolWrapper(TcpProtocol protocol, InputStream inputStream)
    {
        this.protocol = protocol;
        this.inputStream = inputStream;
    }

    @Override
    protected InputStream getDelegate()
    {
        //todo sync
        if (delegate == null)
        {
            delegate = new ByteArrayInputStream((byte[]) protocol.read(inputStream));
        }

        return delegate;
    }
}
