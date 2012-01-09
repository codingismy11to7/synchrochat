package com.progoth.synchrochat.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public abstract class SimpleAsyncCallback<T> implements AsyncCallback<T>
{
    @Override
    public void onFailure(final Throwable aCaught)
    {
        new AlertMessageBox("RPC Error", aCaught.toString()).show();
    }
}
