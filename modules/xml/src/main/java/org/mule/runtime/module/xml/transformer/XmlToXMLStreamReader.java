/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.xml.transformer;

import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.transformer.DiscoverableTransformer;
import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.config.i18n.MessageFactory;
import org.mule.runtime.module.xml.stax.ReversibleXMLStreamReader;
import org.mule.runtime.module.xml.util.XMLUtils;
import org.mule.runtime.core.transformer.types.DataTypeFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.w3c.dom.Document;

public class XmlToXMLStreamReader extends AbstractXmlTransformer implements DiscoverableTransformer
{

    private int priorityWeighting = DiscoverableTransformer.DEFAULT_PRIORITY_WEIGHTING;
    private boolean reversible;
    
    public XmlToXMLStreamReader()
    {
        super();
        registerSourceType(DataTypeFactory.create(Source.class));
        registerSourceType(DataTypeFactory.INPUT_STREAM);
        registerSourceType(DataTypeFactory.create(Document.class));
        registerSourceType(DataTypeFactory.BYTE_ARRAY);
        registerSourceType(DataTypeFactory.STRING);

        setReturnDataType(DataTypeFactory.create(XMLStreamReader.class));
    }

    @Override
    public Object transformMessage(MuleEvent event, String encoding) throws TransformerException
    {
        Object src = event.getMessage().getPayload();
        try
        {
            XMLStreamReader xsr = XMLUtils.toXMLStreamReader(getXMLInputFactory(), src);
            if (xsr == null)
            {
                throw new TransformerException(MessageFactory
                    .createStaticMessage("Unable to convert " + src.getClass() + " to XMLStreamReader."), this);
            }
        
            if (reversible && !(xsr instanceof ReversibleXMLStreamReader))
            {
                return new ReversibleXMLStreamReader(xsr);
            }
            else
            {
                return xsr;
            }
        }
        catch (XMLStreamException e)
        {
            throw new TransformerException(this, e);
        }
    }

    public boolean isReversible()
    {
        return reversible;
    }

    public void setReversible(boolean reversible)
    {
        this.reversible = reversible;
    }

    public int getPriorityWeighting()
    {
        return priorityWeighting;
    }

    public void setPriorityWeighting(int priorityWeighting)
    {
        this.priorityWeighting = priorityWeighting;
    }
}
