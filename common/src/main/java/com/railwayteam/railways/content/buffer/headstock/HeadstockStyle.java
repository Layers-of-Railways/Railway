package com.railwayteam.railways.content.buffer.headstock;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum HeadstockStyle implements StringRepresentable, BlockStateBlockItemGroup.IStyle<Boolean> {
    PLAIN("headstock", "Headstock"),
    BUFFER("headstock_buffer", "Headstock (Buffer)"),
    LINK("headstock_link_and_pin", "Headstock (Link 'n Pin)"),
    LINKLESS("headstock_link_and_pin_linkless", "Headstock (Linkless Link 'n Pin)"),
    KNUCKLE("headstock_knuckle_coupler", "Headstock (Knuckle Coupler)"),
    KNUCKLE_SPLIT("headstock_split_knuckle_coupler", "Headstock (Split Knuckle Coupler)");

    private final String model;
    private final String langName;

    HeadstockStyle(String model, String langName) {
        this.model = model;
        this.langName = langName;
    }

    public ResourceLocation getModel(boolean copycat) {
        return Railways.asResource("block/buffer/headstock/" + (copycat ? "copycat_" : "wooden_") + model);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getLangName(Boolean copycat) {
        return (copycat ? "Copycat " : "Wooden ") + langName;
    }

    @Override
    public ResourceLocation getModel(Boolean context) {
        return getModel((boolean) context);
    }

    @Override
    public String getBlockId(Boolean context) {
        return (context ? "copycat_" : "wooden_") + model;
    }
}
