package com.progoth.synchrochat.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.SynchroUser;

public class ChatCache
{
    private static ChatCache sm_instance = null;

    private static final int MAX_ROOM_HIST = 500;

    public synchronized static ChatCache get()
    {
        if (sm_instance == null)
        {
            sm_instance = new ChatCache();
        }
        return sm_instance;
    }

    private Map<ChatRoom, SortedMap<Date, ChatMessage>> m_chatCache;

    private ChatCache()
    {
        // singleton
        Map<ChatRoom, SortedMap<Date, ChatMessage>> chatCache = SynchroCache.get("ChatCache");
        if (chatCache == null)
        {
            chatCache = Maps.newHashMap();
        }
        init(chatCache);
    }

    ChatCache(final Map<ChatRoom, SortedMap<Date, ChatMessage>> aTestData)
    {
        init(aTestData);
    }

    private ChatMessage createLostMessageBetween(final ChatRoom aRoom, final Date aPreviousDate,
            final Date aDate)
    {
        final ChatMessage ret = new ChatMessage(aRoom, "[unknown message]", new SynchroUser(
                "<unknown>"));
        ret.setPreviousMessageDate(aPreviousDate);
        ret.setDate(aDate);
        return ret;
    }

    public List<ChatMessage> getAllRoomMessages(final ChatRoom aRoom)
    {
        final SortedMap<Date, ChatMessage> map = getMapForRoom(aRoom);
        synchronized (map)
        {
            return sanitizeMap(map);
        }
    }

    public List<ChatMessage> getAllRoomMessagesAfter(final ChatMessage aLastMsg)
    {
        final SortedMap<Date, ChatMessage> map = getMapForRoom(aLastMsg.getRoom());
        synchronized (map)
        {
            final List<ChatMessage> ret = sanitizeMap(map.tailMap(aLastMsg.getDate()));
            if (ret.get(0).getDate().equals(aLastMsg.getDate()))
            {
                ret.remove(0);
            }
            return ret;
        }
    }

    ChatMessage getFirstMessage(final ChatRoom aRoom)
    {
        return getMapForRoom(aRoom).values().iterator().next();
    }

    private synchronized SortedMap<Date, ChatMessage> getMapForRoom(final ChatRoom aRoom)
    {
        if (!m_chatCache.containsKey(aRoom))
        {
            m_chatCache.put(aRoom, Maps.<Date, ChatMessage> newTreeMap());
        }
        return m_chatCache.get(aRoom);
    }

    private void init(final Map<ChatRoom, SortedMap<Date, ChatMessage>> aCache)
    {
        m_chatCache = aCache;
        sm_instance = this;
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

    private List<ChatMessage> sanitizeMap(final SortedMap<Date, ChatMessage> aMap)
    {
        if (aMap.isEmpty() || aMap.size() == 1)
            return Lists.newArrayList(aMap.values());

        final LinkedList<ChatMessage> ret = Lists.newLinkedList(aMap.values());
        final ListIterator<ChatMessage> i = ret.listIterator();
        ChatMessage prevMsg = i.next();
        do
        {
            ChatMessage msg = i.next();
            if (!msg.getPreviousMessageDate().equals(prevMsg.getDate()))
            {
                if (msg.getPreviousMessageDate().before(prevMsg.getDate()))
                {
                    msg.setPreviousMessageDate(prevMsg.getDate());
                }
                else
                {
                    final ChatMessage tmp = createLostMessageBetween(msg.getRoom(),
                        prevMsg.getDate(), msg.getPreviousMessageDate());
                    aMap.put(tmp.getDate(), tmp);
                    i.previous();
                    i.add(tmp);
                    i.next();
                    msg = tmp;
                }
            }
            prevMsg = msg;
        } while (i.hasNext());

        return ret;
    }
}
