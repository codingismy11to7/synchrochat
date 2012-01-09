package com.progoth.synchrochat.client.gui.widgets;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
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
import com.sencha.gxt.widget.core.client.form.HtmlEditor;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class ChatPanel extends BorderLayoutContainer
{
    public ChatPanel()
    {
        createBottom();
        createCenter();
    }

    private void createBottom()
    {
        final HtmlEditor input = new HtmlEditor();

        final BorderLayoutData bld = new BorderLayoutData(100);
        bld.setMargins(new Margins(5, 0, 0, 0));
        bld.setCollapsible(false);
        bld.setSplit(true);
        bld.setMinSize(65);

        final TextButton send = new TextButton("Send", new SelectHandler()
        {
            @Override
            public void onSelect(final SelectEvent aEvent)
            {
                input.setValue(null);
            }
        });
        send.setIcon(SynchroImages.get().font_go());
        send.setIconAlign(IconAlign.TOP);

        final HBoxLayoutContainer horiz = new HBoxLayoutContainer(HBoxLayoutAlign.TOP);
        final BoxLayoutData flex = new BoxLayoutData(new Margins());
        flex.setFlex(1);
        horiz.add(input, flex);
        horiz.add(send, new BoxLayoutData(new Margins(0, 5, 0, 5)));

        horiz.addResizeHandler(new ResizeHandler()
        {
            @Override
            public void onResize(ResizeEvent aEvent)
            {
                horiz.forceLayout();
                send.redraw();
            }
        });

        setSouthWidget(horiz, bld);
    }

    private void createCenter()
    {
        final TextArea output = new TextArea();
        output.setText("lorem ipsum");

        setCenterWidget(output, new MarginData(new Margins()));
    }
}
