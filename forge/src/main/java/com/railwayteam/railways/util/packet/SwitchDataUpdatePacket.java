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

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class SwitchDataUpdatePacket implements S2CPacket {

    final boolean clear;
    final TrackSwitchBlock.SwitchState state;
    final boolean automatic;
    final boolean isWrong;
    final boolean isLocked;

    public SwitchDataUpdatePacket(TrackSwitchBlock.SwitchState state, boolean automatic, boolean isWrong, boolean isLocked) {
        this.state = state;
        this.automatic = automatic;
        this.isWrong = isWrong;
        this.isLocked = isLocked;
        this.clear = false;
    }

    protected SwitchDataUpdatePacket() {
        this.state = null;
        this.automatic = false;
        this.isWrong = false;
        this.isLocked = false;
        this.clear = true;
    }

    public static SwitchDataUpdatePacket clear() {
        return new SwitchDataUpdatePacket();
    }

    public SwitchDataUpdatePacket(FriendlyByteBuf buf) {
        clear = buf.readBoolean();
        if (clear) {
            state = null;
            automatic = false;
            isWrong = false;
            isLocked = false;
        } else {
            state = TrackSwitchBlock.SwitchState.values()[buf.readInt()];
            automatic = buf.readBoolean();
            isWrong = buf.readBoolean();
            isLocked = buf.readBoolean();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(clear);
        if (!clear) {
            buffer.writeInt(state.ordinal());
            buffer.writeBoolean(automatic);
            buffer.writeBoolean(isWrong);
            buffer.writeBoolean(isLocked);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(Minecraft mc) {
        if (clear) {
            TrainHUDSwitchExtension.switchState = null;
        } else {
            TrainHUDSwitchExtension.switchState = state;
            TrainHUDSwitchExtension.isAutomaticSwitch = automatic;
            TrainHUDSwitchExtension.isWrong = isWrong;
            TrainHUDSwitchExtension.isLocked = isLocked;
        }
    }
}
