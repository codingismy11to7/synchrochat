package com.progoth.synchrochat.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public class ChatPanel extends SplitLayoutPanel
{
    public static interface MsgSendListener
    {
        void onMsg(String aMsg);
    }

    private final TextArea m_chatArea, m_input;
    protected List<MsgSendListener> m_lists = new LinkedList<MsgSendListener>();

    public ChatPanel()
    {
        final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
        addSouth(dockLayoutPanel, 100.0);

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        dockLayoutPanel.addEast(horizontalPanel, 7.7);
        horizontalPanel.setSize("100%", "100%");

        final Button btnSend = new Button("Send");
        btnSend.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent aEvent)
            {
                doSend();
            }
        });
        horizontalPanel.add(btnSend);

        m_input = new TextArea();
        m_input.addKeyUpHandler(new KeyUpHandler()
        {
            @Override
            public void onKeyUp(KeyUpEvent aEvent)
            {
                if (aEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER && !aEvent.isAnyModifierKeyDown())
                {
                    aEvent.preventDefault();
                    aEvent.stopPropagation();
                    doSend();
                }
            }
        });
        dockLayoutPanel.add(m_input);

        m_chatArea = new TextArea();
        m_chatArea.setStyleName("chatBox");
        m_chatArea.setReadOnly(true);
        m_chatArea.setText("o hai");
        add(m_chatArea);
    }

    public void addMsgSendListener(final MsgSendListener aListener)
    {
        m_lists.add(aListener);
    }

    public void append(final String aLine)
    {
        m_chatArea.setText(m_chatArea.getText() + "\n" + aLine.trim());
    }

    private void doSend()
    {
        final String msg = m_input.getText().trim();
        m_input.setText("");
        if (!msg.isEmpty())
        {
            for (final MsgSendListener l : m_lists)
            {
                l.onMsg(msg);
            }
        }
        m_input.setFocus(true);
    }
}
