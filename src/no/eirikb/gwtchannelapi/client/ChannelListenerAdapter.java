package no.eirikb.gwtchannelapi.client;

import com.google.gwt.user.client.Window;

public class ChannelListenerAdapter implements ChannelListener
{
    @Override
    public void onClose(final Object aEvt)
    {
        // can be overridden
        Window.alert("close; object=" + aEvt);
    }

    @Override
    public void onError(final Object aEvt)
    {
        // can be overridden
        Window.alert("error; object=" + aEvt);
    }

    @Override
    public void onOpen()
    {
        // can be overridden
    }

    @Override
    public void onReceive(final Message aMessage)
    {
        // can be overridden
    }
}
