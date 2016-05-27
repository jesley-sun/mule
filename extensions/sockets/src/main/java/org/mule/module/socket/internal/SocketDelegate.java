package org.mule.module.socket.internal;

import org.mule.module.socket.api.source.SocketAttributes;
import org.mule.runtime.api.message.MuleMessage;

import java.io.IOException;
import java.io.InputStream;

public interface SocketDelegate
{
    MuleMessage<InputStream, SocketAttributes> getMuleMessage() throws IOException;

    void close();
}
