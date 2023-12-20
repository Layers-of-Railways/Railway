package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BoilerGenerator extends SpecialBlockStateGen {
    protected final @Nullable DyeColor color;

    protected BoilerGenerator(@Nullable DyeColor color) {
        this.color = color;
    }

    @ExpectPlatform
    public static BoilerGenerator create(@Nullable DyeColor color) {
        throw new AssertionError();
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }
}
