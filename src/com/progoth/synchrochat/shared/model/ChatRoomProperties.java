package com.progoth.synchrochat.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ChatRoomProperties extends PropertyAccess<ChatRoom>
{
    @Path("name")
    ModelKeyProvider<ChatRoom> key();

    ValueProvider<ChatRoom, String> name();

    ValueProvider<ChatRoom, Integer> userCount();
}
