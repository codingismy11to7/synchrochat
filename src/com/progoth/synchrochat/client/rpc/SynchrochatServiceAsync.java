package com.progoth.synchrochat.client.rpc;

import java.util.SortedSet;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.progoth.synchrochat.shared.model.Pair;
import com.progoth.synchrochat.shared.model.SynchroUser;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface SynchrochatServiceAsync
{
    void getRoomList(AsyncCallback<SortedSet<ChatRoom>> callback);

    void greetServer(String requestUri, AsyncCallback<LoginResponse> callback);

    void leaveRoom(ChatRoom aRoom, AsyncCallback<SortedSet<ChatRoom>> callback);

    void logout(AsyncCallback<Void> callback);

    void openChannel(AsyncCallback<String> callback);

    void sendMsg(ChatRoom channel, String msg, AsyncCallback<Void> callback);

    void subscribe(ChatRoom aRoom,
            AsyncCallback<Pair<SortedSet<ChatRoom>, SortedSet<SynchroUser>>> callback);
}
