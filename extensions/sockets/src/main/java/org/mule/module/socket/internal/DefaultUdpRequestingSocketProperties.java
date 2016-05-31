/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.RequestingSocketProperties;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

public class DefaultUdpRequestingSocketProperties extends DefaultUdpSocketProperties implements RequestingSocketProperties
{

    /**
     * {@inheritDoc}
     */
    @Parameter
    @Optional
    private Integer localPort;

    /**
     * {@inheritDoc}
     */
    @Parameter
    @Optional
    private String bindingHost;

    @Override
    public Integer getLocalPort()
    {
        return localPort;
    }

    @Override
    public String getBindingHost()
    {
        return bindingHost;
    }
}
