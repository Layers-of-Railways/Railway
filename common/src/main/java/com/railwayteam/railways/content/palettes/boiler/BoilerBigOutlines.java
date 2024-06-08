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

package com.railwayteam.railways.content.palettes.boiler;

import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// TODO - Remove when https://github.com/Creators-of-Create/Create/pull/6356 make's it into a release
@Deprecated
public class BoilerBigOutlines {
    static BlockHitResult result = null;

    public static void pick() {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.cameraEntity instanceof LocalPlayer player))
            return;
        if (mc.level == null)
            return;

        result = null;

        Vec3 origin = player.getEyePosition(AnimationTickHolder.getPartialTicks(mc.level));

        double maxRange = mc.hitResult == null ? Double.MAX_VALUE
                : mc.hitResult.getLocation()
                .distanceToSqr(origin);

        double range = getRange(player);
        Vec3 target = RaycastHelper.getTraceTarget(player, Math.min(maxRange, range) + 1, origin);

        RaycastHelper.rayTraceUntil(origin, target, pos -> {
            BlockPos.MutableBlockPos p = BlockPos.ZERO.mutable();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        p.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = mc.level.getBlockState(p);

                        if (!(blockState.getBlock() instanceof BoilerBlock))
                            continue;

                        BlockHitResult hit = blockState.getInteractionShape(mc.level, p)
                                .clip(origin, target, p.immutable());
                        if (hit == null)
                            continue;

                        if (result != null && Vec3.atCenterOf(p)
                                .distanceToSqr(origin) >= Vec3.atCenterOf(result.getBlockPos())
                                .distanceToSqr(origin))
                            continue;

                        Vec3 vec = hit.getLocation();
                        double interactionDist = vec.distanceToSqr(origin);
                        if (interactionDist >= maxRange)
                            continue;

                        BlockPos hitPos = hit.getBlockPos();

                        // pacifies ServerGamePacketListenerImpl.handleUseItemOn
                        vec = vec.subtract(Vec3.atCenterOf(hitPos));
                        vec = VecHelper.clampComponentWise(vec, 1);
                        vec = vec.add(Vec3.atCenterOf(hitPos));

                        result = new BlockHitResult(vec, hit.getDirection(), hitPos, hit.isInside());
                    }
                }
            }

            return result != null;
        });

        if (result != null)
            mc.hitResult = result;
    }

    @ExpectPlatform
    public static double getRange(Player player) {
        throw new AssertionError();
    }
}
