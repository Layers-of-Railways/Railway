package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0100 extends Schema {
   public V0100(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static TypeTemplate equipment(Schema schema) {
      return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
   }

   protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return equipment(schema);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerEntity(p_registerEntities_1_, map, "ArmorStand");
      registerEntity(p_registerEntities_1_, map, "Creeper");
      registerEntity(p_registerEntities_1_, map, "Skeleton");
      registerEntity(p_registerEntities_1_, map, "Spider");
      registerEntity(p_registerEntities_1_, map, "Giant");
      registerEntity(p_registerEntities_1_, map, "Zombie");
      registerEntity(p_registerEntities_1_, map, "Slime");
      registerEntity(p_registerEntities_1_, map, "Ghast");
      registerEntity(p_registerEntities_1_, map, "PigZombie");
      p_registerEntities_1_.register(map, "Enderman", (p_206609_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "CaveSpider");
      registerEntity(p_registerEntities_1_, map, "Silverfish");
      registerEntity(p_registerEntities_1_, map, "Blaze");
      registerEntity(p_registerEntities_1_, map, "LavaSlime");
      registerEntity(p_registerEntities_1_, map, "EnderDragon");
      registerEntity(p_registerEntities_1_, map, "WitherBoss");
      registerEntity(p_registerEntities_1_, map, "Bat");
      registerEntity(p_registerEntities_1_, map, "Witch");
      registerEntity(p_registerEntities_1_, map, "Endermite");
      registerEntity(p_registerEntities_1_, map, "Guardian");
      registerEntity(p_registerEntities_1_, map, "Pig");
      registerEntity(p_registerEntities_1_, map, "Sheep");
      registerEntity(p_registerEntities_1_, map, "Cow");
      registerEntity(p_registerEntities_1_, map, "Chicken");
      registerEntity(p_registerEntities_1_, map, "Squid");
      registerEntity(p_registerEntities_1_, map, "Wolf");
      registerEntity(p_registerEntities_1_, map, "MushroomCow");
      registerEntity(p_registerEntities_1_, map, "SnowMan");
      registerEntity(p_registerEntities_1_, map, "Ozelot");
      registerEntity(p_registerEntities_1_, map, "VillagerGolem");
      p_registerEntities_1_.register(map, "EntityHorse", (p_206612_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "Rabbit");
      p_registerEntities_1_.register(map, "Villager", (p_206608_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "Shulker");
      p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
      p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
   }
}