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

package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a
 * minecart or falling) - modified by Slimeist to instead change the position to use the conductor position if the player is mounted on a conductor
 *
 * Confirmed to be SC compatible
 */
@Mixin(value = LevelRenderer.class, priority = 1200)
public class LevelRendererMixin {

	/*@Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
	public void securitycraft$onRepositionCamera(ViewArea viewArea, double x, double z) {
		if (!ClientHandler.isPlayerMountedOnCamera())
			viewArea.repositionCamera(x, z);
	}*/

	@Shadow @Final private Minecraft minecraft;

	// optional redirects (Rubidium/Sodium replaces this pipeline)
	@Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"), require = 0)
	private double railways$getX(LocalPlayer instance) {
		return minecraft.getCameraEntity() instanceof ConductorEntity conductor ? conductor.getX() : instance.getX();
	}

	@Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"), require = 0)
	private double railways$getY(LocalPlayer instance) {
		return minecraft.getCameraEntity() instanceof ConductorEntity conductor ? conductor.getY() : instance.getY();
	}

	@Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"), require = 0)
	private double railways$getZ(LocalPlayer instance) {
		return minecraft.getCameraEntity() instanceof ConductorEntity conductor ? conductor.getZ() : instance.getZ();
	}
}
