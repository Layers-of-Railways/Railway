package com.railwayteam.railways.mixin;

import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.contraptions.components.actors.DrillMovementBehaviour;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import com.simibubi.create.content.curiosities.girder.GirderBlock;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackInteraction;
import com.simibubi.create.content.logistics.trains.track.PlaceExtendedCurvePacket;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackBlockItem;
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("MixinAnnotationTarget")
@Mixin(value = {
    PlaceExtendedCurvePacket.class,
    CurvedTrackInteraction.class,
    MechanicalPressTileEntity.class,
    TrackBlockItem.class,
    TrackPlacement.class,
    TrackTileEntity.class,
    DrillMovementBehaviour.class,
    GirderBlock.class,
    TrackBlock.class,
    TrackBlockOutline.class
}, remap = false)
public class MixinReplaceTrackCheck {

  private static boolean check(BlockEntry<?> instance, ItemStack itemStack) {
    if (instance.getId().equals(new ResourceLocation("create", "track"))) {
      return CRTags.AllBlockTags.TRACKS.matches(itemStack);
    } else {
      return instance.isIn(itemStack);
    }
  }

  private static boolean check(BlockEntry<?> instance, BlockState state) {
    if (instance.getId().equals(new ResourceLocation("create", "track"))) {
      return CRTags.AllBlockTags.TRACKS.matches(state);
    } else {
      return instance.has(state);
    }
  }

  @SuppressWarnings("InvalidInjectorMethodSignature")
  @Redirect(method = {
      "sendExtenderPacket",
      "tryConnect",
      "clientTick()V",
      "onClickInput",
      "drawCurveSelection(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
      "drawCustomBlockSelection(Lnet/minecraftforge/client/event/DrawSelectionEvent$HighlightBlock;)V"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"), require = 0, expect = 0, remap = false)
  private static boolean staticCustomTrackCheck(BlockEntry<?> instance, ItemStack itemStack) { //.isIn static
    return check(instance, itemStack);
  }

  @Redirect(method = {
      "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
      "lambda$handle$0",
      "onItemPressed(Lnet/minecraft/world/item/ItemStack;)V",
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"), require = 0, expect = 0, remap = false)
  private boolean customTrackCheck(BlockEntry<?> instance, ItemStack itemStack) { //.isIn not static
    return check(instance, itemStack);
  }

  @Redirect(method = {
      "setRemovedNotDueToChunkUnload()V",
      "canBreak",
      "getConnected"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"), require = 0, expect = 0, remap = false)
  private boolean customTrackCheck2(BlockEntry<?> instance, BlockState state) { //.has not static
    return check(instance, state);
  }

  @Redirect(method = {
      "updateState"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"), require = 0, expect = 0, remap = false)
  private static boolean staticCustomTrackCheck2(BlockEntry<?> instance, BlockState state) { //.has static
    return check(instance, state);
  }
}
