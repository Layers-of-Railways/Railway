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

package com.railwayteam.railways.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.decoration.copycat.FilteredBlockAndTintGetter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.function.Supplier;

@Mixin(CopycatModel.class)
public class CopycatModelMixin {
    @WrapOperation(method = "emitBlockQuads", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/decoration/copycat/CopycatModel;emitBlockQuadsInner(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;Lnet/minecraft/world/level/block/state/BlockState;Lcom/simibubi/create/content/decoration/copycat/CopycatModel$CullFaceRemovalData;Lcom/simibubi/create/content/decoration/copycat/CopycatModel$OcclusionData;)V"))
    private void filterConnectivity(CopycatModel instance, BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                                    Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material,
                                    @Coerce Object cullFaceRemovalData,
                                    @Coerce Object occlusionData, Operation<Void> original) {
        BlockAndTintGetter filteredView;
        if (state.getBlock() instanceof CopycatBlock copycatBlock) {
            filteredView = new FilteredBlockAndTintGetter(blockView, targetPos -> copycatBlock.canConnectTexturesToward(blockView, pos, targetPos, state));
        } else {
            filteredView = blockView;
        }
        original.call(instance, filteredView, state, pos, randomSupplier, context, material, cullFaceRemovalData, occlusionData);
    }
}
