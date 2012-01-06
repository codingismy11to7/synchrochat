package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

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

    @Persistent(serialized="true")
    private SortedMap<String, Set<String>> m_roomMap = new TreeMap<String, Set<String>>();

    @Persistent(serialized="true")
    private SortedMap<String, Set<String>> m_roomsByUser = new TreeMap<String, Set<String>>();

    public void addUserToRoom(final String aRoom, final String aUserId)
    {
        getUsersForRoom(aRoom).add(aUserId);
        getRoomsForUser(aUserId).add(aRoom);
        persist();
    }

    public String getKey()
    {
        return m_key;
    }

    public SortedSet<String> getRooms()
    {
        return new TreeSet<String>(m_roomMap.keySet());
    }

    private Set<String> getRoomsForUser(final String aUser)
    {
        if (!m_roomsByUser.containsKey(aUser))
        {
            m_roomsByUser.put(aUser, new HashSet<String>());
        }
        return m_roomsByUser.get(aUser);
    }

    public Set<String> getSubscribedUsers(final String aRoom)
    {
        return getUsersForRoom(aRoom);
    }

    private Set<String> getUsersForRoom(final String aRoom)
    {
        if (!m_roomMap.containsKey(aRoom))
        {
            m_roomMap.put(aRoom, new HashSet<String>());
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

    public void removeUserFromRoom(final String aRoom, final String aUserId)
    {
        getUsersForRoom(aRoom).remove(aUserId);
        getRoomsForUser(aUserId).remove(aRoom);
        persist();
    }

    public void removeUserFromRooms(final String aUserId)
    {
        final Set<String> rooms = m_roomsByUser.remove(aUserId);
        if (rooms != null)
        {
            for (final String room : rooms)
            {
                getUsersForRoom(room).remove(aUserId);
            }
        }
        persist();
    }

    public void setKey(final String aKey)
    {
        m_key = aKey;
    }

    @SuppressWarnings("unused")
    private void setRoomMap(final SortedMap<String, Set<String>> aRoomMap)
    {
        m_roomMap = aRoomMap;
    }

    @SuppressWarnings("unused")
    private void setRoomsByUser(final SortedMap<String, Set<String>> aRoomsByUser)
    {
        m_roomsByUser = aRoomsByUser;
    }
}
