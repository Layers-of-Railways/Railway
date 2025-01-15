/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.TextUtils;
import net.createmod.catnip.data.Couple;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SmokestackStyle implements StringRepresentable, BlockStateBlockItemGroup.IStyle<Couple<String>> {
    STEEL("steel", "Steel"),
    BRASS_CAP_STEEL("brass_cap_steel", "Brass Capped Steel"),
    COPPER_CAP_STEEL("copper_cap_steel", "Copper Capped Steel"),
    BRASS("brass", "Brass"),
    COPPER_CAP_BRASS("copper_cap_brass", "Copper Capped Brass"),
    COPPER("copper", "Copper"),
    BRASS_CAP_COPPER("brass_cap_copper", "Brass Capped Copper");

    private final String model;
    private final String langName;

    SmokestackStyle(String model, String langName) {
        this.model = model;
        this.langName = langName;
    }

    @Override
    public ResourceLocation getModel(Couple<String> context) {
        return Railways.asResource("block/" + context.getFirst() + model);
    }

    public ResourceLocation getTexture(String variant) {
        if (!variant.equals("caboosestyle"))
            return Railways.asResource("block/smokestack/" + variant + "/" + model);
        return Railways.asResource("block/smokestack/caboosestyle");
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getLangName(Couple<String> context) {
        return langName + " " + TextUtils.titleCaseConversion(context.getSecond());
    }

    @Override
    public String getBlockId(Couple<String> context) {
        return context.getFirst() + model;
    }

    public String getBlockId() {
        return model;
    }

    public static TagKey<Item> variantToTagKey(String variant) {
        return switch (variant) {
            case "caboosestyle" -> CRTags.AllItemTags.CABOOSESTYLE_STACK.tag;
            case "long" -> CRTags.AllItemTags.LONG_STACK.tag;
            case "coalburner" -> CRTags.AllItemTags.COALBURNER_STACK.tag;
            case "oilburner" -> CRTags.AllItemTags.OILBURNER_STACK.tag;
            case "streamlined" -> CRTags.AllItemTags.STREAMLINED_STACK.tag;
            case "woodburner" -> CRTags.AllItemTags.WOODBURNER_STACK.tag;
            default -> throw new IllegalArgumentException();
        };
    }
}
