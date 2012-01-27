package com.progoth.synchrochat.client.gui.controllers;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.shared.FieldVerifier;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class UrlController
{
    private static final UrlController sm_instance = new UrlController();

    private static final RegExp sm_room = RegExp.compile("^#/room/(.*)$");

    public static UrlController get()
    {
        return sm_instance;
    }

    private UrlController()
    {
        // singleton
    }

    public void startupAction()
    {
        final MatchResult m = sm_room.exec(Window.Location.getHash());
        if (m == null)
            return;
        final String room = m.getGroup(1);
        if (FieldVerifier.validChatRoomName(room))
        {
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
                @Override
                public void execute()
                {
                    SynchroBus.get().fireEvent(new RoomJoinRequestEvent(new ChatRoom(room)));
                }
            });
        }
    }

    public void updateUrl(final ChatRoom aSelection)
    {
        String curUrl = Window.Location.getHref();
        if (!Window.Location.getHash().isEmpty())
        {
            curUrl = curUrl.replace(Window.Location.getHash(), "");
        }
        else if (curUrl.endsWith("#"))
        {
            curUrl = curUrl.substring(0, curUrl.length() - 1);
        }

        if (aSelection != null)
        {
            Window.Location.assign(curUrl + "#/room/" + aSelection.getName());
        }
        else
        {
            Window.Location.assign(curUrl + '#');
        }
    }
}
