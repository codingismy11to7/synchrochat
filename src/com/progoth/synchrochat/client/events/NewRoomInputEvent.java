package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;

public class NewRoomInputEvent extends Event<NewRoomInputEvent.Handler>
{
    public static interface Handler
    {
        void newRoomRequested(String aRoomName, String aPassword);
    }

    private final String m_roomName;
    private final String m_password;

    public static final Type<Handler> TYPE = new Type<NewRoomInputEvent.Handler>();

    public NewRoomInputEvent(final String aNewRoom)
    {
        this(aNewRoom, null);
    }

    public NewRoomInputEvent(final String aNewRoom, final String aPassword)
    {
        m_roomName = aNewRoom;
        m_password = aPassword;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.newRoomRequested(m_roomName, m_password);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
