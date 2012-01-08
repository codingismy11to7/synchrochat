package com.progoth.synchrochat.shared.model;

import java.util.Date;

import no.eirikb.gwtchannelapi.client.Message;

public class ChatMessage implements Message
{
    private static final long serialVersionUID = -5319350216421011538L;

    private String m_msg;
    private String m_room;
    private String m_user;
    private Date m_date;

    public ChatMessage()
    {
        this(null, null, null);
    }

    public ChatMessage(final String aRoom, final String aMsg, final String aUser)
    {
        m_room = aRoom;
        m_msg = aMsg;
        m_user = aUser;
        m_date = new Date();
    }

    public Date getDate()
    {
        return m_date;
    }

    public String getMsg()
    {
        return m_msg;
    }

    public String getRoom()
    {
        return m_room;
    }

    public String getUser()
    {
        return m_user;
    }

    public void setDate(final Date aDate)
    {
        m_date = aDate;
    }

    public void setMsg(final String aMsg)
    {
        m_msg = aMsg;
    }

    public void setRoom(final String aRoom)
    {
        m_room = aRoom;
    }

    public void setUser(final String aUser)
    {
        m_user = aUser;
    }
}