package com.ascendiacore.gui.theme;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import com.ascendiacore.gui.theme.definition.ThemeLoader;
import com.ascendiacore.gui.theme.definition.model.ThemeSource;

public final class TestThemes {
    private static final String TEST_PACK = "test";
    private static final String NAMESPACE_SEPARATOR = ":";

    public static final Theme BOOK = load(Themes.BOOK);
    public static final Theme VANILLA = load(Themes.VANILLA);

    private TestThemes() {
    }

    public static ThemeSource source(String id) {
        return new ThemeSource(TEST_PACK, () -> new InputStreamReader(open(id), StandardCharsets.UTF_8));
    }

    public static String text(String id) {
        try (InputStream stream = open(id)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Theme load(String id) {
        return ThemeLoader.read(id, source(id)).getOrThrow();
    }

    private static InputStream open(String id) {
        String[] parts = id.split(NAMESPACE_SEPARATOR, 2);
        String path = "/assets/" + parts[0] + "/" + ThemeLoader.FOLDER + "/" + parts[1] + ".json";
        InputStream stream = TestThemes.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("No theme file at " + path);
        }
        return stream;
    }
}
