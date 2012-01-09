package com.progoth.synchrochat.client.gui.widgets;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.progoth.synchrochat.shared.model.User;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

public class PersonListPanel extends ContentPanel
{
    private static final User.Properties sm_props = GWT.create(User.Properties.class);

    private ListStore<User> m_users;

    public PersonListPanel()
    {
        setHeadingText("Users");

        SimpleContainer sc = new SimpleContainer();

        createList(sc);

        add(sc);
    }

    private void createList(SimpleContainer aContainer)
    {
        m_users = new ListStore<User>(sm_props.key());
        User user = new User();
        user.setName("user1");
        m_users.add(user);
        user = new User();
        user.setName("blahuser");
        m_users.add(user);
        user = new User();
        user.setName("n00b");
        m_users.add(user);

        final List<ColumnConfig<User, ?>> colConfigs = new LinkedList<ColumnConfig<User, ?>>();
        ColumnConfig<User, ?> col;

        col = new ColumnConfig<User, String>(sm_props.name(), 150, "Name");
        final ColumnConfig<User, ?> nameCol = col;
        colConfigs.add(col);

        final ColumnModel<User> cm = new ColumnModel<User>(colConfigs);

        final Grid<User> grid = new Grid<User>(m_users, cm);
        grid.getView().setAutoExpandColumn(nameCol);
        grid.getView().setForceFit(true);
        grid.setBorders(false);
        grid.setHideHeaders(true);
        grid.setStripeRows(true);
        grid.setSelectionModel(new GridSelectionModel<User>());
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
}
