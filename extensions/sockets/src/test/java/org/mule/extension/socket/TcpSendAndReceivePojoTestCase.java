/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.junit.Assert.assertEquals;
import org.mule.module.socket.api.protocol.DirectProtocol;
import org.mule.module.socket.api.protocol.EOFProtocol;
import org.mule.module.socket.api.protocol.LengthProtocol;
import org.mule.module.socket.api.protocol.SafeProtocol;
import org.mule.module.socket.api.protocol.StreamingProtocol;
import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.module.socket.api.protocol.XmlMessageEOFProtocol;
import org.mule.module.socket.api.protocol.XmlMessageProtocol;
import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.tck.junit4.rule.DynamicPort;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TcpSendAndReceivePojoTestCase extends SocketExtensionTestCase
{

    public static final int RESPONSE_AGE = 7;
    public static final String RESPONSE_NAME = "Ronaldo";

    @Rule
    public DynamicPort port1 = new DynamicPort("port_tcp");

    @Parameterized.Parameter(0)
    public TcpProtocol tcpProtocol;

    @Parameterized.Parameter(1)
    public String testName;

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][] {
                {new LengthProtocol(), LengthProtocol.class.getSimpleName()},
                {new DirectProtocol(), DirectProtocol.class.getSimpleName()},
                {new EOFProtocol(), EOFProtocol.class.getSimpleName()},
                {new XmlMessageProtocol(), XmlMessageProtocol.class.getSimpleName()},
                {new XmlMessageEOFProtocol(), XmlMessageEOFProtocol.class.getSimpleName()},
                {new StreamingProtocol(), StreamingProtocol.class.getSimpleName()},
                {new SafeProtocol(), SafeProtocol.class.getSimpleName()}
        });
    }

    @Override
    protected String getConfigFile()
    {
        return "tcp-send-and-receive-pojo-config.xml";
    }

    @Test
    public void sendStringAndReceiveModifiedPojo() throws Exception
    {
        MuleMessage<InputStream, SocketAttributes> message = (MuleMessage) flowRunner("tcp-send-and-receive").
                withPayload(testPojo).
                withFlowVariable("protocol", tcpProtocol).
                run().getMessage();

        TestPojo pojo = (TestPojo) deserializeMessage(message);
        assertEquals(pojo.getAge(), RESPONSE_AGE);
        assertEquals(pojo.getName(), RESPONSE_NAME);
    }
}
