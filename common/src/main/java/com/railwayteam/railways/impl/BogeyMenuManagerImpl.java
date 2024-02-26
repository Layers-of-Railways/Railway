package com.railwayteam.railways.impl;

import com.railwayteam.railways.api.BogeyMenuManager;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BogeyMenuManagerImpl implements BogeyMenuManager {
    private static final Map<ResourceLocation, ResourceLocation> CATEGORIES = new HashMap<>();

    @Override
    public void registerCategory(ResourceLocation id, ResourceLocation iconLocation) {
        CATEGORIES.put(id, iconLocation);
    }

    @Override
    public void addToCategory(ResourceLocation categoryId, BogeyStyle bogeyStyle) {

    }
}
