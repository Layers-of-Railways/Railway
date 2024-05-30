/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class CameraMovePacketOld implements C2SPacket {
    final int id;
    final float yRot;
    final float xRot;

    public CameraMovePacketOld(ConductorEntity entity, float yRot, float xRot) {
        this.id = entity.getId();
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public CameraMovePacketOld(FriendlyByteBuf buf) {
        this.id = buf.readVarInt();
        this.yRot = buf.readFloat();
        this.xRot = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeFloat(yRot);
        buffer.writeFloat(xRot);
    }

    @Override
    public void handle(ServerPlayer sender) {
        if (sender.level.getEntity(id) instanceof ConductorEntity conductor && sender.getCamera() == conductor) {
            conductor.setYRot(yRot % 360.0f);
            conductor.setXRot(Mth.clamp(xRot, -90.0f, 90.0f) % 360.0f);
        }
    }
}
