package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class SwitchDataUpdatePacket implements S2CPacket {

    final boolean clear;
    final TrackSwitchBlock.SwitchState state;
    final boolean automatic;
    final boolean isWrong;

    public SwitchDataUpdatePacket(TrackSwitchBlock.SwitchState state, boolean automatic, boolean isWrong) {
        this.state = state;
        this.automatic = automatic;
        this.isWrong = isWrong;
        this.clear = false;
    }

    protected SwitchDataUpdatePacket() {
        this.state = null;
        this.automatic = false;
        this.isWrong = false;
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
        } else {
            state = TrackSwitchBlock.SwitchState.values()[buf.readInt()];
            automatic = buf.readBoolean();
            isWrong = buf.readBoolean();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(clear);
        if (!clear) {
            buffer.writeInt(state.ordinal());
            buffer.writeBoolean(automatic);
            buffer.writeBoolean(isWrong);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handle(Minecraft mc) {
        if (clear) {
            TrainHUDSwitchExtension.switchState = null;
        } else {
            TrainHUDSwitchExtension.switchState = state;
            TrainHUDSwitchExtension.isAutomaticSwitch = automatic;
            TrainHUDSwitchExtension.isWrong = isWrong;
        }
    }
}
