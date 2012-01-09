package com.progoth.synchrochat.client.gui.controllers;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.events.RoomJoinedEvent;
import com.progoth.synchrochat.client.events.RoomListReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;
import com.progoth.synchrochat.client.rpc.SynchroRpc;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.sencha.gxt.widget.core.client.info.Info;

public class RoomController implements NewRoomInputEvent.Handler, RoomJoinRequestEvent.Handler
{
    private static final RoomController sm_instance = new RoomController();

    public static RoomController get()
    {
        return sm_instance;
    }

    private final SortedMap<String, ChatRoom> m_currentRooms = new TreeMap<String, ChatRoom>();

    private final AsyncCallback<SortedSet<ChatRoom>> m_roomsRcvdHandler = new SimpleAsyncCallback<SortedSet<ChatRoom>>()
    {
        @Override
        public void onSuccess(final SortedSet<ChatRoom> aResult)
        {
            m_currentRooms.clear();
            for (final ChatRoom cr : aResult)
            {
                m_currentRooms.put(cr.getName(), cr);
            }
            SynchroBus.get().fireEvent(new RoomListReceivedEvent(aResult));
        }
    };

    private RoomController()
    {
        SynchroBus.get().addHandler(NewRoomInputEvent.TYPE, this);
        SynchroBus.get().addHandler(RoomJoinRequestEvent.TYPE, this);
    }

    public void getRoomList()
    {
        SynchroRpc.get().getRoomList(m_roomsRcvdHandler);
    }

    public void leaveRoom(final ChatRoom aRoom)
    {
        Info.display("leave room", "implement me " + aRoom.getName());
    }

    @Override
    public void newRoomRequested(final String aRoomName, final String aPassword)
    {
        SynchroRpc.get().subscribe(aRoomName, new SimpleAsyncCallback<SortedSet<ChatRoom>>()
        {
            @Override
            public void onSuccess(final SortedSet<ChatRoom> aResult)
            {
                m_roomsRcvdHandler.onSuccess(aResult);
                SynchroBus.get().fireEvent(new RoomJoinedEvent(m_currentRooms.get(aRoomName)));
            }
        });
    }

    @Override
    public void roomJoinRequest(final ChatRoom aRoom)
    {
        newRoomRequested(aRoom.getName(), null);
    }
}
