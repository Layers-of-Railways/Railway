package com.railwayteam.railways.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface AccessorCreativeModeTabs {
    @Accessor
    static void setCACHED_PARAMETERS(@Nullable CreativeModeTab.ItemDisplayParameters parameters) {
        throw new AssertionError("Should be mixed in");
    }
}
