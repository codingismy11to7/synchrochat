package com.progoth.synchrochat.client.gui.widgets;

import com.google.gwt.user.client.ui.Label;
import com.progoth.synchrochat.client.SynchroController;
import com.progoth.synchrochat.client.events.NewRoomInputEvent;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class RoomListPanel extends ContentPanel
{
    public RoomListPanel()
    {
        setHeadingText("Room List");
        // setHeaderVisible(true);
        // setHideCollapseTool(false);
        // setBodyBorder(true);
        // setBorders(true);

        final VBoxLayoutContainer container = new VBoxLayoutContainer(VBoxLayoutAlign.LEFT);
        container.setPadding(new Padding(5));
        container.setPack(BoxLayoutPack.START);

        createTop(container);

        // container.setCenterWidget(new Label("o hai"));
        container.add(new Label("o hai"));

        add(container);
    }

    private void createTop(final VBoxLayoutContainer aContainer)
    {
        final TextButton newButton = new TextButton("New Room...", new SelectHandler()
        {
            @Override
            public void onSelect(final SelectEvent aEvent)
            {
                final PromptMessageBox nameDialog = new KbPromptMessageBox("New Room",
                        "Name of room:");
                nameDialog.addHideHandler(new HideHandler()
                {
                    @Override
                    public void onHide(final HideEvent aEvent)
                    {
                        String roomName = nameDialog.getValue();
                        if (roomName != null)
                        {
                            roomName = roomName.trim();
                            if (!roomName.isEmpty())
                            {
                                SynchroController.get().fireEvent(new NewRoomInputEvent(roomName));
                            }
                        }
                    }
                });
                nameDialog.show();
            }
        });

        // HBoxLayoutContainer btnContainer = new HBoxLayoutContainer(HBoxLayoutAlign.MIDDLE);
        // btnContainer.setPadding(new Padding(5));
        // btnContainer.setPack(BoxLayoutPack.START);

        // btnContainer.add(newButton);

        // aContainer.add(btnContainer);
        aContainer.add(newButton);
    }
}
