package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;
import com.railwayteam.railways.mixin.AccessorIngredient_TagValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class TFCTrackCompat extends GenericTrackCompat {
    TFCTrackCompat() {
        super(Mods.TFC);
    }

    @Override
    protected Ingredient getIngredientForRail() {
        return Ingredient.fromValues(Stream.of(
                AccessorIngredient_TagValue.railways$create(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/wrought_iron"))),
                AccessorIngredient_TagValue.railways$create(TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods/zinc")))
        ));
    }

    @Override
    protected ResourceLocation getSlabLocation(String name) {
        return asResource("wood/planks/" + name + "_slab");
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of TerraFirmaCraft track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for TerraFirmaCraft");
        new TFCTrackCompat().register(
            "acacia", "ash", "aspen", "birch", "blackwood",
                "chestnut", "douglas_fir", "hickory", "kapok", "mangrove",
                "maple", "oak", "palm", "pine", "rosewood",
                "sequoia", "spruce", "sycamore", "white_cedar", "willow"
        );
    }
}
