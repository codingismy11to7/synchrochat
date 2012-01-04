package com.progoth.synchrochat.client.rpc;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.progoth.synchrochat.client.model.LoginResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync
{
    void greetServer(String requestUri, String name, AsyncCallback<LoginResponse> callback);

    void subscribe(String aRoomName, AsyncCallback<Set<String>> callback);

    void sendMsg(String channel, String msg, AsyncCallback<Void> callback);

    void getRoomList(AsyncCallback<Set<String>> callback);
}
