package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1460 extends NamespacedSchema {
   public V1460(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return V0100.equipment(schema);
      });
   }

   protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      p_registerEntities_1_.registerSimple(map, "minecraft:area_effect_cloud");
      registerEntity(p_registerEntities_1_, map, "minecraft:armor_stand");
      p_registerEntities_1_.register(map, "minecraft:arrow", (p_206552_1_) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:bat");
      registerEntity(p_registerEntities_1_, map, "minecraft:blaze");
      p_registerEntities_1_.registerSimple(map, "minecraft:boat");
      registerEntity(p_registerEntities_1_, map, "minecraft:cave_spider");
      p_registerEntities_1_.register(map, "minecraft:chest_minecart", (p_206546_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:chicken");
      p_registerEntities_1_.register(map, "minecraft:commandblock_minecart", (p_206529_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:cow");
      registerEntity(p_registerEntities_1_, map, "minecraft:creeper");
      p_registerEntities_1_.register(map, "minecraft:donkey", (p_206533_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:dragon_fireball");
      p_registerEntities_1_.registerSimple(map, "minecraft:egg");
      registerEntity(p_registerEntities_1_, map, "minecraft:elder_guardian");
      p_registerEntities_1_.registerSimple(map, "minecraft:ender_crystal");
      registerEntity(p_registerEntities_1_, map, "minecraft:ender_dragon");
      p_registerEntities_1_.register(map, "minecraft:enderman", (p_206523_1_) -> {
         return DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:endermite");
      p_registerEntities_1_.registerSimple(map, "minecraft:ender_pearl");
      p_registerEntities_1_.registerSimple(map, "minecraft:evocation_fangs");
      registerEntity(p_registerEntities_1_, map, "minecraft:evocation_illager");
      p_registerEntities_1_.registerSimple(map, "minecraft:eye_of_ender_signal");
      p_registerEntities_1_.register(map, "minecraft:falling_block", (p_206524_1_) -> {
         return DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:fireball");
      p_registerEntities_1_.register(map, "minecraft:fireworks_rocket", (p_206554_1_) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:furnace_minecart", (p_206515_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:ghast");
      registerEntity(p_registerEntities_1_, map, "minecraft:giant");
      registerEntity(p_registerEntities_1_, map, "minecraft:guardian");
      p_registerEntities_1_.register(map, "minecraft:hopper_minecart", (p_206541_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      p_registerEntities_1_.register(map, "minecraft:horse", (p_206545_1_) -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:husk");
      p_registerEntities_1_.registerSimple(map, "minecraft:illusion_illager");
      p_registerEntities_1_.register(map, "minecraft:item", (p_206520_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:item_frame", (p_206535_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:leash_knot");
      p_registerEntities_1_.register(map, "minecraft:llama", (p_209327_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:llama_spit");
      registerEntity(p_registerEntities_1_, map, "minecraft:magma_cube");
      p_registerEntities_1_.register(map, "minecraft:minecart", (p_206555_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:mooshroom");
      p_registerEntities_1_.register(map, "minecraft:mule", (p_206526_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:ocelot");
      p_registerEntities_1_.registerSimple(map, "minecraft:painting");
      p_registerEntities_1_.registerSimple(map, "minecraft:parrot");
      registerEntity(p_registerEntities_1_, map, "minecraft:pig");
      registerEntity(p_registerEntities_1_, map, "minecraft:polar_bear");
      p_registerEntities_1_.register(map, "minecraft:potion", (p_206542_1_) -> {
         return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:rabbit");
      registerEntity(p_registerEntities_1_, map, "minecraft:sheep");
      registerEntity(p_registerEntities_1_, map, "minecraft:shulker");
      p_registerEntities_1_.registerSimple(map, "minecraft:shulker_bullet");
      registerEntity(p_registerEntities_1_, map, "minecraft:silverfish");
      registerEntity(p_registerEntities_1_, map, "minecraft:skeleton");
      p_registerEntities_1_.register(map, "minecraft:skeleton_horse", (p_206516_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:slime");
      p_registerEntities_1_.registerSimple(map, "minecraft:small_fireball");
      p_registerEntities_1_.registerSimple(map, "minecraft:snowball");
      registerEntity(p_registerEntities_1_, map, "minecraft:snowman");
      p_registerEntities_1_.register(map, "minecraft:spawner_minecart", (p_206527_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:spectral_arrow", (p_206522_1_) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:spider");
      registerEntity(p_registerEntities_1_, map, "minecraft:squid");
      registerEntity(p_registerEntities_1_, map, "minecraft:stray");
      p_registerEntities_1_.registerSimple(map, "minecraft:tnt");
      p_registerEntities_1_.register(map, "minecraft:tnt_minecart", (p_206551_1_) -> {
         return DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:vex");
      p_registerEntities_1_.register(map, "minecraft:villager", (p_206534_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:villager_golem");
      registerEntity(p_registerEntities_1_, map, "minecraft:vindication_illager");
      registerEntity(p_registerEntities_1_, map, "minecraft:witch");
      registerEntity(p_registerEntities_1_, map, "minecraft:wither");
      registerEntity(p_registerEntities_1_, map, "minecraft:wither_skeleton");
      p_registerEntities_1_.registerSimple(map, "minecraft:wither_skull");
      registerEntity(p_registerEntities_1_, map, "minecraft:wolf");
      p_registerEntities_1_.registerSimple(map, "minecraft:xp_bottle");
      p_registerEntities_1_.registerSimple(map, "minecraft:xp_orb");
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie");
      p_registerEntities_1_.register(map, "minecraft:zombie_horse", (p_206521_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie_pigman");
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie_villager");
      return map;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:furnace");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:chest");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:trapped_chest");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:ender_chest");
      p_registerBlockEntities_1_.register(map, "minecraft:jukebox", (p_206549_1_) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_));
      });
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:dispenser");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:dropper");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:sign");
      p_registerBlockEntities_1_.register(map, "minecraft:mob_spawner", (p_206530_1_) -> {
         return TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_);
      });
      p_registerBlockEntities_1_.register(map, "minecraft:piston", (p_206518_1_) -> {
         return DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(p_registerBlockEntities_1_));
      });
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:brewing_stand");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:enchanting_table");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_portal");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:beacon");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:skull");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:daylight_detector");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:hopper");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:comparator");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:banner");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:structure_block");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_gateway");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:command_block");
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:shulker_box");
      p_registerBlockEntities_1_.registerSimple(map, "minecraft:bed");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      p_registerTypes_1_.registerType(false, TypeReferences.LEVEL, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.RECIPE, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "ShoulderEntityRight", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_)))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))))));
      });
      p_registerTypes_1_.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), p_registerTypes_3_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TYPE, () -> {
         return DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), TypeReferences.ENTITY.in(p_registerTypes_1_));
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), p_registerTypes_2_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), V0705.field_206597_b, HookFunction.IDENTITY);
      });
      p_registerTypes_1_.registerType(false, TypeReferences.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ITEM_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
      Supplier<TypeTemplate> supplier = () -> {
         return DSL.compoundList(TypeReferences.ITEM_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType()));
      };
      p_registerTypes_1_.registerType(false, TypeReferences.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:crafted", supplier.get(), "minecraft:used", supplier.get(), "minecraft:broken", supplier.get(), "minecraft:picked_up", supplier.get(), DSL.optionalFields("minecraft:dropped", supplier.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(p_registerTypes_1_)), "Teams", DSL.list(TypeReferences.TEAM.in(p_registerTypes_1_))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CB", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CC", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CD", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.TEAM, DSL::remainder);
      p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "SpawnData", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BIOME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
   }
}