package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
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

@PersistenceCapable(detachable = "true")
public class ClientSession implements Serializable
{
    @NotPersistent
    private static final long serialVersionUID = 4784985396241692237L;

    @NotPersistent
    private static final UserService sm_userService = UserServiceFactory.getUserService();

    @Persistent
    private User m_user;
    @Persistent
    private String m_nick;
    @Persistent
    private Date channelExpiration = null;
    @Persistent
    private String channelName = null;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    public ClientSession()
    {
        m_user = sm_userService.getCurrentUser();
        m_nick = m_user.getNickname();
        key = KeyFactory.createKey(ClientSession.class.getSimpleName(), m_user.getUserId());
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ClientSession))
            return false;
        final ClientSession other = (ClientSession)obj;
        if (m_user == null)
        {
            if (other.m_user != null)
                return false;
        }
        else if (!m_user.equals(other.m_user))
            return false;
        return true;
    }

    public Date getChannelExpiration()
    {
        return channelExpiration;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public Key getKey()
    {
        return key;
    }

    public String getNickx()
    {
        return m_nick;
    }

    public SynchroUser getSynchroUser()
    {
        return new SynchroUser(m_nick);
    }

    public User getUser()
    {
        return m_user;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_user == null) ? 0 : m_user.hashCode());
        return result;
    }

    public void setChannelExpiration(final Date aChannelExpiration)
    {
        channelExpiration = aChannelExpiration;
    }

    public void setChannelName(final String aChannelName)
    {
        channelName = aChannelName;
    }

    @SuppressWarnings("unused")
    private void setKey(final Key aKey)
    {
        key = aKey;
    }

    @SuppressWarnings("unused")
    private void setNick(final String aNick)
    {
        m_nick = aNick;
    }

    @SuppressWarnings("unused")
    private void setUser(final User aUser)
    {
        m_user = aUser;
    }
}
