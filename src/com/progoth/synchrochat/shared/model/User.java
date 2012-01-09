package com.progoth.synchrochat.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class User
{
    public static interface Properties extends PropertyAccess<User>
    {
        @Path("name")
        ModelKeyProvider<User> key();

        ValueProvider<User, String> name();
    }

    private String m_name;

    public String getName()
    {
        return m_name;
    }

    public void setName(final String aName)
    {
        m_name = aName;
    }
}
