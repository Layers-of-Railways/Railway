package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import net.minecraft.util.datafix.TypeReferences;

public class TileEntityId extends DataFix {
   private static final Map<String, String> OLD_TO_NEW_ID_MAP = DataFixUtils.make(Maps.newHashMap(), (p_209293_0_) -> {
      p_209293_0_.put("Airportal", "minecraft:end_portal");
      p_209293_0_.put("Banner", "minecraft:banner");
      p_209293_0_.put("Beacon", "minecraft:beacon");
      p_209293_0_.put("Cauldron", "minecraft:brewing_stand");
      p_209293_0_.put("Chest", "minecraft:chest");
      p_209293_0_.put("Comparator", "minecraft:comparator");
      p_209293_0_.put("Control", "minecraft:command_block");
      p_209293_0_.put("DLDetector", "minecraft:daylight_detector");
      p_209293_0_.put("Dropper", "minecraft:dropper");
      p_209293_0_.put("EnchantTable", "minecraft:enchanting_table");
      p_209293_0_.put("EndGateway", "minecraft:end_gateway");
      p_209293_0_.put("EnderChest", "minecraft:ender_chest");
      p_209293_0_.put("FlowerPot", "minecraft:flower_pot");
      p_209293_0_.put("Furnace", "minecraft:furnace");
      p_209293_0_.put("Hopper", "minecraft:hopper");
      p_209293_0_.put("MobSpawner", "minecraft:mob_spawner");
      p_209293_0_.put("Music", "minecraft:noteblock");
      p_209293_0_.put("Piston", "minecraft:piston");
      p_209293_0_.put("RecordPlayer", "minecraft:jukebox");
      p_209293_0_.put("Sign", "minecraft:sign");
      p_209293_0_.put("Skull", "minecraft:skull");
      p_209293_0_.put("Structure", "minecraft:structure_block");
      p_209293_0_.put("Trap", "minecraft:dispenser");
   });

   public TileEntityId(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      Type<?> type1 = this.getOutputSchema().getType(TypeReferences.ITEM_STACK);
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
      return TypeRewriteRule.seq(this.convertUnchecked("item stack block entity name hook converter", type, type1), this.fixTypeEverywhere("BlockEntityIdFix", taggedchoicetype, taggedchoicetype1, (p_209700_0_) -> {
         return (p_206301_0_) -> {
            return p_206301_0_.mapFirst((p_206302_0_) -> {
               return OLD_TO_NEW_ID_MAP.getOrDefault(p_206302_0_, p_206302_0_);
            });
         };
      }));
   }
}