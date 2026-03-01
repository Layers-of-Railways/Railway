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
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorCarriage;
import com.railwayteam.railways.mixin.AccessorGlobalRailwayManager;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ContraptionRelocationPacket;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainPacket;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

public class ShadowRealm {
    public static final Logger LOGGER = LoggerFactory.getLogger(Railways.ID_NAME+"/ShadowRealm");

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

    public static void handleTrainRelocationPacket(ServerPlayer sender, UUID trainId, RestorationTarget target, CallbackInfo ci) {
        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        if (savedData == null) {
            LOGGER.warn("Received TrainRelocationPacket but saved data was null");
            return;
        }

        var shadowTrains = ((RailwaySavedDataDuck) savedData).railway$getShadowTrains();
        Train shadowTrain = shadowTrains.get(trainId);
        if (shadowTrain == null) {
            LOGGER.warn("Received TrainRelocationPacket for train id {} but no shadow train was found", trainId);
            Train train = Create.RAILWAYS.trains.get(trainId);
            if (train == null) {
                LOGGER.warn("No non-shadow train with id {} was found either", trainId);
            } else {
                LOGGER.warn("However, a train with id {} and shadow key [{}] was found: '{}'", trainId,
                    ((IShadowTrain) train).railways$getShadowKey(), train.name.getString());
            }
            return;
        }

        // don't bother trying to restore a shadow train
        ci.cancel();

        String messagePrefix = sender.getName().getString() + " could not restore Train " + shadowTrain.name.getString();

        if (!sender.hasPermissions(2)) {
            LOGGER.warn("{}: player has insufficient permissions", messagePrefix);
            return;
        }

        int verifyDistance = AllConfigs.server().trains.maxTrackPlacementLength.get() * 2;
        if (!sender.position().closerThan(Vec3.atCenterOf(target.pos), verifyDistance)) {
            LOGGER.warn("{}: player too far from clicked pos", messagePrefix);
            return;
        }

        if (ShadowRealm.restoreTrain(savedData, shadowTrain, target)) {
            sender.displayClientMessage(Lang.translateDirect("train.relocate.success")
                .withStyle(ChatFormatting.GREEN), false);
            LOGGER.info("{} successfully restored '{}' from the shadow realm", sender.getName().getString(), shadowTrain.name.getString());
            return;
        }

        LOGGER.warn("{}: restoration failed server-side", messagePrefix);
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

        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new TrainPacket(train, true));
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
                CRPackets.PACKETS.sendTo(PlayerSelection.tracking(e), new ContraptionRelocationPacket(e.getId()));
            }));

            return true;
        }
    }
}
