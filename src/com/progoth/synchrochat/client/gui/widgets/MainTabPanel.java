package com.progoth.synchrochat.client.gui.widgets;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.progoth.synchrochat.client.events.ChatMessageReceivedEvent;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.events.RoomJoinedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.events.UserListDisplayEvent;
import com.progoth.synchrochat.client.events.UserListReceivedEvent;
import com.progoth.synchrochat.client.gui.controllers.RoomController;
import com.progoth.synchrochat.client.gui.controllers.UrlController;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.progoth.synchrochat.shared.model.SynchroUser;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class MainTabPanel extends TabPanel implements RoomJoinedEvent.Handler,
        UserListReceivedEvent.Handler, ChatMessageReceivedEvent.Handler,
        RoomJoinRequestEvent.Handler
{
    private final SortedMap<ChatRoom, ChatPanel> m_rooms = new TreeMap<ChatRoom, ChatPanel>();
    private final SortedMap<ChatRoom, SortedSet<SynchroUser>> m_userLists = new TreeMap<ChatRoom, SortedSet<SynchroUser>>();
    private ChatPanel m_selectedPanel = null;

    public MainTabPanel(final LoginResponse aResponse)
    {
        setCloseContextMenu(true);
        setAnimScroll(true);
        setTabScroll(true);

        final TextArea overview = new TextArea();
        overview.setReadOnly(true);
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                overview.setText(aResponse.getMessage());
            }
        });

        final TabItemConfig ovConf = new TabItemConfig("Overview", false);
        ovConf.setIcon(SynchroImages.get().information());
        add(overview, ovConf);

        addCloseHandler(new CloseHandler<Widget>()
        {
            @Override
            public void onClose(final CloseEvent<Widget> aEvent)
            {
                ChatRoom room = null;
                for (final Entry<ChatRoom, ChatPanel> entry : m_rooms.entrySet())
                {
                    if (entry.getValue() == aEvent.getItem())
                    {
                        room = entry.getKey();
                        break;
                    }
                }
                RoomController.get().leaveRoom(room);
                m_rooms.remove(room);
            }
        });

        addSelectionHandler(new SelectionHandler<Widget>()
        {
            @Override
            public void onSelection(final SelectionEvent<Widget> aEvent)
            {
                if (aEvent.getSelectedItem() instanceof ChatPanel)
                {
                    m_selectedPanel = (ChatPanel)aEvent.getSelectedItem();
                    UrlController.get().updateUrl(m_selectedPanel.getRoom());
                    fireUserListEvent();
                }
                else
                {
                    UrlController.get().updateUrl(null);
                    SynchroBus.get()
                        .fireEvent(new UserListDisplayEvent(new TreeSet<SynchroUser>()));
                }
            }
        });

        SynchroBus.get().addHandler(RoomJoinedEvent.TYPE, this);
        SynchroBus.get().addHandler(UserListReceivedEvent.TYPE, this);
        SynchroBus.get().addHandler(ChatMessageReceivedEvent.TYPE, this);
        SynchroBus.get().addHandler(RoomJoinRequestEvent.TYPE, this);
    }

    private void fireUserListEvent()
    {
        SynchroBus.get().fireEvent(
            new UserListDisplayEvent(m_userLists.get(m_selectedPanel.getRoom())));
    }

    @Override
    public void messageReceived(final ChatMessage aMsg)
    {
        final ChatPanel room = m_rooms.get(aMsg.getRoom());
        if (room != null)
        {
            room.addMessage(aMsg);
        }
    }

    @Override
    public void onRoomJoined(final ChatRoom aRoom)
    {
        if (m_rooms.containsKey(aRoom))
        {
            setSelectedRoom(m_rooms.get(aRoom));
        }
        else
        {
            final TabItemConfig tmp = new TabItemConfig(aRoom.getName(), true);
            tmp.setIcon(SynchroImages.get().comments());
            final ChatPanel panel = new ChatPanel(aRoom);
            m_rooms.put(aRoom, panel);
            add(panel, tmp);
            setSelectedRoom(panel);
        }
    }

    @Override
    public void onUserListReceived(final ChatRoom aRoom, final SortedSet<SynchroUser> aUsers)
    {
        m_userLists.put(aRoom, aUsers);
        if (m_selectedPanel != null && m_selectedPanel.getRoom().equals(aRoom))
        {
            fireUserListEvent();
        }
    }

    @Override
    public void roomJoinRequest(final ChatRoom aRoom)
    {
        if (m_rooms.containsKey(aRoom))
        {
            setSelectedRoom(m_rooms.get(aRoom));
        }
        else
        {
            RoomController.get().newRoomRequested(aRoom.getName(), aRoom.getPassword());
        }
    }

    private void setSelectedRoom(final ChatPanel aPanel)
    {
        m_selectedPanel = aPanel;
        setWidget(m_selectedPanel);
        focus();
        m_selectedPanel.focus();
        m_selectedPanel.getInput().focus();
    }
}
