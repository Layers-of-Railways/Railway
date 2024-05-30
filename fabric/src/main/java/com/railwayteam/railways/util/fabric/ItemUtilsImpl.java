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

package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.registry.CRItems;
import io.github.fabricators_of_create.porting_lib.util.ItemGroupUtil;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemUtilsImpl {
	public static boolean blocksEndermanView(ItemStack stack, Player wearer, EnderMan enderman) {
		return stack.is(Items.CARVED_PUMPKIN) || stack.is(CRItems.CONDUCTOR_CAPS);
	}

	public static int nextTabId() {
		return ItemGroupUtil.expandArrayAndGetId();
	}
}
