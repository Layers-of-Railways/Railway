package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.mixin.AccessorTrackTargetingBehavior;
import com.railwayteam.railways.registry.CRSounds;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.TrackGraphHelper;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem;
import com.simibubi.create.content.logistics.trains.track.BezierTrackPointLocation;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.List;

import static com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem.withGraphLocation;

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
        BlockState state = level.getBlockState(pos);
        ItemStack stack = ctx.getItemInHand();

        if (state.getBlock() instanceof ITrackBlock track) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;

            Vec3 lookAngle = player.getLookAngle();
            boolean front = track.getNearestTrackAxis(level, pos, state, lookAngle)
                    .getSecond() == Direction.AxisDirection.POSITIVE;
            EdgePointType<?> type = getType(stack);

            MutableObject<TrackTargetingBlockItem.OverlapResult> result = new MutableObject<>(null);
            withGraphLocation(level, pos, front, null, type, (overlap, location) -> result.setValue(overlap));

            if (result.getValue().feedback != null) {
                player.displayClientMessage(Lang.translateDirect(result.getValue().feedback)
                        .withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, null, pos, .5f, 1);
                return InteractionResult.FAIL;
            }

            CompoundTag stackTag = stack.getOrCreateTag();
            stackTag.put("SelectedPos", NbtUtils.writeBlockPos(pos));
            stackTag.putBoolean("SelectedDirection", front);
            player.displayClientMessage(Lang.translateDirect("whistle.used"), true);
            stack.setTag(stackTag);
            level.playSound(null, pos, CRSounds.CONDUCTOR_WHISTLE.get(), SoundSource.BLOCKS, 2f, 1f);

            List<Vec3> trackAxes = track.getTrackAxes(level, pos, state);
            Direction.AxisDirection targetDirection = AccessorTrackTargetingBehavior.getTargetDirection();
            GraphLocation loc = TrackGraphHelper.getGraphLocationAt(level, pos,targetDirection,trackAxes.get(0));
            loc.graph.addPoint(edgePointType, point);
            return InteractionResult.SUCCESS;
        }

//        if (isTrack(level, pos)) {
//            level.playSound(null, pos, CRSounds.CONDUCTOR_WHISTLE.get(),
//                    SoundSource.BLOCKS, 2f, 1f);
//
//
//
//
//        public BezierTrackPointLocation getTargetBezier() {return targetBezier;}
//
//        public GraphLocation determineGraphLocation() {
//            BlockState state = getTrackBlockState();
//            ITrackBlock track = getTrack();
//            List<Vec3> trackAxes = track.getTrackAxes(level, pos, state);
//            Direction.AxisDirection targetDirection = AccessorTrackTargetingBehavior.getTargetDirection();
//
//            return targetBezier != null
//                    ? TrackGraphHelper.getBezierGraphLocationAt(level, pos, targetDirection, targetBezier)
//                    : TrackGraphHelper.getGraphLocationAt(level, pos, targetDirection, trackAxes.get(0));
//        }

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



