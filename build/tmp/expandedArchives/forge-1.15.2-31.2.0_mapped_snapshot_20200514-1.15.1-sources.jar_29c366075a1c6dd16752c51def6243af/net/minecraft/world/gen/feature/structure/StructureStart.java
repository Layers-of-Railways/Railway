package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructureStart {
   public static final StructureStart DUMMY = new StructureStart(Feature.MINESHAFT, 0, 0, MutableBoundingBox.getNewBoundingBox(), 0, 0L) {
      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
      }
   };
   private final Structure<?> structure;
   protected final List<StructurePiece> components = Lists.newArrayList();
   protected MutableBoundingBox bounds;
   private final int chunkPosX;
   private final int chunkPosZ;
   private int references;
   protected final SharedSeedRandom rand;

   public StructureStart(Structure<?> p_i225876_1_, int p_i225876_2_, int p_i225876_3_, MutableBoundingBox p_i225876_4_, int p_i225876_5_, long p_i225876_6_) {
      this.structure = p_i225876_1_;
      this.chunkPosX = p_i225876_2_;
      this.chunkPosZ = p_i225876_3_;
      this.references = p_i225876_5_;
      this.rand = new SharedSeedRandom();
      this.rand.setLargeFeatureSeed(p_i225876_6_, p_i225876_2_, p_i225876_3_);
      this.bounds = p_i225876_4_;
   }

   public abstract void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn);

   public MutableBoundingBox getBoundingBox() {
      return this.bounds;
   }

   public List<StructurePiece> getComponents() {
      return this.components;
   }

   public void generateStructure(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
      synchronized(this.components) {
         Iterator<StructurePiece> iterator = this.components.iterator();

         while(iterator.hasNext()) {
            StructurePiece structurepiece = iterator.next();
            if (structurepiece.getBoundingBox().intersectsWith(p_225565_4_) && !structurepiece.create(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_)) {
               iterator.remove();
            }
         }

         this.recalculateStructureSize();
      }
   }

   protected void recalculateStructureSize() {
      this.bounds = MutableBoundingBox.getNewBoundingBox();

      for(StructurePiece structurepiece : this.components) {
         this.bounds.expandTo(structurepiece.getBoundingBox());
      }

   }

   public CompoundNBT write(int chunkX, int chunkZ) {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (this.isValid()) {
         if (Registry.STRUCTURE_FEATURE.getKey(this.getStructure()) == null) { // FORGE: This is just a more friendly error instead of the 'Null String' below
            throw new RuntimeException("StructureStart \"" + this.getClass().getName() + "\": \"" + this.getStructure() + "\" missing ID Mapping, Modder see MapGenStructureIO");
         }
         compoundnbt.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getStructure()).toString());
         compoundnbt.putInt("ChunkX", chunkX);
         compoundnbt.putInt("ChunkZ", chunkZ);
         compoundnbt.putInt("references", this.references);
         compoundnbt.put("BB", this.bounds.toNBTTagIntArray());
         ListNBT lvt_4_1_ = new ListNBT();
         synchronized(this.components) {
            for(StructurePiece structurepiece : this.components) {
               lvt_4_1_.add(structurepiece.write());
            }
         }

         compoundnbt.put("Children", lvt_4_1_);
         return compoundnbt;
      } else {
         compoundnbt.putString("id", "INVALID");
         return compoundnbt;
      }
   }

   protected void func_214628_a(int p_214628_1_, Random p_214628_2_, int p_214628_3_) {
      int i = p_214628_1_ - p_214628_3_;
      int j = this.bounds.getYSize() + 1;
      if (j < i) {
         j += p_214628_2_.nextInt(i - j);
      }

      int k = j - this.bounds.maxY;
      this.bounds.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   protected void func_214626_a(Random p_214626_1_, int p_214626_2_, int p_214626_3_) {
      int i = p_214626_3_ - p_214626_2_ + 1 - this.bounds.getYSize();
      int j;
      if (i > 1) {
         j = p_214626_2_ + p_214626_1_.nextInt(i);
      } else {
         j = p_214626_2_;
      }

      int k = j - this.bounds.minY;
      this.bounds.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   /**
    * currently only defined for Villages, returns true if Village has more than 2 non-road components
    */
   public boolean isValid() {
      return !this.components.isEmpty();
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }

   public BlockPos getPos() {
      return new BlockPos(this.chunkPosX << 4, 0, this.chunkPosZ << 4);
   }

   public boolean isRefCountBelowMax() {
      return this.references < this.getMaxRefCount();
   }

   public void incrementRefCount() {
      ++this.references;
   }

   public int getRefCount() {
      return this.references;
   }

   protected int getMaxRefCount() {
      return 1;
   }

   public Structure<?> getStructure() {
      return this.structure;
   }
}