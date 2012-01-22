package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.progoth.synchrochat.shared.model.ChatRoom;

@PersistenceCapable
public class SynchroSessions implements Serializable
{
    @NotPersistent
    private static final long serialVersionUID = 8170556146722378460L;

    @NotPersistent
    private static SynchroSessions sm_instance = null;

    @NotPersistent
    private static final UserService sm_userService = UserServiceFactory.getUserService();

    @NotPersistent
    private static final String KEY = "SynchrochatSessions";

    public static SynchroSessions get()
    {
        if (sm_instance == null)
        {
            sm_instance = init();
        }
        return sm_instance;
    }

    private static SynchroSessions init()
    {
        final SynchroSessions ret = SynchroCache.get(KEY);
        if (ret != null)
            return ret;

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            final Query q = pm.newQuery(SynchroSessions.class, "m_key == :smid");
            q.setUnique(true);
            SynchroSessions sessions = (SynchroSessions)q.execute(KEY);
            if (sessions == null)
            {
                sessions = new SynchroSessions();
                pm.makePersistent(sessions);
            }
            else
            {
                sessions.getSessionMap();
                pm.makePersistent(sessions);
            }
            return pm.detachCopy(sessions);
        }
        finally
        {
            pm.close();
        }
    }

    @PrimaryKey
    private String m_key = KEY;

    @Persistent
    private List<ClientSession> m_sessionList = Lists.newLinkedList();

    @NotPersistent
    private Map<User, ClientSession> m_sessionMap = null;

    public RoomList addUserToRoom(final ChatRoom aRoom)
    {
        final ClientSession session = getSession();

        final RoomList rl = RoomList.get();
        rl.addUserToRoom(aRoom, session.getUser());

        return rl;
    }

    public ClientSession endSession()
    {
        final User user = sm_userService.getCurrentUser();

        RoomList.get().removeUserFromRooms(user);

        final ClientSession ret = getSessionMap().get(user);
        /*
         * m_sessionList.remove(ret); persist();
         */
        return ret;
    }

    @SuppressWarnings("unused")
    private String getKey()
    {
        return m_key;
    }

    public ClientSession getSession()
    {
        final User user = sm_userService.getCurrentUser();
        return getSessionMap().get(user);
    }

    public ClientSession getSession(final User aUser)
    {
        return getSessionMap().get(aUser);
    }

    private synchronized Map<User, ClientSession> getSessionMap()
    {
        if (m_sessionMap == null)
        {
            m_sessionMap = Maps.newHashMap();
            final List<ClientSession> toRemove = Lists.newLinkedList();
            for (final ClientSession sess : m_sessionList)
            {
                if (m_sessionMap.containsKey(sess.getUser()))
                {
                    toRemove.add(sess);
                }
                else
                {
                    m_sessionMap.put(sess.getUser(), sess);
                }
            }
            if (!toRemove.isEmpty())
            {
                m_sessionList.removeAll(toRemove);
            }
        }
        return m_sessionMap;
    }

    @SuppressWarnings("unused")
    private List<ClientSession> getSessions()
    {
        return m_sessionList;
    }

    public Set<User> getSessionUsers()
    {
        return getSessionMap().keySet();
    }

    public String openChannel(final boolean aForce)
    {
        final ClientSession sess = getSession();
        final GregorianCalendar now = new GregorianCalendar();
        if (!aForce && sess.channelExpiration != null && sess.channelName != null)
        {
            final GregorianCalendar tmp = new GregorianCalendar();
            tmp.setTime(sess.channelExpiration);
            if (tmp.after(now))
                return sess.channelName;
        }
        final String chanId = ChannelServiceFactory.getChannelService().createChannel(
            sess.getUser().getUserId());
        now.add(Calendar.HOUR_OF_DAY, 2);
        sess.channelExpiration = now.getTime();
        sess.channelName = chanId;
        persist();// Session(sess);
        return chanId;
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

    @SuppressWarnings("unused")
    private void persistSession(final ClientSession aSess)
    {
        SynchroCache.getCache().put(KEY, this);
        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            pm.makePersistent(aSess);
        }
        finally
        {
            pm.close();
        }
    }

    public RoomList removeUserFromRoom(final ChatRoom aRoom)
    {
        final ClientSession session = getSession();

        final RoomList rl = RoomList.get();
        rl.removeUserFromRoom(aRoom, session.getUser());

        return rl;
    }

    @SuppressWarnings("unused")
    private void setKey(final String aKey)
    {
        m_key = aKey;
    }

    @SuppressWarnings("unused")
    private void setSessions(final List<ClientSession> aSessions)
    {
        m_sessionList = aSessions;
    }

    public synchronized ClientSession startSession()
    {
        ClientSession ret;
        if ((ret = getSession()) != null)
            return ret;
        ret = new ClientSession();
        m_sessionList.add(ret);
        getSessionMap().put(ret.getUser(), ret);
        persist();
        return ret;
    }
}
