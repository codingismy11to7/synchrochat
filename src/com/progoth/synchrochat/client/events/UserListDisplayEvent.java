package com.progoth.synchrochat.client.events;

import java.util.SortedSet;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.SynchroUser;

public class UserListDisplayEvent extends Event<UserListDisplayEvent.Handler>
{
    public static interface Handler
    {
        void displayUserList(SortedSet<SynchroUser> aUserList);
    }

    public static final Type<Handler> TYPE = new Type<UserListDisplayEvent.Handler>();

    private final SortedSet<SynchroUser> m_userList;

    public UserListDisplayEvent(final SortedSet<SynchroUser> aUserList)
    {
        m_userList = aUserList;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.displayUserList(m_userList);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
