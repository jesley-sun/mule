/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.json.transformers;

import org.mule.tck.testmodels.fruit.FruitCleaner;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * TODO
 */

public abstract class AppleMixin
{
    AppleMixin(@JsonProperty("bitten") boolean bitten)
    {
        super();
    }

    @JsonIgnore
    abstract FruitCleaner getAppleCleaner();

    @JsonIgnore
    abstract void setAppleCleaner(FruitCleaner fc);
}
