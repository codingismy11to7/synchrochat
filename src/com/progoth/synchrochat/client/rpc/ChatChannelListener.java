package com.progoth.synchrochat.client.rpc;

import no.eirikb.gwtchannelapi.client.Channel;
import no.eirikb.gwtchannelapi.client.ChannelListenerAdapter;
import no.eirikb.gwtchannelapi.client.Message;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.progoth.synchrochat.client.ChatPanel;
import com.progoth.synchrochat.client.model.ChatMessage;

public class ChatChannelListener extends ChannelListenerAdapter
{
    public static interface ClosedListener
    {
        void channelClosed();
    }

    private final ChatPanel m_chatPanel;
    private final Channel m_channel;
    private final ClosedListener m_listener;

    public ChatChannelListener(final Channel aChannel, final ChatPanel aPanel,
            final ClosedListener aListener)
    {
        m_chatPanel = aPanel;
        m_channel = aChannel;
        m_listener = aListener;
    }

    @Override
    public void onError(final Object aEvt)
    {
        m_channel.removeAllListeners();
        m_listener.channelClosed();
    }

    @Override
    public void onReceive(final Message aMessage)
    {
        final ChatMessage msg = (ChatMessage)aMessage;
        final String dateTimeString = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(
            msg.getDate())
                + ' '
                + DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(msg.getDate());
        final String line = dateTimeString + " <" + msg.getRoom() + ">" + " [" + msg.getUser()
                + "]: " + msg.getMsg();
        m_chatPanel.append(line);
    }
}
