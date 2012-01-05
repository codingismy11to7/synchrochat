package com.progoth.synchrochat.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF
{
    private static final PersistenceManagerFactory sm_transOpt = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");
    private static final PersistenceManagerFactory sm_eventual = JDOHelper
        .getPersistenceManagerFactory("eventual-reads-short-deadlines");

    public static PersistenceManagerFactory getEventualReads()
    {
        return sm_eventual;
    }

    public static PersistenceManagerFactory getTransactionsOptional()
    {
        return sm_transOpt;
    }

    private PMF()
    {
        // singleton
    }
}
