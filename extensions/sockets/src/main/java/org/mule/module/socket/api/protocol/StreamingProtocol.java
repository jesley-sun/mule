/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.protocol;

import org.mule.module.socket.internal.StreamingSocketInputStream;

import java.io.IOException;
import java.io.InputStream;

public class StreamingProtocol extends EOFProtocol
{

    public StreamingProtocol()
    {
        super();
    }

    public InputStream read(InputStream is) throws IOException
    {
        if (is instanceof StreamingSocketInputStream)
        {
            ((StreamingSocketInputStream) is).setStreaming(true);
        }

        return is;
    }
}


