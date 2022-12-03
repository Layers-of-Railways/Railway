package com.railwayteam.railways.content.custom_tracks.casing;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;

public class SlabUseOnCurvePacket extends TileEntityConfigurationPacket<TrackTileEntity> {

  private BlockPos targetPos;
  private BlockPos soundSource;

  public SlabUseOnCurvePacket(BlockPos pos, BlockPos targetPos, BlockPos soundSource) {
    super(pos);
    this.targetPos = targetPos;
    this.soundSource = soundSource;
  }

  public SlabUseOnCurvePacket(FriendlyByteBuf buffer) {
    super(buffer);
  }

  @Override
  protected void writeSettings(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(targetPos);
    buffer.writeBlockPos(soundSource);
  }

  @Override
  protected void readSettings(FriendlyByteBuf buffer) {
    targetPos = buffer.readBlockPos();
    soundSource = buffer.readBlockPos();
  }

  private InteractionResult useOn(ServerPlayer player, InteractionHand hand, Level world, IHasTrackCasing casingAble) {
    if (world.isClientSide) return InteractionResult.FAIL;
    ItemStack handStack = player.getItemInHand(hand);
    if (handStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock) {
      SlabBlock currentCasing = casingAble.getTrackCasing();
      if (currentCasing == slabBlock) {
        casingAble.setAlternate(!casingAble.isAlternate());
        return InteractionResult.SUCCESS;
      } else {
        handStack.shrink(1);
        if (currentCasing != null) {
          ItemStack casingStack = new ItemStack(currentCasing);
          if (handStack.isEmpty()) {
            handStack = casingStack;
          } else if (!player.addItem(casingStack)) {
            player.drop(casingStack, false);
          }
        }
        player.setItemInHand(hand, handStack);
        casingAble.setTrackCasing(slabBlock);
      }
      return InteractionResult.SUCCESS;
    } else if (handStack.isEmpty()) {
      SlabBlock currentCasing = casingAble.getTrackCasing();
      if (currentCasing != null) {
        handStack = new ItemStack(currentCasing);
        casingAble.setTrackCasing(null);
        player.setItemInHand(hand, handStack);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  protected void applySettings(ServerPlayer player, TrackTileEntity te) {
    if (!te.getBlockPos()
        .closerThan(player.blockPosition(), 128)) {
      Railways.LOGGER.warn(player.getScoreboardName() + " too far away from slabbed Curve track");
      return;
    }

    Level level = te.getLevel();
    BezierConnection bezierConnection = te.getConnections()
        .get(targetPos);

    if (level != null) {
      InteractionHand hand = InteractionHand.MAIN_HAND;
      InteractionResult result = useOn(player, hand, level, (IHasTrackCasing) bezierConnection);
      if (!result.consumesAction()) {
        hand = InteractionHand.OFF_HAND;
        result = useOn(player, hand, level, (IHasTrackCasing) bezierConnection);
      }
      if (result.shouldSwing())
        player.swing(hand, true);

      te.notifyUpdate();
    }
  }

  @Override
  protected int maxRange() {
    return 64;
  }

  @Override
  protected void applySettings(TrackTileEntity trackTileEntity) {

  }
}
