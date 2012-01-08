package com.progoth.synchrochat.client.gui;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.progoth.synchrochat.client.SynchroController;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.gui.views.MainView;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.info.Info;

public class SynchroChat implements EntryPoint, NewRoomInputEvent.Handler, RoomJoinRequestEvent.Handler
{
    @Override
    public void onModuleLoad()
    {
        Viewport viewport = new Viewport();
        viewport.add(new MainView());
        RootPanel.get().add(viewport);

        SynchroController.get().addHandler(NewRoomInputEvent.TYPE, this);
        SynchroController.get().addHandler(RoomJoinRequestEvent.TYPE, this);
    }

    @Override
    public void newRoomRequested(String aRoomName)
    {
        // subscribe
        Info.display("Created Room", aRoomName);
    }

    @Override
    public void roomJoinRequest(ChatRoom aRoom)
    {
        // subscribe
        Info.display("Joined Room", aRoom.getName());
    }
}
