package com.progoth.synchrochat.server;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import junit.framework.TestCase;

import com.google.common.collect.Maps;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.SynchroUser;

public class TestChatCache extends TestCase
{
    private ChatMessage createFirstMsg()
    {
        return new ChatMessage(createRoom(), "test msg1", createUser());
    }

    private ChatRoom createRoom()
    {
        return new ChatRoom("room1");
    }

    private SynchroUser createUser()
    {
        return new SynchroUser("blah");
    }

    @Override
    public void setUp()
    {
        final ChatRoom room1 = createRoom();
        final SynchroUser user1 = createUser();
        final ChatMessage msg1 = createFirstMsg();

        final Date later1 = new Date(10 + msg1.getDate().getTime());
        final Date later2 = new Date(20 + msg1.getDate().getTime());
        final ChatMessage msg2 = new ChatMessage(room1, "test msg3", user1);
        msg2.setDate(later2);
        msg2.setPreviousMessageDate(later1);

        final SortedMap<Date, ChatMessage> brokenMap = Maps.newTreeMap();
        brokenMap.put(msg1.getDate(), msg1);
        brokenMap.put(msg2.getDate(), msg2);

        final Map<ChatRoom, SortedMap<Date, ChatMessage>> testMap = Maps.newHashMap();
        testMap.put(room1, brokenMap);
        new ChatCache(testMap);
    }

    @Override
    public void tearDown()
    {
        new ChatCache(null);
    }

    public void testMissedMsg()
    {
        final ChatMessage msg1 = ChatCache.get().getFirstMessage(createRoom());

        final List<ChatMessage> msgs = ChatCache.get().getAllRoomMessagesAfter(msg1);

        assertEquals(msgs.get(0).getMsg(), "[unknown message]");
        assertEquals(msgs.get(0).getUser(), new SynchroUser("<unknown>"));
        assertEquals(msgs.get(1).getMsg(), "test msg3");
        assertEquals(msgs.get(1).getUser(), createUser());
    }

    public void testRepair()
    {
        final List<ChatMessage> msgs = ChatCache.get().getAllRoomMessages(new ChatRoom("room1"));

        assertEquals(msgs.get(1).getMsg(), "[unknown message]");
        assertEquals(msgs.get(1).getUser(), new SynchroUser("<unknown>"));
    }
}
