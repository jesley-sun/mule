/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core;

import static org.junit.Assert.assertNull;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.tck.junit4.AbstractMuleContextEndpointTestCase;

public class DefaultMuleMessageTestCase extends AbstractMuleContextEndpointTestCase
{

    public static final String FOO_PROPERTY = "foo";

    private MuleMessage createMuleMessage()
    {
        MuleMessage previousMessage = new DefaultMuleMessage("MULE_MESSAGE", muleContext);
        previousMessage.setOutboundProperty("MuleMessage", "MuleMessage");
        return previousMessage;
    }

    public void testInboundPropertyNamesRemoveMmutable() throws Exception
    {
        MuleMessage message = createMuleMessage();
        message.setOutboundProperty(FOO_PROPERTY, "bar");
        message.getOutboundPropertyNames().remove(FOO_PROPERTY);
        assertNull(message.getInboundProperty(FOO_PROPERTY));
    }

    public void testOutboundPropertyNamesRemoveMmutable() throws Exception
    {
        MuleMessage message = createMuleMessage();
        message.setOutboundProperty(FOO_PROPERTY, "bar");
        message.getOutboundPropertyNames().remove(FOO_PROPERTY);
        assertNull(message.getOutboundProperty(FOO_PROPERTY));
    }
}
