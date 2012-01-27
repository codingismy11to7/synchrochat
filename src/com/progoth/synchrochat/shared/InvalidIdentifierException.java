package com.progoth.synchrochat.shared;

public class InvalidIdentifierException extends Exception
{
    private static final long serialVersionUID = 4072771293139310251L;

    public InvalidIdentifierException()
    {
        
    }

    public InvalidIdentifierException(final String aMsg)
    {
        super(aMsg);
    }
}
