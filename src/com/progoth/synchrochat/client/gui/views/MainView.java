package com.progoth.synchrochat.client.gui.views;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.progoth.synchrochat.client.gui.widgets.RoomListPanel;
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

        BorderLayoutContainer cont = new BorderLayoutContainer();
        cont.setBorders(true);

        createTop(cont);
        createLeft(cont);

        SimpleContainer sc = new SimpleContainer();
        sc.setResize(false);
        sc.add(new TextButton("o hai"), new MarginData(20));
        cont.setCenterWidget(sc, new MarginData());

//        ContentPanel cp = new ContentPanel();
//        cp.setHeadingText("blah");
//        cp.setWidget(new Label("o hai"));
//        BorderLayoutData bld = new BorderLayoutData(150);
//        bld.setFloatable(true);
//        bld.setCollapseMini(true);
//        cont.setEastWidget(cp, bld);

        add(cont);
    }

    private void createLeft(BorderLayoutContainer aContainer)
    {
        final BorderLayoutData layout = new BorderLayoutData(200);
//        layout.setCollapseHidden(false);
//        layout.setMinSize(100);
//        layout.setMaxSize(600);
//        layout.setCollapseMini(true);
//        layout.setFloatable(true);
        layout.setCollapsible(true);
        layout.setSplit(true);
        layout.setCollapseMini(true);
        layout.setMargins(new Margins(0));

        aContainer.setWestWidget(new RoomListPanel(), layout);
    }

    private void createTop(BorderLayoutContainer aContainer)
    {
        final HBoxLayoutContainer north = new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
//        north.setBorders(false);
//        north.setMargins(new Margins(5));
        north.setPack(BoxLayoutPack.END);

        final Anchor signOut = new Anchor(new SafeHtmlBuilder().appendEscaped("Sign")
            .appendHtmlConstant("&nbsp;").appendEscaped("Out").toSafeHtml());
        north.add(signOut);

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
