package com.progoth.synchrochat.shared.model;

import java.util.SortedSet;

import no.eirikb.gwtchannelapi.client.Message;

public class RoomListUpdateMessage implements Message
{
    private static final long serialVersionUID = 2072746555615331335L;

    private SortedSet<ChatRoom> m_rooms;

    public RoomListUpdateMessage()
    {
        this(null);
    }

    public RoomListUpdateMessage(final SortedSet<ChatRoom> aRooms)
    {
        m_rooms = aRooms;
    }

    public SortedSet<ChatRoom> getRooms()
    {
        return m_rooms;
    }

    @SuppressWarnings("unused")
    private void setRooms(final SortedSet<ChatRoom> aRooms)
    {
        m_rooms = aRooms;
    }
}
