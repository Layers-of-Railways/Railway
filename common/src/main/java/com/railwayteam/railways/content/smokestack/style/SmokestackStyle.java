package com.railwayteam.railways.content.smokestack.style;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SmokestackStyle implements StringRepresentable, BlockStateBlockItemGroup.IStyle<Boolean> {
    STEEL("steel", "Steel"),
    BRASS_CAP_STEEL("brass_cap_steel", "Brass Cap Steel"),
    COPPER_CAP_STEEL("copper_cap_steel", "Copper Cap Steel"),
    BRASS("brass", "Brass"),
    COPPER_CAP_BRASS("copper_cap_brass", "Copper Cap Brass"),
    COPPER("copper", "Copper"),
    BRASS_CAP_COPPER("brass_cap_copper", "Brass Cap Copper");

    private final String model;
    private final String langName;

    SmokestackStyle(String model, String langName) {
        this.model = model;
        this.langName = langName;
    }

    public ResourceLocation getModel() {
        return Railways.asResource("block/buffer/headstock/" + model);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getLangName(Boolean copycat) {
        return langName;
    }

    @Override
    public ResourceLocation getModel(Boolean context) {
        return getModel();
    }

    @Override
    public String getBlockId(Boolean context) {
        return model;
    }
}
