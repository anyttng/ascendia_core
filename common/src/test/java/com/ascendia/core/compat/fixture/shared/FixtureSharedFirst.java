package com.ascendia.core.compat.fixture.shared;

import com.ascendia.core.compat.fixture.mod.FixtureModLater;

public final class FixtureSharedFirst {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureSharedFirst() {
    }
}
