package com.progoth.synchrochat.client.gui.widgets;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import com.google.gwt.core.client.GWT;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.events.UserListDisplayEvent;
import com.progoth.synchrochat.shared.model.SynchroUser;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

public class PersonListPanel extends ContentPanel implements UserListDisplayEvent.Handler
{
    private static final SynchroUser.Properties sm_props = GWT.create(SynchroUser.Properties.class);

    private ListStore<SynchroUser> m_users;

    public PersonListPanel()
    {
        setHeadingText("Users");

        final SimpleContainer sc = new SimpleContainer();

        createList(sc);

        add(sc);

        SynchroBus.get().addHandler(UserListDisplayEvent.TYPE, this);
    }

    private void createList(final SimpleContainer aContainer)
    {
        m_users = new ListStore<SynchroUser>(sm_props.key());

        final List<ColumnConfig<SynchroUser, ?>> colConfigs = new LinkedList<ColumnConfig<SynchroUser, ?>>();
        ColumnConfig<SynchroUser, ?> col;

        col = new ColumnConfig<SynchroUser, String>(sm_props.name(), 150, "Name");
        final ColumnConfig<SynchroUser, ?> nameCol = col;
        colConfigs.add(col);

        final ColumnModel<SynchroUser> cm = new ColumnModel<SynchroUser>(colConfigs);

        final Grid<SynchroUser> grid = new Grid<SynchroUser>(m_users, cm);
        grid.getView().setAutoExpandColumn(nameCol);
        grid.getView().setForceFit(true);
        grid.setBorders(false);
        grid.setHideHeaders(true);
        grid.setStripeRows(true);
        grid.setSelectionModel(new GridSelectionModel<SynchroUser>());
        grid.addRowDoubleClickHandler(new RowDoubleClickHandler()
        {
            @Override
            public void onRowDoubleClick(final RowDoubleClickEvent aEvent)
            {
                // fire IM event
            }
        });

        aContainer.add(grid);
    }

    @Override
    public void displayUserList(final SortedSet<SynchroUser> aUserList)
    {
        m_users.clear();
        if (aUserList != null)
        {
            m_users.addAll(aUserList);
        }
    }
}
