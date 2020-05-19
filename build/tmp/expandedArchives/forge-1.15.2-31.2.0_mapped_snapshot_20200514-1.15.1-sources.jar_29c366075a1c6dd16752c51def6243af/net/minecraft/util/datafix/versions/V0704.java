package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0704 extends Schema {
   protected static final Map<String, String> field_206647_b = DataFixUtils.make(Maps.newHashMap(), (p_209318_0_) -> {
      p_209318_0_.put("minecraft:furnace", "minecraft:furnace");
      p_209318_0_.put("minecraft:lit_furnace", "minecraft:furnace");
      p_209318_0_.put("minecraft:chest", "minecraft:chest");
      p_209318_0_.put("minecraft:trapped_chest", "minecraft:chest");
      p_209318_0_.put("minecraft:ender_chest", "minecraft:ender_chest");
      p_209318_0_.put("minecraft:jukebox", "minecraft:jukebox");
      p_209318_0_.put("minecraft:dispenser", "minecraft:dispenser");
      p_209318_0_.put("minecraft:dropper", "minecraft:dropper");
      p_209318_0_.put("minecraft:sign", "minecraft:sign");
      p_209318_0_.put("minecraft:mob_spawner", "minecraft:mob_spawner");
      p_209318_0_.put("minecraft:noteblock", "minecraft:noteblock");
      p_209318_0_.put("minecraft:brewing_stand", "minecraft:brewing_stand");
      p_209318_0_.put("minecraft:enhanting_table", "minecraft:enchanting_table");
      p_209318_0_.put("minecraft:command_block", "minecraft:command_block");
      p_209318_0_.put("minecraft:beacon", "minecraft:beacon");
      p_209318_0_.put("minecraft:skull", "minecraft:skull");
      p_209318_0_.put("minecraft:daylight_detector", "minecraft:daylight_detector");
      p_209318_0_.put("minecraft:hopper", "minecraft:hopper");
      p_209318_0_.put("minecraft:banner", "minecraft:banner");
      p_209318_0_.put("minecraft:flower_pot", "minecraft:flower_pot");
      p_209318_0_.put("minecraft:repeating_command_block", "minecraft:command_block");
      p_209318_0_.put("minecraft:chain_command_block", "minecraft:command_block");
      p_209318_0_.put("minecraft:shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:white_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:green_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:red_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:black_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:bed", "minecraft:bed");
      p_209318_0_.put("minecraft:light_gray_shulker_box", "minecraft:shulker_box");
      p_209318_0_.put("minecraft:banner", "minecraft:banner");
      p_209318_0_.put("minecraft:white_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:orange_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:magenta_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:light_blue_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:yellow_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:lime_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:pink_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:gray_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:silver_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:cyan_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:purple_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:blue_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:brown_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:green_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:red_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:black_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:standing_sign", "minecraft:sign");
      p_209318_0_.put("minecraft:wall_sign", "minecraft:sign");
      p_209318_0_.put("minecraft:piston_head", "minecraft:piston");
      p_209318_0_.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
      p_209318_0_.put("minecraft:unpowered_comparator", "minecraft:comparator");
      p_209318_0_.put("minecraft:powered_comparator", "minecraft:comparator");
      p_209318_0_.put("minecraft:wall_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:standing_banner", "minecraft:banner");
      p_209318_0_.put("minecraft:structure_block", "minecraft:structure_block");
      p_209318_0_.put("minecraft:end_portal", "minecraft:end_portal");
      p_209318_0_.put("minecraft:end_gateway", "minecraft:end_gateway");
      p_209318_0_.put("minecraft:sign", "minecraft:sign");
      p_209318_0_.put("minecraft:shield", "minecraft:banner");
   });
   protected static final HookFunction field_206648_c = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
         return V0099.func_209869_a(new Dynamic<>(p_apply_1_, p_apply_2_), V0704.field_206647_b, "ArmorStand");
      }
   };

   public V0704(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Type<?> getChoiceType(TypeReference p_getChoiceType_1_, String p_getChoiceType_2_) {
      return Objects.equals(p_getChoiceType_1_.typeName(), TypeReferences.BLOCK_ENTITY.typeName()) ? super.getChoiceType(p_getChoiceType_1_, NamespacedSchema.ensureNamespaced(p_getChoiceType_2_)) : super.getChoiceType(p_getChoiceType_1_, p_getChoiceType_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:furnace");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:chest");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:ender_chest");
      p_registerBlockEntities_1_.register(map, "minecraft:jukebox", (p_206641_1_) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_));
      });
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:dispenser");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:dropper");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:sign");
      p_registerBlockEntities_1_.register(map, "minecraft:mob_spawner", (p_206646_1_) -> {
         return TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_);
      });
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:noteblock");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:piston");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:brewing_stand");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:enchanting_table");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_portal");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:beacon");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:skull");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:daylight_detector");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:hopper");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:comparator");
      p_registerBlockEntities_1_.register(map, "minecraft:flower_pot", (p_206640_1_) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerBlockEntities_1_)));
      });
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:banner");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:structure_block");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_gateway");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:command_block");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), p_registerTypes_3_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206648_c, HookFunction.IDENTITY);
      });
   }
}