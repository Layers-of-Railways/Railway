package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin_interfaces.IMountedToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ToolboxHandler.class)
public abstract class MixinToolboxHandler implements IMountedToolboxHandler {

  @Shadow(remap = false)
  public static void syncData(Player player) {}

  @SuppressWarnings("InvalidInjectorMethodSignature") //Minecraft Dev plugin can't figure out the JUMP, so it gets all confused
  @Inject(at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 1),
      method = "entityTick", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
  private static void inj$entityTick(Entity entity, Level world, CallbackInfo ci, ServerPlayer player, boolean sendData, CompoundTag compound, int i, String key, CompoundTag data, BlockPos pos, int slot) {
    if (data.hasUUID("EntityUUID")) {
      if (world instanceof ServerLevel serverWorld) {
        Entity targetedEntity = serverWorld.getEntity(data.getUUID("EntityUUID"));
        if (targetedEntity instanceof ConductorEntity conductorEntity && conductorEntity.isCarryingToolbox()) {
          conductorEntity.getToolboxHolder().connectPlayer(slot, player, i);
        } else {
          compound.remove(key);
          syncData(player);
        }
      }
    }
  }

  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"),
      method = "unequip", remap = false, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
  private static void inj$unequip(Player player, int hotbarSlot, boolean keepItems, CallbackInfo ci, CompoundTag compound, Level world, String key, CompoundTag prevData, BlockPos prevPos, int prevSlot) {
    if (prevData.hasUUID("EntityUUID") && world instanceof ServerLevel serverWorld) {
      Entity targetedEntity = serverWorld.getEntity(prevData.getUUID("EntityUUID"));
      if (targetedEntity instanceof ConductorEntity conductorEntity && conductorEntity.isCarryingToolbox()) {
        conductorEntity.getToolboxHolder().unequip(prevSlot, player, hotbarSlot, keepItems || !IMountedToolboxHandler.withinRange(player, conductorEntity));
      }

      compound.remove(key);
      ci.cancel();
    }
  }
}
