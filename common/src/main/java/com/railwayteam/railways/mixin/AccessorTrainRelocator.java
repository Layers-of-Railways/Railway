/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

import com.simibubi.create.content.trains.entity.TrainRelocator;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(TrainRelocator.class)
public interface AccessorTrainRelocator {
    @Accessor("relocatingTrain")
    static void railways$setRelocatingTrain(UUID trainId) {;
        throw new AssertionError("Mixin failed to apply");
    }

    @Accessor("relocatingOrigin")
    static void railways$setRelocatingOrigin(Vec3 origin) {
        throw new AssertionError("Mixin failed to apply");
    }

    @Accessor("relocatingEntityId")
    static void railways$setRelocatingEntityId(int entityId) {
        throw new AssertionError("Mixin failed to apply");
    }
}
