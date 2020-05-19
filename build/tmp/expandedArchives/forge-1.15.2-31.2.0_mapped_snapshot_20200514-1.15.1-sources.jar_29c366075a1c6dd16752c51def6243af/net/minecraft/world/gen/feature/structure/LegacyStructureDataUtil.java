package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class LegacyStructureDataUtil {
   private static final Map<String, String> field_208220_b = Util.make(Maps.newHashMap(), (p_208213_0_) -> {
      p_208213_0_.put("Village", "Village");
      p_208213_0_.put("Mineshaft", "Mineshaft");
      p_208213_0_.put("Mansion", "Mansion");
      p_208213_0_.put("Igloo", "Temple");
      p_208213_0_.put("Desert_Pyramid", "Temple");
      p_208213_0_.put("Jungle_Pyramid", "Temple");
      p_208213_0_.put("Swamp_Hut", "Temple");
      p_208213_0_.put("Stronghold", "Stronghold");
      p_208213_0_.put("Monument", "Monument");
      p_208213_0_.put("Fortress", "Fortress");
      p_208213_0_.put("EndCity", "EndCity");
   });
   private static final Map<String, String> field_208221_c = Util.make(Maps.newHashMap(), (p_208215_0_) -> {
      p_208215_0_.put("Iglu", "Igloo");
      p_208215_0_.put("TeDP", "Desert_Pyramid");
      p_208215_0_.put("TeJP", "Jungle_Pyramid");
      p_208215_0_.put("TeSH", "Swamp_Hut");
   });
   private final boolean field_208222_d;
   private final Map<String, Long2ObjectMap<CompoundNBT>> field_208223_e = Maps.newHashMap();
   private final Map<String, StructureIndexesSavedData> field_208224_f = Maps.newHashMap();
   private final List<String> field_215132_f;
   private final List<String> field_215133_g;

   public LegacyStructureDataUtil(@Nullable DimensionSavedDataManager p_i51349_1_, List<String> p_i51349_2_, List<String> p_i51349_3_) {
      this.field_215132_f = p_i51349_2_;
      this.field_215133_g = p_i51349_3_;
      this.func_212184_a(p_i51349_1_);
      boolean flag = false;

      for(String s : this.field_215133_g) {
         flag |= this.field_208223_e.get(s) != null;
      }

      this.field_208222_d = flag;
   }

   public void func_208216_a(long p_208216_1_) {
      for(String s : this.field_215132_f) {
         StructureIndexesSavedData structureindexessaveddata = this.field_208224_f.get(s);
         if (structureindexessaveddata != null && structureindexessaveddata.func_208023_c(p_208216_1_)) {
            structureindexessaveddata.func_201762_c(p_208216_1_);
            structureindexessaveddata.markDirty();
         }
      }

   }

   public CompoundNBT func_212181_a(CompoundNBT p_212181_1_) {
      CompoundNBT compoundnbt = p_212181_1_.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (this.func_208209_a(chunkpos.x, chunkpos.z)) {
         p_212181_1_ = this.func_212182_a(p_212181_1_, chunkpos);
      }

      CompoundNBT compoundnbt1 = compoundnbt.getCompound("Structures");
      CompoundNBT compoundnbt2 = compoundnbt1.getCompound("References");

      for(String s : this.field_215133_g) {
         Structure<?> structure = Feature.STRUCTURES.get(s.toLowerCase(Locale.ROOT));
         if (!compoundnbt2.contains(s, 12) && structure != null) {
            int i = structure.getSize();
            LongList longlist = new LongArrayList();

            for(int j = chunkpos.x - i; j <= chunkpos.x + i; ++j) {
               for(int k = chunkpos.z - i; k <= chunkpos.z + i; ++k) {
                  if (this.func_208211_a(j, k, s)) {
                     longlist.add(ChunkPos.asLong(j, k));
                  }
               }
            }

            compoundnbt2.putLongArray(s, longlist);
         }
      }

      compoundnbt1.put("References", compoundnbt2);
      compoundnbt.put("Structures", compoundnbt1);
      p_212181_1_.put("Level", compoundnbt);
      return p_212181_1_;
   }

   private boolean func_208211_a(int p_208211_1_, int p_208211_2_, String p_208211_3_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         return this.field_208223_e.get(p_208211_3_) != null && this.field_208224_f.get(field_208220_b.get(p_208211_3_)).func_208024_b(ChunkPos.asLong(p_208211_1_, p_208211_2_));
      }
   }

   private boolean func_208209_a(int p_208209_1_, int p_208209_2_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         for(String s : this.field_215133_g) {
            if (this.field_208223_e.get(s) != null && this.field_208224_f.get(field_208220_b.get(s)).func_208023_c(ChunkPos.asLong(p_208209_1_, p_208209_2_))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundNBT func_212182_a(CompoundNBT p_212182_1_, ChunkPos p_212182_2_) {
      CompoundNBT compoundnbt = p_212182_1_.getCompound("Level");
      CompoundNBT compoundnbt1 = compoundnbt.getCompound("Structures");
      CompoundNBT compoundnbt2 = compoundnbt1.getCompound("Starts");

      for(String s : this.field_215133_g) {
         Long2ObjectMap<CompoundNBT> long2objectmap = this.field_208223_e.get(s);
         if (long2objectmap != null) {
            long i = p_212182_2_.asLong();
            if (this.field_208224_f.get(field_208220_b.get(s)).func_208023_c(i)) {
               CompoundNBT compoundnbt3 = long2objectmap.get(i);
               if (compoundnbt3 != null) {
                  compoundnbt2.put(s, compoundnbt3);
               }
            }
         }
      }

      compoundnbt1.put("Starts", compoundnbt2);
      compoundnbt.put("Structures", compoundnbt1);
      p_212182_1_.put("Level", compoundnbt);
      return p_212182_1_;
   }

   private void func_212184_a(@Nullable DimensionSavedDataManager p_212184_1_) {
      if (p_212184_1_ != null) {
         for(String s : this.field_215132_f) {
            CompoundNBT compoundnbt = new CompoundNBT();

            try {
               compoundnbt = p_212184_1_.load(s, 1493).getCompound("data").getCompound("Features");
               if (compoundnbt.isEmpty()) {
                  continue;
               }
            } catch (IOException var13) {
               ;
            }

            for(String s1 : compoundnbt.keySet()) {
               CompoundNBT compoundnbt1 = compoundnbt.getCompound(s1);
               long i = ChunkPos.asLong(compoundnbt1.getInt("ChunkX"), compoundnbt1.getInt("ChunkZ"));
               ListNBT listnbt = compoundnbt1.getList("Children", 10);
               if (!listnbt.isEmpty()) {
                  String s3 = listnbt.getCompound(0).getString("id");
                  String s4 = field_208221_c.get(s3);
                  if (s4 != null) {
                     compoundnbt1.putString("id", s4);
                  }
               }

               String s6 = compoundnbt1.getString("id");
               this.field_208223_e.computeIfAbsent(s6, (p_208208_0_) -> {
                  return new Long2ObjectOpenHashMap<>();
               }).put(i, compoundnbt1);
            }

            String s5 = s + "_index";
            StructureIndexesSavedData structureindexessaveddata = p_212184_1_.getOrCreate(() -> {
               return new StructureIndexesSavedData(s5);
            }, s5);
            if (!structureindexessaveddata.getAll().isEmpty()) {
               this.field_208224_f.put(s, structureindexessaveddata);
            } else {
               StructureIndexesSavedData structureindexessaveddata1 = new StructureIndexesSavedData(s5);
               this.field_208224_f.put(s, structureindexessaveddata1);

               for(String s2 : compoundnbt.keySet()) {
                  CompoundNBT compoundnbt2 = compoundnbt.getCompound(s2);
                  structureindexessaveddata1.func_201763_a(ChunkPos.asLong(compoundnbt2.getInt("ChunkX"), compoundnbt2.getInt("ChunkZ")));
               }

               structureindexessaveddata1.markDirty();
            }
         }

      }
   }

   public static LegacyStructureDataUtil func_215130_a(DimensionType p_215130_0_, @Nullable DimensionSavedDataManager p_215130_1_) {
      if (p_215130_0_ == DimensionType.OVERWORLD) {
         return new LegacyStructureDataUtil(p_215130_1_, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
      } else if (p_215130_0_ == DimensionType.THE_NETHER) {
         List<String> list1 = ImmutableList.of("Fortress");
         return new LegacyStructureDataUtil(p_215130_1_, list1, list1);
      } else if (p_215130_0_ == DimensionType.THE_END) {
         List<String> list = ImmutableList.of("EndCity");
         return new LegacyStructureDataUtil(p_215130_1_, list, list);
      } else {
         throw new RuntimeException(String.format("Unknown dimension type : %s", p_215130_0_));
      }
   }
}