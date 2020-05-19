package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0705 extends NamespacedSchema {
   protected static final HookFunction field_206597_b = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
         return V0099.func_209869_a(new Dynamic<>(p_apply_1_, p_apply_2_), V0704.field_206647_b, "minecraft:armor_stand");
      }
   };

   public V0705(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return V0100.equipment(schema);
      });
   }

   protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      p_registerEntities_1_.registerSimple(map, "minecraft:area_effect_cloud");
      registerEntity(p_registerEntities_1_, map, "minecraft:armor_stand");
      p_registerEntities_1_.register(map, "minecraft:arrow", (p_206582_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:bat");
      registerEntity(p_registerEntities_1_, map, "minecraft:blaze");
      p_registerEntities_1_.registerSimple(map, "minecraft:boat");
      registerEntity(p_registerEntities_1_, map, "minecraft:cave_spider");
      p_registerEntities_1_.register(map, "minecraft:chest_minecart", (p_206574_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:chicken");
      p_registerEntities_1_.register(map, "minecraft:commandblock_minecart", (p_206575_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:cow");
      registerEntity(p_registerEntities_1_, map, "minecraft:creeper");
      p_registerEntities_1_.register(map, "minecraft:donkey", (p_206594_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:dragon_fireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:egg");
      registerEntity(p_registerEntities_1_, map, "minecraft:elder_guardian");
      p_registerEntities_1_.registerSimple(map, "minecraft:ender_crystal");
      registerEntity(p_registerEntities_1_, map, "minecraft:ender_dragon");
      p_registerEntities_1_.register(map, "minecraft:enderman", (p_206567_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:endermite");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:ender_pearl");
      p_registerEntities_1_.registerSimple(map, "minecraft:eye_of_ender_signal");
      p_registerEntities_1_.register(map, "minecraft:falling_block", (p_206586_1_) -> {
         return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:fireball");
      p_registerEntities_1_.register(map, "minecraft:fireworks_rocket", (p_206588_1_) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:furnace_minecart", (p_206570_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:ghast");
      registerEntity(p_registerEntities_1_, map, "minecraft:giant");
      registerEntity(p_registerEntities_1_, map, "minecraft:guardian");
      p_registerEntities_1_.register(map, "minecraft:hopper_minecart", (p_206584_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      p_registerEntities_1_.register(map, "minecraft:horse", (p_206595_1_) -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:husk");
      p_registerEntities_1_.register(map, "minecraft:item", (p_206578_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:item_frame", (p_206587_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:leash_knot");
      registerEntity(p_registerEntities_1_, map, "minecraft:magma_cube");
      p_registerEntities_1_.register(map, "minecraft:minecart", (p_206568_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:mooshroom");
      p_registerEntities_1_.register(map, "minecraft:mule", (p_206579_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:ocelot");
      p_registerEntities_1_.registerSimple(map, "minecraft:painting");
      p_registerEntities_1_.registerSimple(map, "minecraft:parrot");
      registerEntity(p_registerEntities_1_, map, "minecraft:pig");
      registerEntity(p_registerEntities_1_, map, "minecraft:polar_bear");
      p_registerEntities_1_.register(map, "minecraft:potion", (p_206573_1_) -> {
         return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:rabbit");
      registerEntity(p_registerEntities_1_, map, "minecraft:sheep");
      registerEntity(p_registerEntities_1_, map, "minecraft:shulker");
      p_registerEntities_1_.registerSimple(map, "minecraft:shulker_bullet");
      registerEntity(p_registerEntities_1_, map, "minecraft:silverfish");
      registerEntity(p_registerEntities_1_, map, "minecraft:skeleton");
      p_registerEntities_1_.register(map, "minecraft:skeleton_horse", (p_206592_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:slime");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:small_fireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:snowball");
      registerEntity(p_registerEntities_1_, map, "minecraft:snowman");
      p_registerEntities_1_.register(map, "minecraft:spawner_minecart", (p_206583_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:spectral_arrow", (p_206571_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:spider");
      registerEntity(p_registerEntities_1_, map, "minecraft:squid");
      registerEntity(p_registerEntities_1_, map, "minecraft:stray");
      p_registerEntities_1_.registerSimple(map, "minecraft:tnt");
      p_registerEntities_1_.register(map, "minecraft:tnt_minecart", (p_206591_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "minecraft:villager", (p_206580_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:villager_golem");
      registerEntity(p_registerEntities_1_, map, "minecraft:witch");
      registerEntity(p_registerEntities_1_, map, "minecraft:wither");
      registerEntity(p_registerEntities_1_, map, "minecraft:wither_skeleton");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:wither_skull");
      registerEntity(p_registerEntities_1_, map, "minecraft:wolf");
      registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:xp_bottle");
      p_registerEntities_1_.registerSimple(map, "minecraft:xp_orb");
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie");
      p_registerEntities_1_.register(map, "minecraft:zombie_horse", (p_206569_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie_pigman");
      registerEntity(p_registerEntities_1_, map, "minecraft:zombie_villager");
      p_registerEntities_1_.registerSimple(map, "minecraft:evocation_fangs");
      registerEntity(p_registerEntities_1_, map, "minecraft:evocation_illager");
      p_registerEntities_1_.registerSimple(map, "minecraft:illusion_illager");
      p_registerEntities_1_.register(map, "minecraft:llama", (p_209329_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "minecraft:llama_spit");
      registerEntity(p_registerEntities_1_, map, "minecraft:vex");
      registerEntity(p_registerEntities_1_, map, "minecraft:vindication_illager");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), p_registerTypes_2_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206597_b, HookFunction.IDENTITY);
      });
   }
}