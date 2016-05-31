/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import java.io.IOException;
import java.io.InputStream;

public abstract class DelegatingInputStream extends InputStream
{

    protected abstract InputStream getDelegate() throws IOException;

    public int available() throws IOException
    {
        return getDelegate().available();
    }

    public synchronized void mark(int readlimit)
    {
        try
        {
            getDelegate().mark(readlimit);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean markSupported()
    {
        try
        {
            return getDelegate().markSupported();
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public synchronized void reset() throws IOException
    {
        getDelegate().reset();
    }

    public long skip(long n) throws IOException
    {
        return getDelegate().skip(n);
    }

    public int read() throws IOException
    {
        return getDelegate().read();
    }

    public int read(byte b[]) throws IOException
    {
        return getDelegate().read(b);
    }

    public int read(byte b[], int off, int len) throws IOException
    {
        return getDelegate().read(b, off, len);
    }

    public void close() throws IOException
    {
        getDelegate().close();
    }

}
