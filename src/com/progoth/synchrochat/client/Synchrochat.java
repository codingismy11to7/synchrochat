package com.progoth.synchrochat.client;

import java.util.SortedSet;

import no.eirikb.gwtchannelapi.client.Channel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.progoth.synchrochat.client.events.ChatMessageSendEvent;
import com.progoth.synchrochat.client.events.RoomListReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.rpc.ChatChannelListener;
import com.progoth.synchrochat.client.rpc.ChatChannelListener.ClosedListener;
import com.progoth.synchrochat.client.rpc.SynchrochatService;
import com.progoth.synchrochat.client.rpc.SynchrochatServiceAsync;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Synchrochat implements EntryPoint
{
    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final SynchrochatServiceAsync greetingService = GWT.create(SynchrochatService.class);
    private LoginResponse m_loginInfo;

    private ListBox m_roomList;
    private ChatPanel m_chatPanel;
    private Channel m_channel;

    private void subscribe(final String aRoomName)
    {
        greetingService.subscribe(aRoomName.trim(), new SimpleAsyncCallback<SortedSet<ChatRoom>>()
        {
            @Override
            public void onSuccess(final SortedSet<ChatRoom> aResult)
            {
                onRoomsReceived(aResult);
                m_chatPanel.append("Joined " + aRoomName.trim());
            }
        });
    }

    private void onRoomsReceived(SortedSet<ChatRoom> aRooms)
    {
        m_roomList.clear();
        for (ChatRoom room : aRooms) m_roomList.addItem(room.getName());
        SynchroBus.get().fireEvent(new RoomListReceivedEvent(aRooms));
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
                {
                    greetingService.logout(new AsyncCallback<Void>()
                    {
                        @Override
                        public void onFailure(Throwable aCaught)
                        {
                            Window.Location.assign(m_loginInfo.getLogoutUrl());
                        }

                        @Override
                        public void onSuccess(Void aResult)
                        {
                            Window.Location.assign(m_loginInfo.getLogoutUrl());
                        }
                    });
                }
            }
        });
        horizontalPanel.add(signOut);

        final SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        dockLayoutPanel.add(splitLayoutPanel);

        final LayoutPanel layoutPanel = new LayoutPanel();
        splitLayoutPanel.addWest(layoutPanel, 150.0);

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
        
        RoomListPanel roomListPanel = new RoomListPanel();
        layoutPanel.add(roomListPanel);
        layoutPanel.setWidgetLeftWidth(roomListPanel, 0.0, Unit.PX, 100.0, Unit.PCT);
        layoutPanel.setWidgetTopHeight(roomListPanel, 158.0, Unit.PX, 100.0, Unit.PX);
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
        SynchroBus.get().addHandler(ChatMessageSendEvent.TYPE,
            new ChatMessageSendEvent.Handler()
            {
                @Override
                public void onMessage(ChatMessageSendEvent aEvt)
                {
                    if (m_roomList.getItemCount() == 0)
                        return;
                    final int idx = m_roomList.getSelectedIndex();
                    String room = m_roomList.getItemText(0);
                    if (idx >= 0)
                    {
                        room = m_roomList.getItemText(idx);
                    }

                    greetingService.sendMsg(room, aEvt.getMessage(),
                        new SimpleAsyncCallback<Void>()
                        {
                            @Override
                            public void onSuccess(final Void aResult)
                            {

                            }
                        });
                }
            });

        splitLayoutPanel.add(m_chatPanel);

        Window.addWindowClosingHandler(new ClosingHandler()
        {
            @Override
            public void onWindowClosing(ClosingEvent aEvent)
            {
                greetingService.logout(new SimpleAsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void aResult)
                    {
                        // don't care
                    }
                });
            }
        });
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
        greetingService.greetServer(Window.Location.getHref(),
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

                        greetingService.getRoomList(new SimpleAsyncCallback<SortedSet<ChatRoom>>()
                        {
                            @Override
                            public void onSuccess(final SortedSet<ChatRoom> aResult)
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
