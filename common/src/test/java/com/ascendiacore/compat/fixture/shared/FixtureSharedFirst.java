package com.ascendiacore.compat.fixture.shared;

import com.ascendiacore.compat.fixture.mod.FixtureModLater;

public final class FixtureSharedFirst {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureSharedFirst() {
    }
}
