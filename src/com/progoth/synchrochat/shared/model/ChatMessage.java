package com.progoth.synchrochat.shared.model;

import java.util.Date;

import no.eirikb.gwtchannelapi.client.Message;

public class ChatMessage implements Message
{
    private static final long serialVersionUID = 9187281495288225152L;

    private String m_msg;
    private ChatRoom m_room;
    private SynchroUser m_user;
    private Date m_date;

    public ChatMessage()
    {
        this(null, null, null);
    }

    public ChatMessage(final ChatRoom aRoom, final String aMsg, final SynchroUser aUser)
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

    public ChatRoom getRoom()
    {
        return m_room;
    }

    public SynchroUser getUser()
    {
        return m_user;
    }

    @SuppressWarnings("unused")
    private void setDate(final Date aDate)
    {
        m_date = aDate;
    }

    @SuppressWarnings("unused")
    private void setMsg(final String aMsg)
    {
        m_msg = aMsg;
    }

    @SuppressWarnings("unused")
    private void setRoom(final ChatRoom aRoom)
    {
        m_room = aRoom;
    }

    @SuppressWarnings("unused")
    private void setUser(final SynchroUser aUser)
    {
        m_user = aUser;
    }
}
