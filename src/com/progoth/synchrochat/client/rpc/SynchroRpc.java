package com.progoth.synchrochat.client.rpc;

import com.google.gwt.core.client.GWT;

public class SynchroRpc
{
    private static final SynchrochatServiceAsync sm_service = GWT.create(SynchrochatService.class);

    public static SynchrochatServiceAsync get()
    {
        return sm_service;
    }

    private SynchroRpc()
    {
        // con't instantiate
    }
}
