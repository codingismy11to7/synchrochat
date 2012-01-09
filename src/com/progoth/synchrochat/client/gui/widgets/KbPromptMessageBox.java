package com.progoth.synchrochat.client.gui.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.AddEvent.AddHandler;

public class KbPromptMessageBox extends PromptMessageBox
{
    public KbPromptMessageBox(final String aTitle, final String aMessage)
    {
        super(aTitle, aMessage);

        setIcon(ICONS.question());

        setOnEsc(true);
        setClosable(true);
        getTextField().addKeyUpHandler(new KeyUpHandler()
        {
            @Override
            public void onKeyUp(final KeyUpEvent aEvent)
            {
                if (aEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                {
                    hide(getButtonById(PredefinedButton.OK.name()));
                }
            }
        });

        addAddHandler(new AddHandler()
        {
            @Override
            public void onAdd(final AddEvent aEvent)
            {
                Scheduler.get().scheduleDeferred(new ScheduledCommand()
                {
                    @Override
                    public void execute()
                    {
                        focusBox();
                    }
                });
            }
        });
    }

    protected void focusBox()
    {
        focus();
        getTextField().focus();
    }

    // hack until they fix this, whatever it is that's wrong with it
    @Override
    protected void onKeyPress(final Event we)
    {
        final int keyCode = we.getKeyCode();

        if (keyCode == KeyCodes.KEY_ESCAPE && isClosable() && isOnEsc())
        {
            hide();
        }
        else
        {
            super.onKeyPress(we);
        }
    }
}
