package com.progoth.synchrochat.shared.model;

import java.io.Serializable;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class SynchroUser implements Comparable<SynchroUser>, Serializable
{
    private static final long serialVersionUID = 7336128993111779007L;

    public static interface Properties extends PropertyAccess<SynchroUser>
    {
        @Path("name")
        ModelKeyProvider<SynchroUser> key();

        ValueProvider<SynchroUser, String> name();
    }

    private String m_name;

    public SynchroUser()
    {
        this(null);
    }

    public SynchroUser(final String aName)
    {
        m_name = aName;
    }

    @Override
    public int compareTo(final SynchroUser aO)
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
        if (!(obj instanceof SynchroUser))
            return false;
        final SynchroUser other = (SynchroUser)obj;
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

    @Override
    public String toString()
    {
        return "User [m_name=" + m_name + "]";
    }
}
