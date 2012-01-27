package com.progoth.synchrochat.shared;

public class AccessDeniedException extends Exception
{
    private static final long serialVersionUID = -3718155554947507148L;

    public AccessDeniedException()
    {
        super("Access Denied");
    }
}
