
package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractMonoBogeyBlock<T extends MonoBogeyBlockEntity> extends AbstractBogeyBlock<T> implements IBE<T>, ProperWaterloggedBlock, ISpecialBlockItemRequirement {

    public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");

    public AbstractMonoBogeyBlock(Properties pProperties) {
        super(pProperties, BogeySizes.SMALL);
        registerDefaultState(defaultBlockState().setValue(UPSIDE_DOWN, false));
    }

    @Override
    public BlockState getVersion(BlockState base, boolean upsideDown) {
        if (!base.hasProperty(UPSIDE_DOWN))
            return base;
        return base.setValue(UPSIDE_DOWN, upsideDown);
    }

    @Override
    public TrackMaterial.TrackType getTrackType(BogeyStyle style) {
        return CRTrackMaterials.CRTrackType.MONORAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UPSIDE_DOWN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public double getWheelPointSpacing() {
        return 2;
    }

    @Override
    public double getWheelRadius() {
        return 6 / 16d;
    }

    @Override
    public Vec3 getConnectorAnchorOffset(boolean upsideDown) {
        return new Vec3(0, upsideDown ? 21 / 32f : 11 / 32f, 32 / 32f);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return getConnectorAnchorOffset(false);
    }

    @Override
    public boolean allowsSingleBogeyCarriage() {
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return AllBlocks.RAILWAY_CASING.asStack();
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        return state;
    }

    @Override
    public boolean canBeUpsideDown() {
        return true;
    }

    @Override
    public boolean isUpsideDown(BlockState state) {
        return state.hasProperty(UPSIDE_DOWN) && state.getValue(UPSIDE_DOWN);
    }

    private final List<Property<?>> properties_to_copy = ImmutableList.<Property<?>>builder()
        .addAll(super.propertiesToCopy())
        .add(UPSIDE_DOWN)
        .build();

    @Override
    public List<Property<?>> propertiesToCopy() {
        return properties_to_copy;
    }
}
