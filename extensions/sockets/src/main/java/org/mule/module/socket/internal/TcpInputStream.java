/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import java.io.IOException;
import java.io.InputStream;

public class TcpInputStream extends DelegatingInputStream
{
    private InputStream delegate;
    private boolean streaming = false;
    
    public TcpInputStream(InputStream inputStream)
    {
        delegate = inputStream;
    }

    public boolean isStreaming()
    {
        return streaming;
    }

    public void setStreaming(boolean streaming)
    {
        this.streaming = streaming;
    }

    @Override
    protected InputStream getDelegate() throws IOException
    {
        return delegate;
    }
}


