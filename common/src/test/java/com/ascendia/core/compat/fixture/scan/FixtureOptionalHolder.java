package com.ascendia.core.compat.fixture.scan;

import com.ascendia.core.compat.fixture.mod.FixtureModLater;

public final class FixtureOptionalHolder {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureOptionalHolder() {
    }
}
