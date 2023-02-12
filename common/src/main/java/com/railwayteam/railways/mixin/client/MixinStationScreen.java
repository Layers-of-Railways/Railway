package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.railwayteam.railways.mixin_interfaces.ISidedStation;
import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.AbstractStationScreen;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationScreen;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
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
    private IconButton openLeft, openRight;
    private Indicator openLeftIndicator, openRightIndicator;

    private MixinStationScreen(StationTileEntity te, GlobalStation station) {
        super(te, station);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/management/edgePoint/station/StationScreen;tickTrainDisplay()V", remap = false), remap = true)
    private void initCheckbox(CallbackInfo ci) {
        int x = guiLeft;
        int y = guiTop;
        limitEnableCheckbox = new Checkbox(x + 8, y + background.height - 26, 50, 20, Components.translatable("railways.station.train_limit"), station != null && ((ILimited) station).isLimitEnabled()) {
            @Override
            public void onPress() {
                super.onPress();
                AllPackets.channel.sendToServer(ILimited.makeLimitEnabledPacket(te.getBlockPos(), this.selected()));
            }
        };
        addRenderableWidget(limitEnableCheckbox);

        int buttonXOffset = 100;

        openLeft = new IconButton(x + 8 + buttonXOffset, y + background.height - 21, CRIcons.I_STATION_OPEN_LEFT);
        openLeft.withCallback(() -> {
            if (te.getStation() != null)
                station = te.getStation();
            boolean shouldOpenLeft = station == null || !((ISidedStation) station).opensLeft();
            AllPackets.channel.sendToServer(ISidedStation.makeOpenLeftPacket(te.getBlockPos(), shouldOpenLeft));
            openLeftIndicator.state = shouldOpenLeft ? Indicator.State.ON : Indicator.State.OFF;
        });
        openLeft.setToolTip(Components.translatable("railways.station.open_left"));
        addRenderableWidget(openLeft);
        openLeftIndicator = new Indicator(x + 8 + buttonXOffset, y + background.height - 27, Components.translatable("railways.station.open_left"));
        openLeftIndicator.state = (station != null && ((ISidedStation) station).opensLeft()) ? Indicator.State.ON : Indicator.State.OFF;
        addRenderableWidget(openLeftIndicator);

        openRight = new IconButton(x + 8 + 20 + buttonXOffset, y + background.height - 21, CRIcons.I_STATION_OPEN_RIGHT);
        openRight.withCallback(() -> {
            if (te.getStation() != null)
                station = te.getStation();
            boolean shouldOpenRight = station == null || !((ISidedStation) station).opensRight();
            AllPackets.channel.sendToServer(ISidedStation.makeOpenRightPacket(te.getBlockPos(), shouldOpenRight));
            openRightIndicator.state = shouldOpenRight ? Indicator.State.ON : Indicator.State.OFF;
        });
        openRight.setToolTip(Components.translatable("railways.station.open_right"));
        addRenderableWidget(openRight);
        openRightIndicator = new Indicator(x + 8 + 20 + buttonXOffset, y + background.height - 27, Components.translatable("railways.station.open_right"));
        openRightIndicator.state = (station != null && ((ISidedStation) station).opensRight()) ? Indicator.State.ON : Indicator.State.OFF;
        addRenderableWidget(openRightIndicator);
    }
}
