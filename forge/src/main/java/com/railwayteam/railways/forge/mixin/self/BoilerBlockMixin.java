/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

package com.railwayteam.railways.forge.mixin.self;

import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BoilerBlock.class)
public class BoilerBlockMixin implements IClientBlockExtensions {
    @Override
    public boolean addDestroyEffects(BlockState state, Level worldIn, BlockPos pos, ParticleEngine manager) {
        if (!(worldIn instanceof ClientLevel))
            return true;
        ClientLevel world = (ClientLevel) worldIn;
        VoxelShape voxelshape = state.getShape(world, pos);
        MutableInt amtBoxes = new MutableInt(0);
        voxelshape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> amtBoxes.increment());
        double chance = 1d / amtBoxes.getValue();

        if (state.isAir())
            return true;

        voxelshape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            double w = x2 - x1;
            double h = y2 - y1;
            double l = z2 - z1;
            int xParts = Math.max(2, Mth.ceil(Math.min(1, w) * 4));
            int yParts = Math.max(2, Mth.ceil(Math.min(1, h) * 4));
            int zParts = Math.max(2, Mth.ceil(Math.min(1, l) * 4));

            for (int xIndex = 0; xIndex < xParts; ++xIndex) {
                for (int yIndex = 0; yIndex < yParts; ++yIndex) {
                    for (int zIndex = 0; zIndex < zParts; ++zIndex) {
                        if (world.random.nextDouble() > chance)
                            continue;

                        double d4 = (xIndex + .5) / xParts;
                        double d5 = (yIndex + .5) / yParts;
                        double d6 = (zIndex + .5) / zParts;
                        double x = pos.getX() + d4 * w + x1;
                        double y = pos.getY() + d5 * h + y1;
                        double z = pos.getZ() + d6 * l + z1;

                        manager.add(new TerrainParticle(world, x, y, z, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state, pos)
                                .updateSprite(state, pos));
                    }
                }
            }
        });

        return true;
    }
}
