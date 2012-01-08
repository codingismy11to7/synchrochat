package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;

public class NewRoomInputEvent extends Event<NewRoomInputEvent.Handler>
{
    public static interface Handler
    {
        void newRoomRequested(String aRoomName);
    }

    private final String m_roomName;

    public static final Type<Handler> TYPE = new Type<NewRoomInputEvent.Handler>();

    public NewRoomInputEvent(final String aNewRoom)
    {
        m_roomName = aNewRoom;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.newRoomRequested(m_roomName);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
