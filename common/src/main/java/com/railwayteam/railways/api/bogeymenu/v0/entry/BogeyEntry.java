package com.railwayteam.railways.api.bogeymenu.v0.entry;

import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record BogeyEntry(@NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
    public static final Map<BogeyStyle, BogeyEntry> STYLE_TO_ENTRY = new HashMap<>();

    public static BogeyEntry getOrCreate(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        BogeyEntry entry = getOrCreate(bogeyStyle, iconLocation, scale);
        categoryEntry.addToBogeyEntryList(entry);
        return entry;
    }

    public static BogeyEntry getOrCreate(@NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        return STYLE_TO_ENTRY.computeIfAbsent(bogeyStyle, (bs) -> new BogeyEntry(bogeyStyle, iconLocation, scale));
    }

    @ApiStatus.Internal
    public BogeyEntry(@NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        this.bogeyStyle = bogeyStyle;
        this.iconLocation = iconLocation;
        this.scale = scale;
    }
}
