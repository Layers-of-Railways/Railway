package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(value = ToolboxHandlerClient.class, remap = false)
public class MixinToolboxHandlerClient {

  @Unique
  private static ConductorEntity railway$getConductorForSlot(int slot) {
    Minecraft mc = Minecraft.getInstance();
    LocalPlayer player = mc.player;
    CompoundTag toolboxData = EntityUtils.getPersistentData(player).getCompound("CreateToolboxData");
    String slotKey = String.valueOf(slot);

    CompoundTag data = toolboxData.getCompound(slotKey);
    if (!data.hasUUID("EntityUUID"))
      return null;
    UUID uuid = data.getUUID("EntityUUID");
    // can't do UUID lookup on clients...
    ConductorEntity conductor = null;
    for (ConductorEntity ce : ConductorEntity.WITH_TOOLBOXES.get(mc.level)) {
      if (ce.getUUID().equals(uuid)) {
        conductor = ce;
        break;
      }
    }
    return conductor;
  }

  // region --- can reach selected ---
  // when opening the radial menu, Create will check if the toolbox for the selected slot can be reached.
  // for conductor toolboxes, we need to use the entity's pos instead of the stored BE pos (non-existent)
  // the conductor is cached on first get (railway$conductorForSelectedSlot) and stored for second check.

  @Unique
  @Nullable
  private static ConductorEntity railway$conductorForSelectedSlot;

  @ModifyVariable(
          method = "onKeyInput",
          at = @At(
                  value = "INVOKE",
                  target = "Lcom/simibubi/create/content/curiosities/toolbox/ToolboxHandler;getMaxRange(Lnet/minecraft/world/entity/player/Player;)D",
                  remap = true
          )
  )
  private static BlockPos railway$useConductorToolboxDistance(BlockPos pos) {
    //noinspection ConstantConditions
    ConductorEntity conductor = railway$getConductorForSlot(Minecraft.getInstance().player.getInventory().selected);
    railway$conductorForSelectedSlot = conductor;
    return conductor == null ? pos : conductor.blockPosition();
  }

  @ModifyVariable(
          method = "onKeyInput",
          remap = false,
          at = @At(
                  value = "INVOKE_ASSIGN",
                  target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                  remap = true
          )
  )
  @SuppressWarnings("InvalidInjectorMethodSignature")
  private static BlockEntity railway$getConductorToolbox(BlockEntity be) {
    ConductorEntity conductor = railway$conductorForSelectedSlot;
    return conductor == null ? be : conductor.getToolbox();
  }

  // endregion
  // region --- reach toolbox for rendered slot ---
  // out-of-reach toolboxes get a different texture in the radial menu.
  // We need to use the entity pos here, but we need the index of the currently rendered slot to get it.
  // slot is grabbed and stored for use when needed.

  @Unique
  private static int railway$currentRenderedSlot;

  @ModifyArg(
          method = "renderOverlay",
          remap = false,
          at = @At(
                  value = "INVOKE",
                  target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"
          )
  )
  private static int railway$grabSlot(int slot) {
    railway$currentRenderedSlot = slot;
    return slot;
  }

  @ModifyVariable(
          method = "renderOverlay",
          remap = false,
          at = @At(
                  value = "INVOKE_ASSIGN",
                  target = "Lnet/minecraft/nbt/NbtUtils;readBlockPos(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/core/BlockPos;",
                  remap = true
          )
  )
  @SuppressWarnings("InvalidInjectorMethodSignature")
  private static BlockPos railway$useConductorToolboxForBackground(BlockPos pos) {
    ConductorEntity conductor = railway$getConductorForSlot(railway$currentRenderedSlot);
    return conductor == null ? pos : conductor.blockPosition();
  }

  // endregion
}
