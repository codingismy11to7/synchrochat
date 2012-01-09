package com.progoth.synchrochat.client.rpc;

public class ReallyDontCareCallback<T> extends DontCareCallback<T>
{
    public static <X> ReallyDontCareCallback<X> get()
    {
        return new ReallyDontCareCallback<X>();
    }

    @Override
    public void onFailure(final Throwable aCaught)
    {
        // don't care
    }
}
