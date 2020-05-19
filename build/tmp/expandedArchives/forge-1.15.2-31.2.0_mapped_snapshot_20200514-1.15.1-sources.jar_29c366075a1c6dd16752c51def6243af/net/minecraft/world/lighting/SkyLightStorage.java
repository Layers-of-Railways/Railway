package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class SkyLightStorage extends SectionLightStorage<SkyLightStorage.StorageMap> {
   private static final Direction[] field_215554_k = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private final LongSet field_215555_l = new LongOpenHashSet();
   private final LongSet field_215556_m = new LongOpenHashSet();
   private final LongSet field_215557_n = new LongOpenHashSet();
   private final LongSet field_215558_o = new LongOpenHashSet();
   private volatile boolean field_215553_p;

   protected SkyLightStorage(IChunkLightProvider p_i51288_1_) {
      super(LightType.SKY, p_i51288_1_, new SkyLightStorage.StorageMap(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
   }

   protected int getLightOrDefault(long worldPos) {
      long i = SectionPos.worldToSection(worldPos);
      int j = SectionPos.extractY(i);
      SkyLightStorage.StorageMap skylightstorage$storagemap = this.uncachedLightData;
      int k = skylightstorage$storagemap.field_215653_c.get(SectionPos.toSectionColumnPos(i));
      if (k != skylightstorage$storagemap.field_215652_b && j < k) {
         NibbleArray nibblearray = this.getArray(skylightstorage$storagemap, i);
         if (nibblearray == null) {
            for(worldPos = BlockPos.func_218288_f(worldPos); nibblearray == null; nibblearray = this.getArray(skylightstorage$storagemap, i)) {
               i = SectionPos.withOffset(i, Direction.UP);
               ++j;
               if (j >= k) {
                  return 15;
               }

               worldPos = BlockPos.offset(worldPos, 0, 16, 0);
            }
         }

         return nibblearray.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
      } else {
         return 15;
      }
   }

   protected void func_215524_j(long p_215524_1_) {
      int i = SectionPos.extractY(p_215524_1_);
      if ((this.cachedLightData).field_215652_b > i) {
         (this.cachedLightData).field_215652_b = i;
         (this.cachedLightData).field_215653_c.defaultReturnValue((this.cachedLightData).field_215652_b);
      }

      long j = SectionPos.toSectionColumnPos(p_215524_1_);
      int k = (this.cachedLightData).field_215653_c.get(j);
      if (k < i + 1) {
         (this.cachedLightData).field_215653_c.put(j, i + 1);
         if (this.field_215558_o.contains(j)) {
            this.func_223404_q(p_215524_1_);
            if (k > (this.cachedLightData).field_215652_b) {
               long l = SectionPos.asLong(SectionPos.extractX(p_215524_1_), k - 1, SectionPos.extractZ(p_215524_1_));
               this.func_223403_p(l);
            }

            this.func_215552_e();
         }
      }

   }

   private void func_223403_p(long p_223403_1_) {
      this.field_215557_n.add(p_223403_1_);
      this.field_215556_m.remove(p_223403_1_);
   }

   private void func_223404_q(long p_223404_1_) {
      this.field_215556_m.add(p_223404_1_);
      this.field_215557_n.remove(p_223404_1_);
   }

   private void func_215552_e() {
      this.field_215553_p = !this.field_215556_m.isEmpty() || !this.field_215557_n.isEmpty();
   }

   protected void func_215523_k(long p_215523_1_) {
      long i = SectionPos.toSectionColumnPos(p_215523_1_);
      boolean flag = this.field_215558_o.contains(i);
      if (flag) {
         this.func_223403_p(p_215523_1_);
      }

      int j = SectionPos.extractY(p_215523_1_);
      if ((this.cachedLightData).field_215653_c.get(i) == j + 1) {
         long k;
         for(k = p_215523_1_; !this.hasSection(k) && this.func_215550_a(j); k = SectionPos.withOffset(k, Direction.DOWN)) {
            --j;
         }

         if (this.hasSection(k)) {
            (this.cachedLightData).field_215653_c.put(i, j + 1);
            if (flag) {
               this.func_223404_q(k);
            }
         } else {
            (this.cachedLightData).field_215653_c.remove(i);
         }
      }

      if (flag) {
         this.func_215552_e();
      }

   }

   protected void func_215526_b(long p_215526_1_, boolean p_215526_3_) {
      this.processAllLevelUpdates();
      if (p_215526_3_ && this.field_215558_o.add(p_215526_1_)) {
         int i = (this.cachedLightData).field_215653_c.get(p_215526_1_);
         if (i != (this.cachedLightData).field_215652_b) {
            long j = SectionPos.asLong(SectionPos.extractX(p_215526_1_), i - 1, SectionPos.extractZ(p_215526_1_));
            this.func_223404_q(j);
            this.func_215552_e();
         }
      } else if (!p_215526_3_) {
         this.field_215558_o.remove(p_215526_1_);
      }

   }

   protected boolean hasSectionsToUpdate() {
      return super.hasSectionsToUpdate() || this.field_215553_p;
   }

   protected NibbleArray getOrCreateArray(long sectionPosIn) {
      NibbleArray nibblearray = this.newArrays.get(sectionPosIn);
      if (nibblearray != null) {
         return nibblearray;
      } else {
         long i = SectionPos.withOffset(sectionPosIn, Direction.UP);
         int j = (this.cachedLightData).field_215653_c.get(SectionPos.toSectionColumnPos(sectionPosIn));
         if (j != (this.cachedLightData).field_215652_b && SectionPos.extractY(i) < j) {
            NibbleArray nibblearray1;
            while((nibblearray1 = this.getArray(i, true)) == null) {
               i = SectionPos.withOffset(i, Direction.UP);
            }

            return new NibbleArray((new NibbleArrayRepeater(nibblearray1, 0)).getData());
         } else {
            return new NibbleArray();
         }
      }
   }

   protected void updateSections(LightEngine<SkyLightStorage.StorageMap, ?> engine, boolean updateSkyLight, boolean updateBlockLight) {
      super.updateSections(engine, updateSkyLight, updateBlockLight);
      if (updateSkyLight) {
         if (!this.field_215556_m.isEmpty()) {
            for(long i : this.field_215556_m) {
               int j = this.getLevel(i);
               if (j != 2 && !this.field_215557_n.contains(i) && this.field_215555_l.add(i)) {
                  if (j == 1) {
                     this.cancelSectionUpdates(engine, i);
                     if (this.dirtyCachedSections.add(i)) {
                        this.cachedLightData.copyArray(i);
                     }

                     Arrays.fill(this.getArray(i, true).getData(), (byte)-1);
                     int i3 = SectionPos.toWorld(SectionPos.extractX(i));
                     int k3 = SectionPos.toWorld(SectionPos.extractY(i));
                     int i4 = SectionPos.toWorld(SectionPos.extractZ(i));

                     for(Direction direction : field_215554_k) {
                        long j1 = SectionPos.withOffset(i, direction);
                        if ((this.field_215557_n.contains(j1) || !this.field_215555_l.contains(j1) && !this.field_215556_m.contains(j1)) && this.hasSection(j1)) {
                           for(int k1 = 0; k1 < 16; ++k1) {
                              for(int l1 = 0; l1 < 16; ++l1) {
                                 long i2;
                                 long j2;
                                 switch(direction) {
                                 case NORTH:
                                    i2 = BlockPos.pack(i3 + k1, k3 + l1, i4);
                                    j2 = BlockPos.pack(i3 + k1, k3 + l1, i4 - 1);
                                    break;
                                 case SOUTH:
                                    i2 = BlockPos.pack(i3 + k1, k3 + l1, i4 + 16 - 1);
                                    j2 = BlockPos.pack(i3 + k1, k3 + l1, i4 + 16);
                                    break;
                                 case WEST:
                                    i2 = BlockPos.pack(i3, k3 + k1, i4 + l1);
                                    j2 = BlockPos.pack(i3 - 1, k3 + k1, i4 + l1);
                                    break;
                                 default:
                                    i2 = BlockPos.pack(i3 + 16 - 1, k3 + k1, i4 + l1);
                                    j2 = BlockPos.pack(i3 + 16, k3 + k1, i4 + l1);
                                 }

                                 engine.scheduleUpdate(i2, j2, engine.getEdgeLevel(i2, j2, 0), true);
                              }
                           }
                        }
                     }

                     for(int j4 = 0; j4 < 16; ++j4) {
                        for(int k4 = 0; k4 < 16; ++k4) {
                           long l4 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)), SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                           long i5 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)) - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                           engine.scheduleUpdate(l4, i5, engine.getEdgeLevel(l4, i5, 0), true);
                        }
                     }
                  } else {
                     for(int k = 0; k < 16; ++k) {
                        for(int l = 0; l < 16; ++l) {
                           long i1 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + k, SectionPos.toWorld(SectionPos.extractY(i)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + l);
                           engine.scheduleUpdate(Long.MAX_VALUE, i1, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.field_215556_m.clear();
         if (!this.field_215557_n.isEmpty()) {
            for(long k2 : this.field_215557_n) {
               if (this.field_215555_l.remove(k2) && this.hasSection(k2)) {
                  for(int l2 = 0; l2 < 16; ++l2) {
                     for(int j3 = 0; j3 < 16; ++j3) {
                        long l3 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(k2)) + l2, SectionPos.toWorld(SectionPos.extractY(k2)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(k2)) + j3);
                        engine.scheduleUpdate(Long.MAX_VALUE, l3, 15, false);
                     }
                  }
               }
            }
         }

         this.field_215557_n.clear();
         this.field_215553_p = false;
      }
   }

   protected boolean func_215550_a(int p_215550_1_) {
      return p_215550_1_ >= (this.cachedLightData).field_215652_b;
   }

   protected boolean func_215551_l(long p_215551_1_) {
      int i = BlockPos.unpackY(p_215551_1_);
      if ((i & 15) != 15) {
         return false;
      } else {
         long j = SectionPos.worldToSection(p_215551_1_);
         long k = SectionPos.toSectionColumnPos(j);
         if (!this.field_215558_o.contains(k)) {
            return false;
         } else {
            int l = (this.cachedLightData).field_215653_c.get(k);
            return SectionPos.toWorld(l) == i + 16;
         }
      }
   }

   protected boolean func_215549_m(long p_215549_1_) {
      long i = SectionPos.toSectionColumnPos(p_215549_1_);
      int j = (this.cachedLightData).field_215653_c.get(i);
      return j == (this.cachedLightData).field_215652_b || SectionPos.extractY(p_215549_1_) >= j;
   }

   protected boolean func_215548_n(long p_215548_1_) {
      long i = SectionPos.toSectionColumnPos(p_215548_1_);
      return this.field_215558_o.contains(i);
   }

   public static final class StorageMap extends LightDataMap<SkyLightStorage.StorageMap> {
      private int field_215652_b;
      private final Long2IntOpenHashMap field_215653_c;

      public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50496_1_, Long2IntOpenHashMap p_i50496_2_, int p_i50496_3_) {
         super(p_i50496_1_);
         this.field_215653_c = p_i50496_2_;
         p_i50496_2_.defaultReturnValue(p_i50496_3_);
         this.field_215652_b = p_i50496_3_;
      }

      public SkyLightStorage.StorageMap copy() {
         return new SkyLightStorage.StorageMap(this.arrays.clone(), this.field_215653_c.clone(), this.field_215652_b);
      }
   }
}