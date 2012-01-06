package com.progoth.synchrochat.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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
    private static final String KEY = "SessionMap";

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
            return pm.detachCopy(sessions);
        }
        finally
        {
            pm.close();
        }
    }

    @PrimaryKey
    private String m_key = KEY;

    @Persistent(serialized="true")
    private Map<String, ClientSession> m_sessions = new HashMap<String, ClientSession>();

    public RoomList addUserToRoom(final String aRoom)
    {
        final ClientSession session = getSession();

        final RoomList rl = RoomList.get();
        rl.addUserToRoom(aRoom, session.getUser().getUserId());

        session.getRoomList().add(aRoom);
        persist();

        return rl;
    }

    public ClientSession endSession()
    {
        final User user = sm_userService.getCurrentUser();

        RoomList.get().removeUserFromRooms(user.getUserId());

        final ClientSession ret = m_sessions.remove(user.getUserId());
        persist();
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
        return m_sessions.get(user.getUserId());
    }

    @SuppressWarnings("unused")
    private Map<String, ClientSession> getSessions()
    {
        return m_sessions;
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
    private void setKey(final String aKey)
    {
        m_key = aKey;
    }

    @SuppressWarnings("unused")
    private void setSessions(final Map<String, ClientSession> aSessions)
    {
        m_sessions = aSessions;
    }

    public ClientSession startSession()
    {
        final ClientSession ret = new ClientSession();
        m_sessions.put(ret.getUser().getUserId(), ret);
        persist();
        return ret;
    }
}
