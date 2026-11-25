package com.ascendiacore.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class GuiImportBoundaryTest {
    private static final String REPO_ROOT_PROPERTY = "ascendia_core.repoRoot";
    private static final String GUI_SOURCES = "common/src/main/java/com/ascendiacore/gui";
    private static final String JAVA_SUFFIX = ".java";
    private static final List<String> FORBIDDEN_PACKAGES = List.of("net.minecraft.client.gui", "com.mojang.blaze3d");

    @Test
    void noGuiSourceNamesAGameGuiClass() throws IOException {
        Path root = Path.of(System.getProperty(REPO_ROOT_PROPERTY)).resolve(GUI_SOURCES);
        List<Path> sources = findSources(root);
        assertFalse(sources.isEmpty(), "no sources under " + root);

        List<String> found = new ArrayList<>();
        for (Path source : sources) {
            List<String> lines = Files.readAllLines(source);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (FORBIDDEN_PACKAGES.stream().anyMatch(line::contains)) {
                    found.add(root.relativize(source) + ":" + (i + 1) + " " + line.strip());
                }
            }
        }
        assertEquals(List.of(), found);
    }

    private static List<Path> findSources(Path root) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(file -> file.toString().endsWith(JAVA_SUFFIX)).sorted().toList();
        }
    }
}
