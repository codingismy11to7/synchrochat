package com.progoth.synchrochat.client.rpc;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.progoth.synchrochat.client.model.LoginResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService
{
    LoginResponse greetServer(String requestUri, String name) throws IllegalArgumentException;
    Set<String> subscribe(String aRoomName);
    void sendMsg(String channel, String msg);
    Set<String> getRoomList();
}
