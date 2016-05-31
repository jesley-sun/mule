/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.mule.functional.junit4.ExtensionFunctionalTestCase;
import org.mule.module.socket.api.SocketsExtension;
import org.mule.module.socket.api.source.ImmutableSocketAttributes;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.api.message.NullPayload;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.DefaultMuleMessage;
import org.mule.runtime.core.el.context.MessageContext;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.core.util.ValueHolder;
import org.mule.tck.probe.JUnitLambdaProbe;
import org.mule.tck.probe.PollingProber;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hamcrest.MatcherAssert;

public abstract class SocketExtensionTestCase extends ExtensionFunctionalTestCase
{

    protected static final int TIMEOUT_MILLIS = 5000;
    protected static final int POLL_DELAY_MILLIS = 100;
    public static final String TEST_STRING = "This is a test string";
    protected static List<MuleMessage<?, ImmutableSocketAttributes>> receivedMessages;


    protected static final String NAME = "Messi";
    protected static final int AGE = 10;
    protected TestPojo testPojo;

    protected void assertPojo(MuleMessage<?, ImmutableSocketAttributes> message, TestPojo expectedContent) throws Exception
    {
        if (message.getPayload() == null)
        {
            fail("Null payload");
        }

        TestPojo pojo = (TestPojo) deserializeMessage(message);
        MatcherAssert.assertThat(pojo.getAge(), is(expectedContent.getAge()));
        MatcherAssert.assertThat(pojo.getName(), is(expectedContent.getName()));
    }

    @Override
    protected void doSetUpBeforeMuleContextCreation() throws Exception
    {
        super.doSetUpBeforeMuleContextCreation();
        receivedMessages = new CopyOnWriteArrayList<>();
        testPojo = new TestPojo();
        testPojo.setAge(AGE);
        testPojo.setName(NAME);
    }

    @Override
    protected void doTearDown() throws Exception
    {
        receivedMessages = null;
    }

    public static void onIncomingConnection(MessageContext messageContext)
    {
        MuleMessage message = new DefaultMuleMessage(messageContext.getPayload(), (DataType<Object>) messageContext.getDataType(), messageContext.getAttributes());
        receivedMessages.add(message);
    }

    @Override
    protected Class<?>[] getAnnotatedExtensionClasses()
    {
        return new Class<?>[] {SocketsExtension.class};
    }

    protected void assertNullPayload(MuleMessage<?, ImmutableSocketAttributes> message)
    {
        assertThat(message.getPayload(), instanceOf(NullPayload.class));
    }

    protected void assertEvent(MuleMessage<?, ImmutableSocketAttributes> message, Object expectedContent) throws Exception
    {
        String payload = IOUtils.toString((InputStream) message.getPayload());
        assertEquals(expectedContent, payload);
    }

    protected Object deserializeMessage(MuleMessage<?, ImmutableSocketAttributes> message) throws Exception
    {
        return muleContext.getObjectSerializer().deserialize(IOUtils.toByteArray((InputStream) message.getPayload()));
    }

    protected MuleMessage<?, ImmutableSocketAttributes> receiveConnection()
    {
        PollingProber prober = new PollingProber(TIMEOUT_MILLIS, POLL_DELAY_MILLIS);
        ValueHolder<MuleMessage<?, ImmutableSocketAttributes>> messageHolder = new ValueHolder<>();
        prober.check(new JUnitLambdaProbe(() -> {
            for (MuleMessage<?, ImmutableSocketAttributes> message : receivedMessages)
            {
                messageHolder.set(message);
                return true;
            }

            return false;
        }));

        return messageHolder.get();
    }

    protected void sendString(String flowName) throws Exception
    {
        flowRunner(flowName).withPayload(TEST_STRING).run();
        assertEvent(receiveConnection(), TEST_STRING);
    }

    protected void sendPojo(String flownName) throws Exception
    {
        flowRunner(flownName).withPayload(testPojo).run();
        assertPojo(receiveConnection(), testPojo);
    }
}
