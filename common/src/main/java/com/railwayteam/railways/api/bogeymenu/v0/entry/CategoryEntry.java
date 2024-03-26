package com.railwayteam.railways.api.bogeymenu.v0.entry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntry {
    private final @NotNull Component name;
    private final @NotNull ResourceLocation id;
    private final @NotNull List<BogeyEntry> bogeyEntryList = new ArrayList<>();

    public CategoryEntry(@NotNull Component name, @NotNull ResourceLocation id) {
        this.name = name;
        this.id = id;
    }

    public @NotNull Component getName() {
        return name;
    }

    public @NotNull ResourceLocation getId() {
        return id;
    }

    public @NotNull List<BogeyEntry> getBogeyEntryList() {
        return bogeyEntryList;
    }

    @ApiStatus.Internal
    void addToBogeyEntryList(BogeyEntry entry) {
        bogeyEntryList.add(entry);
    }
}
