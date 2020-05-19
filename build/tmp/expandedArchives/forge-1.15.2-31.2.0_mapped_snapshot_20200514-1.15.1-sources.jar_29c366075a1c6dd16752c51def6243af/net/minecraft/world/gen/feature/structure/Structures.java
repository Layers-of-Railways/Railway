package net.minecraft.world.gen.feature.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Structures {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Structure<?> MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
   public static final Structure<?> PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
   public static final Structure<?> FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
   public static final Structure<?> STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
   public static final Structure<?> JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
   public static final Structure<?> OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
   public static final Structure<?> DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
   public static final Structure<?> IGLOO = register("Igloo", Feature.IGLOO);
   public static final Structure<?> SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
   public static final Structure<?> MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
   public static final Structure<?> ENDCITY = register("EndCity", Feature.END_CITY);
   public static final Structure<?> MANSION = register("Mansion", Feature.WOODLAND_MANSION);
   public static final Structure<?> BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
   public static final Structure<?> SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
   public static final Structure<?> VILLAGE = register("Village", Feature.VILLAGE);

   private static Structure<?> register(String key, Structure<?> p_215141_1_) {
      if (true) return p_215141_1_; // FORGE: Registry replaced with slave map
      return Registry.register(Registry.STRUCTURE_FEATURE, key.toLowerCase(Locale.ROOT), p_215141_1_);
   }

   public static void init() {
   }

   @Nullable
   public static StructureStart func_227456_a_(ChunkGenerator<?> p_227456_0_, TemplateManager p_227456_1_, CompoundNBT p_227456_2_) {
      String s = p_227456_2_.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.DUMMY;
      } else {
         Structure<?> structure = Registry.STRUCTURE_FEATURE.getOrDefault(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
         if (structure == null) {
            LOGGER.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            int i = p_227456_2_.getInt("ChunkX");
            int j = p_227456_2_.getInt("ChunkZ");
            int k = p_227456_2_.getInt("references");
            MutableBoundingBox mutableboundingbox = p_227456_2_.contains("BB") ? new MutableBoundingBox(p_227456_2_.getIntArray("BB")) : MutableBoundingBox.getNewBoundingBox();
            ListNBT listnbt = p_227456_2_.getList("Children", 10);

            try {
               StructureStart structurestart = structure.getStartFactory().create(structure, i, j, mutableboundingbox, k, p_227456_0_.getSeed());

               for(int l = 0; l < listnbt.size(); ++l) {
                  CompoundNBT compoundnbt = listnbt.getCompound(l);
                  String s1 = compoundnbt.getString("id");
                  IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.getOrDefault(new ResourceLocation(s1.toLowerCase(Locale.ROOT)));
                  if (istructurepiecetype == null) {
                     LOGGER.error("Unknown structure piece id: {}", (Object)s1);
                  } else {
                     try {
                        StructurePiece structurepiece = istructurepiecetype.load(p_227456_1_, compoundnbt);
                        structurestart.components.add(structurepiece);
                     } catch (Exception exception) {
                        LOGGER.error("Exception loading structure piece with id {}", s1, exception);
                     }
                  }
               }

               return structurestart;
            } catch (Exception exception1) {
               LOGGER.error("Failed Start with id {}", s, exception1);
               return null;
            }
         }
      }
   }
}