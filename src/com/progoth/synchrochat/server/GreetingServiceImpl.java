package com.progoth.synchrochat.server;

import java.util.Set;
import java.util.SortedSet;

import no.eirikb.gwtchannelapi.server.ChannelServer;

import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.progoth.synchrochat.client.rpc.GreetingService;
import com.progoth.synchrochat.shared.FieldVerifier;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.LoginResponse;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService
{
    private static final long serialVersionUID = 8471232977034188023L;

    @Override
    public SortedSet<String> getRoomList()
    {
        return RoomList.get().getRooms();
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
    public void sendMsg(final String aChannel, final String aMsg)
    {
        final ChatMessage msg = new ChatMessage(aChannel, aMsg, getUser().getNickname());
        for (final String user : RoomList.get().getSubscribedUsers(aChannel))
        {
            ChannelServer.send(user, msg);
        }
    }

    @Override
    public Set<String> subscribe(final String aName)
    {
        final RoomList rl = SynchroSessions.get().addUserToRoom(aName.trim());

        return rl.getRooms();
    }
}
