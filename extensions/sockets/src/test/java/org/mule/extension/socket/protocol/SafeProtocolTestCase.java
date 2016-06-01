/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.protocol;

import org.mule.extension.socket.SocketExtensionTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import org.junit.Rule;
import org.junit.Test;

public class SafeProtocolTestCase extends SocketExtensionTestCase
{
    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");

    @Override
    protected String getConfigFile()
    {
        return "safe-protocol-config.xml";
    }

    @Test
    public void sendMessageWithSafeCookie() throws Exception
    {
        flowRunner("tcp-write").withPayload(TEST_STRING).run();
        assertEvent(receiveConnection(), TEST_STRING);
    }
}
