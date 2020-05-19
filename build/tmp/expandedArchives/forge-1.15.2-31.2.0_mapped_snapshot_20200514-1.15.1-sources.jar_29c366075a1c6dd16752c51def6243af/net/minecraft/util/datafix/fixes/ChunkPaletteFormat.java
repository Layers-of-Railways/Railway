package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPaletteFormat extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final BitSet VIRTUAL = new BitSet(256);
   private static final BitSet FIX = new BitSet(256);
   private static final Dynamic<?> PUMPKIN = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:pumpkin'}");
   private static final Dynamic<?> SNOWY_PODZOL = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
   private static final Dynamic<?> SNOWY_GRASS = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
   private static final Dynamic<?> SNOWY_MYCELIUM = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
   private static final Dynamic<?> UPPER_SUNFLOWER = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
   private static final Dynamic<?> UPPER_LILAC = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
   private static final Dynamic<?> UPPER_TALL_GRASS = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
   private static final Dynamic<?> UPPER_LARGE_FERN = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
   private static final Dynamic<?> UPPER_ROSE_BUSH = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
   private static final Dynamic<?> UPPER_PEONY = BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:peony',Properties:{half:'upper'}}");
   private static final Map<String, Dynamic<?>> FLOWER_POT_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209306_0_) -> {
      p_209306_0_.put("minecraft:air0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:flower_pot'}"));
      p_209306_0_.put("minecraft:red_flower0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_poppy'}"));
      p_209306_0_.put("minecraft:red_flower1", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_blue_orchid'}"));
      p_209306_0_.put("minecraft:red_flower2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_allium'}"));
      p_209306_0_.put("minecraft:red_flower3", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_azure_bluet'}"));
      p_209306_0_.put("minecraft:red_flower4", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_red_tulip'}"));
      p_209306_0_.put("minecraft:red_flower5", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_orange_tulip'}"));
      p_209306_0_.put("minecraft:red_flower6", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_white_tulip'}"));
      p_209306_0_.put("minecraft:red_flower7", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_pink_tulip'}"));
      p_209306_0_.put("minecraft:red_flower8", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_oxeye_daisy'}"));
      p_209306_0_.put("minecraft:yellow_flower0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dandelion'}"));
      p_209306_0_.put("minecraft:sapling0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_oak_sapling'}"));
      p_209306_0_.put("minecraft:sapling1", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_spruce_sapling'}"));
      p_209306_0_.put("minecraft:sapling2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_birch_sapling'}"));
      p_209306_0_.put("minecraft:sapling3", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_jungle_sapling'}"));
      p_209306_0_.put("minecraft:sapling4", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_acacia_sapling'}"));
      p_209306_0_.put("minecraft:sapling5", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dark_oak_sapling'}"));
      p_209306_0_.put("minecraft:red_mushroom0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_red_mushroom'}"));
      p_209306_0_.put("minecraft:brown_mushroom0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_brown_mushroom'}"));
      p_209306_0_.put("minecraft:deadbush0", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_dead_bush'}"));
      p_209306_0_.put("minecraft:tallgrass2", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:potted_fern'}"));
      p_209306_0_.put("minecraft:cactus0", BlockStateFlatteningMap.getFixedNBTForID(2240));
   });
   private static final Map<String, Dynamic<?>> SKULL_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209308_0_) -> {
      mapSkull(p_209308_0_, 0, "skeleton", "skull");
      mapSkull(p_209308_0_, 1, "wither_skeleton", "skull");
      mapSkull(p_209308_0_, 2, "zombie", "head");
      mapSkull(p_209308_0_, 3, "player", "head");
      mapSkull(p_209308_0_, 4, "creeper", "head");
      mapSkull(p_209308_0_, 5, "dragon", "head");
   });
   private static final Map<String, Dynamic<?>> DOOR_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209298_0_) -> {
      mapDoor(p_209298_0_, "oak_door", 1024);
      mapDoor(p_209298_0_, "iron_door", 1136);
      mapDoor(p_209298_0_, "spruce_door", 3088);
      mapDoor(p_209298_0_, "birch_door", 3104);
      mapDoor(p_209298_0_, "jungle_door", 3120);
      mapDoor(p_209298_0_, "acacia_door", 3136);
      mapDoor(p_209298_0_, "dark_oak_door", 3152);
   });
   private static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209302_0_) -> {
      for(int i = 0; i < 26; ++i) {
         p_209302_0_.put("true" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
         p_209302_0_.put("false" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
      }

   });
   private static final Int2ObjectMap<String> DYE_COLOR_MAP = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_209296_0_) -> {
      p_209296_0_.put(0, "white");
      p_209296_0_.put(1, "orange");
      p_209296_0_.put(2, "magenta");
      p_209296_0_.put(3, "light_blue");
      p_209296_0_.put(4, "yellow");
      p_209296_0_.put(5, "lime");
      p_209296_0_.put(6, "pink");
      p_209296_0_.put(7, "gray");
      p_209296_0_.put(8, "light_gray");
      p_209296_0_.put(9, "cyan");
      p_209296_0_.put(10, "purple");
      p_209296_0_.put(11, "blue");
      p_209296_0_.put(12, "brown");
      p_209296_0_.put(13, "green");
      p_209296_0_.put(14, "red");
      p_209296_0_.put(15, "black");
   });
   private static final Map<String, Dynamic<?>> BED_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209304_0_) -> {
      for(Entry<String> entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "red")) {
            addBeds(p_209304_0_, entry.getIntKey(), entry.getValue());
         }
      }

   });
   private static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209299_0_) -> {
      for(Entry<String> entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
         if (!Objects.equals(entry.getValue(), "white")) {
            addBanners(p_209299_0_, 15 - entry.getIntKey(), entry.getValue());
         }
      }

   });
   private static final Dynamic<?> AIR = BlockStateFlatteningMap.getFixedNBTForID(0);

   public ChunkPaletteFormat(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   private static void mapSkull(Map<String, Dynamic<?>> p_209300_0_, int p_209300_1_, String p_209300_2_, String p_209300_3_) {
      p_209300_0_.put(p_209300_1_ + "north", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'north'}}"));
      p_209300_0_.put(p_209300_1_ + "east", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'east'}}"));
      p_209300_0_.put(p_209300_1_ + "south", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'south'}}"));
      p_209300_0_.put(p_209300_1_ + "west", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_wall_" + p_209300_3_ + "',Properties:{facing:'west'}}"));

      for(int i = 0; i < 16; ++i) {
         p_209300_0_.put(p_209300_1_ + "" + i, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209300_2_ + "_" + p_209300_3_ + "',Properties:{rotation:'" + i + "'}}"));
      }

   }

   private static void mapDoor(Map<String, Dynamic<?>> p_209301_0_, String p_209301_1_, int p_209301_2_) {
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 4));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperleftfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 8));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperleftfalsetrue", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 10));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 9));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrightfalsetrue", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 11));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "eastupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 3));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 7));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "northupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 1));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 5));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "southupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrightfalsefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 2));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrighttruefalse", BlockStateFlatteningMap.getFixedNBTForID(p_209301_2_ + 6));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westlowerrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperleftfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperleftfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperlefttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperlefttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrightfalsefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrightfalsetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrighttruefalse", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      p_209301_0_.put("minecraft:" + p_209301_1_ + "westupperrighttruetrue", BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209301_1_ + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
   }

   private static void addBeds(Map<String, Dynamic<?>> p_209307_0_, int p_209307_1_, String p_209307_2_) {
      p_209307_0_.put("southfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("westfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("northfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("eastfalsefoot" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
      p_209307_0_.put("southfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
      p_209307_0_.put("westfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
      p_209307_0_.put("northfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
      p_209307_0_.put("eastfalsehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
      p_209307_0_.put("southtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
      p_209307_0_.put("westtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
      p_209307_0_.put("northtruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
      p_209307_0_.put("easttruehead" + p_209307_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209307_2_ + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
   }

   private static void addBanners(Map<String, Dynamic<?>> p_209297_0_, int p_209297_1_, String p_209297_2_) {
      for(int i = 0; i < 16; ++i) {
         p_209297_0_.put("" + i + "_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_banner',Properties:{rotation:'" + i + "'}}"));
      }

      p_209297_0_.put("north_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'north'}}"));
      p_209297_0_.put("south_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'south'}}"));
      p_209297_0_.put("west_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'west'}}"));
      p_209297_0_.put("east_" + p_209297_1_, BlockStateFlatteningMap.makeDynamic("{Name:'minecraft:" + p_209297_2_ + "_wall_banner',Properties:{facing:'east'}}"));
   }

   public static String getName(Dynamic<?> p_209726_0_) {
      return p_209726_0_.get("Name").asString("");
   }

   public static String getProperty(Dynamic<?> p_209719_0_, String p_209719_1_) {
      return p_209719_0_.get("Properties").get(p_209719_1_).asString("");
   }

   public static int idFor(IntIdentityHashBiMap<Dynamic<?>> p_209724_0_, Dynamic<?> p_209724_1_) {
      int i = p_209724_0_.getId(p_209724_1_);
      if (i == -1) {
         i = p_209724_0_.add(p_209724_1_);
      }

      return i;
   }

   private Dynamic<?> fix(Dynamic<?> p_209712_1_) {
      Optional<? extends Dynamic<?>> optional = p_209712_1_.get("Level").get();
      return optional.isPresent() && optional.get().get("Sections").asStreamOpt().isPresent() ? p_209712_1_.set("Level", (new ChunkPaletteFormat.UpgradeChunk(optional.get())).write()) : p_209712_1_;
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", type, type1, this::fix);
   }

   public static int getSideMask(boolean p_210957_0_, boolean p_210957_1_, boolean p_210957_2_, boolean p_210957_3_) {
      int i = 0;
      if (p_210957_2_) {
         if (p_210957_1_) {
            i |= 2;
         } else if (p_210957_0_) {
            i |= 128;
         } else {
            i |= 1;
         }
      } else if (p_210957_3_) {
         if (p_210957_0_) {
            i |= 32;
         } else if (p_210957_1_) {
            i |= 8;
         } else {
            i |= 16;
         }
      } else if (p_210957_1_) {
         i |= 4;
      } else if (p_210957_0_) {
         i |= 64;
      }

      return i;
   }

   static {
      FIX.set(2);
      FIX.set(3);
      FIX.set(110);
      FIX.set(140);
      FIX.set(144);
      FIX.set(25);
      FIX.set(86);
      FIX.set(26);
      FIX.set(176);
      FIX.set(177);
      FIX.set(175);
      FIX.set(64);
      FIX.set(71);
      FIX.set(193);
      FIX.set(194);
      FIX.set(195);
      FIX.set(196);
      FIX.set(197);
      VIRTUAL.set(54);
      VIRTUAL.set(146);
      VIRTUAL.set(25);
      VIRTUAL.set(26);
      VIRTUAL.set(51);
      VIRTUAL.set(53);
      VIRTUAL.set(67);
      VIRTUAL.set(108);
      VIRTUAL.set(109);
      VIRTUAL.set(114);
      VIRTUAL.set(128);
      VIRTUAL.set(134);
      VIRTUAL.set(135);
      VIRTUAL.set(136);
      VIRTUAL.set(156);
      VIRTUAL.set(163);
      VIRTUAL.set(164);
      VIRTUAL.set(180);
      VIRTUAL.set(203);
      VIRTUAL.set(55);
      VIRTUAL.set(85);
      VIRTUAL.set(113);
      VIRTUAL.set(188);
      VIRTUAL.set(189);
      VIRTUAL.set(190);
      VIRTUAL.set(191);
      VIRTUAL.set(192);
      VIRTUAL.set(93);
      VIRTUAL.set(94);
      VIRTUAL.set(101);
      VIRTUAL.set(102);
      VIRTUAL.set(160);
      VIRTUAL.set(106);
      VIRTUAL.set(107);
      VIRTUAL.set(183);
      VIRTUAL.set(184);
      VIRTUAL.set(185);
      VIRTUAL.set(186);
      VIRTUAL.set(187);
      VIRTUAL.set(132);
      VIRTUAL.set(139);
      VIRTUAL.set(199);
   }

   public static enum Direction {
      DOWN(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Y),
      UP(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Y),
      NORTH(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Z),
      SOUTH(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Z),
      WEST(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.X),
      EAST(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.X);

      private final ChunkPaletteFormat.Direction.Axis axis;
      private final ChunkPaletteFormat.Direction.Offset axisDirection;

      private Direction(ChunkPaletteFormat.Direction.Offset axisIn, ChunkPaletteFormat.Direction.Axis directionIn) {
         this.axis = directionIn;
         this.axisDirection = axisIn;
      }

      public ChunkPaletteFormat.Direction.Offset getAxisDirection() {
         return this.axisDirection;
      }

      public ChunkPaletteFormat.Direction.Axis getAxis() {
         return this.axis;
      }

      public static enum Axis {
         X,
         Y,
         Z;
      }

      public static enum Offset {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private Offset(int p_i49694_3_) {
            this.step = p_i49694_3_;
         }

         public int getStep() {
            return this.step;
         }
      }
   }

   static class NibbleArray {
      private final byte[] data;

      public NibbleArray() {
         this.data = new byte[2048];
      }

      public NibbleArray(byte[] p_i49577_1_) {
         this.data = p_i49577_1_;
         if (p_i49577_1_.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_i49577_1_.length);
         }
      }

      public int get(int p_210932_1_, int p_210932_2_, int p_210932_3_) {
         int i = this.getPosition(p_210932_2_ << 8 | p_210932_3_ << 4 | p_210932_1_);
         return this.isFirst(p_210932_2_ << 8 | p_210932_3_ << 4 | p_210932_1_) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
      }

      private boolean isFirst(int p_210933_1_) {
         return (p_210933_1_ & 1) == 0;
      }

      private int getPosition(int p_210934_1_) {
         return p_210934_1_ >> 1;
      }
   }

   static class Section {
      private final IntIdentityHashBiMap<Dynamic<?>> palette = new IntIdentityHashBiMap<>(32);
      private Dynamic<?> listTag;
      private final Dynamic<?> section;
      private final boolean hasData;
      private final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap<>();
      private final IntList update = new IntArrayList();
      public final int y;
      private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(Dynamic<?> chunkSectionIn) {
         this.listTag = chunkSectionIn.emptyList();
         this.section = chunkSectionIn;
         this.y = chunkSectionIn.get("Y").asInt(0);
         this.hasData = chunkSectionIn.get("Blocks").get().isPresent();
      }

      public Dynamic<?> getBlock(int p_210056_1_) {
         if (p_210056_1_ >= 0 && p_210056_1_ <= 4095) {
            Dynamic<?> dynamic = this.palette.getByValue(this.buffer[p_210056_1_]);
            return dynamic == null ? ChunkPaletteFormat.AIR : dynamic;
         } else {
            return ChunkPaletteFormat.AIR;
         }
      }

      public void setBlock(int p_210053_1_, Dynamic<?> p_210053_2_) {
         if (this.seen.add(p_210053_2_)) {
            this.listTag = this.listTag.merge("%%FILTER_ME%%".equals(ChunkPaletteFormat.getName(p_210053_2_)) ? ChunkPaletteFormat.AIR : p_210053_2_);
         }

         this.buffer[p_210053_1_] = ChunkPaletteFormat.idFor(this.palette, p_210053_2_);
      }

      public int upgrade(int p_199207_1_) {
         if (!this.hasData) {
            return p_199207_1_;
         } else {
            ByteBuffer bytebuffer = this.section.get("Blocks").asByteBufferOpt().get();
            ChunkPaletteFormat.NibbleArray chunkpaletteformat$nibblearray = this.section.get("Data").asByteBufferOpt().map((p_210055_0_) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(p_210055_0_));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            ChunkPaletteFormat.NibbleArray chunkpaletteformat$nibblearray1 = this.section.get("Add").asByteBufferOpt().map((p_210052_0_) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(p_210052_0_));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            this.seen.add(ChunkPaletteFormat.AIR);
            ChunkPaletteFormat.idFor(this.palette, ChunkPaletteFormat.AIR);
            this.listTag = this.listTag.merge(ChunkPaletteFormat.AIR);

            for(int i = 0; i < 4096; ++i) {
               int j = i & 15;
               int k = i >> 8 & 15;
               int l = i >> 4 & 15;
               int i1 = chunkpaletteformat$nibblearray1.get(j, k, l) << 12 | (bytebuffer.get(i) & 255) << 4 | chunkpaletteformat$nibblearray.get(j, k, l);
               if (ChunkPaletteFormat.FIX.get(i1 >> 4)) {
                  this.addFix(i1 >> 4, i);
               }

               if (ChunkPaletteFormat.VIRTUAL.get(i1 >> 4)) {
                  int j1 = ChunkPaletteFormat.getSideMask(j == 0, j == 15, l == 0, l == 15);
                  if (j1 == 0) {
                     this.update.add(i);
                  } else {
                     p_199207_1_ |= j1;
                  }
               }

               this.setBlock(i, BlockStateFlatteningMap.getFixedNBTForID(i1));
            }

            return p_199207_1_;
         }
      }

      private void addFix(int p_199205_1_, int p_199205_2_) {
         IntList intlist = this.toFix.get(p_199205_1_);
         if (intlist == null) {
            intlist = new IntArrayList();
            this.toFix.put(p_199205_1_, intlist);
         }

         intlist.add(p_199205_2_);
      }

      public Dynamic<?> write() {
         Dynamic<?> dynamic = this.section;
         if (!this.hasData) {
            return dynamic;
         } else {
            dynamic = dynamic.set("Palette", this.listTag);
            int i = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            BitArray bitarray = new BitArray(i, 4096);

            for(int j = 0; j < this.buffer.length; ++j) {
               bitarray.setAt(j, this.buffer[j]);
            }

            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(bitarray.getBackingLongArray())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
         }
      }
   }

   static final class UpgradeChunk {
      private int sides;
      private final ChunkPaletteFormat.Section[] sections = new ChunkPaletteFormat.Section[16];
      private final Dynamic<?> level;
      private final int x;
      private final int z;
      private final Int2ObjectMap<Dynamic<?>> tileEntities = new Int2ObjectLinkedOpenHashMap<>(16);

      public UpgradeChunk(Dynamic<?> chunkIn) {
         this.level = chunkIn;
         this.x = chunkIn.get("xPos").asInt(0) << 4;
         this.z = chunkIn.get("zPos").asInt(0) << 4;
         chunkIn.get("TileEntities").asStreamOpt().ifPresent((p_210061_1_) -> {
            p_210061_1_.forEach((p_210063_1_) -> {
               int l3 = p_210063_1_.get("x").asInt(0) - this.x & 15;
               int i4 = p_210063_1_.get("y").asInt(0);
               int j4 = p_210063_1_.get("z").asInt(0) - this.z & 15;
               int k4 = i4 << 8 | j4 << 4 | l3;
               if (this.tileEntities.put(k4, p_210063_1_) != null) {
                  ChunkPaletteFormat.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, l3, i4, j4);
               }

            });
         });
         boolean flag = chunkIn.get("convertedFromAlphaFormat").asBoolean(false);
         chunkIn.get("Sections").asStreamOpt().ifPresent((p_210062_1_) -> {
            p_210062_1_.forEach((p_210065_1_) -> {
               ChunkPaletteFormat.Section chunkpaletteformat$section1 = new ChunkPaletteFormat.Section(p_210065_1_);
               this.sides = chunkpaletteformat$section1.upgrade(this.sides);
               this.sections[chunkpaletteformat$section1.y] = chunkpaletteformat$section1;
            });
         });

         for(ChunkPaletteFormat.Section chunkpaletteformat$section : this.sections) {
            if (chunkpaletteformat$section != null) {
               label258:
               for(java.util.Map.Entry<Integer, IntList> entry : chunkpaletteformat$section.toFix.entrySet()) {
                  int i = chunkpaletteformat$section.y << 12;
                  switch(entry.getKey()) {
                  case 2:
                     IntListIterator intlistiterator9 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator9.hasNext()) {
                           continue label258;
                        }

                        int i3 = intlistiterator9.next();
                        i3 = i3 | i;
                        Dynamic<?> dynamic11 = this.getBlock(i3);
                        if ("minecraft:grass_block".equals(ChunkPaletteFormat.getName(dynamic11))) {
                           String s12 = ChunkPaletteFormat.getName(this.getBlock(relative(i3, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s12) || "minecraft:snow_layer".equals(s12)) {
                              this.setBlock(i3, ChunkPaletteFormat.SNOWY_GRASS);
                           }
                        }
                     }
                  case 3:
                     IntListIterator intlistiterator8 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator8.hasNext()) {
                           continue label258;
                        }

                        int l2 = intlistiterator8.next();
                        l2 = l2 | i;
                        Dynamic<?> dynamic10 = this.getBlock(l2);
                        if ("minecraft:podzol".equals(ChunkPaletteFormat.getName(dynamic10))) {
                           String s11 = ChunkPaletteFormat.getName(this.getBlock(relative(l2, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s11) || "minecraft:snow_layer".equals(s11)) {
                              this.setBlock(l2, ChunkPaletteFormat.SNOWY_PODZOL);
                           }
                        }
                     }
                  case 25:
                     IntListIterator intlistiterator7 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator7.hasNext()) {
                           continue label258;
                        }

                        int k2 = intlistiterator7.next();
                        k2 = k2 | i;
                        Dynamic<?> dynamic9 = this.removeTileEntity(k2);
                        if (dynamic9 != null) {
                           String s10 = Boolean.toString(dynamic9.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic9.get("note").asInt(0), 0), 24);
                           this.setBlock(k2, ChunkPaletteFormat.NOTE_BLOCK_MAP.getOrDefault(s10, ChunkPaletteFormat.NOTE_BLOCK_MAP.get("false0")));
                        }
                     }
                  case 26:
                     IntListIterator intlistiterator6 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator6.hasNext()) {
                           continue label258;
                        }

                        int j2 = intlistiterator6.next();
                        j2 = j2 | i;
                        Dynamic<?> dynamic8 = this.getTileEntity(j2);
                        Dynamic<?> dynamic14 = this.getBlock(j2);
                        if (dynamic8 != null) {
                           int k3 = dynamic8.get("color").asInt(0);
                           if (k3 != 14 && k3 >= 0 && k3 < 16) {
                              String s16 = ChunkPaletteFormat.getProperty(dynamic14, "facing") + ChunkPaletteFormat.getProperty(dynamic14, "occupied") + ChunkPaletteFormat.getProperty(dynamic14, "part") + k3;
                              if (ChunkPaletteFormat.BED_BLOCK_MAP.containsKey(s16)) {
                                 this.setBlock(j2, ChunkPaletteFormat.BED_BLOCK_MAP.get(s16));
                              }
                           }
                        }
                     }
                  case 64:
                  case 71:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                     IntListIterator intlistiterator5 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator5.hasNext()) {
                           continue label258;
                        }

                        int i2 = intlistiterator5.next();
                        i2 = i2 | i;
                        Dynamic<?> dynamic7 = this.getBlock(i2);
                        if (ChunkPaletteFormat.getName(dynamic7).endsWith("_door")) {
                           Dynamic<?> dynamic13 = this.getBlock(i2);
                           if ("lower".equals(ChunkPaletteFormat.getProperty(dynamic13, "half"))) {
                              int j3 = relative(i2, ChunkPaletteFormat.Direction.UP);
                              Dynamic<?> dynamic15 = this.getBlock(j3);
                              String s1 = ChunkPaletteFormat.getName(dynamic13);
                              if (s1.equals(ChunkPaletteFormat.getName(dynamic15))) {
                                 String s2 = ChunkPaletteFormat.getProperty(dynamic13, "facing");
                                 String s3 = ChunkPaletteFormat.getProperty(dynamic13, "open");
                                 String s4 = flag ? "left" : ChunkPaletteFormat.getProperty(dynamic15, "hinge");
                                 String s5 = flag ? "false" : ChunkPaletteFormat.getProperty(dynamic15, "powered");
                                 this.setBlock(i2, ChunkPaletteFormat.DOOR_MAP.get(s1 + s2 + "lower" + s4 + s3 + s5));
                                 this.setBlock(j3, ChunkPaletteFormat.DOOR_MAP.get(s1 + s2 + "upper" + s4 + s3 + s5));
                              }
                           }
                        }
                     }
                  case 86:
                     IntListIterator intlistiterator4 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator4.hasNext()) {
                           continue label258;
                        }

                        int l1 = intlistiterator4.next();
                        l1 = l1 | i;
                        Dynamic<?> dynamic6 = this.getBlock(l1);
                        if ("minecraft:carved_pumpkin".equals(ChunkPaletteFormat.getName(dynamic6))) {
                           String s9 = ChunkPaletteFormat.getName(this.getBlock(relative(l1, ChunkPaletteFormat.Direction.DOWN)));
                           if ("minecraft:grass_block".equals(s9) || "minecraft:dirt".equals(s9)) {
                              this.setBlock(l1, ChunkPaletteFormat.PUMPKIN);
                           }
                        }
                     }
                  case 110:
                     IntListIterator intlistiterator3 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator3.hasNext()) {
                           continue label258;
                        }

                        int k1 = intlistiterator3.next();
                        k1 = k1 | i;
                        Dynamic<?> dynamic5 = this.getBlock(k1);
                        if ("minecraft:mycelium".equals(ChunkPaletteFormat.getName(dynamic5))) {
                           String s8 = ChunkPaletteFormat.getName(this.getBlock(relative(k1, ChunkPaletteFormat.Direction.UP)));
                           if ("minecraft:snow".equals(s8) || "minecraft:snow_layer".equals(s8)) {
                              this.setBlock(k1, ChunkPaletteFormat.SNOWY_MYCELIUM);
                           }
                        }
                     }
                  case 140:
                     IntListIterator intlistiterator2 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator2.hasNext()) {
                           continue label258;
                        }

                        int j1 = intlistiterator2.next();
                        j1 = j1 | i;
                        Dynamic<?> dynamic4 = this.removeTileEntity(j1);
                        if (dynamic4 != null) {
                           String s7 = dynamic4.get("Item").asString("") + dynamic4.get("Data").asInt(0);
                           this.setBlock(j1, ChunkPaletteFormat.FLOWER_POT_MAP.getOrDefault(s7, ChunkPaletteFormat.FLOWER_POT_MAP.get("minecraft:air0")));
                        }
                     }
                  case 144:
                     IntListIterator intlistiterator1 = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator1.hasNext()) {
                           continue label258;
                        }

                        int i1 = intlistiterator1.next();
                        i1 = i1 | i;
                        Dynamic<?> dynamic3 = this.getTileEntity(i1);
                        if (dynamic3 != null) {
                           String s6 = String.valueOf(dynamic3.get("SkullType").asInt(0));
                           String s14 = ChunkPaletteFormat.getProperty(this.getBlock(i1), "facing");
                           String s15;
                           if (!"up".equals(s14) && !"down".equals(s14)) {
                              s15 = s6 + s14;
                           } else {
                              s15 = s6 + String.valueOf(dynamic3.get("Rot").asInt(0));
                           }

                           dynamic3.remove("SkullType");
                           dynamic3.remove("facing");
                           dynamic3.remove("Rot");
                           this.setBlock(i1, ChunkPaletteFormat.SKULL_MAP.getOrDefault(s15, ChunkPaletteFormat.SKULL_MAP.get("0north")));
                        }
                     }
                  case 175:
                     IntListIterator intlistiterator = entry.getValue().iterator();

                     while(true) {
                        if (!intlistiterator.hasNext()) {
                           continue label258;
                        }

                        int l = intlistiterator.next();
                        l = l | i;
                        Dynamic<?> dynamic2 = this.getBlock(l);
                        if ("upper".equals(ChunkPaletteFormat.getProperty(dynamic2, "half"))) {
                           Dynamic<?> dynamic12 = this.getBlock(relative(l, ChunkPaletteFormat.Direction.DOWN));
                           String s13 = ChunkPaletteFormat.getName(dynamic12);
                           if ("minecraft:sunflower".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_SUNFLOWER);
                           } else if ("minecraft:lilac".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_LILAC);
                           } else if ("minecraft:tall_grass".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_TALL_GRASS);
                           } else if ("minecraft:large_fern".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_LARGE_FERN);
                           } else if ("minecraft:rose_bush".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_ROSE_BUSH);
                           } else if ("minecraft:peony".equals(s13)) {
                              this.setBlock(l, ChunkPaletteFormat.UPPER_PEONY);
                           }
                        }
                     }
                  case 176:
                  case 177:
                     for(int j : entry.getValue()) {
                        j = j | i;
                        Dynamic<?> dynamic = this.getTileEntity(j);
                        Dynamic<?> dynamic1 = this.getBlock(j);
                        if (dynamic != null) {
                           int k = dynamic.get("Base").asInt(0);
                           if (k != 15 && k >= 0 && k < 16) {
                              String s = ChunkPaletteFormat.getProperty(dynamic1, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
                              if (ChunkPaletteFormat.BANNER_BLOCK_MAP.containsKey(s)) {
                                 this.setBlock(j, ChunkPaletteFormat.BANNER_BLOCK_MAP.get(s));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }

      @Nullable
      private Dynamic<?> getTileEntity(int p_210066_1_) {
         return this.tileEntities.get(p_210066_1_);
      }

      @Nullable
      private Dynamic<?> removeTileEntity(int p_210059_1_) {
         return this.tileEntities.remove(p_210059_1_);
      }

      public static int relative(int p_199223_0_, ChunkPaletteFormat.Direction p_199223_1_) {
         switch(p_199223_1_.getAxis()) {
         case X:
            int i = (p_199223_0_ & 15) + p_199223_1_.getAxisDirection().getStep();
            return i >= 0 && i <= 15 ? p_199223_0_ & -16 | i : -1;
         case Y:
            int j = (p_199223_0_ >> 8) + p_199223_1_.getAxisDirection().getStep();
            return j >= 0 && j <= 255 ? p_199223_0_ & 255 | j << 8 : -1;
         case Z:
            int k = (p_199223_0_ >> 4 & 15) + p_199223_1_.getAxisDirection().getStep();
            return k >= 0 && k <= 15 ? p_199223_0_ & -241 | k << 4 : -1;
         default:
            return -1;
         }
      }

      private void setBlock(int p_210060_1_, Dynamic<?> p_210060_2_) {
         if (p_210060_1_ >= 0 && p_210060_1_ <= 65535) {
            ChunkPaletteFormat.Section chunkpaletteformat$section = this.getSection(p_210060_1_);
            if (chunkpaletteformat$section != null) {
               chunkpaletteformat$section.setBlock(p_210060_1_ & 4095, p_210060_2_);
            }
         }
      }

      @Nullable
      private ChunkPaletteFormat.Section getSection(int p_199221_1_) {
         int i = p_199221_1_ >> 12;
         return i < this.sections.length ? this.sections[i] : null;
      }

      public Dynamic<?> getBlock(int p_210064_1_) {
         if (p_210064_1_ >= 0 && p_210064_1_ <= 65535) {
            ChunkPaletteFormat.Section chunkpaletteformat$section = this.getSection(p_210064_1_);
            return chunkpaletteformat$section == null ? ChunkPaletteFormat.AIR : chunkpaletteformat$section.getBlock(p_210064_1_ & 4095);
         } else {
            return ChunkPaletteFormat.AIR;
         }
      }

      public Dynamic<?> write() {
         Dynamic<?> dynamic = this.level;
         if (this.tileEntities.isEmpty()) {
            dynamic = dynamic.remove("TileEntities");
         } else {
            dynamic = dynamic.set("TileEntities", dynamic.createList(this.tileEntities.values().stream()));
         }

         Dynamic<?> dynamic1 = dynamic.emptyMap();
         Dynamic<?> dynamic2 = dynamic.emptyList();

         for(ChunkPaletteFormat.Section chunkpaletteformat$section : this.sections) {
            if (chunkpaletteformat$section != null) {
               dynamic2 = dynamic2.merge(chunkpaletteformat$section.write());
               dynamic1 = dynamic1.set(String.valueOf(chunkpaletteformat$section.y), dynamic1.createIntList(Arrays.stream(chunkpaletteformat$section.update.toIntArray())));
            }
         }

         Dynamic<?> dynamic3 = dynamic.emptyMap();
         dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sides));
         dynamic3 = dynamic3.set("Indices", dynamic1);
         return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic2);
      }
   }
}