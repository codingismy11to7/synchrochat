package com.progoth.synchrochat.client.events;

import java.util.SortedSet;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class RoomListReceivedEvent extends Event<RoomListReceivedEvent.Handler>
{
    public static interface Handler
    {
        void onRoomListReceived(RoomListReceivedEvent aEvt);
    }

    public static final Type<RoomListReceivedEvent.Handler> TYPE = new Type<RoomListReceivedEvent.Handler>();

    private final SortedSet<ChatRoom> m_roomList;

    public RoomListReceivedEvent(final SortedSet<ChatRoom> aRooms)
    {
        m_roomList = aRooms;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.onRoomListReceived(this);
    }

    @Override
    public Event.Type<Handler> getAssociatedType()
    {
        return TYPE;
    }

    public SortedSet<ChatRoom> getRoomList()
    {
        return m_roomList;
    }
}
