package com.progoth.synchrochat.server;

import java.util.Set;
import java.util.SortedSet;

import no.eirikb.gwtchannelapi.server.ChannelServer;

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
import com.progoth.synchrochat.shared.model.RoomListUpdateMessage;
import com.progoth.synchrochat.shared.model.SynchroUser;
import com.progoth.synchrochat.shared.model.UserListUpdateMessage;

/**
 * The server side implementation of the RPC service.
 */
public class SynchrochatServiceImpl extends RemoteServiceServlet implements SynchrochatService
{
    private static final long serialVersionUID = 8471232977034188023L;

    @Override
    public SortedSet<ChatRoom> getRoomList()
    {
        return RoomList.get().getClientSafeRooms();
    }

    private User getUser()
    {
        final UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    private SortedSet<SynchroUser> getUserList(final ChatRoom aRoom, final RoomList aRoomList)
    {
        final Set<User> users = aRoomList.getSubscribedUsers(aRoom);
        final SortedSet<SynchroUser> ret = Sets.newTreeSet();
        for (final User user : users)
        {
            ret.add(SynchroSessions.get().getSession(user).getSynchroUser());
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
    public SortedSet<ChatRoom> leaveRoom(final ChatRoom aRoom)
    {
        final RoomList rl = SynchroSessions.get().removeUserFromRoom(aRoom);

        final SortedSet<SynchroUser> userList = getUserList(aRoom, rl);

        pushUserListUpdateMessage(aRoom, userList, rl);

        return rl.getClientSafeRooms();
    }

    @Override
    public void logout()
    {
        final User user = getUser();
        for (final ChatRoom room : RoomList.get().getRoomsForUser(user))
        {
            leaveRoom(room);
        }

        SynchroSessions.get().endSession();
    }

    @Override
    public String openChannel(final boolean aForce)
    {
        return SynchroSessions.get().openChannel(aForce);
    }

    private void pushRoomListUpdateMessage(final SortedSet<ChatRoom> aRoomList)
    {
        final RoomListUpdateMessage msg = new RoomListUpdateMessage(aRoomList);
        for (final User user : SynchroSessions.get().getSessionUsers())
        {
            ChannelServer.send(user.getUserId(), msg);
        }
    }

    private void pushUserListUpdateMessage(final ChatRoom aRoom,
            final SortedSet<SynchroUser> aUserList, final RoomList aRoomList)
    {
        final UserListUpdateMessage msg = new UserListUpdateMessage(aRoom.clientSafe(), aUserList);
        for (final User user : aRoomList.getSubscribedUsers(aRoom))
        {
            ChannelServer.send(user.getUserId(), msg);
        }
    }

    @Override
    public void sendMsg(final ChatRoom aRoom, final String aMsg)
    {
        final SynchroUser synchrouser = SynchroSessions.get().getSession().getSynchroUser();
        final ChatMessage msg = new ChatMessage(aRoom.clientSafe(), aMsg, synchrouser);
        for (final User user : RoomList.get().getSubscribedUsers(aRoom))
        {
            ChannelServer.send(user.getUserId(), msg);
        }
    }

    @Override
    public Pair<SortedSet<ChatRoom>, SortedSet<SynchroUser>> subscribe(final ChatRoom aRoom)
    {
        final RoomList rl = SynchroSessions.get().addUserToRoom(aRoom);

        final SortedSet<SynchroUser> userList = getUserList(aRoom, rl);
        final SortedSet<ChatRoom> roomList = rl.getClientSafeRooms();

        pushUserListUpdateMessage(aRoom, userList, rl);
        pushRoomListUpdateMessage(roomList);

        return new Pair<SortedSet<ChatRoom>, SortedSet<SynchroUser>>(roomList, userList);
    }
}
