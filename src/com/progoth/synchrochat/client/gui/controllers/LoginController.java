package com.progoth.synchrochat.client.gui.controllers;

import com.google.gwt.user.client.Window;
import com.progoth.synchrochat.client.events.LoginResponseReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.rpc.ReallyDontCareCallback;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;
import com.progoth.synchrochat.client.rpc.SynchroRpc;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class LoginController
{
    private static String sm_logoutUrl = null;

    private static void doLogout(final boolean aRedirect)
    {
        SynchroRpc.get().logout(new ReallyDontCareCallback<Void>()
        {
            @Override
            public void onSuccess(final Void aResult)
            {
                if (aRedirect)
                {
                    Window.Location.assign(sm_logoutUrl);
                }
            }
        });
    }

    public static void login()
    {
        if (sm_logoutUrl != null)
        {
            new AlertMessageBox("Error", "Already logged in!").show();
            return;
        }

        SynchroRpc.get().greetServer(Window.Location.getHref(),
            new SimpleAsyncCallback<LoginResponse>()
            {
                @Override
                public void onSuccess(final LoginResponse aResult)
                {
                    if (aResult.isLoggedIn())
                    {
                        sm_logoutUrl = aResult.getLogoutUrl();
                        SynchroBus.get().fireEvent(new LoginResponseReceivedEvent(aResult));
                    }
                    else
                    {
                        Window.Location.assign(aResult.getLoginUrl());
                    }
                }
            });
    }

    public static void logout(final boolean aConfirm, final boolean aRedirect)
    {
        if (sm_logoutUrl == null)
        {
            new AlertMessageBox("Error", "Not logged in?").show();
            return;
        }

        if (!aConfirm)
        {
            doLogout(aRedirect);
        }
        else
        {
            final ConfirmMessageBox confirm = new ConfirmMessageBox("Sign out",
                    "Are you sure you want to sign out?<br/>"
                            + "This will log you out of all Google services!");
            confirm.addHideHandler(new HideHandler()
            {
                @Override
                public void onHide(final HideEvent aEvent)
                {
                    if (confirm.getHideButton() == confirm.getButtonById(PredefinedButton.YES
                        .name()))
                    {
                        doLogout(aRedirect);
                    }
                }
            });
            confirm.show();
        }
    }
}
