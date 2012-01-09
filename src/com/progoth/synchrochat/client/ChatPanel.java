package com.progoth.synchrochat.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.progoth.synchrochat.client.events.ChatMessageSendEvent;
import com.progoth.synchrochat.client.events.SynchroBus;

public class ChatPanel extends SplitLayoutPanel
{
    private final TextArea m_chatArea, m_input;

    private final ScheduledCommand m_focusCmd = new ScheduledCommand()
    {
        @Override
        public void execute()
        {
            m_input.setFocus(true);
        }
    };

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
            public void onKeyUp(final KeyUpEvent aEvent)
            {
                if (aEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER
                        && !aEvent.isAnyModifierKeyDown())
                {
                    aEvent.preventDefault();
                    aEvent.stopPropagation();
                    doSend();
                }
            }
        });
        dockLayoutPanel.add(m_input);

        m_chatArea = new TextArea();
        m_chatArea.addStyleName("chatBox");
        m_chatArea.setReadOnly(true);
        m_chatArea.setText("o hai");
        m_chatArea.removeStyleDependentName("readonly");
        add(m_chatArea);
    }

    public void append(final String aLine)
    {
        final String newText = m_chatArea.getText() + "\n" + aLine.trim();
        m_chatArea.setText(newText);
        m_chatArea.setCursorPos(newText.length());
        m_chatArea.getElement().setScrollTop(m_chatArea.getElement().getScrollHeight());
        focusInput();
    }

    private void doSend()
    {
        final String msg = m_input.getText().trim();
        m_input.setText("");
        if (!msg.isEmpty())
        {
            SynchroBus.get().fireEvent(new ChatMessageSendEvent(msg));
        }

        focusInput();
    }

    private void focusInput()
    {
        Scheduler.get().scheduleDeferred(m_focusCmd);
    }
}
