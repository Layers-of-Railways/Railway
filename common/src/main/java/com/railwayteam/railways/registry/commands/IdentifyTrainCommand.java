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
import com.railwayteam.railways.compat.journeymap.UsernameUtils;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.railwayteam.railways.multiloader.ClientCommands.*;

public class IdentifyTrainCommand {
    public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
        return literal("identify_train")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> $identify(ctx.getSource()));
    }

    @Environment(EnvType.CLIENT)
    private static int $identify(SharedSuggestionProvider source) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return 0;

        Couple<Vec3> rayInputs = ContraptionHandlerClient.getRayInputs(mc.player);
        Vec3 origin = rayInputs.getFirst();
        Vec3 target = rayInputs.getSecond();

        AABB aabb = new AABB(origin, target);
        List<CarriageContraptionEntity> intersectingContraptions =
            mc.level.getEntitiesOfClass(CarriageContraptionEntity.class, aabb);

        for (CarriageContraptionEntity contraptionEntity : intersectingContraptions) {
            if (ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity) == null)
                continue;

            Train train = contraptionEntity.getCarriage().train;

            var msg = Component.literal("Targeted train: '")
                .append(train.name)
                .append("' ")
                .append(Components.literal(train.id.toString().substring(0, 5) + "[...]").withStyle(Style.EMPTY
                    .withColor(ChatFormatting.GRAY)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(train.id.toString())))
                ));

            if (train.owner != null) {
                msg.append(" (owned by '" + UsernameUtils.INSTANCE.getName(train.owner) + "')");
            }

            msg.append(Components.literal(" [Copy ID]").withStyle(Style.EMPTY
                .withColor(ChatFormatting.GOLD)
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, train.id.toString()))
            ));

            sendSuccess(source, msg);
            return 1;
        }

        sendFailure(source, Component.literal("No train found at your crosshair"));
        return 0;
    }
}
