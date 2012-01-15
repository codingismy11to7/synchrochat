package com.progoth.synchrochat.server;

import java.util.Set;
import java.util.SortedSet;

import no.eirikb.gwtchannelapi.server.ChannelServer;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Sets;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.progoth.synchrochat.client.rpc.SynchrochatService;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.progoth.synchrochat.shared.model.Pair;
import com.progoth.synchrochat.shared.model.SynchroUser;

/**
 * The server side implementation of the RPC service.
 */
public class SynchrochatServiceImpl extends RemoteServiceServlet implements SynchrochatService
{
    private static final long serialVersionUID = 8471232977034188023L;

    @Override
    public SortedSet<ChatRoom> getRoomList()
    {
        return RoomList.get().getRooms();
    }

    private User getUser()
    {
        final UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    private SortedSet<SynchroUser> getUserList(final ChatRoom aRoom)
    {
        final Set<String> users = RoomList.get().getSubscribedUsers(aRoom.getName());
        final SortedSet<SynchroUser> ret = Sets.newTreeSet();
        for (final String userId : users)
        {
            ret.add(SynchroSessions.get().getSession(userId).getSynchroUser());
        }
        return ret;
    }

    @Override
    public LoginResponse greetServer(final String requestUri) throws IllegalArgumentException
    {
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
            resp.setNickname(user.getNickname());
            resp.setMessage("Hello, " + user.getNickname() + "!\n\nI am running " + serverInfo
                    + ".\n\nIt looks like you are using:\n" + y.asString());

            SynchroSessions.get().startSession();
        }
        return resp;
    }

    @Override
    public void logout()
    {
        SynchroSessions.get().endSession();
    }

    @Override
    public String openChannel()
    {
        final User user = getUser();
        return ChannelServiceFactory.getChannelService().createChannel(user.getUserId());
    }

    @Override
    public void sendMsg(final ChatRoom aRoom, final String aMsg)
    {
        final ChatMessage msg = new ChatMessage(aRoom.getName(), aMsg, getUser().getNickname());
        for (final String user : RoomList.get().getSubscribedUsers(aRoom.getName()))
        {
            ChannelServer.send(user, msg);
        }
    }

    @Override
    public Pair<SortedSet<ChatRoom>, SortedSet<SynchroUser>> subscribe(final ChatRoom aRoom)
    {
        final RoomList rl = SynchroSessions.get().addUserToRoom(aRoom);

        return new Pair<SortedSet<ChatRoom>, SortedSet<SynchroUser>>(rl.getRooms(),
                getUserList(aRoom));
    }
}
