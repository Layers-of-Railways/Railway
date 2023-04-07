package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.components.actors.DrillMovementBehaviour;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import com.simibubi.create.content.curiosities.girder.GirderBlock;
import com.simibubi.create.content.logistics.trains.track.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.railwayteam.railways.mixin_interfaces.ITrackCheck.check;

@SuppressWarnings("MixinAnnotationTarget")
@Mixin(value = { //TODO _track api ALL OF IT
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
})
public class MixinReplaceTrackCheck {

  @SuppressWarnings({"InvalidInjectorMethodSignature", "target"})
  @Redirect(method = {
      "sendExtenderPacket",
//      "tryConnect", //this needs to be a specific check actually
      "clientTick()V",
      "onClickInput",
      "drawCurveSelection(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
      "drawCustomBlockSelection(Lnet/minecraftforge/client/event/DrawSelectionEvent$HighlightBlock;)V"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"), require = 0, expect = 0, remap = false)
  private static boolean staticCustomTrackCheck(BlockEntry<?> instance, ItemStack itemStack) { //.isIn static
    return check(instance, itemStack);
  }

  @SuppressWarnings("target")
  @Redirect(method = {
//      "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
//      "m_6225_(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
      "lambda$handle$0",
      "onItemPressed(Lnet/minecraft/world/item/ItemStack;)V",
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"), require = 0, expect = 0, remap = false)
  private boolean customTrackCheck(BlockEntry<?> instance, ItemStack itemStack) { //.isIn not static
    return check(instance, itemStack);
  }

  @SuppressWarnings("target")
  @Redirect(method = {
      "remove()V",
      "canBreak(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
      "getConnected(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZLcom/simibubi/create/content/logistics/trains/TrackNodeLocation;)Ljava/util/Collection;"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"), remap = false)
  private boolean customTrackCheck2(BlockEntry<?> instance, BlockState state) { //.has not static
    return check(instance, state);
  }

  @SuppressWarnings("target")
  @Redirect(method = {
      "updateState"
  }, at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"), require = 0, expect = 0, remap = false)
  private static boolean staticCustomTrackCheck2(BlockEntry<?> instance, BlockState state) { //.has static
    return check(instance, state);
  }
}
