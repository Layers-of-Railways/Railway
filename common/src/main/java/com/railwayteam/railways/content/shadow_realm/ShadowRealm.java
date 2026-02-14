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

package com.railwayteam.railways.content.shadow_realm;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.railwayteam.railways.mixin.AccessorCarriage;
import com.railwayteam.railways.mixin.AccessorGlobalRailwayManager;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ContraptionRelocationPacket;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainPacket;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

public class ShadowRealm {
    @ApiStatus.Internal
    public static final UUID MARKER = UUID.fromString("b9347f13-e5b2-4519-b7a4-f34017e7080e");

    @ApiStatus.Internal
    public static Train clientShadowRestoringTrain;

    private static final DynamicCommandExceptionType DUPLICATE_KEY = new DynamicCommandExceptionType(
        key -> () -> "Shadow key '" + key + "' is already in use"
    );

    public static void banishTrain(Train train, ResourceLocation shadowKey) throws CommandSyntaxException {
        IShadowTrain shadowTrain = (IShadowTrain) train;
        if (shadowTrain.railways$isShadow()) return;

        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        if (((RailwaySavedDataDuck) savedData).railways$getShadowKeys().containsKey(shadowKey))
            throw DUPLICATE_KEY.create(shadowKey);

        shadowTrain.railways$setShadow(shadowKey);
        for (Carriage carriage : train.carriages) {
            for (DimensionalCarriageEntity dce : ((AccessorCarriage) carriage).railways$getEntities().values()) {
                // discard all passengers
                dce.updatePassengerLoadout();
            }
        }

        train.navigation.cancelNavigation();
        train.speed = 0;
        train.derailed = true;
        train.graph = null;
        train.status.displayInformation("railways.shadow_realm.banished", true);
    }

    public static boolean restoreTrain(RailwaySavedData savedData, Train train, RestorationTarget target) {
        IShadowTrain shadowTrain = (IShadowTrain) train;
        if (!shadowTrain.railways$isShadow()) return true;

        if (!target.apply(train)) return false;

        ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().remove(train.id);
        ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().remove(shadowTrain.railways$getShadowKey());

        shadowTrain.railways$clearShadow();
        Create.RAILWAYS.addTrain(train);
        savedData.setDirty();

        AllPackets.getChannel().sendToClientsInCurrentServer(new TrainPacket(train, true));
        train.status.displayInformation("railways.shadow_realm.restored", true);
        return true;
    }

    public record RestorationTarget(
        Level level,
        BlockPos pos,
        BezierTrackPointLocation bezier,
        boolean bezierDirection,
        Vec3 lookAngle
    ) {
        public boolean apply(Train train) {
            if (!TrainRelocator.relocate(train, level, pos, bezier, bezierDirection, lookAngle, false))
                return false;

            train.carriages.forEach(c -> c.forEachPresentEntity(e -> {
                e.nonDamageTicks = 10;
                AllPackets.getChannel().sendToClientsTracking(new ContraptionRelocationPacket(e.getId()), e);
            }));

            return true;
        }
    }
}
