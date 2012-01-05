package com.progoth.synchrochat.server;

import java.util.Set;
import java.util.SortedSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import no.eirikb.gwtchannelapi.server.ChannelServer;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.progoth.synchrochat.client.model.ChatMessage;
import com.progoth.synchrochat.client.model.LoginResponse;
import com.progoth.synchrochat.client.model.RoomList;
import com.progoth.synchrochat.client.rpc.GreetingService;
import com.progoth.synchrochat.shared.FieldVerifier;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService
{
    private static final long serialVersionUID = 8471232977034188023L;

    // private static final SortedMap<String, Set<String>> sm_rooms = Collections
    // .synchronizedSortedMap(new TreeMap<String, Set<String>>());

    @Override
    public SortedSet<String> getRoomList()
    {
        return getRoomListImpl(getRoomListObject());
    }

    private SortedSet<String> getRoomListImpl(final RoomList aRoomList)
    {
        return aRoomList.getRooms();
    }

    private RoomList getRoomListObject()
    {
        final RoomList ret = SynchroCache.get(RoomList.KEY);
        if (ret != null)
            return ret;

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            Query q = pm.newQuery(RoomList.class, "m_key == :rlid");
            q.setUnique(true);
            RoomList roomList = (RoomList)q.execute(RoomList.KEY);
//            RoomList roomList = pm.getObjectById(RoomList.class, RoomList.KEY);
            if (roomList == null)
            {
                roomList = new RoomList();
                pm.makePersistent(roomList);
            }
            return pm.detachCopy(roomList);
        }
        catch(Exception e){
            e.printStackTrace();return null;
        }
        finally
        {
            pm.close();
        }
    }

    private User getUser()
    {
        final UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    @Override
    public LoginResponse greetServer(final String requestUri, final String input)
            throws IllegalArgumentException
    {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input))
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name must be at least 4 characters long");

        final User user = getUser();
        final UserService userService = UserServiceFactory.getUserService();

        final LoginResponse resp = new LoginResponse();
        if (user == null)
        {
            resp.setLoggedIn(false);
            resp.setLoginUrl(userService.createLoginURL(requestUri));
        }
        else
        {
            final String serverInfo = getServletContext().getServerInfo();
            final String userAgent = getThreadLocalRequest().getHeader("User-Agent");

            // Escape data from the client to avoid cross-site script vulnerabilities.
            // final SafeHtml x = new SafeHtmlBuilder().appendEscaped(input).toSafeHtml();
            final SafeHtml y = new SafeHtmlBuilder().appendEscaped(userAgent).toSafeHtml();
            // input = escapeHtml(input);
            // userAgent = escapeHtml(userAgent);

            resp.setLoggedIn(true);
            resp.setLogoutUrl(userService.createLogoutURL(requestUri));
            resp.setEmailAddress(user.getEmail());
            resp.setNickname("Hello, " + user.getNickname() + "!\n\nI am running " + serverInfo
                    + ".\n\nIt looks like you are using:\n" + y.asString());
        }
        return resp;
    }

    @Override
    public String openChannel()
    {
        final User user = getUser();
        return ChannelServiceFactory.getChannelService().createChannel(user.getUserId());
    }

    private void persistRoomList(final RoomList aRoomList)
    {
        SynchroCache.getCache().put(RoomList.KEY, aRoomList);

        final PersistenceManager pm = PMF.getEventualReads().getPersistenceManager();
        try
        {
            pm.makePersistent(aRoomList);
        }
        finally
        {
            pm.close();
        }
    }

    @Override
    public void sendMsg(final String aChannel, final String aMsg)
    {
        final ChatMessage msg = new ChatMessage(aChannel, aMsg, getUser().getNickname());
        for (final String user : getRoomListObject().getSubscribedUsers(aChannel))
        {
            ChannelServer.send(user, msg);
        }
    }

    @Override
    public Set<String> subscribe(final String aName)
    {
        final User user = getUser();
        if (user == null)
            throw new RuntimeException("null user");

        final String userId = user.getUserId();

        final RoomList rl = getRoomListObject();
        rl.addUserToRoom(aName, userId);
        persistRoomList(rl);
        return getRoomListImpl(rl);
    }
}
