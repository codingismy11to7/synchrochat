package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class RoomJoinedEvent extends Event<RoomJoinedEvent.Handler>
{
    public static interface Handler
    {
        void onRoomJoined(ChatRoom aRoom);
    }

    public static final Type<Handler> TYPE = new Type<RoomJoinedEvent.Handler>();

    private final ChatRoom m_room;

    public RoomJoinedEvent(final ChatRoom aRoom)
    {
        m_room = aRoom;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.onRoomJoined(m_room);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
