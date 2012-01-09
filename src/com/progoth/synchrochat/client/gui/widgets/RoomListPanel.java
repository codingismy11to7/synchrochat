package com.progoth.synchrochat.client.gui.widgets;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.progoth.synchrochat.client.events.RoomJoinRequestEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.shared.model.ChatRoom;
import com.progoth.synchrochat.shared.model.ChatRoomProperties;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class RoomListPanel extends ContentPanel
{
    private static final ChatRoomProperties sm_props = GWT.create(ChatRoomProperties.class);

    private ListStore<ChatRoom> m_rooms;

    public RoomListPanel()
    {
        setHeadingText("Room List");

        final BorderLayoutContainer container = new BorderLayoutContainer();
        container.setBorders(false);

        createTop(container);

        createCenter(container);

        add(container);
    }

    private void createCenter(final BorderLayoutContainer aContainer)
    {
        m_rooms = new ListStore<ChatRoom>(sm_props.key());
        ChatRoom room = new ChatRoom();
        room.setName("a room");
        room.setUserCount(500);
        m_rooms.add(room);
        room = new ChatRoom();
        room.setName("blah");
        room.setUserCount(0);
        m_rooms.add(room);
        room = new ChatRoom();
        room.setName("blah2");
        room.setUserCount(10);
        m_rooms.add(room);

        final List<ColumnConfig<ChatRoom, ?>> colConfigs = new LinkedList<ColumnConfig<ChatRoom, ?>>();
        ColumnConfig<ChatRoom, ?> col;

        col = new ColumnConfig<ChatRoom, String>(sm_props.name(), 150, "Name");
        final ColumnConfig<ChatRoom, ?> nameCol = col;
        colConfigs.add(col);

        col = new ColumnConfig<ChatRoom, Integer>(sm_props.userCount(), 30, "Users");
        col.setAlignment(HorizontalAlignmentConstant.endOf(Direction.DEFAULT));
        col.setFixed(true);
        colConfigs.add(col);

        final ColumnModel<ChatRoom> cm = new ColumnModel<ChatRoom>(colConfigs);

        final Grid<ChatRoom> grid = new Grid<ChatRoom>(m_rooms, cm);
        grid.getView().setAutoExpandColumn(nameCol);
        grid.getView().setForceFit(true);
        grid.setBorders(false);
        grid.setHideHeaders(true);
        grid.setStripeRows(true);
        grid.setSelectionModel(new GridSelectionModel<ChatRoom>());
        grid.addRowDoubleClickHandler(new RowDoubleClickHandler()
        {
            @Override
            public void onRowDoubleClick(final RowDoubleClickEvent aEvent)
            {
                SynchroBus.get().fireEvent(
                    new RoomJoinRequestEvent(grid.getSelectionModel().getSelectedItem()));
            }
        });

        aContainer.setCenterWidget(grid, new MarginData(0));
    }

    private void createTop(final BorderLayoutContainer aContainer)
    {
        final SplitButton newButton = new SplitButton("New Room");
        newButton.addSelectHandler(new SelectHandler()
        {
            @Override
            public void onSelect(final SelectEvent aEvent)
            {
                onNewRoomClick(false);
            }
        });
        final MenuItem withPw = new MenuItem("With Password", SynchroImages.get().lock_add());
        withPw.addSelectionHandler(new SelectionHandler<Item>()
        {
            @Override
            public void onSelection(final SelectionEvent<Item> aEvent)
            {
                onNewRoomClick(true);
            }
        });
        final Menu newBtnMenu = new Menu();
        newBtnMenu.add(withPw);
        newButton.setMenu(newBtnMenu);

        newButton.setIcon(SynchroImages.get().add());

        final HBoxLayoutContainer btnContainer = new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
        btnContainer.setPack(BoxLayoutPack.START);

        btnContainer.add(newButton);

        final BorderLayoutData bld = new BorderLayoutData(32);
        bld.setMargins(new Margins(5));
        aContainer.setNorthWidget(btnContainer, bld);
    }

    private void onNewRoomClick(final boolean aWithPassword)
    {
        final KbPromptMessageBox nameDialog = new KbPromptMessageBox("New Room", "Name of room:");
        nameDialog.addHideHandler(new HideHandler()
        {
            @Override
            public void onHide(final HideEvent aEvent)
            {
                if (nameDialog.getHideButton() != nameDialog.getButtonById(PredefinedButton.OK
                    .name()))
                    return;

                String roomName = nameDialog.getTextFieldValue();
                if (roomName != null)
                {
                    roomName = roomName.trim();
                    if (!roomName.isEmpty())
                    {
                        if (aWithPassword)
                        {
                            requestPassword(roomName);
                        }
                        else
                        {
                            SynchroBus.get().fireEvent(new NewRoomInputEvent(roomName));
                        }
                    }
                }
            }
        });
        nameDialog.show();
        nameDialog.grabFocus();
    }

    private void requestPassword(final String aRoomName)
    {
        final KbPromptMessageBox pwDialog = new KbPromptMessageBox("Room Password",
                "Enter password for new room " + aRoomName);
        pwDialog.addHideHandler(new HideHandler()
        {

            @Override
            public void onHide(final HideEvent aEvent)
            {
                if (pwDialog.getHideButton() != pwDialog.getButtonById(PredefinedButton.OK.name()))
                    return;

                final String pw = pwDialog.getTextFieldValue();
                if (pw != null && !pw.isEmpty())
                {
                    SynchroBus.get().fireEvent(new NewRoomInputEvent(aRoomName, pw));
                }
            }
        });
        pwDialog.show();
        pwDialog.grabFocus();
    }
}
