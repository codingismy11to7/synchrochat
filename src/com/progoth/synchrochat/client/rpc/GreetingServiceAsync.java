package com.progoth.synchrochat.client.rpc;

import java.util.SortedSet;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync
{
    void getRoomList(AsyncCallback<SortedSet<ChatRoom>> callback);

    void greetServer(String requestUri, String name, AsyncCallback<LoginResponse> callback);

    void logout(AsyncCallback<Void> callback);

    void openChannel(AsyncCallback<String> callback);

    void sendMsg(String channel, String msg, AsyncCallback<Void> callback);

    void subscribe(String aRoomName, AsyncCallback<SortedSet<ChatRoom>> callback);
}
