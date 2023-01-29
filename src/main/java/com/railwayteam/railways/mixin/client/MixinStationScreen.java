package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.AbstractStationScreen;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationScreen;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.gui.components.Checkbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StationScreen.class, remap = false)
public abstract class MixinStationScreen extends AbstractStationScreen {
    private Checkbox limitEnableCheckbox;

    private MixinStationScreen(StationTileEntity te, GlobalStation station) {
        super(te, station);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/management/edgePoint/station/StationScreen;tickTrainDisplay()V"))
    private void initCheckbox(CallbackInfo ci) {
        int x = guiLeft;
        int y = guiTop;
        limitEnableCheckbox = new Checkbox(x + 8, y + background.height - 26, 50, 20, Components.translatable("railways.station.limit_trains"), te.getStation() != null && ((ILimited) te.getStation()).isLimitEnabled()) {
            @Override
            public void onPress() {
                super.onPress();
                AllPackets.channel.sendToServer(ILimited.makeLimitEnabledPacket(te.getBlockPos(), this.selected()));
            }
        };
        addRenderableWidget(limitEnableCheckbox);
    }
}
