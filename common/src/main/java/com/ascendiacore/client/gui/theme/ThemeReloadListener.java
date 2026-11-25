package com.ascendiacore.client.gui.theme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ascendiacore.AscendiaCore;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.theme.definition.ThemeLoader;
import com.ascendiacore.gui.theme.definition.model.ThemeSource;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class ThemeReloadListener extends SimplePreparableReloadListener<Map<String, Theme>> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(AscendiaCore.MODID, "themes");
    private static final FileToIdConverter THEME_FILES = FileToIdConverter.json(ThemeLoader.FOLDER);

    @Override
    protected Map<String, Theme> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, Theme> themes = new HashMap<>();
        for (Map.Entry<Identifier, List<Resource>> file : THEME_FILES.listMatchingResourceStacks(manager).entrySet()) {
            String id = THEME_FILES.fileToId(file.getKey()).toString();
            List<ThemeSource> stackTopFirst = file.getValue().reversed().stream()
                    .map(resource -> new ThemeSource(resource.sourcePackId(), resource::openAsReader))
                    .toList();
            ThemeLoader.load(id, stackTopFirst).ifPresent(theme -> themes.put(id, theme));
        }
        return themes;
    }

    @Override
    protected void apply(Map<String, Theme> themes, ResourceManager manager, ProfilerFiller profiler) {
        Themes.replaceAll(themes);
    }
}
