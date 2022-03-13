package com.railwayteam.railways.content.Boiler;

import com.railwayteam.railways.base.ConnectedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BoilerBlockEntity extends ConnectedBlockEntity {
  public static final int CAPACITY = 1; // buckets
  protected LazyOptional<IFluidHandler> fluidCapability;
  protected FluidTank fluidContainer;

  public BoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  //  fluidContainer = new SmartFluidTank(1000 * CAPACITY, this::onFluidStackChanged);
  //  fluidCapability = LazyOptional.of(()->fluidContainer);
  }
}
