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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.mixin_interfaces.IHasCustomOutline;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Implemented was inspired/derived from <a href="https://github.com/XFactHD/FramedBlocks/blob/17c8274ca380c3a868763b1b05657d07860c364b/src/main/java/xfacthd/framedblocks/client/render/special/BlockOutlineRenderer.java">Framed Blocks</a>
 * <p>
 * Which is licensed under <a href="https://github.com/XFactHD/FramedBlocks/blob/17c8274ca380c3a868763b1b05657d07860c364b/LICENSE">LGPL</a>
 */
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private boolean levelRender(LevelRenderer instance, PoseStack poseStack, VertexConsumer consumer, Entity entity,
                                double camX, double camY, double camZ, BlockPos pos, BlockState state, @Local(argsOnly = true) Camera camera) {
        if (state.getBlock() instanceof IHasCustomOutline hasCustomOutline) {
            MultiBufferSource.BufferSource bufferSource = ((AccessorLevelRenderer) instance).railways$getRenderBuffers().bufferSource();
            VertexConsumer lineVb = bufferSource.getBuffer(RenderType.lines());

            Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(camera.getPosition());

            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            poseStack.translate(0.5, 0.5, 0.5);
            hasCustomOutline.matrixRotation(poseStack, state);
            poseStack.translate(-0.5, -0.5, -0.5);

            hasCustomOutline.customOutline(poseStack, lineVb, state);

            poseStack.popPose();

            return false;
        }
        return true;
    }
}
