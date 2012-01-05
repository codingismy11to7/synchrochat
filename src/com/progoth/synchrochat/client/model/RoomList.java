package com.progoth.synchrochat.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
    public static final String KEY = "RoomList";

    @PrimaryKey
    private String m_key = KEY;

    @Persistent
    private final SortedMap<String, Set<String>> m_roomMap = new TreeMap<String, Set<String>>();

    public void addUserToRoom(final String aRoom, final String aUserId)
    {
        getUsersForRoom(aRoom).add(aUserId);
    }

    public String getKey()
    {
        return m_key;
    }

    public SortedSet<String> getRooms()
    {
        return new TreeSet<String>(m_roomMap.keySet());
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

    public void setKey(final String aKey)
    {
        m_key = aKey;
    }
}
