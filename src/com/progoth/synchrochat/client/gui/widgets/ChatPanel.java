package com.progoth.synchrochat.client.gui.widgets;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.client.gui.widgets.SynchroHtmlEditor.EditSendHandler;
import com.progoth.synchrochat.client.rpc.ReallyDontCareCallback;
import com.progoth.synchrochat.client.rpc.SynchroRpc;
import com.progoth.synchrochat.shared.model.ChatMessage;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class ChatPanel extends BorderLayoutContainer
{
    private final ChatRoom m_room;
    private SynchroHtmlEditor m_input;
    private TextArea m_output;
    private final SortedMap<Date, ChatMessage> m_msgMap = new TreeMap<Date, ChatMessage>();

    private final ScheduledCommand sm_scrollCommand = new ScheduledCommand()
    {
        @Override
        public void execute()
        {
            final Element area = m_output.getElement().getFirstChildElement()
                .getFirstChildElement();
            area.setScrollTop(area.getScrollHeight());
        }
    };

    public ChatPanel(final ChatRoom aRoom)
    {
        m_room = aRoom;
        createBottom();
        createCenter();
    }

    public void addMessage(final ChatMessage aMessage)
    {
        m_msgMap.put(aMessage.getDate(), aMessage);
        final String newText = getText();
        m_output.setText(newText);
        m_output.setCursorPos(newText.length());
        Scheduler.get().scheduleDeferred(sm_scrollCommand);
    }

    private void createBottom()
    {
        m_input = new SynchroHtmlEditor();

        final BorderLayoutData bld = new BorderLayoutData(100);
        bld.setMargins(new Margins(5, 0, 0, 0));
        bld.setCollapsible(false);
        bld.setSplit(true);
        bld.setMinSize(65);

        final SelectHandler clickHandler = new SelectHandler()
        {
            @Override
            public void onSelect(final SelectEvent aEvent)
            {
                SynchroRpc.get().sendMsg(m_room, m_input.getValue(),
                    new ReallyDontCareCallback<Void>());
                m_input.setValue(null);
            }
        };
        final TextButton send = new TextButton("Send", clickHandler);
        send.setIcon(SynchroImages.get().font_go());
        send.setIconAlign(IconAlign.TOP);

        m_input.addListener(new EditSendHandler()
        {
            @Override
            public void onSendRequested(final SynchroHtmlEditor aSource)
            {
                clickHandler.onSelect(null);
            }
        });

        final HBoxLayoutContainer horiz = new HBoxLayoutContainer(HBoxLayoutAlign.TOP);
        final BoxLayoutData flex = new BoxLayoutData(new Margins());
        flex.setFlex(1);
        horiz.add(m_input, flex);
        horiz.add(send, new BoxLayoutData(new Margins(0, 5, 0, 5)));

        horiz.addResizeHandler(new ResizeHandler()
        {
            @Override
            public void onResize(final ResizeEvent aEvent)
            {
                horiz.forceLayout();
                send.redraw();
            }
        });

        setSouthWidget(horiz, bld);
    }

    private void createCenter()
    {
        m_output = new TextArea();
        m_output.setReadOnly(true);
        // m_output.setText("lorem ipsum");

        setCenterWidget(m_output, new MarginData(new Margins()));
    }

    public SynchroHtmlEditor getInput()
    {
        return m_input;
    }

    public ChatRoom getRoom()
    {
        return m_room;
    }

    private String getText()
    {
        final StringBuilder box = new StringBuilder();
        for (final ChatMessage e : m_msgMap.values())
        {
            box.append(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(e.getDate())
                    + ' '
                    + DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(e.getDate()));
            box.append(" [" + e.getUser().getName() + "]: " + e.getMsg() + '\n');
        }
        return box.toString();
    }
}
