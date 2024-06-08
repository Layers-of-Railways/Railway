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

package com.railwayteam.railways.mixin.client;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = StationScreen.class, remap = false)
public abstract class MixinStationScreen extends AbstractStationScreen {
    @Shadow private EditBox trainNameBox;
    private Checkbox limitEnableCheckbox;
    private List<ResourceLocation> iconTypes;
    private ScrollInput iconTypeScroll;

    private MixinStationScreen(StationBlockEntity te, GlobalStation station) {
        super(te, station);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/StationScreen;tickTrainDisplay()V"), remap = true)
    private void initCheckbox(CallbackInfo ci) {
        int x = guiLeft;
        int y = guiTop;
        limitEnableCheckbox = new Checkbox(x + background.width - 98, y + background.height - 26, 50, 20, Components.translatable("railways.station.train_limit"), station != null && ((ILimited) station).isLimitEnabled(), true) {
            @Override
            public void onPress() {
                super.onPress();
                CRPackets.PACKETS.send(ILimited.makeLimitEnabledPacket(blockEntity.getBlockPos(), this.selected()));
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                if (this.isHoveredOrFocused()) {
                    guiGraphics.renderComponentTooltip(font, ImmutableList.of(Components.translatable("railways.station.train_limit.tooltip.1"), Components.translatable("railways.station.train_limit.tooltip.2")), mouseX, mouseY);
                }
            }
        };
        addRenderableWidget(limitEnableCheckbox);

        iconTypes = TrainIconType.REGISTRY.keySet()
                .stream()
                .toList();
        iconTypeScroll = new ScrollInput(x + 4, y + 17, 184, 14).titled(Lang.translateDirect("station.icon_type"));
        iconTypeScroll.withRange(0, iconTypes.size());
        iconTypeScroll.withStepFunction(ctx -> -iconTypeScroll.standardStep()
                .apply(ctx));
        iconTypeScroll.calling(s -> {
            Train train = displayedTrain.get();
            if (train != null) {
                train.icon = TrainIconType.byId(iconTypes.get(s));
                Utils.sendCreatePacketToServer(
                        new TrainEditPacket(train.id, trainNameBox.getValue(), train.icon.getId()));
            }
        });
        iconTypeScroll.active = false;
    }

    @Inject(method = "tickTrainDisplay", at = @At("HEAD"))
    private void tickIconScroll(CallbackInfo ci) {
        Train train = displayedTrain.get();

        if (train == null) {
            if (iconTypeScroll.active) {
                iconTypeScroll.active = false;
                removeWidget(iconTypeScroll);
            }

            Train imminentTrain = getImminent();

            if (imminentTrain != null) {
                iconTypeScroll.active = true;
                iconTypeScroll.setState(iconTypes.indexOf(imminentTrain.icon.getId()));
                addRenderableWidget(iconTypeScroll);
            }
        }
    }
}
