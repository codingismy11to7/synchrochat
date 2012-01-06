package com.progoth.synchrochat.client;

import com.google.web.bindery.event.shared.SimpleEventBus;

public class SynchroController extends SimpleEventBus
{
    private static SynchroController sm_instance = new SynchroController();

    public static SynchroController get()
    {
        return sm_instance;
    }

    private SynchroController()
    {
        // singleton
    }
}
