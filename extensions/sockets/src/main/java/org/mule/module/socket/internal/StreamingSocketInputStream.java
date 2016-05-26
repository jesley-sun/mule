/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import java.io.InputStream;

import org.apache.commons.io.input.AutoCloseInputStream;

public class StreamingSocketInputStream extends AutoCloseInputStream
{

    private boolean streaming;

    /**
     * Creates an automatically closing proxy for the given input stream.
     *
     * @param in underlying input stream
     */
    public StreamingSocketInputStream(InputStream in)
    {
        super(in);
    }

    public void setStreaming(boolean streaming)
    {
        this.streaming = streaming;
    }

    public boolean isStreaming()
    {
        return streaming;
    }
}
