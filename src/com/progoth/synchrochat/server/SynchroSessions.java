package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class SynchroSessions implements Serializable
{
    private static final long serialVersionUID = 8170556146722378460L;

    private static SynchroSessions sm_instance = null;

    private static final UserService sm_userService = UserServiceFactory.getUserService();

    private static final String KEY = "SynchrochatSessions";

    public static SynchroSessions get()
    {
        if (sm_instance == null)
        {
            sm_instance = init();
        }
        return sm_instance;
    }

    private static synchronized SynchroSessions init()
    {
        final SynchroSessions ret = SynchroCache.get(KEY);
        if (ret != null)
            return ret;

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            final Extent<ClientSession> ext = pm.getExtent(ClientSession.class, true);
            try
            {
                final SynchroSessions sessions = new SynchroSessions();
                for (final ClientSession sess : ext)
                {
                    sessions.m_sessionList.add(pm.detachCopy(sess));
                }
                return sessions;
            }
            finally
            {
                ext.closeAll();
            }
        }
        finally
        {
            pm.close();
        }
    }

    private Set<ClientSession> m_sessionList = Sets.newHashSet();

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

        final ClientSession ret = getSession();
        /*
         * m_sessionList.remove(ret); persist();
         */
        return ret;
    }

    public ClientSession getSession()
    {
        final User user = sm_userService.getCurrentUser();
        return getSession(user);
    }

    public ClientSession getSession(final User aUser)
    {
        for (final ClientSession sess : m_sessionList)
            if (sess.getUser().equals(aUser))
                return sess;
        return null;
    }

    @SuppressWarnings("unused")
    private Set<ClientSession> getSessions()
    {
        return m_sessionList;
    }

    public Set<User> getSessionUsers()
    {
        return Sets.newHashSet(Collections2.transform(m_sessionList,
            new Function<ClientSession, User>()
            {
                @Override
                public User apply(final ClientSession aArg0)
                {
                    return aArg0.getUser();
                }
            }));
    }

    public String openChannel(final boolean aForce)
    {
        final ClientSession sess = getSession();
        final GregorianCalendar now = new GregorianCalendar();
        if (!aForce && sess.getChannelExpiration() != null && sess.getChannelName() != null)
        {
            final GregorianCalendar tmp = new GregorianCalendar();
            tmp.setTime(sess.getChannelExpiration());
            if (tmp.after(now))
                return sess.getChannelName();
        }
        final String chanId = ChannelServiceFactory.getChannelService().createChannel(
            sess.getUser().getUserId());
        now.add(Calendar.HOUR_OF_DAY, 2);
        sess.setChannelExpiration(now.getTime());
        sess.setChannelName(chanId);
        persist(sess);
        return chanId;
    }

    private ClientSession persist(final ClientSession aSess)
    {
        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        ClientSession ret;
        try
        {
            pm.makePersistent(aSess);
            ret = pm.detachCopy(aSess);
        }
        finally
        {
            pm.close();
        }
        m_sessionList.remove(ret);
        m_sessionList.add(ret);
        SynchroCache.put(KEY, this);
        return ret;
    }

    public RoomList removeUserFromRoom(final ChatRoom aRoom)
    {
        final ClientSession session = getSession();

        final RoomList rl = RoomList.get();
        rl.removeUserFromRoom(aRoom, session.getUser());

        return rl;
    }

    @SuppressWarnings("unused")
    private void setSessions(final Set<ClientSession> aSessions)
    {
        m_sessionList = aSessions;
    }

    public synchronized ClientSession startSession()
    {
        ClientSession ret;
        if ((ret = getSession()) != null)
            return ret;

        ret = new ClientSession();
        return persist(ret);
    }
}
