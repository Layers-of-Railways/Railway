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

package com.railwayteam.railways.multiloader.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.server.command.EnumArgument;

@ImplClass
public class PlatformAbstractionHelperImpl {
    public static int getBurnTime(Item item) {
        return ForgeHooks.getBurnTime(item.getDefaultInstance(), null);
    }

    public static <T extends Enum<T>> ArgumentType<T> enumArgument(Class<T> enumClass) {
        return EnumArgument.enumArgument(enumClass);
    }
}
