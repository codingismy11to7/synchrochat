package com.progoth.synchrochat.client;

import java.util.Set;

import no.eirikb.gwtchannelapi.client.Channel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.progoth.synchrochat.client.ChatPanel.MsgSendListener;
import com.progoth.synchrochat.client.model.LoginResponse;
import com.progoth.synchrochat.client.rpc.ChatChannelListener;
import com.progoth.synchrochat.client.rpc.ChatChannelListener.ClosedListener;
import com.progoth.synchrochat.client.rpc.GreetingService;
import com.progoth.synchrochat.client.rpc.GreetingServiceAsync;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Synchrochat implements EntryPoint
{
    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    private LoginResponse m_loginInfo;

    private ListBox m_roomList;
    private ChatPanel m_chatPanel;
    private Channel m_channel;

    private void subscribe(final String aRoomName)
    {
        greetingService.subscribe(aRoomName.trim(), new SimpleAsyncCallback<Set<String>>()
        {
            @Override
            public void onSuccess(final Set<String> aResult)
            {
                onRoomsReceived(aResult);
                m_chatPanel.append("Joined " + aRoomName.trim());
            }
        });
    }

    private void onRoomsReceived(Set<String> aRooms)
    {
        m_roomList.clear();
        for (String room : aRooms) m_roomList.addItem(room);
    }

    /**
     * @wbp.parser.entryPoint
     */
    private void create()
    {
        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        // RootPanel rootPanel = RootPanel.get();

        final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
        RootLayoutPanel.get().add(dockLayoutPanel);
        // dockLayoutPanel.setSize("100%", "100%");

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dockLayoutPanel.addNorth(horizontalPanel, 1.5);
        horizontalPanel.setWidth("100%");

        final Anchor signOut = new Anchor("Sign Out");
        signOut.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent aEvent)
            {
                if (Window.confirm("this will log you out of all google services!"))
                    Window.Location.assign(m_loginInfo.getLogoutUrl());
            }
        });
        horizontalPanel.add(signOut);

        final SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        dockLayoutPanel.add(splitLayoutPanel);

        final LayoutPanel layoutPanel = new LayoutPanel();
        splitLayoutPanel.addWest(layoutPanel, 100.0);

        final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
        horizontalPanel_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel_1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        layoutPanel.add(horizontalPanel_1);
        layoutPanel.setWidgetLeftWidth(horizontalPanel_1, 0.0, Unit.PX, 100.0, Unit.PCT);
        layoutPanel.setWidgetTopHeight(horizontalPanel_1, 0.0, Unit.PX, 30.0, Unit.PX);

        final Button btnNew = new Button("New...");
        btnNew.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent aEvent)
            {
                final String name = Window.prompt("name", "");
                if (name == null)
                    return;
                subscribe(name);
            }
        });
        horizontalPanel_1.add(btnNew);

        m_roomList = new ListBox();
        layoutPanel.add(m_roomList);
        layoutPanel.setWidgetLeftWidth(m_roomList, 0.0, Unit.PX, 100.0, Unit.PCT);
        layoutPanel.setWidgetTopHeight(m_roomList, 36.0, Unit.PX, 100.0, Unit.PX);
        m_roomList.setVisibleItemCount(10);
        m_roomList.addDoubleClickHandler(new DoubleClickHandler()
        {
            @Override
            public void onDoubleClick(DoubleClickEvent aEvent)
            {
                subscribe(m_roomList.getItemText(m_roomList.getSelectedIndex()));
            }
        });

        m_chatPanel = new ChatPanel();
        m_chatPanel.append(m_loginInfo.getNickname());
        m_chatPanel.addMsgSendListener(new MsgSendListener()
        {
            @Override
            public void onMsg(final String aMsg)
            {
                if (m_roomList.getItemCount() == 0)
                    return;
                final int idx = m_roomList.getSelectedIndex();
                String room = m_roomList.getItemText(0);
                if (idx >= 0)
                {
                    room = m_roomList.getItemText(idx);
                }
                greetingService.sendMsg(room, aMsg, new SimpleAsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(final Void aResult)
                    {

                    }
                });
            }
        });
        splitLayoutPanel.add(m_chatPanel);

    }

    private final ClosedListener m_channelCloseListener = new ClosedListener()
    {
        @Override
        public void channelClosed()
        {
            openChannel();
        }
    };

    private void openChannel()
    {
        greetingService.openChannel(new SimpleAsyncCallback<String>()
        {
            @Override
            public void onSuccess(String aResult)
            {
                m_channel = new Channel(aResult);
                ChatChannelListener list = new ChatChannelListener(m_channel, m_chatPanel,
                        m_channelCloseListener);
                m_channel.addChannelListener(list);
                m_channel.join();
            }
        });
    }

    @Override
    public void onModuleLoad()
    {
        greetingService.greetServer(Window.Location.getHref(), "blah",
            new SimpleAsyncCallback<LoginResponse>()
            {
                @Override
                public void onSuccess(final LoginResponse aResult)
                {
                    m_loginInfo = aResult;
                    if (m_loginInfo.isLoggedIn())
                    {
                        create();

                        openChannel();

                        greetingService.getRoomList(new SimpleAsyncCallback<Set<String>>()
                        {
                            @Override
                            public void onSuccess(final Set<String> aResult)
                            {
                                onRoomsReceived(aResult);
                            }
                        });
                    }
                    else
                    {
                        Window.Location.assign(m_loginInfo.getLoginUrl());
                    }
                }
            });
    }
}
