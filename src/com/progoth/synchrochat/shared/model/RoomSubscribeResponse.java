package com.progoth.synchrochat.shared.model;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;

public class RoomSubscribeResponse implements Serializable
{
    private static final long serialVersionUID = -5499536158526010939L;

    private SortedSet<ChatRoom> m_roomList;
    private SortedSet<SynchroUser> m_roomUsers;
    private List<ChatMessage> m_roomLog;

    @SuppressWarnings("unused")
    private RoomSubscribeResponse()
    {
        // for serialization
    }

    public RoomSubscribeResponse(final SortedSet<ChatRoom> aNewRoomList,
            final SortedSet<SynchroUser> aRoomUsers, final List<ChatMessage> aRoomLog)
    {
        m_roomList = aNewRoomList;
        m_roomUsers = aRoomUsers;
        m_roomLog = aRoomLog;
    }

    public SortedSet<ChatRoom> getRoomList()
    {
        return m_roomList;
    }

    public List<ChatMessage> getRoomLog()
    {
        return m_roomLog;
    }

    public SortedSet<SynchroUser> getRoomUsers()
    {
        return m_roomUsers;
    }

    @SuppressWarnings("unused")
    private void setRoomList(final SortedSet<ChatRoom> aRoomList)
    {
        m_roomList = aRoomList;
    }

    @SuppressWarnings("unused")
    private void setRoomLog(final List<ChatMessage> aRoomLog)
    {
        m_roomLog = aRoomLog;
    }

    @SuppressWarnings("unused")
    private void setRoomUsers(final SortedSet<SynchroUser> aRoomUsers)
    {
        m_roomUsers = aRoomUsers;
    }
}
