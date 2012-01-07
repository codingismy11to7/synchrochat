package com.progoth.synchrochat.client;

import java.util.ArrayList;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.progoth.synchrochat.client.events.RoomListReceivedEvent;
import com.progoth.synchrochat.shared.model.ChatRoom;

public class RoomListPanel extends DockLayoutPanel implements RoomListReceivedEvent.Handler
{
    private final CellList<ChatRoom> m_cellList;

    public RoomListPanel()
    {
        super(Unit.PX);

        SynchroController.get().addHandler(RoomListReceivedEvent.TYPE, this);

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.setSpacing(3);
        addNorth(horizontalPanel, 32.0);
        horizontalPanel.setSize("100%", "100%");

        final Button btnNew = new Button("New...");
        horizontalPanel.add(btnNew);

        m_cellList = new CellList<ChatRoom>(new AbstractCell<ChatRoom>()
        {
            @Override
            public void render(final Context context, final ChatRoom value, final SafeHtmlBuilder sb)
            {
                sb.appendHtmlConstant("<div class=\"chatRoomItem\">");
                sb.appendEscaped(value.getName());
                sb.appendHtmlConstant("<span style=\"font-weight:bold;\">");
                sb.appendEscaped(" (" + value.getUserCount() + ')');
                sb.appendHtmlConstant("</span>");
                sb.appendHtmlConstant("</div>");
            }
        });

        m_cellList.setSelectionModel(new SingleSelectionModel<ChatRoom>());

        ScrollPanel scroll = new ScrollPanel(m_cellList);

        add(scroll);
    }

    @Override
    public void onRoomListReceived(final RoomListReceivedEvent aEvt)
    {
        m_cellList.setRowCount(aEvt.getRoomList().size(), true);
        m_cellList.setRowData(new ArrayList<ChatRoom>(aEvt.getRoomList()));
    }
}
