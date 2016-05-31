/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.hamcrest.MatcherAssert.assertThat;
import org.mule.runtime.core.util.IOUtils;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;

public class TcpSendAndReceiveTestCase extends SocketExtensionTestCase
{

    @Rule
    public DynamicPort dynamicPortTcp = new DynamicPort("port_tcp");

    @Override
    protected String getConfigFile()
    {
        return "tcp-send-and-receive-config.xml";
    }


    @Test
    public void sendStringAndReceiveModified() throws Exception
    {
        String flowName = "tcp-send-and-receive";
        InputStream inputStream = (InputStream) flowRunner(flowName).withPayload(TEST_STRING).run().getMessage().getPayload();
        String response = IOUtils.toString(inputStream);
        assertThat(response, equals(TEST_STRING.concat("_modified")));

    }
}
