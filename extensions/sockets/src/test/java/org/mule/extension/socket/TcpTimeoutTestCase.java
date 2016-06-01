/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import org.mule.tck.junit4.rule.DynamicPort;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;

public class TcpTimeoutTestCase extends SocketExtensionTestCase
{

    public static final String TEST_STRING = "test string";
    public static final long MILISECONDS_TIMEOUT = 100;

    @Rule
    public DynamicPort dynamicPortTcp = new DynamicPort("port1");

    @Override
    protected String getConfigFile()
    {
        return "tcp-timeout-config.xml";
    }


    @Test
    public void socketThrowsTimeout() throws Exception
    {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        try
        {
            flowRunner("tcp-write-with-timeout").withPayload(TEST_STRING).runExpectingException();
            fail("Expected timeout exception but no exception was thrown");
        }
        catch (Exception e)
        {
            assertThat(stopwatch.elapsed(TimeUnit.MILLISECONDS), greaterThan(MILISECONDS_TIMEOUT));
        }
    }
}
