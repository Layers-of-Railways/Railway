package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.BlockStateBlockItemGroup;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.foundation.utility.Couple;
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
