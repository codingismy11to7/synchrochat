package com.progoth.synchrochat.client.rpc;

import java.util.SortedSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.progoth.synchrochat.shared.AccessDeniedException;
import com.progoth.synchrochat.shared.InvalidIdentifierException;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.progoth.synchrochat.shared.model.RoomSubscribeResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("synchrochat")
public interface SynchrochatService extends RemoteService
{
    void clearCaches() throws AccessDeniedException;

    SortedSet<ChatRoom> getRoomList();

    LoginResponse greetServer(String requestUri) throws IllegalArgumentException;

    SortedSet<ChatRoom> leaveRoom(ChatRoom aRoom);

    void logout();

    String openChannel(boolean aForce);

    void sendMsg(ChatRoom channel, String msg);

    RoomSubscribeResponse subscribe(ChatRoom aRoom) throws InvalidIdentifierException;
}
