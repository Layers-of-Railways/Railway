package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.google.common.collect.ImmutableSet;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.railwayteam.railways.registry.CRShapes.shape;

public class InvisibleMonoBogeyBlock extends AbstractMonoBogeyBlock<InvisibleMonoBogeyBlockEntity> {
    public InvisibleMonoBogeyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return CRBogeyStyles.INVISIBLE_MONOBOGEY;
    }

    @Override
    public Class<InvisibleMonoBogeyBlockEntity> getBlockEntityClass() {
        return InvisibleMonoBogeyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InvisibleMonoBogeyBlockEntity> getBlockEntityType() {
        return CRBlockEntities.INVISIBLE_MONO_BOGEY.get();
    }

    @Override
    public boolean isOnIncompatibleTrack(Carriage carriage, boolean leading) {
        TravellingPoint point = leading ? carriage.getLeadingPoint() : carriage.getTrailingPoint();
        CarriageBogey bogey = leading ? carriage.leadingBogey() : carriage.trailingBogey();
        return point.edge.getTrackMaterial().trackType != getTrackType(bogey.getStyle())
            && point.edge.getTrackMaterial().trackType != CRTrackType.WIDE_GAUGE
            && point.edge.getTrackMaterial().trackType != CRTrackType.NARROW_GAUGE
            && point.edge.getTrackMaterial().trackType != CRTrackType.STANDARD;
    }

    @Override
    public Set<TrackMaterial.TrackType> getValidPathfindingTypes(BogeyStyle style) {
        return ImmutableSet.of(getTrackType(style), CRTrackType.WIDE_GAUGE, CRTrackType.NARROW_GAUGE, CRTrackType.STANDARD);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.hasProperty(UPSIDE_DOWN) && state.getValue(UPSIDE_DOWN)) {
            return shape(0, 0, 0, 16, 16-7, 16).build();
        } else {
            return shape(0, 7, 0, 16, 16, 16).build();
        }
    }
}
