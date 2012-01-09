package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.Event;
import com.progoth.synchrochat.shared.model.LoginResponse;

public class LoginResponseReceivedEvent extends Event<LoginResponseReceivedEvent.Handler>
{
    public static interface Handler
    {
        void loginReceived(LoginResponse aResponse);
    }

    public static final Type<Handler> TYPE = new Type<LoginResponseReceivedEvent.Handler>();

    private final LoginResponse m_resp;

    public LoginResponseReceivedEvent(final LoginResponse aResp)
    {
        m_resp = aResp;
    }

    @Override
    protected void dispatch(final Handler aHandler)
    {
        aHandler.loginReceived(m_resp);
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }
}
