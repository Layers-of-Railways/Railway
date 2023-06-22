package com.railwayteam.railways.content.custom_bogeys;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CRBogeyBlockEntity extends AbstractBogeyBlockEntity implements IHaveGoggleInformation {
    public CRBogeyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        if (getBlockState().getBlock() instanceof CRBogeyBlock bogeyBlock)
            return bogeyBlock.getDefaultStyle();
        return CRBogeyStyles.SINGLEAXLE;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.builder()
                .add(Components.empty().append(getStyle().displayName).withStyle(ChatFormatting.GOLD))
                .forGoggles(tooltip);
        return true;
    }
}
