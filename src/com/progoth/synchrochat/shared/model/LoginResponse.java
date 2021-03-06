package com.progoth.synchrochat.shared.model;

import java.io.Serializable;

public class LoginResponse implements Serializable
{
    private static final long serialVersionUID = 3554843768477114287L;

    private boolean m_loggedIn = false;
    private boolean m_admin = false;
    private String m_loginUrl;
    private String m_logoutUrl;
    private String m_emailAddress;
    private String m_nickname;
    private String m_message;

    public String getEmailAddress()
    {
        return m_emailAddress;
    }

    public String getLoginUrl()
    {
        return m_loginUrl;
    }

    public String getLogoutUrl()
    {
        return m_logoutUrl;
    }

    public String getMessage()
    {
        return m_message;
    }

    public String getNickname()
    {
        return m_nickname;
    }

    public boolean isAdmin()
    {
        return m_admin;
    }

    public boolean isLoggedIn()
    {
        return m_loggedIn;
    }

    public void setAdmin(final boolean aAdmin)
    {
        m_admin = aAdmin;
    }

    public void setEmailAddress(final String aEmailAddress)
    {
        m_emailAddress = aEmailAddress;
    }

    public void setLoggedIn(final boolean aLoggedIn)
    {
        m_loggedIn = aLoggedIn;
    }

    public void setLoginUrl(final String aLoginUrl)
    {
        m_loginUrl = aLoginUrl;
    }

    public void setLogoutUrl(final String aLogoutUrl)
    {
        m_logoutUrl = aLogoutUrl;
    }

    public void setMessage(final String aMessage)
    {
        m_message = aMessage;
    }

    public void setNickname(final String aNickname)
    {
        m_nickname = aNickname;
    }
}
