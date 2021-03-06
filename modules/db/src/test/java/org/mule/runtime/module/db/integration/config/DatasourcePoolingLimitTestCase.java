/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.db.integration.config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.mule.runtime.core.api.MessagingException;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.client.MuleClient;
import org.mule.runtime.module.db.integration.model.AbstractTestDatabase;

import org.junit.Test;

public class DatasourcePoolingLimitTestCase extends AbstractDatasourcePoolingTestCase
{

    public DatasourcePoolingLimitTestCase(String dataSourceConfigResource, AbstractTestDatabase testDatabase)
    {
        super(dataSourceConfigResource, testDatabase);
    }

    @Override
    protected String[] getFlowConfigurationResources()
    {
        return new String[] {"integration/config/derby-minimum-pooling-db-config.xml", "integration/config/connection-pooling-config.xml"};
    }

    @Test
    public void limitsConnections() throws Exception
    {
        try
        {
            flowRunner("dataSourcePooling").withPayload(TEST_MESSAGE).asynchronously().run();
            flowRunner("dataSourcePooling").withPayload(TEST_MESSAGE).asynchronously().run();

            MuleClient client = muleContext.getClient();
            MuleMessage response = client.request("test://connectionError", RECEIVE_TIMEOUT);

            assertThat(response.getExceptionPayload().getException(), is(instanceOf(MessagingException.class)));
        }
        finally
        {
            connectionLatch.countDown();
        }
    }
}
