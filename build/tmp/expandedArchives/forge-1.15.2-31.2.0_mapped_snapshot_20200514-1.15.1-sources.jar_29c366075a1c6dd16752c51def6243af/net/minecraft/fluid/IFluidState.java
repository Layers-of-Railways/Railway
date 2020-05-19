package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IFluidState extends IStateHolder<IFluidState>, net.minecraftforge.common.extensions.IForgeFluidState {
   Fluid getFluid();

   default boolean isSource() {
      return this.getFluid().isSource(this);
   }

   default boolean isEmpty() {
      return this.getFluid().isEmpty();
   }

   default float getActualHeight(IBlockReader p_215679_1_, BlockPos p_215679_2_) {
      return this.getFluid().getActualHeight(this, p_215679_1_, p_215679_2_);
   }

   default float getHeight() {
      return this.getFluid().getHeight(this);
   }

   default int getLevel() {
      return this.getFluid().getLevel(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean shouldRenderSides(IBlockReader worldIn, BlockPos pos) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = pos.add(i, 0, j);
            IFluidState ifluidstate = worldIn.getFluidState(blockpos);
            if (!ifluidstate.getFluid().isEquivalentTo(this.getFluid()) && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   default void tick(World worldIn, BlockPos pos) {
      this.getFluid().tick(worldIn, pos, this);
   }

   @OnlyIn(Dist.CLIENT)
   default void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_) {
      this.getFluid().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
   }

   default boolean ticksRandomly() {
      return this.getFluid().ticksRandomly();
   }

   default void randomTick(World worldIn, BlockPos pos, Random random) {
      this.getFluid().randomTick(worldIn, pos, this, random);
   }

   default Vec3d getFlow(IBlockReader p_215673_1_, BlockPos p_215673_2_) {
      return this.getFluid().getFlow(p_215673_1_, p_215673_2_, this);
   }

   default BlockState getBlockState() {
      return this.getFluid().getBlockState(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   default IParticleData getDripParticleData() {
      return this.getFluid().getDripParticleData();
   }

   default boolean isTagged(Tag<Fluid> tagIn) {
      return this.getFluid().isIn(tagIn);
   }

   @Deprecated //Forge: Use more sensitive version.
   default float getExplosionResistance() {
      return this.getFluid().getExplosionResistance();
   }

   default boolean canDisplace(IBlockReader p_215677_1_, BlockPos p_215677_2_, Fluid p_215677_3_, Direction p_215677_4_) {
      return this.getFluid().canDisplace(this, p_215677_1_, p_215677_2_, p_215677_3_, p_215677_4_);
   }

   static <T> Dynamic<T> serialize(DynamicOps<T> p_215680_0_, IFluidState p_215680_1_) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_215680_1_.getValues();
      T t;
      if (immutablemap.isEmpty()) {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getFluid()).toString())));
      } else {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getFluid()).toString()), p_215680_0_.createString("Properties"), p_215680_0_.createMap(immutablemap.entrySet().stream().map((p_215675_1_) -> {
            return Pair.of(p_215680_0_.createString(p_215675_1_.getKey().getName()), p_215680_0_.createString(IStateHolder.getName(p_215675_1_.getKey(), p_215675_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic<>(p_215680_0_, t);
   }

   static <T> IFluidState deserialize(Dynamic<T> p_215681_0_) {
      Fluid fluid = Registry.FLUID.getOrDefault(new ResourceLocation(p_215681_0_.getElement("Name").flatMap(p_215681_0_.getOps()::getStringValue).orElse("minecraft:empty")));
      Map<String, String> map = p_215681_0_.get("Properties").asMap((p_215678_0_) -> {
         return p_215678_0_.asString("");
      }, (p_215674_0_) -> {
         return p_215674_0_.asString("");
      });
      IFluidState ifluidstate = fluid.getDefaultState();
      StateContainer<Fluid, IFluidState> statecontainer = fluid.getStateContainer();

      for(Entry<String, String> entry : map.entrySet()) {
         String s = entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            ifluidstate = IStateHolder.withString(ifluidstate, iproperty, s, p_215681_0_.toString(), entry.getValue());
         }
      }

      return ifluidstate;
   }

   default VoxelShape getShape(IBlockReader p_215676_1_, BlockPos p_215676_2_) {
      return this.getFluid().func_215664_b(this, p_215676_1_, p_215676_2_);
   }
}