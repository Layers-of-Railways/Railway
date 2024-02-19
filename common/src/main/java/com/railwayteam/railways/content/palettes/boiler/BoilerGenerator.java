package com.railwayteam.railways.content.palettes.boiler;

import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BoilerGenerator extends SpecialBlockStateGen {
    protected final @Nullable DyeColor color;
    protected final @Nullable Wrapping wrapping;

    protected BoilerGenerator(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        this.color = color;
        this.wrapping = wrapping;
    }

    @ExpectPlatform
    public static BoilerGenerator create(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
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
