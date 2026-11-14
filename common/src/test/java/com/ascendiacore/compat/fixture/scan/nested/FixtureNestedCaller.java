package com.ascendiacore.compat.fixture.scan.nested;

import com.ascendiacore.compat.fixture.mod.FixtureModLater;

public final class FixtureNestedCaller {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureNestedCaller() {
    }
}
