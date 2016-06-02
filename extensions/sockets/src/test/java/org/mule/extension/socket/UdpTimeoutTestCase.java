/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.rules.ExpectedException.none;
import org.mule.runtime.core.api.MessagingException;
import org.mule.tck.junit4.rule.DynamicPort;

import java.net.SocketTimeoutException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UdpTimeoutTestCase extends SocketExtensionTestCase
{

    public static final String TEST_STRING = "test string";
    public static final long MILISECONDS_TIMEOUT = 100;

    @Rule
    public DynamicPort dynamicPortTcp = new DynamicPort("port1");

    @Rule
    public ExpectedException expectedException = none();

    @Override
    protected String getConfigFile()
    {
        return "udp-timeout-config.xml";
    }

    @Test
    public void socketThrowsTimeout() throws Exception
    {
        expectedException.expect(MessagingException.class);
        expectedException.expectCause(instanceOf(SocketTimeoutException.class));

        //expectedException.expectMessage("UDP socket timed out while waiting for a response");
        flowRunner("udp-send-with-timeout").withPayload(TEST_STRING).run();
    }
}
