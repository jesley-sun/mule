/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.socket.api.protocol;

import org.mule.module.socket.internal.stream.ByteArrayProtocolWrapper;
import org.mule.runtime.core.api.serialization.ObjectSerializer;
import org.mule.runtime.core.util.NumberUtils;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Abstract class has been introduced so as to have the byte protocols (i.e. the
 * protocols that had only a single write method taking just an array of bytes as a
 * parameter) to inherit from since they will all behave the same, i.e. if the object
 * is serializable, serialize it into an array of bytes and send it.
 *
 * @since 4.0
 */
public abstract class AbstractByteProtocol implements TcpProtocol
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractByteProtocol.class);
    private static final long PAUSE_PERIOD = 100;
    protected static final int EOF = NumberUtils.INTEGER_MINUS_ONE;
    protected static final int NO_MAX_LENGTH = NumberUtils.INTEGER_MINUS_ONE;
    public static final boolean STREAM_OK = true;
    public static final boolean NO_STREAM = false;
    protected ObjectSerializer objectSerializer;
    protected final boolean streamOk;

    /**
     * Rethrow the exception if read fails
     */
    @Parameter
    @Optional(defaultValue = "false")
    protected boolean rethrowExceptionOnRead = false;

    public boolean getRethrowExceptionOnRead()
    {
        return rethrowExceptionOnRead;
    }

    public AbstractByteProtocol(boolean streamOk)
    {
        this.streamOk = streamOk;
    }

    protected void writeByteArray(OutputStream os, byte[] data) throws IOException
    {
        os.write(data);
    }

    protected int safeRead(InputStream is, byte[] buffer) throws IOException
    {
        return safeRead(is, buffer, buffer.length);
    }

    /**
     * Manage non-blocking reads and handle errors
     *
     * @param is     The input stream to read from
     * @param buffer The buffer to read into
     * @param size   The amount of data (upper bound) to read
     * @return The amount of data read (always non-zero, -1 on EOF or socket exception)
     * @throws IOException other than socket exceptions
     */
    protected int safeRead(InputStream is, byte[] buffer, int size) throws IOException
    {
        int len;
        do
        {
            len = is.read(buffer, 0, size);
            if (0 == len)
            {
                // wait for non-blocking input stream
                // use new lock since not expecting notification
                try
                {
                    Thread.sleep(PAUSE_PERIOD);
                }
                catch (InterruptedException e)
                {
                    // no-op
                }
            }
        }
        while (0 == len);
        return len;
    }

    /**
     * Make a single transfer from source to dest via a byte array buffer
     *
     * @param source Source of data
     * @param buffer Buffer array for transfer
     * @param dest   Destination of data
     * @return Amount of data transferred, or -1 on eof or socket error
     * @throws IOException On non-socket error
     */
    protected int copy(InputStream source, byte[] buffer, OutputStream dest) throws IOException
    {
        return copy(source, buffer, dest, buffer.length);
    }

    /**
     * Make a single transfer from source to dest via a byte array buffer
     *
     * @param source Source of data
     * @param buffer Buffer array for transfer
     * @param dest   Destination of data
     * @param size   The amount of data (upper bound) to read
     * @return Amount of data transferred, or -1 on eof or socket error
     * @throws IOException On non-socket error
     */
    protected int copy(InputStream source, byte[] buffer, OutputStream dest, int size) throws IOException
    {
        int len = safeRead(source, buffer, size);
        if (len > 0)
        {
            dest.write(buffer, 0, len);
        }
        return len;
    }

    protected InputStream toInputStream(byte[] data)
    {
        return new ByteArrayInputStream(data);
    }

    public void setObjectSerializer(ObjectSerializer objectSerializer)
    {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public InputStream getInputStreamWrapper(InputStream inputStream)
    {
        return new ByteArrayProtocolWrapper(this, inputStream);
    }
}
