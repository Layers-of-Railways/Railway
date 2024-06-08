/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
    KNUCKLE_SPLIT("headstock_split_knuckle_coupler", "Headstock (Split Knuckle Coupler)"),
    SCREWLINK("headstock_screwlink_coupler", "Headstock (Screwlink Coupler)");

    private final String model;
    private final String langName;

    HeadstockStyle(String model, String langName) {
        this.model = model;
        this.langName = langName;
    }

    public ResourceLocation getModel(boolean copycat, boolean upsideDown) {
        return Railways.asResource("block/buffer/headstock/" + (copycat ? "copycat_" : "wooden_") + model + (upsideDown ? "_upside_down" : ""));
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
        return getModel(context, false);
    }

    @Override
    public String getBlockId(Boolean context) {
        return (context ? "copycat_" : "wooden_") + model;
    }
}
