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
                sessions.index();
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
    private final Map<String, ClientSession> m_sessionMap = Maps.newTreeMap();

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

        final ClientSession ret = m_sessionMap.get(user.getUserId());
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
        return m_sessionMap.get(user.getUserId());
    }

    public ClientSession getSession(final User aUser)
    {
        return m_sessionMap.get(aUser.getUserId());
    }

    public Set<String> getSessionIds()
    {
        return m_sessionMap.keySet();
    }

    @SuppressWarnings("unused")
    private List<ClientSession> getSessions()
    {
        return m_sessionList;
    }

    private void index()
    {
        for (final ClientSession sess : m_sessionList)
        {
            m_sessionMap.put(sess.getUser().getUserId(), sess);
        }
    }

    public String openChannel()
    {
        final ClientSession sess = getSession();
        final GregorianCalendar now = new GregorianCalendar();
        if (sess.channelExpiration != null && sess.channelName != null)
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
        persist();
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

    public ClientSession startSession()
    {
        ClientSession ret;
        if ((ret = getSession()) != null)
            return ret;
        ret = new ClientSession();
        m_sessionList.add(ret);
        m_sessionMap.put(ret.getUser().getUserId(), ret);
        persist();
        return ret;
    }
}
