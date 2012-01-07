package com.progoth.synchrochat.client.rpc;

import java.util.SortedSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService
{
    SortedSet<ChatRoom> getRoomList();

    LoginResponse greetServer(String requestUri, String name) throws IllegalArgumentException;

    void logout();

    String openChannel();

    void sendMsg(String channel, String msg);

    SortedSet<ChatRoom> subscribe(String aRoomName);
}
