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

package com.railwayteam.railways.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;

// empty mixin to pass class through the mixin plugin (can't just implement enum adding in mixin because we can't access the RollingMode class)
@Mixin(targets = "com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode", remap = false)
public class RollingModeMixin { }
