package com.progoth.synchrochat.client.gui.resources;

import com.google.gwt.core.client.GWT;

public class SynchroImages
{
    private static final ISynchroImages sm_bundle = GWT.create(ISynchroImages.class);

    public static ISynchroImages get()
    {
        return sm_bundle;
    }

    private SynchroImages()
    {
        // can't instantiate
    }
}
