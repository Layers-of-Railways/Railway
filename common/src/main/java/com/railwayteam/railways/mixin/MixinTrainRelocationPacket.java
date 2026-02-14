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

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.foundation.networking.SimplePacketBase.Context;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(TrainRelocationPacket.class)
public class MixinTrainRelocationPacket {
    @Shadow
    UUID trainId;

    @Shadow
    BlockPos pos;

    @Shadow private BezierTrackPointLocation hoveredBezier;

    @Shadow private boolean direction;

    @Shadow
    Vec3 lookAngle;

    @Inject(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/networking/SimplePacketBase$Context;getSender()Lnet/minecraft/server/level/ServerPlayer;"), cancellable = true)
    private void relocateShadowTrain(Context context, CallbackInfo ci) {
        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        if (savedData == null) return;

        var shadowTrains = ((RailwaySavedDataDuck) savedData).railway$getShadowTrains();
        Train shadowTrain = shadowTrains.get(trainId);
        if (shadowTrain == null) return;

        // don't bother trying to restore a shadow train
        ci.cancel();

        ServerPlayer sender = context.getSender();
        if (sender == null) return;

        String messagePrefix = sender.getName().getString() + " could not restore Train " + shadowTrain.name.getString();

        if (!sender.hasPermissions(2)) {
            Railways.LOGGER.warn("{}: player has insufficient permissions", messagePrefix);
            return;
        }

        int verifyDistance = AllConfigs.server().trains.maxTrackPlacementLength.get() * 2;
        if (!sender.position().closerThan(Vec3.atCenterOf(pos), verifyDistance)) {
            Railways.LOGGER.warn("{}: player too far from clicked pos", messagePrefix);
            return;
        }

        if (ShadowRealm.restoreTrain(savedData, shadowTrain, new ShadowRealm.RestorationTarget(sender.level(), pos, hoveredBezier, direction, lookAngle))) {
            sender.displayClientMessage(Lang.translateDirect("train.relocate.success")
                .withStyle(ChatFormatting.GREEN), false);
            return;
        }

        Railways.LOGGER.warn("{}: restoration failed server-side", messagePrefix);
    }
}
