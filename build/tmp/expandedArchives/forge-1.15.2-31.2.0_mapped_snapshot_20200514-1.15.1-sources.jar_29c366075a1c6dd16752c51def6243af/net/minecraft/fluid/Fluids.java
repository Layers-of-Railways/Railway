package net.minecraft.fluid;

import net.minecraft.util.registry.Registry;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = register("lava", new LavaFluid.Source());

   private static <T extends Fluid> T register(String key, T p_215710_1_) {
      return (T)(Registry.register(Registry.FLUID, key, p_215710_1_));
   }

   static {
      for(Fluid fluid : Registry.FLUID) {
         for(IFluidState ifluidstate : fluid.getStateContainer().getValidStates()) {
            Fluid.STATE_REGISTRY.add(ifluidstate);
         }
      }

   }
}