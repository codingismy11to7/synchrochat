package com.progoth.synchrochat.client.rpc;

import no.eirikb.gwtchannelapi.client.Channel;
import no.eirikb.gwtchannelapi.client.ChannelListenerAdapter;
import no.eirikb.gwtchannelapi.client.Message;

public class ChatChannelListener extends ChannelListenerAdapter
{
    public static interface ChannelListener
    {
        void channelClosed(boolean aError);

        void messageReceived(Message aMessage);
    }

    private final Channel m_channel;
    private final ChannelListener m_listener;

    public ChatChannelListener(final Channel aChannel, final ChannelListener aListener)
    {
        m_channel = aChannel;
        m_listener = aListener;
    }

    @Override
    public void onError(final Object aEvt)
    {
        m_channel.removeAllListeners();
        m_listener.channelClosed(true);
    }

    @Override
    public void onReceive(final Message aMessage)
    {
        m_listener.messageReceived(aMessage);
    }
}
