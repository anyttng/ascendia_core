package com.ascendiacore.gui.theme;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.ascendiacore.AscendiaCore;
import com.ascendiacore.gui.theme.definition.ThemeLoader;
import com.ascendiacore.gui.theme.definition.model.ThemeDefinition;
import com.ascendiacore.gui.theme.definition.model.ThemeSource;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

public final class TestThemes {
    private static final String TEST_PACK = "test";
    private static final String NAMESPACE_SEPARATOR = ":";
    private static final String FILE_SUFFIX = ".json";

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

    public static List<String> listOwnIds() {
        String folderPath = folderPath(AscendiaCore.MODID);
        URL folder = TestThemes.class.getResource(folderPath);
        if (folder == null) {
            throw new IllegalStateException("No theme folder at " + folderPath);
        }
        try (Stream<Path> files = Files.list(Path.of(folder.toURI()))) {
            return files.map(file -> file.getFileName().toString())
                    .filter(name -> name.endsWith(FILE_SUFFIX))
                    .map(name -> AscendiaCore.MODID + NAMESPACE_SEPARATOR
                            + name.substring(0, name.length() - FILE_SUFFIX.length()))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ThemeDefinition readDefinition(String id) {
        return ThemeDefinition.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(text(id))).getOrThrow();
    }

    private static Theme load(String id) {
        return ThemeLoader.read(id, source(id)).getOrThrow();
    }

    private static InputStream open(String id) {
        String[] parts = id.split(NAMESPACE_SEPARATOR, 2);
        String path = folderPath(parts[0]) + "/" + parts[1] + FILE_SUFFIX;
        InputStream stream = TestThemes.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("No theme file at " + path);
        }
        return stream;
    }

    private static String folderPath(String namespace) {
        return "/assets/" + namespace + "/" + ThemeLoader.FOLDER;
    }
}
