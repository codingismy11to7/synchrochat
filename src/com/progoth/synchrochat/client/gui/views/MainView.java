package com.progoth.synchrochat.client.gui.views;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.progoth.synchrochat.client.SynchroController;
import com.progoth.synchrochat.client.events.LoginResponseReceivedEvent;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.client.gui.widgets.RoomListPanel;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class MainView extends ContentPanel
{
    public MainView()
    {
        monitorWindowResize = true;
        Window.enableScrolling(false);
        setPixelSize(Window.getClientWidth(), Window.getClientHeight());

        setHeaderVisible(false);
        setBodyBorder(false);
        setBorders(false);

        final BorderLayoutContainer cont = new BorderLayoutContainer();
        cont.setBorders(true);

        createTop(cont);
        createLeft(cont);

        final SimpleContainer sc = new SimpleContainer();
        sc.setResize(false);
        sc.add(new TextButton("o hai"), new MarginData(20));
        cont.setCenterWidget(sc, new MarginData());

        add(cont);
    }

    private void createLeft(final BorderLayoutContainer aContainer)
    {
        final BorderLayoutData layout = new BorderLayoutData(200);
        // layout.setMinSize(100);
        // layout.setMaxSize(600);
        layout.setCollapsible(true);
        layout.setSplit(true);
        layout.setCollapseMini(true);
        layout.setMargins(new Margins(0));

        aContainer.setWestWidget(new RoomListPanel(), layout);
    }

    private void createTop(final BorderLayoutContainer aContainer)
    {
        final HBoxLayoutContainer north = new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
        north.setPack(BoxLayoutPack.END);

        final Menu soMenu = new Menu();
        final MenuItem logoutMenuItem = new MenuItem("Sign Out", new SelectionHandler<MenuItem>()
        {
            @Override
            public void onSelection(final SelectionEvent<MenuItem> aEvent)
            {
                Info.display("sign out", "now");
            }
        });
        logoutMenuItem.setIcon(SynchroImages.get().disconnect());
        soMenu.add(logoutMenuItem);
        final TextButton signOutButton = new TextButton("me");
        signOutButton.setIcon(SynchroImages.get().user_gray());
        signOutButton.setMenu(soMenu);
        SynchroController.get().addHandler(LoginResponseReceivedEvent.TYPE,
            new LoginResponseReceivedEvent.Handler()
            {
                @Override
                public void loginReceived(final LoginResponse aResponse)
                {
                    signOutButton.setText(aResponse.getNickname());
                }
            });

        final BorderLayoutData layout = new BorderLayoutData(28);
        layout.setCollapsible(false);
        layout.setMargins(new Margins(5));

        aContainer.setNorthWidget(north, layout);
    }

    @Override
    protected void onWindowResize(final int aWidth, final int aHeight)
    {
        setPixelSize(aWidth, aHeight);
    }
}
