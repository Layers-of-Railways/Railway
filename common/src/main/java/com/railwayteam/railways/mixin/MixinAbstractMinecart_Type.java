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

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = AbstractMinecart.Type.class, priority = 1674) // random priority to hopefully apply in a consistent order, extra safety
public class MixinAbstractMinecart_Type {
	@Final
	@Mutable
	@Shadow(aliases = { "field_7673", "f_vsosozul" }) // yarn, qm
	private static Type[] $VALUES; // moj

	@Invoker("<init>")
	private static Type railways$createType(String name, int ordinal) {
		throw new AssertionError();
	}

	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void railways$addTypes(CallbackInfo ci) {
		ArrayList<Type> types = new ArrayList<>(List.of($VALUES));
		types.add(railways$createType("RAILWAY_JUKEBOX", $VALUES.length));
		types.add(railways$createType("RAILWAY_WORKBENCH", $VALUES.length + 1));
		$VALUES = types.toArray(Type[]::new);
	}
}
