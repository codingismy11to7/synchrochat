package com.progoth.synchrochat.client.gui.widgets;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.sencha.gxt.widget.core.client.form.HtmlEditor;

public class SynchroHtmlEditor extends HtmlEditor
{
    public static interface EditSendHandler
    {
        void onSendRequested(SynchroHtmlEditor source);
    }

    private final List<EditSendHandler> m_lists = new LinkedList<SynchroHtmlEditor.EditSendHandler>();

    public SynchroHtmlEditor()
    {
        textArea.addKeyDownHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(final KeyDownEvent aEvent)
            {
                if (aEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER && !aEvent.isAnyModifierKeyDown())
                {
                    aEvent.stopPropagation();
                    aEvent.preventDefault();
                    onSend();
                }
            }
        });
    }

    public void addListener(final EditSendHandler aList)
    {
        m_lists.add(aList);
    }

    protected void onSend()
    {
        for (final EditSendHandler list : m_lists)
        {
            list.onSendRequested(this);
        }
    }

    public void removeListener(final EditSendHandler aList)
    {
        m_lists.remove(aList);
    }
}
