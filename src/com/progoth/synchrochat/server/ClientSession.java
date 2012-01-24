package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.progoth.synchrochat.shared.model.SynchroUser;

@PersistenceCapable
public class ClientSession implements Serializable
{
    @NotPersistent
    private static final long serialVersionUID = -8860944605518060941L;

    @NotPersistent
    private static final UserService sm_userService = UserServiceFactory.getUserService();

    @Persistent
    private User m_user;
    @Persistent(serialized = "true")
    private SynchroUser m_synchroUser;
    @Persistent
    Date channelExpiration = null;
    @Persistent
    String channelName = null;

    @PrimaryKey
    @Persistent
    private Key m_key;

    public ClientSession()
    {
        m_user = sm_userService.getCurrentUser();
        m_synchroUser = new SynchroUser(m_user.getNickname());
        m_key = KeyFactory.createKey(ClientSession.class.getSimpleName(), m_user.getUserId());
    }

    public Key getKey()
    {
        return m_key;
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
    private void setKey(final Key aKey)
    {
        m_key = aKey;
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
