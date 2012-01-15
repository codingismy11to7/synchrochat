package com.progoth.synchrochat.shared.model;

import java.util.SortedSet;

import no.eirikb.gwtchannelapi.client.Message;

public class UserListUpdateMessage implements Message
{
    private static final long serialVersionUID = -4888704608553500225L;

    private ChatRoom m_room;
    private SortedSet<SynchroUser> m_users;

    public UserListUpdateMessage()
    {
        this(null, null);
    }

    public UserListUpdateMessage(final ChatRoom aRoom, final SortedSet<SynchroUser> aUsers)
    {
        m_room = aRoom;
        m_users = aUsers;
    }

    public ChatRoom getRoom()
    {
        return m_room;
    }

    public SortedSet<SynchroUser> getUsers()
    {
        return m_users;
    }

    @SuppressWarnings("unused")
    private void setRoom(final ChatRoom aRoom)
    {
        m_room = aRoom;
    }

    @SuppressWarnings("unused")
    private void setUsers(final SortedSet<SynchroUser> aUsers)
    {
        m_users = aUsers;
    }
}
