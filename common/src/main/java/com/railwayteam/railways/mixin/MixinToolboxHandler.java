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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(value = ToolboxHandler.class, remap = false)
public abstract class MixinToolboxHandler {

    @Shadow
    public static void syncData(Player player) {
        throw new AssertionError();
    }

    @Inject(method = {"onLoad", "onUnload"}, at = @At("HEAD"), cancellable = true)
    private static void railways$keepMountedToolboxesOutOfMap(ToolboxBlockEntity be, CallbackInfo ci) {
        if (be instanceof MountedToolbox)
            ci.cancel();
    }

    @Inject(
            method = "entityTick",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;isLoaded(Lnet/minecraft/core/BlockPos;)Z",
                    remap = true
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void railways$connectConductorToolboxes(Entity entity, Level world, CallbackInfo ci,
                                                           ServerPlayer player, boolean sendData, CompoundTag compound, int i,
                                                           String key, CompoundTag data, BlockPos pos, int slot) {
        if (!data.hasUUID("EntityUUID") || !(world instanceof ServerLevel level))
            return;
        UUID uuid = data.getUUID("EntityUUID");
        Entity toolboxHolder = level.getEntity(uuid);
        if (toolboxHolder instanceof ConductorEntity conductor) {
            MountedToolbox toolbox = conductor.getToolbox();
            if (toolbox != null)
                toolbox.connectPlayer(slot, player, i);
        } else {
            compound.remove(key);
            syncData(player);
        }
    }

    @ModifyVariable(
            method = "unequip",
            remap = false,
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    remap = true
            )
    )
    @SuppressWarnings("InvalidInjectorMethodSignature")
    private static BlockEntity railways$getConductorToolbox(BlockEntity be,
                                                            Player player, int hotbarSlot, boolean keepItems) {
        if (!(player.level instanceof ServerLevel level))
            return be;
        CompoundTag toolboxData = EntityUtils.getPersistentData(player).getCompound("CreateToolboxData");
        String key = String.valueOf(hotbarSlot);
        CompoundTag data = toolboxData.getCompound(key);
        if (!data.hasUUID("EntityUUID"))
            return be;
        UUID uuid = data.getUUID("EntityUUID");
        Entity entity = level.getEntity(uuid);
        if (!(entity instanceof ConductorEntity conductor) || !conductor.isCarryingToolbox())
            return be;
        return conductor.getToolbox();
    }

    @Inject(method = "getNearest", at = @At("RETURN"))
    private static void railways$findNearbyConductors(LevelAccessor world, Player player, int maxAmount,
                                                      CallbackInfoReturnable<List<ToolboxBlockEntity>> cir) {
        List<ToolboxBlockEntity> toolboxes = cir.getReturnValue();
        Set<ConductorEntity> conductors = ConductorEntity.WITH_TOOLBOXES.get(world);
        if (conductors.isEmpty())
            return;
        Vec3 playerPos = player.position();
        double maxRangeSqr = Math.pow(ToolboxHandler.getMaxRange(player), 2);
        for (ConductorEntity conductor : conductors) {
            if (ToolboxHandler.distance(playerPos, conductor.blockPosition()) < maxRangeSqr)
                toolboxes.add(conductor.getToolbox());
        }
        toolboxes.sort((be1, be2) -> {
            double d1 = ToolboxHandler.distance(playerPos, be1.getBlockPos());
            double d2 = ToolboxHandler.distance(playerPos, be2.getBlockPos());
            return Double.compare(d1, d2);
        });
    }
}
