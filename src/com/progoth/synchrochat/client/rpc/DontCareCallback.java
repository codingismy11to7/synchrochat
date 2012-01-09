package com.progoth.synchrochat.client.rpc;

public class DontCareCallback<T> extends SimpleAsyncCallback<T>
{
    public static <X> DontCareCallback<X> get()
    {
        return new DontCareCallback<X>();
    }

    @Override
    public void onSuccess(final T aResult)
    {
        // don't care!
    }
}
