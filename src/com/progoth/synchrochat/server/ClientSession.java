package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ClientSession implements Serializable
{
    private static final long serialVersionUID = -8860944605518060941L;

    private static final UserService sm_userService = UserServiceFactory.getUserService();

    private SortedSet<String> m_roomList = new TreeSet<String>();

    private User m_user;

    public ClientSession()
    {
        m_user = sm_userService.getCurrentUser();
    }

    public SortedSet<String> getRoomList()
    {
        return m_roomList;
    }

    public User getUser()
    {
        return m_user;
    }

    @SuppressWarnings("unused")
    private void setRoomList(final SortedSet<String> aRoomList)
    {
        m_roomList = aRoomList;
    }

    @SuppressWarnings("unused")
    private void setUser(final User aUser)
    {
        m_user = aUser;
    }
}
