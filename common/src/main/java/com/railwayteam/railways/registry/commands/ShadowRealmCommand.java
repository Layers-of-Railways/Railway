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

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.railwayteam.railways.content.shadow_realm.ShadowRealm;
import com.railwayteam.railways.mixin.AccessorGlobalRailwayManager;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.ShadowTrainRestorePacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShadowRealmCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return literal("shadow_realm")
            .requires(cs -> cs.hasPermission(2))
            .then(banish())
            .then(restore())
            .then(kill());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> banish() {
        return literal("banish")
            .then(argument("train", UuidArgument.uuid())
                .then(argument("key", ResourceLocationArgument.id())
                    .executes(ctx -> $banish(
                        ctx.getSource(),
                        UuidArgument.getUuid(ctx, "train"),
                        ResourceLocationArgument.getId(ctx, "key")
                    ))));
    }

    private static int $banish(CommandSourceStack source, UUID trainId, ResourceLocation shadowKey) throws CommandSyntaxException {
        Train train = Create.RAILWAYS.trains.get(trainId);
        if (train == null) {
            source.sendFailure(Components.literal("No Train with id " + trainId.toString()
                .substring(0, 5) + "[...] was found"));
            return 0;
        }

        IShadowTrain shadowTrain = (IShadowTrain) train;
        if (shadowTrain.railways$isShadow()) {
            source.sendFailure(Components.literal("Train '").append(train.name)
                .append("' is already a shadow train"));
            return 0;
        }

        ShadowRealm.banishTrain(train, shadowKey);

        source.sendSuccess(() -> Components.literal("Train '").append(train.name)
            .append("' banished to the shadow realm"), true);
        return 1;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> restore() {
        return literal("restore")
            .then(argument("key", ResourceLocationArgument.id())
                .suggests(ShadowRealmCommand::suggestKeys)
                .executes(ctx -> $restore(
                    ctx.getSource(),
                    ResourceLocationArgument.getId(ctx, "key")
                )));
    }

    private static int $restore(CommandSourceStack source, ResourceLocation shadowKey) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        UUID trainId = ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().get(shadowKey);
        if (trainId == null) {
            source.sendFailure(Components.literal("No shadow train with key '" + shadowKey + "' was found"));
            return 0;
        }

        Train train = ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().get(trainId);
        if (train == null) {
            source.sendFailure(Components.literal("Shadow train with key '" + shadowKey + "' has disappeared"));
            return 0;
        }

        CRPackets.PACKETS.sendTo(player, new ShadowTrainRestorePacket(train));

        source.sendSuccess(() -> Components.literal("Use a wrench on a track to restore '").append(train.name).append("'"), true);
        return 1;
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> kill() {
        return literal("kill")
            .then(argument("key", ResourceLocationArgument.id())
                .suggests(ShadowRealmCommand::suggestKeys)
                .executes(ctx -> $kill(
                    ctx.getSource(),
                    ResourceLocationArgument.getId(ctx, "key")
                )));
    }

    private static int $kill(CommandSourceStack source, ResourceLocation shadowKey) {
        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        UUID trainId = ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().get(shadowKey);
        if (trainId == null) {
            source.sendFailure(Components.literal("No shadow train with key '" + shadowKey + "' was found"));
            return 0;
        }

        Train train = ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().get(trainId);
        if (train == null) {
            source.sendFailure(Components.literal("Shadow train with key '" + shadowKey + "' has disappeared"));
            return 0;
        }

        ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().remove(trainId);
        ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().remove(shadowKey);
        savedData.setDirty();

        source.sendSuccess(() -> Components.literal("Shadow train '").append(train.name).append("' has been permanently removed"), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestKeys(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder suggestionsBuilder) {
        var savedData = ((AccessorGlobalRailwayManager) Create.RAILWAYS).railways$getSavedData();
        if (savedData == null) return Suggestions.empty();
        var shadowKeys = ((RailwaySavedDataDuck) savedData).railways$getShadowKeys();
        return SharedSuggestionProvider.suggestResource(shadowKeys.keySet(), suggestionsBuilder);
    }
}
