package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.progoth.synchrochat.shared.model.ChatRoom;

@PersistenceCapable
public class RoomList implements Serializable
{
    @NotPersistent
    private static final long serialVersionUID = -5597664726286916614L;

    @NotPersistent
    private static final String KEY = "RoomList";

    public static RoomList get()
    {
        final RoomList ret = SynchroCache.get(KEY);
        if (ret != null)
            return ret;

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            final Query q = pm.newQuery(RoomList.class, "m_key == :rlid");
            q.setUnique(true);
            RoomList roomList = (RoomList)q.execute(KEY);
            if (roomList == null)
            {
                roomList = new RoomList();
                pm.makePersistent(roomList);
            }
            return pm.detachCopy(roomList);
        }
        finally
        {
            pm.close();
        }
    }

    @PrimaryKey
    private String m_key = KEY;

    @Persistent(serialized = "true")
    private SortedMap<ChatRoom, Set<User>> m_roomMap = Maps.newTreeMap();

    @Persistent(serialized = "true")
    private SortedMap<User, SortedSet<ChatRoom>> m_roomsByUser = Maps.newTreeMap();

    public void addUserToRoom(final ChatRoom aRoom, final User aUser)
    {
        getUsersForRoom(aRoom).add(aUser);
        getRoomsForUserImpl(aUser).add(getStoredRoom(aRoom));
        updateUserCounts();
        persist();
    }

    public SortedSet<ChatRoom> getClientSafeRooms()
    {
        return Sets.newTreeSet(Collections2.transform(m_roomMap.keySet(),
            new Function<ChatRoom, ChatRoom>()
            {
                @Override
                public ChatRoom apply(final ChatRoom aArg0)
                {
                    return aArg0.clientSafe();
                }
            }));
    }

    public String getKey()
    {
        return m_key;
    }

    public SortedSet<ChatRoom> getRooms()
    {
        return Sets.newTreeSet(m_roomMap.keySet());
    }

    public SortedSet<ChatRoom> getRoomsForUser(final User aUser)
    {
        return getRoomsForUserImpl(aUser);
    }

    private SortedSet<ChatRoom> getRoomsForUserImpl(final User aUser)
    {
        if (!m_roomsByUser.containsKey(aUser))
        {
            m_roomsByUser.put(aUser, new TreeSet<ChatRoom>());
        }
        return m_roomsByUser.get(aUser);
    }

    private ChatRoom getStoredRoom(final ChatRoom aForRoom)
    {
        for (final ChatRoom room : m_roomMap.keySet())
            if (room.equals(aForRoom))
                return room;
        throw new RuntimeException("shouldn't happen, room not found");
    }

    public Set<User> getSubscribedUsers(final ChatRoom aRoom)
    {
        return getUsersForRoom(aRoom);
    }

    private Set<User> getUsersForRoom(final ChatRoom aRoom)
    {
        if (!m_roomMap.containsKey(aRoom))
        {
            m_roomMap.put(aRoom, new HashSet<User>());
        }
        return m_roomMap.get(aRoom);
    }

    private void persist()
    {
        SynchroCache.getCache().put(KEY, this);

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            pm.makePersistent(this);
        }
        finally
        {
            pm.close();
        }
    }

    public void removeUserFromRoom(final ChatRoom aRoom, final User aUser)
    {
        getUsersForRoom(aRoom).remove(aUser);
        getRoomsForUserImpl(aUser).remove(aRoom);
        updateUserCounts();
        persist();
    }

    public void removeUserFromRooms(final User aUser)
    {
        final Set<ChatRoom> rooms = m_roomsByUser.remove(aUser);
        if (rooms != null)
        {
            for (final ChatRoom room : rooms)
            {
                getUsersForRoom(room).remove(aUser);
            }
        }
        updateUserCounts();
        persist();
    }

    public void setKey(final String aKey)
    {
        m_key = aKey;
    }

    @SuppressWarnings("unused")
    private void setRoomMap(final SortedMap<ChatRoom, Set<User>> aRoomMap)
    {
        m_roomMap = aRoomMap;
    }

    @SuppressWarnings("unused")
    private void setRoomsByUser(final SortedMap<User, SortedSet<ChatRoom>> aRoomsByUser)
    {
        m_roomsByUser = aRoomsByUser;
    }

    private void updateUserCounts()
    {
        for (final ChatRoom room : m_roomMap.keySet())
        {
            room.setUserCount(getUsersForRoom(room).size());
        }
    }
}
