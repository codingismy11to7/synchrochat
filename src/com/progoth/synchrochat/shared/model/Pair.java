package com.progoth.synchrochat.shared.model;

import java.io.Serializable;

public class Pair<A, B> implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -980411768146462171L;
    private A m_a;
    private B m_b;

    public Pair()
    {
        this(null, null);
    }

    public Pair(final A aA, final B aB)
    {
        m_a = aA;
        m_b = aB;
    }

    public A getA()
    {
        return m_a;
    }

    public B getB()
    {
        return m_b;
    }

    @SuppressWarnings("unused")
    private void setA(final A aA)
    {
        m_a = aA;
    }

    @SuppressWarnings("unused")
    private void setB(final B aB)
    {
        m_b = aB;
    }
}
