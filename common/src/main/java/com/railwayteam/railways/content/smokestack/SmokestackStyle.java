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

package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.TextUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SmokestackStyle implements StringRepresentable, BlockStateBlockItemGroup.IStyle<SmokestackStyle.Context> {
    STEEL(Material.STEEL),
    BRASS_CAP_STEEL(Material.STEEL, Material.BRASS),
    COPPER_CAP_STEEL(Material.STEEL, Material.COPPER),
    IRON_CAP_STEEL(Material.STEEL, Material.IRON),

    BRASS(Material.BRASS),
    COPPER_CAP_BRASS(Material.BRASS, Material.COPPER),
    IRON_CAP_BRASS(Material.BRASS, Material.IRON),

    COPPER(Material.COPPER),
    BRASS_CAP_COPPER(Material.COPPER, Material.BRASS),
    IRON_CAP_COPPER(Material.COPPER, Material.IRON);

    private final String model;
    private final String segmentModel;
    private final String langName;

    SmokestackStyle(Material material) {
        assert !material.capOnly : "Material " + material.lang() + " is cap only and cannot be used for a full smokestack style!";

        this.model = material.id();
        this.segmentModel = material.id();
        this.langName = material.lang();
    }

    SmokestackStyle(Material material, Material capMaterial) {
        assert !material.capOnly : "Material " + material.lang() + " is cap only and cannot be used for a full smokestack style!";
        assert material != capMaterial : "Material " + material.lang() + " cannot be used as both the cap and body material!";

        this.model = capMaterial.id() + "_cap_" + material.id();
        this.segmentModel = material.id();
        this.langName = TextUtils.titleCaseConversion(capMaterial.lang() + " Capped " + material.lang());
    }

    @Override
    public ResourceLocation getModel(Context context) {
        return Railways.asResource("block/" + context.prefix + model + context.modelSuffix);
    }

    public ResourceLocation getTexture(String variant) {
        if (!variant.equals("caboosestyle"))
            return Railways.asResource("block/smokestack/" + variant + "/" + model);
        return Railways.asResource("block/smokestack/caboosestyle");
    }

    public ResourceLocation getSegmentTexture(String variant) {
        return Railways.asResource("block/smokestack/" + variant + "/segment_" + segmentModel);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getLangName(Context context) {
        return langName + " " + TextUtils.titleCaseConversion(context.description);
    }

    @Override
    public String getBlockId(Context context) {
        return context.prefix + model;
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

    private enum Material {
        BRASS(false),
        COPPER(false),
        STEEL(false),
        IRON(true);

        public final boolean capOnly;

        Material(boolean capOnly) {
            this.capOnly = capOnly;
        }

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        public String lang() {
            return TextUtils.titleCaseConversion(name());
        }
    }

    public record Context(String prefix, String description, String modelSuffix) {}
}
