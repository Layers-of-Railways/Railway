package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Fluid extends net.minecraftforge.registries.ForgeRegistryEntry<Fluid> implements net.minecraftforge.common.extensions.IForgeFluid {
   public static final ObjectIntIdentityMap<IFluidState> STATE_REGISTRY = new ObjectIntIdentityMap<>();
   protected final StateContainer<Fluid, IFluidState> stateContainer;
   private IFluidState defaultState;

   protected Fluid() {
      StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder<>(this);
      this.fillStateContainer(builder);
      this.stateContainer = builder.create(FluidState::new);
      this.setDefaultState(this.stateContainer.getBaseState());
   }

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
   }

   public StateContainer<Fluid, IFluidState> getStateContainer() {
      return this.stateContainer;
   }

   protected final void setDefaultState(IFluidState state) {
      this.defaultState = state;
   }

   public final IFluidState getDefaultState() {
      return this.defaultState;
   }

   public abstract Item getFilledBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
   }

   protected void tick(World worldIn, BlockPos pos, IFluidState state) {
   }

   protected void randomTick(World p_207186_1_, BlockPos pos, IFluidState state, Random random) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticleData() {
      return null;
   }

   protected abstract boolean canDisplace(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_);

   protected abstract Vec3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_);

   public abstract int getTickRate(IWorldReader p_205569_1_);

   protected boolean ticksRandomly() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getActualHeight(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_);

   public abstract float getHeight(IFluidState p_223407_1_);

   protected abstract BlockState getBlockState(IFluidState state);

   public abstract boolean isSource(IFluidState state);

   public abstract int getLevel(IFluidState p_207192_1_);

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == this;
   }

   public boolean isIn(Tag<Fluid> tagIn) {
      return tagIn.contains(this);
   }

   public abstract VoxelShape func_215664_b(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_);

   private final net.minecraftforge.common.util.ReverseTagWrapper<Fluid> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, net.minecraft.tags.FluidTags::getGeneration, net.minecraft.tags.FluidTags::getCollection);
   @Override
   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   /**
    * Creates the fluid attributes object, which will contain all the extended values for the fluid that aren't part of the vanilla system.
    * Do not call this from outside. To retrieve the values use {@link Fluid#getAttributes()}
    */
   protected net.minecraftforge.fluids.FluidAttributes createAttributes()
   {
      return net.minecraftforge.common.ForgeHooks.createVanillaFluidAttributes(this);
   }

   private net.minecraftforge.fluids.FluidAttributes forgeFluidAttributes;
   public final net.minecraftforge.fluids.FluidAttributes getAttributes() {
      if (forgeFluidAttributes == null)
         forgeFluidAttributes = createAttributes();
      return forgeFluidAttributes;
   }
}