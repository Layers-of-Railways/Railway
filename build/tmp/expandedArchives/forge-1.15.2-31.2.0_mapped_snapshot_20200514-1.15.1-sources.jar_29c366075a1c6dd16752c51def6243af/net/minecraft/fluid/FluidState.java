package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateHolder;

public class FluidState extends StateHolder<Fluid, IFluidState> implements IFluidState {
   public FluidState(Fluid p_i48997_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i48997_2_) {
      super(p_i48997_1_, p_i48997_2_);
   }

   public Fluid getFluid() {
      return this.object;
   }
}