/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient {
    @WrapOperation(method = "handleSpecialInteractions", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/TrainRelocator;carriageWrenched(Lnet/minecraft/world/phys/Vec3;Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;)Z"))
    private static boolean shadowRealmShortcut(
        Vec3 vec3,
        CarriageContraptionEntity entity,
        Operation<Boolean> original,
        AbstractContraptionEntity contraptionEntity,
        Player player,
        BlockPos localPos,
        Direction side,
        InteractionHand interactionHand
    ) {
        if (!player.isShiftKeyDown())
            return original.call(vec3, entity);

        ItemStack stack = player.getItemInHand(interactionHand);
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("ShadowHammer")) {
            if (!(player.isCreative() && CRConfigs.client().universalShadowWrench.get()))
                return original.call(vec3, entity);
        }

        Minecraft mc = Minecraft.getInstance();
        ((AccessorMinecraft) mc).railways$openChatScreen("/snr shadow_realm banish " + entity.trainId + " ");
        return true;
    }
}
