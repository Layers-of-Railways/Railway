/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerServer;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.createmod.catnip.data.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BogeyStyleSelectionPacket implements C2SPacket {
    final BogeyStyle style;
    @Nullable
    final BogeySize size;

    public BogeyStyleSelectionPacket(@NotNull BogeyStyle style) {
        this(style, null);
    }

    public BogeyStyleSelectionPacket(@NotNull BogeyStyle style, @Nullable BogeySize size) {
        this.style = style;
        this.size = size;
    }

    public BogeyStyleSelectionPacket(FriendlyByteBuf buf) {
        ResourceLocation loc = buf.readResourceLocation();
        style = AllBogeyStyles.BOGEY_STYLES.getOrDefault(loc, AllBogeyStyles.STANDARD);
        if (buf.readBoolean()) {
            ResourceLocation sizeLoc = buf.readResourceLocation();
            size = BogeySizes.allSortedIncreasing().stream()
                    .filter((s) -> s.id().equals(sizeLoc))
                    .findFirst().orElse(null);
        } else {
            size = null;
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(style.id);
        buffer.writeBoolean(size != null);
        if (size != null)
            buffer.writeResourceLocation(size.id());
    }

    @Override
    public void handle(ServerPlayer sender) {
        BogeyMenuHandlerServer.addStyle(sender.getUUID(), Pair.of(style, size));
    }
}
