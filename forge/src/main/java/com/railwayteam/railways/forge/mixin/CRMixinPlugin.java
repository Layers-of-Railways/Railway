/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.forge.asm.ContainerLevelAccessASM;
import com.railwayteam.railways.forge.asm.RollingModeEnumAdder;
import com.railwayteam.railways.util.ConditionalMixinManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CRMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) { } // NO-OP

    @Override
    public String getRefMapperConfig() { return null; } // DEFAULT

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return ConditionalMixinManager.shouldApply(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { } // NO-OP

    @Override
    public List<String> getMixins() { return null; } // DEFAULT

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Adds an Enum
        if (targetClassName.equals("com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode"))
            RollingModeEnumAdder.processRollingMode(targetClass);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Adds an instanceof check and return
        if (targetClassName.equals("net.minecraft.world.inventory.ContainerLevelAccess"))
            ContainerLevelAccessASM.processNode(targetClass);
    }
}
