package com.tuning.tuningprototype.testutil;

import org.hibernate.collection.spi.LazyInitializable;

import java.util.ArrayList;

// A plain ArrayList that also implements Hibernate's LazyInitializable, reporting
// itself as never-initialized. Hibernate.isInitialized(Object) checks for exactly
// this interface (among proxy checks that don't apply to a plain collection), so
// setting one of these on an entity's collection field lets tests exercise the
// "lazy, not yet fetched" branch of Hibernate.isInitialized(...) without a real
// Hibernate session.
public class UninitializedList<T> extends ArrayList<T> implements LazyInitializable {

    @Override
    public boolean wasInitialized() {
        return false;
    }

    @Override
    public void forceInitialization() {
        throw new UnsupportedOperationException("not expected to be called in tests");
    }
}
