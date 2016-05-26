/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.source.ImmutableSocketAttributes;

import java.io.InputStream;

public class ConnectionEvent
{

    private InputStream content;

    public ImmutableSocketAttributes getAttributes()
    {
        return attributes;
    }

    private ImmutableSocketAttributes attributes;

    public InputStream getContent()
    {
        return content;
    }

    public ConnectionEvent(InputStream content, ImmutableSocketAttributes attributes)
    {
        this.content = content;
        this.attributes = attributes;
    }

    public ConnectionEvent(ImmutableSocketAttributes attributes)
    {
        this.attributes = attributes;
    }
}
