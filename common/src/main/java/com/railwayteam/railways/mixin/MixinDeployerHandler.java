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
import com.railwayteam.railways.mixin_interfaces.IDeployAnywayBlockItem;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.world.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(DeployerHandler.class)
public class MixinDeployerHandler {
    // target `item instanceof BlockItem`
    @WrapOperation(method = "activateInner", constant = @Constant(classValue = BlockItem.class, ordinal = 0))
    private static boolean handcarsAreDeployable(Object object, Operation<Boolean> original) {
        if (object instanceof IDeployAnywayBlockItem)
            return false;
        return original.call(object);
    }
}
