package com.ascendia.core.compat.fixture.shared;

import com.ascendia.core.compat.fixture.mod.FixtureModApi;

public final class FixtureSharedSecond {
    public static int read(FixtureModApi api) {
        return api.answer();
    }

    private FixtureSharedSecond() {
    }
}
