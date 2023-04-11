package com.railwayteam.railways.content.coupling;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerTileEntity;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayLayout;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

import static com.simibubi.create.content.logistics.block.display.source.BoilerDisplaySource.notEnoughSpaceSingle;

public class TrackCouplerDisplaySource extends DisplaySource {

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (stats.maxRows() < 2)
            return notEnoughSpaceSingle;
        if (!(context.getSourceTE() instanceof TrackCouplerTileEntity te))
            return EMPTY;
        TrackCouplerTileEntity.OperationInfo info = te.getOperationInfo();
        switch (info.mode()) {
            case NONE -> {
                return ImmutableList.of(
                    Components.translatable("railways.display_source.coupler.no_action").withStyle(ChatFormatting.BOLD),
                    te.getClientInfo().error
                );
            }
            case COUPLING -> {
                return ImmutableList.of(
                        Components.translatable("railways.display_source.coupler.coupling").append(info.frontCarriage().train.name),
                        Components.translatable("railways.display_source.coupler.coupling.to").append(info.backCarriage().train.name)
                );
            }
            case DECOUPLING -> {
                return ImmutableList.of(
                        Components.translatable("railways.display_source.coupler.decoupling").append(info.frontCarriage().train.name)
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
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayTileEntity flapDisplay, FlapDisplayLayout layout) {
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
