package com.ascendia.core.compat.fixture.scan.nested;

import com.ascendia.core.compat.fixture.mod.FixtureModLater;

public final class FixtureNestedCaller {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureNestedCaller() {
    }
}
