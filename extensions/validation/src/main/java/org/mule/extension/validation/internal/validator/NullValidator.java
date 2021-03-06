/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.config.i18n.Message;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.message.NullPayload;

/**
 * An {@link AbstractValidator} which verifies that
 * a {@link #value} is {@code null} or an instance
 * of {@link NullPayload}
 *
 * @since 3.7.0
 */
public class NullValidator extends AbstractValidator
{

    private final Object value;

    public NullValidator(Object value, ValidationContext validationContext)
    {
        super(validationContext);
        this.value = value;
    }

    @Override
    public ValidationResult validate(MuleEvent event)
    {
        return value == null || value instanceof NullPayload
               ? ok()
               : fail();
    }

    @Override
    protected Message getDefaultErrorMessage()
    {
        return getMessages().wasExpectingNull();
    }
}
