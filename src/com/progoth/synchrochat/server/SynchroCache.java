package com.progoth.synchrochat.server;

import java.util.Collections;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public class SynchroCache
{
    private static Cache sm_cache;
    static
    {
        CacheFactory fact;
        try
        {
            fact = CacheManager.getInstance().getCacheFactory();
            sm_cache = fact.createCache(Collections.emptyMap());
        }
        catch (final CacheException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <X> X get(final Object aKey)
    {
        if (!sm_cache.containsKey(aKey))
            return null;
        return (X)getCache().get(aKey);
    }

    public static Cache getCache()
    {
        return sm_cache;
    }

    private SynchroCache()
    {
        // singleton
    }
}
