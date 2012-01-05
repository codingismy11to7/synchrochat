/**
 * Copyright (c) 2010, Eirik Brandtzæg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  *  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL EIRIK BRANDTZÆG BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.eirikb.gwtchannelapi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;

/**
 * 
 * @author Eirik Brandtzæg <eirikb@eirikb.no>
 * 
 */
public class Channel
{
    private final List<ChannelListener> m_channelListeners;
    private final String m_channelKey;

    /**
     * 
     * @param aChannelKey
     *            key of the channel. This key is the key you get from ChannelServiceFactory
     *            .getChannelService().createChannel(String) on server side
     */
    public Channel(final String aChannelKey)
    {
        m_channelKey = aChannelKey;
        m_channelListeners = new ArrayList<ChannelListener>();
    }

    /**
     * Add a ChannelListener that will respond on events
     * 
     * @param channelListener
     */
    public void addChannelListener(final ChannelListener channelListener)
    {
        m_channelListeners.add(channelListener);
    }

    /**
     * Join the channel
     */
    public void join()
    {
        join(m_channelKey);
    }

    private native void join(String channelKey) /*-{
		var c = new $wnd.goog.appengine.Channel(channelKey);
		var socket = c.open();
		var me = this;
		socket.onmessage = function(evt) {
			var s = evt.data;
			me.@no.eirikb.gwtchannelapi.client.Channel::onMessage(Ljava/lang/String;)(s);
		}
		socket.onopen = function() {
			me.@no.eirikb.gwtchannelapi.client.Channel::onOpen();
		}
		socket.onclose = function(evt) {
			me.@no.eirikb.gwtchannelapi.client.Channel::onClose(Ljava/lang/Object;)(evt);
		}
		socket.onerror = function(evt) {
			me.@no.eirikb.gwtchannelapi.client.Channel::onError(Ljava/lang/Object;)(evt);
		}
    }-*/;

    private void onClose(final Object evt)
    {
        for (final ChannelListener list : m_channelListeners)
        {
            list.onClose(evt);
        }
    }

    private void onError(final Object evt)
    {
        for (final ChannelListener list : m_channelListeners)
        {
            list.onError(evt);
        }
    }

    private void onMessage(final String aEncoded)
    {
        final SerializationStreamFactory ssf = GWT.create(ChannelService.class);
        try
        {
            final Message message = (Message)ssf.createStreamReader(aEncoded).readObject();
            for (int i = 0; i < m_channelListeners.size(); i++)
            {
                m_channelListeners.get(i).onReceive(message);
            }
        }
        catch (final SerializationException e)
        {
            e.printStackTrace();
        }
    }

    private void onOpen()
    {
        for (final ChannelListener list : m_channelListeners)
        {
            list.onOpen();
        }
    }

    public void removeAllListeners()
    {
        m_channelListeners.clear();
    }

    public void removeChannelListener(final ChannelListener channelListener)
    {
        m_channelListeners.remove(channelListener);
    }
}
