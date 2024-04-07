package com.railwayteam.railways.content.custom_bogeys.special.monobogey;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MonoBogeyBlock extends AbstractMonoBogeyBlock<MonoBogeyBlockEntity> {
    public MonoBogeyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.MONOBOGEY;
    }

    @Override
    public Class<MonoBogeyBlockEntity> getBlockEntityClass() {
        return MonoBogeyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MonoBogeyBlockEntity> getBlockEntityType() {
        return CRBlockEntities.MONO_BOGEY.get();
    }
}
