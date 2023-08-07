package com.railwayteam.railways.content.custom_tracks.casing.fabric;

import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CasingCollisionBlockImpl extends CasingCollisionBlock implements CustomLandingEffectsBlock, CustomRunningEffectsBlock {
    public CasingCollisionBlockImpl(Properties properties) {
        super(properties);
    }

    public static CasingCollisionBlock create(Properties properties) {
        return new CasingCollisionBlockImpl(properties);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }
}
