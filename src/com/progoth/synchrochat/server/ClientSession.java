package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.SortedSet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Sets;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.SynchroUser;

public class ClientSession implements Serializable
{
    private static final long serialVersionUID = -8860944605518060941L;

    private static final UserService sm_userService = UserServiceFactory.getUserService();

    private SortedSet<ChatRoom> m_roomList = Sets.newTreeSet();

    private User m_user;
    private SynchroUser m_synchroUser;

    public ClientSession()
    {
        m_user = sm_userService.getCurrentUser();
        m_synchroUser = new SynchroUser(m_user.getNickname());
    }

    public SortedSet<ChatRoom> getRoomList()
    {
        return m_roomList;
    }

    public SynchroUser getSynchroUser()
    {
        return m_synchroUser;
    }

    public User getUser()
    {
        return m_user;
    }

    @SuppressWarnings("unused")
    private void setRoomList(final SortedSet<ChatRoom> aRoomList)
    {
        m_roomList = aRoomList;
    }

    @SuppressWarnings("unused")
    private void setSynchroUser(final SynchroUser aSynchroUser)
    {
        m_synchroUser = aSynchroUser;
    }

    @SuppressWarnings("unused")
    private void setUser(final User aUser)
    {
        m_user = aUser;
    }
}
