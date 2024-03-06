package com.railwayteam.railways.content.palettes.boiler.forge;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.ForgeMod;

public class BoilerBlockPlacementHelperImpl {
    public static Attribute getAttribute() {
        return ForgeMod.BLOCK_REACH.get();
    }
}
