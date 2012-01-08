package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class RoomJoinRequestEvent extends Event<RoomJoinRequestEvent.Handler>
{
    public static interface Handler
    {
        void roomJoinRequest(ChatRoom aRoom);
    }

    private final ChatRoom m_room;

    public static final Type<Handler> TYPE = new Type<RoomJoinRequestEvent.Handler>();

    public RoomJoinRequestEvent(final ChatRoom aRoom)
    {
        m_room = aRoom;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.roomJoinRequest(m_room);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
