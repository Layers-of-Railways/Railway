package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.registry.CRSounds;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ConductorWhistleItem extends Item {

    public ConductorWhistleItem(Item.Properties properties) {
        super(properties);

    }
    static boolean isTrack (Block block) { return block.equals( AllBlocks.TRACK.get()); }
    static boolean isTrack (BlockState state) { return isTrack(state.getBlock()); }
    static boolean isTrack (Level level, BlockPos pos) { return isTrack(level.getBlockState(pos)); }

    @Nonnull
    @Override
    public InteractionResult useOn (UseOnContext ctx) {
        Level level  = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();

        if (isTrack(level, pos)) {
            level.playSound(null, pos, CRSounds.CONDUCTOR_WHISTLE.get(),
                    SoundSource.BLOCKS, 2f, 1f);
//            if (level.isClientSide) {
//                System.out.println("reached sound line");
//                level.playSound(null, pos, CRSounds.CONDUCTOR_WHISTLE.get(), SoundSource.BLOCKS, 2f, 1f);
//                //return InteractionResult.SUCCESS;
//            }
        }
        return super.useOn(ctx);
    }
}



