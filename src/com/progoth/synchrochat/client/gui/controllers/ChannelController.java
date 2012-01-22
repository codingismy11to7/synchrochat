package com.progoth.synchrochat.client.gui.controllers;

import no.eirikb.gwtchannelapi.client.Channel;
import no.eirikb.gwtchannelapi.client.Message;

import com.progoth.synchrochat.client.events.ChatMessageReceivedEvent;
import com.progoth.synchrochat.client.events.RoomListReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.events.UserListReceivedEvent;
import com.progoth.synchrochat.client.rpc.ChatChannelListener;
import com.progoth.synchrochat.client.rpc.ChatChannelListener.ChannelListener;
import com.progoth.synchrochat.client.rpc.SimpleAsyncCallback;
import com.progoth.synchrochat.client.rpc.SynchroRpc;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.RoomListUpdateMessage;
import com.progoth.synchrochat.shared.model.UserListUpdateMessage;

public class ChannelController
{
    private static ChannelController sm_instance = new ChannelController();

    public static ChannelController get()
    {
        return sm_instance;
    }

    private Channel m_channel;
    private final ChannelListener m_channelCloseListener = new ChannelListener()
    {
        @Override
        public void channelClosed(final boolean aError)
        {
            openChannel(aError);
        }

        @Override
        public void messageReceived(final Message aMessage)
        {
            /*
             * final ChatMessage msg = (ChatMessage)aMessage; final String dateTimeString =
             * DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format( msg.getDate()) + ' ' +
             * DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(msg.getDate()); final
             * String line = dateTimeString + " <" + msg.getRoom() + ">" + " [" + msg.getUser() +
             * "]: " + msg.getMsg(); m_chatPanel.append(line);
             */
            if (aMessage instanceof ChatMessage)
            {
                SynchroBus.get().fireEvent(new ChatMessageReceivedEvent((ChatMessage)aMessage));
            }
            else if (aMessage instanceof UserListUpdateMessage)
            {
                final UserListUpdateMessage msg = (UserListUpdateMessage)aMessage;
                SynchroBus.get()
                    .fireEvent(new UserListReceivedEvent(msg.getRoom(), msg.getUsers()));
            }
            else if (aMessage instanceof RoomListUpdateMessage)
            {
                final RoomListUpdateMessage msg = (RoomListUpdateMessage)aMessage;
                SynchroBus.get().fireEvent(new RoomListReceivedEvent(msg.getRooms()));
            }
        }
    };

    private ChannelController()
    {
        // singleton
        openChannel(false);
    }

    private void openChannel(final boolean aForce)
    {
        SynchroRpc.get().openChannel(aForce, new SimpleAsyncCallback<String>()
        {
            @Override
            public void onSuccess(final String aResult)
            {
                m_channel = new Channel(aResult);
                final ChatChannelListener list = new ChatChannelListener(m_channel,
                        m_channelCloseListener);
                m_channel.addChannelListener(list);
                m_channel.join();
            }
        });
    }

    public void stopListening()
    {
        m_channel.removeAllListeners();
    }
}
