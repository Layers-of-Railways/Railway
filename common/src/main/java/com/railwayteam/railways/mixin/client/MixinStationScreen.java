package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.gui.components.Checkbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StationScreen.class, remap = false)
public abstract class MixinStationScreen extends AbstractStationScreen { // fixme rework opening system
    private Checkbox limitEnableCheckbox;

    private MixinStationScreen(StationBlockEntity te, GlobalStation station) {
        super(te, station);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/StationScreen;tickTrainDisplay()V"), remap = true)
    private void initCheckbox(CallbackInfo ci) {
        int x = guiLeft;
        int y = guiTop;
        limitEnableCheckbox = new Checkbox(x + 8, y + background.height - 26, 50, 20, Components.translatable("railways.station.train_limit"), station != null && ((ILimited) station).isLimitEnabled()) {
            @Override
            public void onPress() {
                super.onPress();
                CRPackets.PACKETS.send(ILimited.makeLimitEnabledPacket(blockEntity.getBlockPos(), this.selected()));
            }
        };
        addRenderableWidget(limitEnableCheckbox);
    }
}
