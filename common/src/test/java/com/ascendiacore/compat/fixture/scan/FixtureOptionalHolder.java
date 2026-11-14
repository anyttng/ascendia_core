package com.ascendiacore.compat.fixture.scan;

import com.ascendiacore.compat.fixture.mod.FixtureModLater;

public final class FixtureOptionalHolder {
    public static Object create() {
        return new FixtureModLater();
    }

    private FixtureOptionalHolder() {
    }
}
