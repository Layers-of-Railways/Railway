package com.railwayteam.railways.content.custom_tracks.casing.forge;

import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CasingCollisionBlockImpl extends CasingCollisionBlock {
    public CasingCollisionBlockImpl(Properties properties) {
        super(properties);
    }

    public static CasingCollisionBlock create(BlockBehaviour.Properties properties) {
        return new CasingCollisionBlockImpl(properties);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return true;
    }
}
