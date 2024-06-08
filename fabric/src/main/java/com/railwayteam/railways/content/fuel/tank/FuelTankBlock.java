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

package com.railwayteam.railways.content.fuel.tank;

import com.railwayteam.railways.registry.fabric.CRBlockEntitiesImpl;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.Lang;
import io.github.fabricators_of_create.porting_lib.block.CustomSoundTypeBlock;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FuelTankBlock extends Block implements IWrenchable, IBE<FuelTankBlockEntity>, CustomSoundTypeBlock {

    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, LivingEntity pPlacer,
                            @NotNull ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public FuelTankBlock(Properties properties) {
        super(setLightFunction(properties));
        registerDefaultState(defaultBlockState().setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(SHAPE, Shape.WINDOW)
                .setValue(LIGHT_LEVEL, 0));
    }

    private static Properties setLightFunction(Properties properties) {
        return properties.lightLevel(state -> state.getValue(LIGHT_LEVEL));
    }

    public static boolean isTank(BlockState state) {
        return state.getBlock() instanceof FuelTankBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        // fabric: see comment in FuelTankItem
        Consumer<FuelTankBlockEntity> consumer = FuelTankItem.IS_PLACING_NBT
                ? FuelTankBlockEntity::queueConnectivityUpdate
                : FuelTankBlockEntity::updateConnectivity;
        withBlockEntityDo(world, pos, consumer);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, SHAPE, LIGHT_LEVEL);
    }

    // Handled via LIGHT_LEVEL state property
//	@Override
//	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
//		FuelTankBlockEntity tankAt = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
//		if (tankAt == null)
//			return 0;
//		FuelTankBlockEntity controllerBE = tankAt.getControllerBE();
//		if (controllerBE == null || !controllerBE.window)
//			return 0;
//		return tankAt.luminosity;
//	}

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), FuelTankBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState pState, @NotNull BlockGetter pReader,
                                                    @NotNull BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean onClient = world.isClientSide;

        if (heldItem.isEmpty())
            return InteractionResult.PASS;
        if (!player.isCreative())
            return InteractionResult.PASS;


        FluidHelper.FluidExchange exchange = null;
        FuelTankBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (be == null)
            return InteractionResult.FAIL;

        Direction direction = ray.getDirection();
        Storage<FluidVariant> fluidTank = be.getFluidStorage(direction);
        if (fluidTank == null)
            return InteractionResult.PASS;

        FluidStack prevFluidInTank = TransferUtil.firstCopyOrEmpty(fluidTank);

        if (FluidHelper.tryEmptyItemIntoBE(world, player, hand, heldItem, be, direction))
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        else if (FluidHelper.tryFillItemFromBE(world, player, hand, heldItem, be, direction))
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;

        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(world, heldItem)
                    || GenericItemFilling.canItemBeFilled(world, heldItem))
                return InteractionResult.SUCCESS;
            return InteractionResult.PASS;
        }

        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInTank = TransferUtil.firstOrEmpty(fluidTank);

        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            Fluid fluid = fluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidVariantAttributes.getEmptySound(FluidVariant.of(fluid));
        }

        if (exchange == FluidHelper.FluidExchange.TANK_TO_ITEM) {
            Fluid fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState()
                    .createLegacyBlock();
            soundevent = FluidVariantAttributes.getFillSound(FluidVariant.of(fluid));
        }

        if (soundevent != null && !onClient) {
            float pitch = Mth
                    .clamp(1 - (1f * fluidInTank.getAmount() / (FuelTankBlockEntity.getCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (world.random.nextFloat() - .5f) / 4f;
            world.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
        }

        if (!fluidInTank.isFluidEqual(prevFluidInTank)) {
            FuelTankBlockEntity controllerBE = be.getControllerBE();
            if (controllerBE != null) {
                if (onClient) {
                    BlockParticleOption blockParticleData =
                            new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                    float level = (float) fluidInTank.getAmount() / TransferUtil.firstCapacity(fluidTank);

                    boolean reversed = FluidVariantAttributes.isLighterThanAir(fluidInTank.getType());
                    if (reversed)
                        level = 1 - level;

                    Vec3 vec = ray.getLocation();
                    vec = new Vec3(vec.x, controllerBE.getBlockPos()
                            .getY() + level * (controllerBE.height - .5f) + .25f, vec.z);
                    Vec3 motion = player.position()
                            .subtract(vec)
                            .scale(1 / 20f);
                    vec = vec.add(motion);
                    world.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                    return InteractionResult.SUCCESS;
                }

                controllerBE.sendDataImmediately();
                controllerBE.setChanged();
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FuelTankBlockEntity tankBE))
                return;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }

    @Override
    public Class<FuelTankBlockEntity> getBlockEntityClass() {
        return FuelTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FuelTankBlockEntity> getBlockEntityType() {
        return CRBlockEntitiesImpl.FUEL_TANK.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        boolean x = mirror == Mirror.FRONT_BACK;
        return switch (state.getValue(SHAPE)) {
            case WINDOW_NE -> state.setValue(SHAPE, x ? Shape.WINDOW_NW : Shape.WINDOW_SE);
            case WINDOW_NW -> state.setValue(SHAPE, x ? Shape.WINDOW_NE : Shape.WINDOW_SW);
            case WINDOW_SE -> state.setValue(SHAPE, x ? Shape.WINDOW_SW : Shape.WINDOW_NE);
            case WINDOW_SW -> state.setValue(SHAPE, x ? Shape.WINDOW_SE : Shape.WINDOW_NW);
            default -> state;
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState rotate(@NotNull BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        return switch (state.getValue(SHAPE)) {
            case WINDOW_NE -> state.setValue(SHAPE, Shape.WINDOW_SE);
            case WINDOW_NW -> state.setValue(SHAPE, Shape.WINDOW_NE);
            case WINDOW_SE -> state.setValue(SHAPE, Shape.WINDOW_SW);
            case WINDOW_SW -> state.setValue(SHAPE, Shape.WINDOW_NW);
            default -> state;
        };
    }

    public enum Shape implements StringRepresentable {
        PLAIN, WINDOW, WINDOW_NW, WINDOW_SW, WINDOW_NE, WINDOW_SE;

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }

    // Tanks are less noisy when placed in batch
    public static final SoundType SILENCED_METAL =
            new SoundType(0.1F, 1.5F, SoundEvents.METAL_BREAK, SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = getSoundType(state);
        if (entity != null && entity.getExtraCustomData()
                .getBoolean("SilenceTankSound"))
            return SILENCED_METAL;
        return soundType;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level worldIn, @NotNull BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(FuelTankBlockEntity::getControllerBE)
                .map(be -> ComparatorUtil.fractionToRedstoneLevel(be.getFillState()))
                .orElse(0);
    }
}
