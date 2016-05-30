/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import org.mule.tck.junit4.rule.DynamicPort;

import org.junit.Rule;
import org.junit.Test;

public class TcpSendTestCase extends SocketExtensionTestCase
{

    @Rule
    public DynamicPort dynamicPortTcp = new DynamicPort("port_tcp");

    @Override
    protected String getConfigFile()
    {
        return "tcp-send-config.xml";
    }

    @Test
    public void sendPojo() throws Exception
    {
        sendPojo("tcp-send");
    }

    @Test
    public void sendString() throws Exception
    {
        sendString("tcp-send");
    }

    @Test
    public void multipleSendString() throws Exception
    {
        for (int i = 0; i < 5; i++)
        {
            flowRunner("tcp-send").withPayload(TEST_STRING).run();
        }

        for (int i = 0; i < 5; i++)
        {
            assertEvent(receiveConnection(), TEST_STRING);
        }
    }
}
