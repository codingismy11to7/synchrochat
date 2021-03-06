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
import com.progoth.synchrochat.shared.AccessDeniedException;
import com.progoth.synchrochat.shared.FieldVerifier;
import com.progoth.synchrochat.shared.InvalidIdentifierException;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.progoth.synchrochat.shared.model.RoomListUpdateMessage;
import com.progoth.synchrochat.shared.model.RoomSubscribeResponse;
import com.progoth.synchrochat.shared.model.SynchroUser;
import com.progoth.synchrochat.shared.model.UserListUpdateMessage;

/**
 * The server side implementation of the RPC service.
 */
public class SynchrochatServiceImpl extends RemoteServiceServlet implements SynchrochatService
{
    private static final long serialVersionUID = 8471232977034188023L;

    private static final Set<String> sm_allowedEmails = Sets.newHashSet("progoth@gmail.com",
        "subsystem@gmail.com", "joshua.andrew.guy@gmail.com", "angela.froning@gmail.com",
        "sung.whang@gmail.com", "steven@codemettle.com");

    @Override
    public void clearCaches() throws AccessDeniedException
    {
        if (!UserServiceFactory.getUserService().isUserAdmin())
            throw new AccessDeniedException();
        SynchroCache.getCache().clear();
    }

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
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        final LoginResponse resp = new LoginResponse();
        if (!userService.isUserLoggedIn())
        {
            resp.setLoggedIn(false);
            resp.setLoginUrl(userService.createLoginURL(requestUri));
        }
        else if (!userService.isUserAdmin() && !sm_allowedEmails.contains(user.getEmail()))
        {
            resp.setLoggedIn(false);
            resp.setLoginUrl("https://github.com/codingismy11to7/synchrochat");
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

            resp.setAdmin(userService.isUserAdmin());
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
        ChatCache.get().recordAndUpdateNewMessage(msg);
        for (final User user : RoomList.get().getSubscribedUsers(aRoom))
        {
            ChannelServer.send(user.getUserId(), msg);
        }
    }

    @Override
    public RoomSubscribeResponse subscribe(final ChatRoom aRoom) throws InvalidIdentifierException
    {
        if (!FieldVerifier.validChatRoomName(aRoom.getName()))
            throw new InvalidIdentifierException("Invalid room name.");
        final RoomList rl = SynchroSessions.get().addUserToRoom(aRoom);

        final SortedSet<SynchroUser> userList = getUserList(aRoom, rl);
        final SortedSet<ChatRoom> roomList = rl.getClientSafeRooms();

        pushUserListUpdateMessage(aRoom, userList, rl);
        pushRoomListUpdateMessage(roomList);

        return new RoomSubscribeResponse(roomList, userList, ChatCache.get().getAllRoomMessages(
            aRoom));
    }
}
