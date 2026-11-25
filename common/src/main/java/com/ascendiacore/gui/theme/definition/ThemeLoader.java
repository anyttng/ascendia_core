package com.ascendiacore.gui.theme.definition;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

import com.ascendiacore.AscendiaCore;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.definition.model.ThemeDefinition;
import com.ascendiacore.gui.theme.definition.model.ThemeSource;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

public final class ThemeLoader {
    public static final String FOLDER = AscendiaCore.MODID + "/themes";

    private ThemeLoader() {
    }

    public static Optional<Theme> load(String id, List<ThemeSource> stackTopFirst) {
        for (ThemeSource source : stackTopFirst) {
            DataResult<Theme> theme = read(id, source);
            if (theme.isSuccess()) {
                return theme.result();
            }
            theme.error().ifPresent(error -> AscendiaCore.LOGGER.warn("Theme {} from pack {} is skipped: {}", id,
                    source.packId(), error.message()));
        }
        return Optional.empty();
    }

    public static DataResult<Theme> read(String id, ThemeSource source) {
        try (Reader reader = source.reader().get()) {
            return ThemeDefinition.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                    .map(definition -> ThemeResolver.resolve(id, definition));
        } catch (IOException | JsonParseException e) {
            return DataResult.error(e::getMessage);
        }
    }
}
