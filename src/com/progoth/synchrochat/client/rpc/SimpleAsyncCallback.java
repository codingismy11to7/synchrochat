package com.progoth.synchrochat.client.rpc;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SimpleAsyncCallback<T> implements AsyncCallback<T>
{

    @Override
    public void onFailure(Throwable aCaught)
    {
        Window.alert(aCaught.toString());
    }

}
