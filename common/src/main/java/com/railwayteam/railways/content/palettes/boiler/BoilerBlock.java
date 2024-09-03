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

package com.railwayteam.railways.content.palettes.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.mixin_interfaces.IForceRenderingSodium;
import com.railwayteam.railways.mixin_interfaces.IHasCustomOutline;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import com.simibubi.create.foundation.placement.PoleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class BoilerBlock extends Block implements IWrenchable, IForceRenderingSodium, IHasCustomOutline {
    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);
    public static final EnumProperty<Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty RAISED = BooleanProperty.create("raised"); // raise by 1/2 block

    public BoilerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(STYLE, Style.GULLET)
                .setValue(HORIZONTAL_AXIS, Axis.X)
                .setValue(RAISED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE, HORIZONTAL_AXIS, RAISED);
    }

    /*@Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return IWrenchable.super.updateAfterWrenched(newState, context)
            .cycle(STYLE);
    }*/

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis() == originalState.getValue(HORIZONTAL_AXIS))
            return originalState.cycle(STYLE);
        return IWrenchable.super.getRotatedBlockState(originalState, targetedFace);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(@NotNull BlockState state, BlockState adjacentBlockState, @NotNull Direction direction) {
        return adjacentBlockState.is(this)
                && adjacentBlockState.getValue(HORIZONTAL_AXIS) == state.getValue(HORIZONTAL_AXIS)
                && adjacentBlockState.getValue(RAISED) == state.getValue(RAISED);
    }

    @Override
    public boolean forceRenderingSodium(@NotNull BlockState state, BlockState adjacentBlockState, @NotNull Direction direction) {
        return !skipRendering(state, adjacentBlockState, direction);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> state.cycle(HORIZONTAL_AXIS);
            default -> state;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean raised = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        Axis axis = context.getClickedFace().getAxis();
        if (axis == Axis.Y)
            axis = context.getHorizontalDirection().getAxis();
        return defaultBlockState()
                .setValue(HORIZONTAL_AXIS, axis)
                .setValue(RAISED, raised);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);

        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
        if (helper.matchesItem(heldItem))
            return helper.getOffset(player, level, state, pos, hit)
                    .placeInWorld(level, (BlockItem) heldItem.getItem(), player, hand, hit);

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return getShapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                                 @NotNull CollisionContext context) {
        return getShapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return getShapeForState(state);
    }

    public @NotNull VoxelShape getShapeForState(BlockState state) {
        if (state.getValue(RAISED))
            return CRShapes.BOILER_RAISED.get(state.getValue(HORIZONTAL_AXIS));
        return CRShapes.BOILER.get(state.getValue(HORIZONTAL_AXIS));
    }

    @Override
    public void customOutline(PoseStack poseStack, VertexConsumer consumer, BlockState state) {
        double offset = state.getValue(RAISED) ? 8 : 0;

        for (int zeroAndOne : Iterate.zeroAndOne) {
            int i = zeroAndOne * 16;

            // First line / direction on right side
            drawLineWithAxisOffset(consumer, poseStack, 1.37258, -8, i, -8, 1.37258, i, offset, Axis.Y);
            // Second line | direction on right side
            drawLineWithAxisOffset(consumer, poseStack, -8, 1.37258, i, -8, 14.6274, i, offset, Axis.Y);
            // Third line \ direction on right side
            drawLineWithAxisOffset(consumer, poseStack, -8, 14.6274, i, 1.37258, 24, i, offset, Axis.Y);
            // Fourth line - direction on middle
            drawLineWithAxisOffset(consumer, poseStack, 1.37258, 24, i, 14.6274, 24, i, offset, Axis.Y);
            // Fifth line / direction on left side
            drawLineWithAxisOffset(consumer, poseStack, 14.6274, 24, i, 24, 14.6274, i, offset, Axis.Y);
            // Sixth line | direction on left side
            drawLineWithAxisOffset(consumer, poseStack, 24, 14.6274, i, 24, 1.37258, i, offset, Axis.Y);
            // Seventh line \ direction on left side
            drawLineWithAxisOffset(consumer, poseStack, 24, 1.37258, i, 14.6274, -8, i, offset, Axis.Y);
            // Eighth line - direction on middle
            drawLineWithAxisOffset(consumer, poseStack, 14.6274, -8, i, 1.37258, -8, i, offset, Axis.Y);
        }

        // -- Sides --
        drawLineWithAxisOffset(consumer, poseStack, 1.37258, -8, 0, 1.37258, -8, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, -8, 1.37258, 0, -8, 1.37258, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, -8, 14.6274, 0, -8, 14.6274, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, 1.37258, 24, 0, 1.37258, 24, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, 14.6274, 24, 0, 14.6274, 24, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, 24, 14.6274, 0, 24, 14.6274, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, 24, 1.37258, 0, 24, 1.37258, 16, offset, Axis.Y);
        drawLineWithAxisOffset(consumer, poseStack, 14.6274, -8, 0, 14.6274, -8, 16, offset, Axis.Y);
    }

    @Override
    public void matrixRotation(PoseStack poseStack, BlockState state) {
        if (state.getValue(HORIZONTAL_AXIS) == Axis.X)
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
    }

    public enum Style implements StringRepresentable {
        GULLET("boiler_gullet"),
        SMOKEBOX("smokebox_door");

        private final String texture;

        Style(String texture) {
            this.texture = texture;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public String getTexture() {
            return texture;
        }
    }

    // Boilers can follow shaft placement, it's pretty much 1:1 apart from boilers being Horizontal only
    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction.Axis> {
        private PlacementHelper() {
            super(state -> state.getBlock() instanceof BoilerBlock,
                    state -> state.getValue(HORIZONTAL_AXIS), HORIZONTAL_AXIS);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BoilerBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof BoilerBlock;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            PlacementOffset offset = PlacementOffset.fail();

            List<Direction> directions = IPlacementHelper.orderedByDistance(pos, ray.getLocation(), dir -> dir.getAxis() == axisFunction.apply(state));
            for (Direction dir : directions) {
                dir = dir.getOpposite();
                int range = AllConfigs.server().equipment.placementAssistRange.get();
                if (player != null) {
                    AttributeInstance reach = player.getAttribute(getAttribute());
                    if (reach != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier))
                        range += 4;
                }
                int poles = attachedPoles(level, pos, dir);
                if (poles >= range)
                    continue;

                BlockPos newPos = pos.relative(dir, poles + 1);
                BlockState newState = level.getBlockState(newPos);

                if (newState.getMaterial().isReplaceable())
                    offset = PlacementOffset.success(newPos, bState -> bState.setValue(HORIZONTAL_AXIS, state.getValue(HORIZONTAL_AXIS)));
            }

            if (offset.isSuccessful()) {
                offset.withTransform(offset.getTransform()
                        .andThen(s -> s.setValue(RAISED, state.getValue(RAISED))));
            }

            return offset;
        }

        @ExpectPlatform
        public static Attribute getAttribute() {
            throw new AssertionError();
        }
    }
}
