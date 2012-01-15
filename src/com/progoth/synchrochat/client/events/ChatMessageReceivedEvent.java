package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.ChatMessage;

public class ChatMessageReceivedEvent extends Event<ChatMessageReceivedEvent.Handler>
{
    public static interface Handler
    {
        void messageReceived(ChatMessage aMsg);
    }

    public static final Type<Handler> TYPE = new Type<ChatMessageReceivedEvent.Handler>();
    private final ChatMessage m_msg;

    public ChatMessageReceivedEvent(final ChatMessage aMsg)
    {
        m_msg = aMsg;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.messageReceived(m_msg);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
