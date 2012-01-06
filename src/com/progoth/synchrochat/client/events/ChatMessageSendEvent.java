package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;

public class ChatMessageSendEvent extends Event<ChatMessageSendEvent.Handler>
{
    public static interface Handler
    {
        void onMessage(ChatMessageSendEvent aEvt);
    }

    public static Event.Type<ChatMessageSendEvent.Handler> TYPE = new Type<ChatMessageSendEvent.Handler>();

    private final String m_message;

    public ChatMessageSendEvent(final String aMessage)
    {
        m_message = aMessage;
    }

    @Override
    protected void dispatch(final ChatMessageSendEvent.Handler aHandler)
    {
        aHandler.onMessage(this);
    }

    @Override
    public Event.Type<ChatMessageSendEvent.Handler> getAssociatedType()
    {
        return TYPE;
    }

    public String getMessage()
    {
        return m_message;
    }

}
