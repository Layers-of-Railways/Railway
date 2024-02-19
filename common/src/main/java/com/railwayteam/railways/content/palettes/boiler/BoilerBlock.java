package com.railwayteam.railways.content.palettes.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.mixin_interfaces.IForceRenderingSodium;
import com.railwayteam.railways.mixin_interfaces.IHasCustomOutline;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BoilerBlock extends Block implements IWrenchable, IForceRenderingSodium, IHasCustomOutline {
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
        boolean raised = context.getPlayer() != null && context.getPlayer().isCrouching();
        Axis axis = context.getClickedFace().getAxis();
        if (axis == Axis.Y)
            axis = context.getHorizontalDirection().getAxis();
        return defaultBlockState()
            .setValue(HORIZONTAL_AXIS, axis)
            .setValue(RAISED, raised);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CRShapes.BOILER.get(state.getValue(HORIZONTAL_AXIS));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CRShapes.BOILER.get(state.getValue(HORIZONTAL_AXIS));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return CRShapes.BOILER.get(state.getValue(HORIZONTAL_AXIS));
    }

    @Override
    public void customOutline(PoseStack poseStack, VertexConsumer consumer, BlockState state) {
        // First line / direction on right side
        drawLine(consumer, poseStack, 2, -7, 0, -7, 2, 0);
        // Second line | direction on right side
        drawLine(consumer, poseStack, -7, 2, 0, -7, 14, 0);
        // Third line \ direction on right side
        drawLine(consumer, poseStack, -7, 14, 0, 2, 23, 0);
        // Fourth line - direction on middle
        drawLine(consumer, poseStack, 2, 23, 0, 14, 23, 0);
        // Fifth line / direction on left side
        drawLine(consumer, poseStack, 14, 23, 0, 23, 14, 0);
        // Sixth line | direction on left side
        drawLine(consumer, poseStack, 23, 14, 0, 23, 2, 0);
        // Seventh line \ direction on left side
        drawLine(consumer, poseStack, 23, 2, 0, 14, -7, 0);
        // Eighth line - direction on middle
        drawLine(consumer, poseStack, 14, -7, 0, 2, -7, 0);

        // -- Backside --

        // First line / direction on right side
        drawLine(consumer, poseStack, 2, -7, 16, -7, 2, 16);
        // Second line | direction on right side
        drawLine(consumer, poseStack, -7, 2, 16, -7, 14, 16);
        // Third line \ direction on right side
        drawLine(consumer, poseStack, -7, 14, 16, 2, 23, 16);
        // Fourth line - direction on middle
        drawLine(consumer, poseStack, 2, 23, 16, 14, 23, 16);
        // Fifth line / direction on left side
        drawLine(consumer, poseStack, 14, 23, 16, 23, 14, 16);
        // Sixth line | direction on left side
        drawLine(consumer, poseStack, 23, 14, 16, 23, 2, 16);
        // Seventh line \ direction on left side
        drawLine(consumer, poseStack, 23, 2, 16, 14, -7, 16);
        // Eighth line - direction on middle
        drawLine(consumer, poseStack, 14, -7, 16, 2, -7, 16);

        // -- Sides --
        drawLine(consumer, poseStack, 2, -7, 0, 2, -7, 16);
        drawLine(consumer, poseStack, -7, 2, 0, -7, 2, 16);
        drawLine(consumer, poseStack, -7, 14, 0, -7, 14, 16);
        drawLine(consumer, poseStack, 2, 23, 0, 2, 23, 16);
        drawLine(consumer, poseStack, 14, 23, 0, 14, 23, 16);
        drawLine(consumer, poseStack, 23, 14, 0, 23, 14, 16);
        drawLine(consumer, poseStack, 23, 2, 0, 23, 2, 16);
        drawLine(consumer, poseStack, 14, -7, 0, 14, -7, 16);
    }

    @Override
    public void matrixRotation(PoseStack poseStack, BlockState state) {
        if (state.getValue(HORIZONTAL_AXIS) == Axis.X)
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
    }

    public enum Style implements StringRepresentable {
        GULLET("boiler_gullet"),
        SMOKEBOX("smokebox_door")
        ;

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
}
