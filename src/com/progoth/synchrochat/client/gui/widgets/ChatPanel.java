package com.progoth.synchrochat.client.gui.widgets;

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
    private TextArea m_output;
    private final ScheduledCommand sm_scrollCommand = new ScheduledCommand()
    {
        @Override
        public void execute()
        {
            Element area = m_output.getElement().getFirstChildElement().getFirstChildElement();
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
        final String dateTimeString = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(
            aMessage.getDate())
                + ' '
                + DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(aMessage.getDate());
        final String line = dateTimeString + " [" + aMessage.getUser().getName() + "]: "
                + aMessage.getMsg() + '\n';
        final String newText = m_output.getText() + line;
        m_output.setText(newText);
        m_output.setCursorPos(newText.length());
        Scheduler.get().scheduleDeferred(sm_scrollCommand);
    }

    private void createBottom()
    {
        final SynchroHtmlEditor input = new SynchroHtmlEditor();

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
                SynchroRpc.get().sendMsg(m_room, input.getValue(),
                    new ReallyDontCareCallback<Void>());
                input.setValue(null);
            }
        };
        final TextButton send = new TextButton("Send", clickHandler);
        send.setIcon(SynchroImages.get().font_go());
        send.setIconAlign(IconAlign.TOP);

        input.addListener(new EditSendHandler()
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
        horiz.add(input, flex);
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

    public ChatRoom getRoom()
    {
        return m_room;
    }
}
