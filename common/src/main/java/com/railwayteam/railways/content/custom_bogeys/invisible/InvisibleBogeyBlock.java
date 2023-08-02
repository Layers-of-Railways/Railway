package com.railwayteam.railways.content.custom_bogeys.invisible;

import com.google.common.collect.ImmutableSet;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;

public class InvisibleBogeyBlock extends AbstractBogeyBlock<InvisibleBogeyBlockEntity>
	implements IBE<InvisibleBogeyBlockEntity>, ProperWaterloggedBlock, ISpecialBlockItemRequirement {

	public InvisibleBogeyBlock(Properties props) {
		super(props, BogeySizes.SMALL);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	public TrackType getTrackType(BogeyStyle style) {
		return TrackType.STANDARD;
	}

	@Override
	public boolean isOnIncompatibleTrack(Carriage carriage, boolean leading) {
		TravellingPoint point = leading ? carriage.getLeadingPoint() : carriage.getTrailingPoint();
		CarriageBogey bogey = leading ? carriage.leadingBogey() : carriage.trailingBogey();
		return point.edge.getTrackMaterial().trackType != getTrackType(bogey.getStyle())
			&& point.edge.getTrackMaterial().trackType != CRTrackType.WIDE_GAUGE
			&& point.edge.getTrackMaterial().trackType != CRTrackType.NARROW_GAUGE
			&& point.edge.getTrackMaterial().trackType != CRTrackType.MONORAIL;
	}

	@Override
	public Set<TrackType> getValidPathfindingTypes(BogeyStyle style) {
		return ImmutableSet.of(getTrackType(style), CRTrackType.WIDE_GAUGE, CRTrackType.NARROW_GAUGE, CRTrackType.MONORAIL);
	}

	@Override
	public double getWheelPointSpacing() {
		return 2;
	}

	@Override
	public double getWheelRadius() {
		return 6.5 / 16d;
	}

	@Override
	public Vec3 getConnectorAnchorOffset() {
		return new Vec3(0, 7 / 32f, 1);
	}

	@Override
	public BogeyStyle getDefaultStyle() {
		return CRBogeyStyles.INVISIBLE;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return AllBlocks.RAILWAY_CASING.asStack();
	}

	@Override
	public Class<InvisibleBogeyBlockEntity> getBlockEntityClass() {
		return InvisibleBogeyBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends InvisibleBogeyBlockEntity> getBlockEntityType() {
		return CRBlockEntities.INVISIBLE_BOGEY.get();
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return CRShapes.INVISIBLE_BOGEY;
	}
}
