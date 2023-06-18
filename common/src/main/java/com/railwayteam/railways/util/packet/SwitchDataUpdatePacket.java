package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public class SwitchDataUpdatePacket implements S2CPacket {

    final boolean clear;
    final TrackSwitchBlock.SwitchState state;
    final boolean automatic;

    public SwitchDataUpdatePacket(TrackSwitchBlock.SwitchState state, boolean automatic) {
        this.state = state;
        this.automatic = automatic;
        this.clear = false;
    }

    protected SwitchDataUpdatePacket() {
        this.state = null;
        this.automatic = false;
        this.clear = true;
    }

    public static SwitchDataUpdatePacket clear() {
        return new SwitchDataUpdatePacket();
    }

    private static Optional<String> optionalString(String string) {
        return (string == null || string.isEmpty()) ? Optional.empty() : Optional.of(string);
    }

    public SwitchDataUpdatePacket(FriendlyByteBuf buf) {
        clear = buf.readBoolean();
        if (clear) {
            state = null;
            automatic = false;
        } else {
            state = TrackSwitchBlock.SwitchState.values()[buf.readInt()];
            automatic = buf.readBoolean();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(clear);
        if (!clear) {
            buffer.writeInt(state.ordinal());
            buffer.writeBoolean(automatic);
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
        }
    }
}
