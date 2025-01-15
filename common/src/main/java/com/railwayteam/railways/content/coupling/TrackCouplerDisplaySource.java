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

package com.railwayteam.railways.content.coupling;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockEntity;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

import static com.simibubi.create.content.redstone.displayLink.source.BoilerDisplaySource.notEnoughSpaceSingle;


public class TrackCouplerDisplaySource extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (stats.maxRows() < 2)
            return notEnoughSpaceSingle;
        if (!(context.getSourceBlockEntity() instanceof TrackCouplerBlockEntity te))
            return EMPTY;
        TrackCouplerBlockEntity.OperationInfo info = te.getOperationInfo();
        switch (info.mode()) {
            case NONE -> {
                return ImmutableList.of(
                    Component.translatable("railways.display_source.coupler.no_action").withStyle(ChatFormatting.BOLD),
                    te.getClientInfo().error
                );
            }
            case COUPLING -> {
                return ImmutableList.of(
                        Component.translatable("railways.display_source.coupler.coupling").append(info.frontCarriage().train.name),
                        Component.translatable("railways.display_source.coupler.coupling.to").append(info.backCarriage().train.name)
                );
            }
            case DECOUPLING -> {
                return ImmutableList.of(
                        Component.translatable("railways.display_source.coupler.decoupling").append(info.frontCarriage().train.name)
                );
            }
        }
        return null;
    }

    @Override
    protected String getTranslationKey() {
        return "track_coupler_info";
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        if (!layout.isLayout("Default"))
            layout.configure("Default",
                ImmutableList.of(createSectionForValue(context, flapDisplay.getMaxCharCount())));
    }

    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "alphabet", false, false);
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 40;
    }
}
