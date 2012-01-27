package com.progoth.synchrochat.client.gui;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.progoth.synchrochat.client.events.LoginResponseReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.gui.controllers.ChannelController;
import com.progoth.synchrochat.client.gui.controllers.LoginController;
import com.progoth.synchrochat.client.gui.controllers.RoomController;
import com.progoth.synchrochat.client.gui.controllers.UrlController;
import com.progoth.synchrochat.client.gui.views.MainView;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class SynchroChat implements EntryPoint, LoginResponseReceivedEvent.Handler
{
    @Override
    public void loginReceived(final LoginResponse aResponse)
    {
        if (aResponse.isLoggedIn())
        {
            final Viewport viewport = new Viewport();
            viewport.add(new MainView(aResponse));
            RootPanel.get().add(viewport);

            ChannelController.get().start();

            RoomController.get().getRoomList();

            DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));
            RootPanel.getBodyElement().getStyle().setBackgroundColor("white");

            UrlController.get().startupAction();
        }
    }

    @Override
    public void onModuleLoad()
    {
        SynchroBus.get().addHandler(LoginResponseReceivedEvent.TYPE, this);

        LoginController.get().login();

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
}
