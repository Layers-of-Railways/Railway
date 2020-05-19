package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.PaletteIdentity;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkSection {
   private static final IPalette<BlockState> REGISTRY_PALETTE = new PaletteIdentity<>(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
   private final int yBase;
   private short blockRefCount;
   private short blockTickRefCount;
   private short fluidRefCount;
   private final PalettedContainer<BlockState> data;

   public ChunkSection(int yBaseIn) {
      this(yBaseIn, (short)0, (short)0, (short)0);
   }

   public ChunkSection(int yBaseIn, short blockRefCountIn, short blockTickRefCountIn, short fluidRefCountIn) {
      this.yBase = yBaseIn;
      this.blockRefCount = blockRefCountIn;
      this.blockTickRefCount = blockTickRefCountIn;
      this.fluidRefCount = fluidRefCountIn;
      this.data = new PalettedContainer<>(REGISTRY_PALETTE, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
   }

   public BlockState getBlockState(int x, int y, int z) {
      return this.data.get(x, y, z);
   }

   public IFluidState getFluidState(int x, int y, int z) {
      return this.data.get(x, y, z).getFluidState();
   }

   public void lock() {
      this.data.lock();
   }

   public void unlock() {
      this.data.unlock();
   }

   public BlockState setBlockState(int x, int y, int z, BlockState blockStateIn) {
      return this.setBlockState(x, y, z, blockStateIn, true);
   }

   public BlockState setBlockState(int x, int y, int z, BlockState state, boolean useLocks) {
      BlockState blockstate;
      if (useLocks) {
         blockstate = this.data.lockedSwap(x, y, z, state);
      } else {
         blockstate = this.data.swap(x, y, z, state);
      }

      IFluidState ifluidstate = blockstate.getFluidState();
      IFluidState ifluidstate1 = state.getFluidState();
      if (!blockstate.isAir()) {
         --this.blockRefCount;
         if (blockstate.ticksRandomly()) {
            --this.blockTickRefCount;
         }
      }

      if (!ifluidstate.isEmpty()) {
         --this.fluidRefCount;
      }

      if (!state.isAir()) {
         ++this.blockRefCount;
         if (state.ticksRandomly()) {
            ++this.blockTickRefCount;
         }
      }

      if (!ifluidstate1.isEmpty()) {
         ++this.fluidRefCount;
      }

      return blockstate;
   }

   /**
    * Returns whether or not this block storage's Chunk is fully empty, based on its internal reference count.
    */
   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public static boolean isEmpty(@Nullable ChunkSection section) {
      return section == Chunk.EMPTY_SECTION || section.isEmpty();
   }

   public boolean needsRandomTickAny() {
      return this.needsRandomTick() || this.needsRandomTickFluid();
   }

   /**
    * Returns whether or not this block storage's Chunk will require random ticking, used to avoid looping through
    * random block ticks when there are no blocks that would randomly tick.
    */
   public boolean needsRandomTick() {
      return this.blockTickRefCount > 0;
   }

   public boolean needsRandomTickFluid() {
      return this.fluidRefCount > 0;
   }

   /**
    * Gets the y coordinate that this chunk section starts at (which is a multiple of 16). To get the y number, use
    * <code>section.getYLocation() >> 4</code>. Note that there is a section below the world for lighting purposes.
    */
   public int getYLocation() {
      return this.yBase;
   }

   public void recalculateRefCounts() {
      this.blockRefCount = 0;
      this.blockTickRefCount = 0;
      this.fluidRefCount = 0;
      this.data.count((p_225496_1_, p_225496_2_) -> {
         IFluidState ifluidstate = p_225496_1_.getFluidState();
         if (!p_225496_1_.isAir()) {
            this.blockRefCount = (short)(this.blockRefCount + p_225496_2_);
            if (p_225496_1_.ticksRandomly()) {
               this.blockTickRefCount = (short)(this.blockTickRefCount + p_225496_2_);
            }
         }

         if (!ifluidstate.isEmpty()) {
            this.blockRefCount = (short)(this.blockRefCount + p_225496_2_);
            if (ifluidstate.ticksRandomly()) {
               this.fluidRefCount = (short)(this.fluidRefCount + p_225496_2_);
            }
         }

      });
   }

   public PalettedContainer<BlockState> getData() {
      return this.data;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer packetBufferIn) {
      this.blockRefCount = packetBufferIn.readShort();
      this.data.read(packetBufferIn);
   }

   public void write(PacketBuffer packetBufferIn) {
      packetBufferIn.writeShort(this.blockRefCount);
      this.data.write(packetBufferIn);
   }

   public int getSize() {
      return 2 + this.data.getSerializedSize();
   }

   public boolean contains(BlockState state) {
      return this.data.contains(state);
   }
}