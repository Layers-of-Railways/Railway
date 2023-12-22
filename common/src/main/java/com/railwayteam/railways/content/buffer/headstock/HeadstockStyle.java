package com.railwayteam.railways.content.buffer.headstock;

import com.railwayteam.railways.Railways;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum HeadstockStyle implements StringRepresentable {
    PLAIN("headstock"),
    BUFFER("headstock_buffer"),
    LINK("headstock_link_and_pin"),
    LINKLESS("headstock_link_and_pin_linkless"),
    KNUCKLE("headstock_knuckle_coupler"),
    KNUCKLE_SPLIT("headstock_split_knuckle_coupler");

    private final String model;

    HeadstockStyle(String model) {
        this.model = model;
    }

    public ResourceLocation getModel(boolean copycat) {
        return Railways.asResource("block/buffer/headstock/" + (copycat ? "copycat_" : "wooden_") + model);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
