package com.progoth.synchrochat.client.gui.widgets;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.Widget;
import com.progoth.synchrochat.client.events.LoginResponseReceivedEvent;
import com.progoth.synchrochat.client.events.RoomJoinedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.gui.controllers.RoomController;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class MainTabPanel extends TabPanel implements RoomJoinedEvent.Handler
{
    private final SortedMap<ChatRoom, ChatPanel> m_rooms = new TreeMap<ChatRoom, ChatPanel>();

    public MainTabPanel()
    {
        setCloseContextMenu(true);
        setAnimScroll(true);
        setTabScroll(true);

        final TextArea overview = new TextArea();
        overview.setReadOnly(true);
        SynchroBus.get().addHandler(LoginResponseReceivedEvent.TYPE,
            new LoginResponseReceivedEvent.Handler()
            {
                @Override
                public void loginReceived(final LoginResponse aResponse)
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

        SynchroBus.get().addHandler(RoomJoinedEvent.TYPE, this);
    }

    @Override
    public void onRoomJoined(final ChatRoom aRoom)
    {
        if (m_rooms.containsKey(aRoom))
        {
            setWidget(m_rooms.get(aRoom));
        }
        else
        {
            final TabItemConfig tmp = new TabItemConfig(aRoom.getName(), true);
            tmp.setIcon(SynchroImages.get().comments());
            final ChatPanel panel = new ChatPanel();
            m_rooms.put(aRoom, panel);
            add(panel, tmp);
            setWidget(panel);
        }
    }
}
