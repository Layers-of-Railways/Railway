package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.registry.CRSounds;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

public class ConductorWhistleItem extends Item {

    public ConductorWhistleItem(Item.Properties properties) {
        super(properties);
    }

    static boolean isTrack (Block block) { return CRTags.AllBlockTags.TRACKS.matches(block); }
    static boolean isTrack (BlockState state) { return CRTags.AllBlockTags.TRACKS.matches(state); }
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


        }
        return super.useOn(ctx);
    }



    @SubscribeEvent
    public static void interactWithConductor(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null)
            return;
        if (player.isSpectator())
            return;

        Entity rootVehicle = entity.getRootVehicle();
        if (!(rootVehicle instanceof CarriageContraptionEntity))
            return;
        if (!(entity instanceof LivingEntity living))
            return;

    }
}



