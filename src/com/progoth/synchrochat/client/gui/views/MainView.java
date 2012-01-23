package com.progoth.synchrochat.client.gui.views;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.progoth.synchrochat.client.gui.controllers.LoginController;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.client.gui.widgets.MainTabPanel;
import com.progoth.synchrochat.client.gui.widgets.PersonListPanel;
import com.progoth.synchrochat.client.gui.widgets.RoomListPanel;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class MainView extends ContentPanel
{
    public MainView(final LoginResponse aResponse)
    {
        monitorWindowResize = true;
        Window.enableScrolling(false);
        setPixelSize(Window.getClientWidth(), Window.getClientHeight());

        setHeaderVisible(false);
        setBodyBorder(false);
        setBorders(false);

        final BorderLayoutContainer cont = new BorderLayoutContainer();
        cont.setBorders(true);

        createTop(cont, aResponse);
        createLeft(cont);
        createRight(cont);

        cont.setCenterWidget(new MainTabPanel(aResponse), new MarginData());

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
        layout.setMargins(new Margins(0, 5, 0, 0));

        aContainer.setWestWidget(new RoomListPanel(), layout);
    }

    private void createRight(final BorderLayoutContainer aContainer)
    {
        final BorderLayoutData layout = new BorderLayoutData(200);
        // layout.setMinSize(100);
        // layout.setMaxSize(600);
        layout.setCollapsible(true);
        layout.setSplit(true);
        layout.setCollapseMini(true);
        layout.setMargins(new Margins(0, 0, 0, 5));

        aContainer.setEastWidget(new PersonListPanel(), layout);
    }

    private void createTop(final BorderLayoutContainer aContainer, final LoginResponse aResponse)
    {
        final HBoxLayoutContainer north = new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
        north.setPadding(new Padding(5));

        final Menu soMenu = new Menu();
        final MenuItem logoutMenuItem = new MenuItem("Sign Out", new SelectionHandler<MenuItem>()
        {
            @Override
            public void onSelection(final SelectionEvent<MenuItem> aEvent)
            {
                LoginController.get().logout(true, true);
            }
        });
        logoutMenuItem.setIcon(SynchroImages.get().disconnect());
        soMenu.add(logoutMenuItem);
        final TextButton signOutButton = new TextButton(aResponse.getNickname());
        signOutButton.setIcon(SynchroImages.get().user_gray());
        signOutButton.setMenu(soMenu);

        final BoxLayoutData bld = new BoxLayoutData(new Margins());
        bld.setFlex(1);
        north.add(new Label(), bld);
        north.add(signOutButton, new BoxLayoutData(new Margins(0, 5, 0, 0)));

        final BorderLayoutData layout = new BorderLayoutData(32);
        layout.setCollapsible(false);

        aContainer.setNorthWidget(north, layout);
    }

    @Override
    protected void onWindowResize(final int aWidth, final int aHeight)
    {
        setPixelSize(aWidth, aHeight);
    }
}
