package com.progoth.synchrochat.client.gui.widgets;

import com.progoth.synchrochat.client.events.LoginResponseReceivedEvent;
import com.progoth.synchrochat.client.events.SynchroBus;
import com.progoth.synchrochat.client.gui.resources.SynchroImages;
import com.progoth.synchrochat.shared.model.LoginResponse;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class MainTabPanel extends TabPanel
{
    public MainTabPanel()
    {
        setCloseContextMenu(true);
        setAnimScroll(true);
        setTabScroll(true);

        final TextArea overview = new TextArea();
        overview.setReadOnly(true);
        SynchroBus.get().addHandler(LoginResponseReceivedEvent.TYPE,
            new LoginResponseReceivedEvent.Handler()
            {
                @Override
                public void loginReceived(final LoginResponse aResponse)
                {
                    overview.setText(aResponse.getMessage());
                }
            });

        final TabItemConfig ovConf = new TabItemConfig("Overview", false);
        ovConf.setIcon(SynchroImages.get().information());
        add(overview, ovConf);

        final TabItemConfig tmp = new TabItemConfig("blah", true);
        tmp.setIcon(SynchroImages.get().comments());
        add(new ChatPanel(), tmp);
    }
}
