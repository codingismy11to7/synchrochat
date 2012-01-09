package com.progoth.synchrochat.client.events;

import com.google.web.bindery.event.shared.SimpleEventBus;

public class SynchroBus extends SimpleEventBus
{
    private static SynchroBus sm_instance = new SynchroBus();

    public static SynchroBus get()
    {
        return sm_instance;
    }

    private SynchroBus()
    {
        // singleton
    }
}
