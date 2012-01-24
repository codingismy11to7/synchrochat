package com.progoth.synchrochat.client.gui.controllers;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.progoth.synchrochat.client.events.ChatMessageReceivedEvent;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.progoth.synchrochat.client.events.RoomJoinedEvent;
import com.progoth.synchrochat.client.events.RoomListReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.events.UserListReceivedEvent;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;
import com.progoth.synchrochat.client.rpc.SynchroRpc;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.RoomSubscribeResponse;

public class RoomController implements NewRoomInputEvent.Handler
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

        Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                getRoomList();
                return true;
            }
        }, 30 * 1000);
    }

    public void getRoomList()
    {
        SynchroRpc.get().getRoomList(m_roomsRcvdHandler);
    }

    public void leaveRoom(final ChatRoom aRoom)
    {
        SynchroRpc.get().leaveRoom(aRoom, m_roomsRcvdHandler);
    }

    @Override
    public void newRoomRequested(final String aRoomName, final String aPassword)
    {
        SynchroRpc.get().subscribe(new ChatRoom(aRoomName, aPassword),
            new SimpleAsyncCallback<RoomSubscribeResponse>()
            {
                @Override
                public void onSuccess(final RoomSubscribeResponse aResult)
                {
                    m_roomsRcvdHandler.onSuccess(aResult.getRoomList());
                    final ChatRoom room = m_currentRooms.get(aRoomName);
                    SynchroBus.get().fireEvent(new RoomJoinedEvent(room));
                    SynchroBus.get().fireEvent(
                        new UserListReceivedEvent(room, aResult.getRoomUsers()));
                    for (final ChatMessage msg : aResult.getRoomLog())
                    {
                        SynchroBus.get().fireEvent(new ChatMessageReceivedEvent(msg));
                    }
                }
            });
    }
}
