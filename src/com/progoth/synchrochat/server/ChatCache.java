package com.progoth.synchrochat.server;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class ChatCache
{
    private static final ChatCache sm_instance = new ChatCache();

    private static final int MAX_ROOM_HIST = 500;

    public static ChatCache get()
    {
        return sm_instance;
    }

    private Map<ChatRoom, SortedMap<Date, ChatMessage>> m_chatCache;

    private ChatCache()
    {
        // singleton
        m_chatCache = SynchroCache.get("ChatCache");
        if (m_chatCache == null)
        {
            m_chatCache = Maps.newHashMap();
        }
    }

    public List<ChatMessage> getAllRoomMessages(final ChatRoom aRoom)
    {
        final SortedMap<Date, ChatMessage> map = getMapForRoom(aRoom);
        synchronized (map)
        {
            return Lists.newArrayList(map.tailMap(new Date(0l)).values());
        }
    }

    public List<ChatMessage> getAllRoomMessagesAfter(final ChatMessage aLastMsg)
    {
        final Date searchDate = new Date(1 + aLastMsg.getDate().getTime());
        final SortedMap<Date, ChatMessage> map = getMapForRoom(aLastMsg.getRoom());
        synchronized (map)
        {
            return Lists.newArrayList(map.tailMap(searchDate).values());
        }
    }

    private synchronized SortedMap<Date, ChatMessage> getMapForRoom(final ChatRoom aRoom)
    {
        if (!m_chatCache.containsKey(aRoom))
        {
            m_chatCache.put(aRoom, Maps.<Date, ChatMessage> newTreeMap());
        }
        return m_chatCache.get(aRoom);
    }

    public void recordAndUpdateNewMessage(final ChatMessage aMsg)
    {
        final SortedMap<Date, ChatMessage> map = getMapForRoom(aMsg.getRoom());
        synchronized (map)
        {
            if (!map.isEmpty())
            {
                final ChatMessage lastMsg = map.get(map.lastKey());
                while (aMsg.getDate().compareTo(lastMsg.getDate()) <= 0)
                {
                    aMsg.setDate(new Date(1 + aMsg.getDate().getTime()));
                }
                aMsg.setPreviousMessageDate(lastMsg.getDate());
            }
            map.put(aMsg.getDate(), aMsg);
            while (map.size() > MAX_ROOM_HIST)
            {
                map.remove(map.firstKey());
            }
        }
        SynchroCache.put("ChatCache", m_chatCache);
    }
}
