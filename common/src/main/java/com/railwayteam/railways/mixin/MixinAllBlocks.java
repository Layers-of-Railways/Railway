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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.annotation.mixin.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.animated_flywheel.FlywheelMovementBehaviour;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

// Prevent future conflicts if EF updates
@ConditionalMixin(mods = Mods.EXTENDEDFLYWHEELS, applyIfPresent = false)
@Mixin(AllBlocks.class)
public class MixinAllBlocks {
    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/builders/BlockBuilder;properties(Lcom/tterrag/registrate/util/nullness/NonNullUnaryOperator;)Lcom/tterrag/registrate/builders/BlockBuilder;",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=sequenced_gearshift")
            ),
            remap = false
    )
    private static <T extends Block, P> BlockBuilder<T, P> railways$addFlywheelMovementBehaviour(BlockBuilder<T, P> instance, NonNullUnaryOperator<BlockBehaviour.Properties> func, Operation<BlockBuilder<T, P>> original) {
        return original.call(instance, func).onRegister(AllMovementBehaviours.movementBehaviour(new FlywheelMovementBehaviour()));
    }
}
