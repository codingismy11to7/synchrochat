package com.progoth.synchrochat.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.progoth.synchrochat.client.rpc.GreetingService;
import com.progoth.synchrochat.shared.FieldVerifier;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService
{

    /**
     * 
     */
    private static final long serialVersionUID = 8471232977034188023L;
    private static final Map<String, Set<String>> sm_rooms = new HashMap<String, Set<String>>();

    @Override
    public Set<String> getRoomList()
    {
        return new HashSet<String>(sm_rooms.keySet());
    }

    private User getUser()
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        return user;
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
            resp.setChannelKey(ChannelServiceFactory.getChannelService().createChannel(
                user.getUserId()));
            resp.setNickname("Hello, " + user.getNickname() + "!\n\nI am running " + serverInfo
                    + ".\n\nIt looks like you are using:\n" + y.asString());
        }
        return resp;
    }

    @Override
    public void sendMsg(final String aChannel, final String aMsg)
    {
        final ChatMessage msg = new ChatMessage(aChannel, aMsg, getUser().getNickname());
        // msg.setMsg(aMsg);
        if (sm_rooms.containsKey(aChannel))
        {
            for (final String user : sm_rooms.get(aChannel))
            {
                ChannelServer.send(user, msg);
            }
        }
    }

    @Override
    public Set<String> subscribe(final String aName)
    {
        final User user = getUser();
        if (user == null)
            throw new RuntimeException("null user");
        final String userId = user.getUserId();
        // String key = ChannelServiceFactory.getChannelService().createChannel(userId);
        // if (key == null) throw new RuntimeException("null key!");
        if (!sm_rooms.containsKey(aName))
        {
            sm_rooms.put(aName, new HashSet<String>());
        }
        sm_rooms.get(aName).add(userId);
        // return key;
        return getRoomList();
    }
}
