package com.progoth.synchrochat.client.gui;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.gui.controllers.ChannelController;
import com.progoth.synchrochat.client.gui.controllers.LoginController;
import com.progoth.synchrochat.client.gui.controllers.RoomController;
import com.progoth.synchrochat.client.gui.views.MainView;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.info.Info;

public class SynchroChat implements EntryPoint, NewRoomInputEvent.Handler,
        RoomJoinRequestEvent.Handler
{
    @Override
    public void newRoomRequested(final String aRoomName, final String aPassword)
    {
        // subscribe
        Info.display("Created Room", aRoomName + (aPassword != null ? "; pw: " + aPassword : ""));
    }

    @Override
    public void onModuleLoad()
    {
        LoginController.get().login();

        final Viewport viewport = new Viewport();
        viewport.add(new MainView());
        RootPanel.get().add(viewport);

        SynchroBus.get().addHandler(NewRoomInputEvent.TYPE, this);
        SynchroBus.get().addHandler(RoomJoinRequestEvent.TYPE, this);

        RoomController.get().getRoomList();

        Window.addWindowClosingHandler(new ClosingHandler()
        {
            @Override
            public void onWindowClosing(final ClosingEvent aEvent)
            {
                ChannelController.get().stopListening();
                LoginController.get().logout(false, false);
            }
        });
    }

    @Override
    public void roomJoinRequest(final ChatRoom aRoom)
    {
        // subscribe
        Info.display("Joined Room", aRoom.getName());
    }
}
