package com.ascendiacore.compat.fixture.shared;

import com.ascendiacore.compat.fixture.mod.FixtureModApi;

public final class FixtureSharedSecond {
    public static int read(FixtureModApi api) {
        return api.answer();
    }

    private FixtureSharedSecond() {
    }
}
