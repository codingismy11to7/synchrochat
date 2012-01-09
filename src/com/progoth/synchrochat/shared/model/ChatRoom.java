package com.progoth.synchrochat.shared.model;

import java.io.Serializable;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class ChatRoom implements Comparable<ChatRoom>, Serializable
{
    private static final long serialVersionUID = 2647307916701864970L;

    public static interface Properties extends PropertyAccess<ChatRoom>
    {
        @Path("name")
        ModelKeyProvider<ChatRoom> key();

        ValueProvider<ChatRoom, String> name();

        ValueProvider<ChatRoom, Integer> userCount();
    }

    private String m_name;
    private int m_userCount;

    @Override
    public int compareTo(final ChatRoom aO)
    {
        return m_name.compareTo(aO.m_name);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ChatRoom))
            return false;
        final ChatRoom other = (ChatRoom)obj;
        if (m_name == null)
        {
            if (other.m_name != null)
                return false;
        }
        else if (!m_name.equals(other.m_name))
            return false;
        return true;
    }

    public String getName()
    {
        return m_name;
    }

    public int getUserCount()
    {
        return m_userCount;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        return result;
    }

    public void setName(final String aName)
    {
        m_name = aName;
    }

    public void setUserCount(final int aUserCount)
    {
        m_userCount = aUserCount;
    }
}
