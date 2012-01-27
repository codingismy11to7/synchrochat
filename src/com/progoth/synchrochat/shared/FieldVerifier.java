package com.progoth.synchrochat.shared;

import com.google.gwt.regexp.shared.RegExp;

public class FieldVerifier
{
    private static final RegExp sm_chatRegex = RegExp.compile("^[a-z0-9#_-]{2,20}$", "i");

    public static boolean validChatRoomName(final String aName)
    {
        return sm_chatRegex.test(aName);
    }
}
