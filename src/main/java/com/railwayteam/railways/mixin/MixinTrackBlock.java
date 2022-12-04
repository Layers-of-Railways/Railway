package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.util.AllBlocksWrapper;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.simibubi.create.content.schematics.ItemRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Mixin(value = TrackBlock.class, remap = false)
public abstract class MixinTrackBlock extends Block implements IHasTrackMaterial {
  public MixinTrackBlock(Properties pProperties) {
    super(pProperties);
  }

  /**
   * @author Railways
   * @reason Need to add different types of items
   */
  @Overwrite
  public ItemRequirement getRequiredItems(BlockState state, BlockEntity te) {
    int sameTypeTrackAmount = 1;
    EnumMap<TrackMaterial, Integer> otherTrackAmounts = new EnumMap<>(TrackMaterial.class);
    int girderAmount = 0;

    if (te instanceof TrackTileEntity track) {
      for (BezierConnection bezierConnection : track.getConnections()
          .values()) {
        if (!bezierConnection.isPrimary())
          continue;
        TrackMaterial material = ((IHasTrackMaterial) bezierConnection).getMaterial();
        if (material == getMaterial()) {
          sameTypeTrackAmount += bezierConnection.getTrackItemCost();
        } else {
          otherTrackAmounts.put(material, otherTrackAmounts.getOrDefault(material, 0) + 1);
        }
        girderAmount += bezierConnection.getGirderItemCost();
      }
    }

    List<ItemStack> stacks = new ArrayList<>();
    while (sameTypeTrackAmount > 0) {
      stacks.add(new ItemStack(state.getBlock(), Math.min(sameTypeTrackAmount, 64)));
      sameTypeTrackAmount -= 64;
    }
    for (TrackMaterial material : otherTrackAmounts.keySet()) {
      int amt = otherTrackAmounts.get(material);
      while (amt > 0) {
        stacks.add(new ItemStack(material.getTrackBlock().get(), Math.min(amt, 64)));
        amt -= 64;
      }
    }
    while (girderAmount > 0) {
      stacks.add(AllBlocksWrapper.metalGirder().asStack(Math.min(girderAmount, 64)));
      girderAmount -= 64;
    }

    return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stacks);
  }

  @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
  private void extendedUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    InteractionResult result = CustomTrackBlock.casingUse(state, world, pos, player, hand, hit);
    if (result != null) {
      cir.setReturnValue(result);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootContext.@NotNull Builder builder) {
    List<ItemStack> superList = super.getDrops(state, builder);
    if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof IHasTrackCasing casing && casing.getTrackCasing() != null) {
      superList.add(new ItemStack(casing.getTrackCasing()));
    }
    return superList;
  }
}
