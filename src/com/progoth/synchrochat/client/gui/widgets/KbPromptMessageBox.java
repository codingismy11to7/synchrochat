package com.progoth.synchrochat.client.gui.widgets;

import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;

public class KbPromptMessageBox extends PromptMessageBox
{
    public KbPromptMessageBox(final String aTitle, final String aMessage)
    {
        super(aTitle, aMessage);

        setIcon(ICONS.question());

        setOnEsc(true);
        setClosable(true);

        new KeyNav(getTextField())
        {
            @Override
            public void onEnter(final NativeEvent aEvt)
            {
                aEvt.preventDefault();
                aEvt.stopPropagation();

                hide(getButtonById(PredefinedButton.OK.name()));
            }
        };
    }

    public String getTextFieldValue()
    {
        return getTextField().getText();
    }

    public void grabFocus()
    {
        focus();
        getTextField().focus();
    }
}
