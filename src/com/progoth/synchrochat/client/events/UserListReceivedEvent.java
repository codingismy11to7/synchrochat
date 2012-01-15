package com.progoth.synchrochat.client.events;

import java.util.SortedSet;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.SynchroUser;

public class UserListReceivedEvent extends Event<UserListReceivedEvent.Handler>
{
    public static interface Handler
    {
        void onUserListReceived(ChatRoom aRoom, SortedSet<SynchroUser> aUsers);
    }

    public static final Type<Handler> TYPE = new Type<UserListReceivedEvent.Handler>();

    private final ChatRoom m_room;
    private final SortedSet<SynchroUser> m_users;

    public UserListReceivedEvent(final ChatRoom aRoom, final SortedSet<SynchroUser> aUsers)
    {
        m_room = aRoom;
        m_users = aUsers;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.onUserListReceived(m_room, m_users);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
