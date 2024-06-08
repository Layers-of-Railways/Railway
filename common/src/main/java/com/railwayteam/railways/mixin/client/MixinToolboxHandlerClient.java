/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
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
  private static ConductorEntity railways$getConductorForSlot(int slot) {
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
  // the conductor is cached on first get (railways$conductorForSelectedSlot) and stored for second check.

  @Unique
  @Nullable
  private static ConductorEntity railways$conductorForSelectedSlot;

  @ModifyVariable(
          method = "onKeyInput",
          at = @At(
                  value = "INVOKE",
                  target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxHandler;getMaxRange(Lnet/minecraft/world/entity/player/Player;)D",
                  remap = true
          )
  )
  @SuppressWarnings("DataFlowIssue")
  private static BlockPos railways$useConductorToolboxDistance(BlockPos pos) {
    ConductorEntity conductor = railways$getConductorForSlot(Minecraft.getInstance().player.getInventory().selected);
    railways$conductorForSelectedSlot = conductor;
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
  private static BlockEntity railways$getConductorToolbox(BlockEntity be) {
    ConductorEntity conductor = railways$conductorForSelectedSlot;
    return conductor == null ? be : conductor.getToolbox();
  }

  // endregion
  // region --- reach toolbox for rendered slot ---
  // out-of-reach toolboxes get a different texture in the radial menu.
  // We need to use the entity pos here, but we need the index of the currently rendered slot to get it.
  // slot is grabbed and stored for use when needed.

  @Unique
  private static int railways$currentRenderedSlot;

  @ModifyArg(
          method = "renderOverlay",
          remap = false,
          at = @At(
                  value = "INVOKE",
                  target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"
          )
  )
  private static int railways$grabSlot(int slot) {
    railways$currentRenderedSlot = slot;
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
  private static BlockPos railways$useConductorToolboxForBackground(BlockPos pos) {
    ConductorEntity conductor = railways$getConductorForSlot(railways$currentRenderedSlot);
    return conductor == null ? pos : conductor.blockPosition();
  }

  // endregion
}
