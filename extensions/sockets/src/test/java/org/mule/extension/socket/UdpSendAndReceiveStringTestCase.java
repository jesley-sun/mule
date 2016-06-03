/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.junit.Assert.assertEquals;
import static org.mule.extension.socket.TcpSendAndReceiveStringTestCase.RESPONSE_TEST_STRING;
import org.mule.runtime.core.util.IOUtils;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;

public class UdpSendAndReceiveStringTestCase extends SocketExtensionTestCase
{

    @Rule
    public DynamicPort dynamicPortUdp = new DynamicPort("port_udp");

    @Override
    protected String getConfigFile()
    {
        return "udp-send-and-receive-string-config.xml";
    }

    @Test
    public void sendStringAndReceiveModifiedPojo() throws Exception
    {
        InputStream inputStream = (InputStream) flowRunner("udp-send-and-receive").
                withPayload(TEST_STRING).
                run().getMessage().getPayload();

        String response = IOUtils.toString(inputStream);
        assertEquals(response, RESPONSE_TEST_STRING);
    }
}
