package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoilerGenerator extends SpecialBlockStateGen {
    private final @Nullable DyeColor color;

    public BoilerGenerator(@Nullable DyeColor color) {
        this.color = color;
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return getModelStatic(ctx, prov, state, color);
    }

    @ExpectPlatform
    private static <T extends Block> ModelFile getModelStatic(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state, DyeColor color) {
        throw new AssertionError();
    }
}
