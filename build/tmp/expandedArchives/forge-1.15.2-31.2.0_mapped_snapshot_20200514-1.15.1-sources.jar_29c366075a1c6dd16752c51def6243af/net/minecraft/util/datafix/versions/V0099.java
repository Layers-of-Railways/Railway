package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V0099 extends Schema {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<String, String> field_206693_d = DataFixUtils.make(Maps.newHashMap(), (p_209320_0_) -> {
      p_209320_0_.put("minecraft:furnace", "Furnace");
      p_209320_0_.put("minecraft:lit_furnace", "Furnace");
      p_209320_0_.put("minecraft:chest", "Chest");
      p_209320_0_.put("minecraft:trapped_chest", "Chest");
      p_209320_0_.put("minecraft:ender_chest", "EnderChest");
      p_209320_0_.put("minecraft:jukebox", "RecordPlayer");
      p_209320_0_.put("minecraft:dispenser", "Trap");
      p_209320_0_.put("minecraft:dropper", "Dropper");
      p_209320_0_.put("minecraft:sign", "Sign");
      p_209320_0_.put("minecraft:mob_spawner", "MobSpawner");
      p_209320_0_.put("minecraft:noteblock", "Music");
      p_209320_0_.put("minecraft:brewing_stand", "Cauldron");
      p_209320_0_.put("minecraft:enhanting_table", "EnchantTable");
      p_209320_0_.put("minecraft:command_block", "CommandBlock");
      p_209320_0_.put("minecraft:beacon", "Beacon");
      p_209320_0_.put("minecraft:skull", "Skull");
      p_209320_0_.put("minecraft:daylight_detector", "DLDetector");
      p_209320_0_.put("minecraft:hopper", "Hopper");
      p_209320_0_.put("minecraft:banner", "Banner");
      p_209320_0_.put("minecraft:flower_pot", "FlowerPot");
      p_209320_0_.put("minecraft:repeating_command_block", "CommandBlock");
      p_209320_0_.put("minecraft:chain_command_block", "CommandBlock");
      p_209320_0_.put("minecraft:standing_sign", "Sign");
      p_209320_0_.put("minecraft:wall_sign", "Sign");
      p_209320_0_.put("minecraft:piston_head", "Piston");
      p_209320_0_.put("minecraft:daylight_detector_inverted", "DLDetector");
      p_209320_0_.put("minecraft:unpowered_comparator", "Comparator");
      p_209320_0_.put("minecraft:powered_comparator", "Comparator");
      p_209320_0_.put("minecraft:wall_banner", "Banner");
      p_209320_0_.put("minecraft:standing_banner", "Banner");
      p_209320_0_.put("minecraft:structure_block", "Structure");
      p_209320_0_.put("minecraft:end_portal", "Airportal");
      p_209320_0_.put("minecraft:end_gateway", "EndGateway");
      p_209320_0_.put("minecraft:shield", "Banner");
   });
   protected static final HookFunction field_206691_b = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
         return V0099.func_209869_a(new Dynamic<>(p_apply_1_, p_apply_2_), V0099.field_206693_d, "ArmorStand");
      }
   };

   public V0099(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static TypeTemplate equipment(Schema schema) {
      return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
   }

   protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return equipment(schema);
      });
   }

   protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
   }

   protected static void registerMinecart(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
      });
   }

   protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      p_registerEntities_1_.register(map, "Item", (p_206678_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "XPOrb");
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEgg");
      p_registerEntities_1_.registerSimple(map, "LeashKnot");
      p_registerEntities_1_.registerSimple(map, "Painting");
      p_registerEntities_1_.register(map, "Arrow", (p_206682_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "TippedArrow", (p_206655_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "SpectralArrow", (p_206671_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "Snowball");
      registerThrowableProjectile(p_registerEntities_1_, map, "Fireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "SmallFireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEnderpearl");
      p_registerEntities_1_.registerSimple(map, "EyeOfEnderSignal");
      p_registerEntities_1_.register(map, "ThrownPotion", (p_206688_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownExpBottle");
      p_registerEntities_1_.register(map, "ItemFrame", (p_206661_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "WitherSkull");
      p_registerEntities_1_.registerSimple(map, "PrimedTnt");
      p_registerEntities_1_.register(map, "FallingSand", (p_206679_1_) -> {
         return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "FireworksRocketEntity", (p_206651_1_) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "Boat");
      p_registerEntities_1_.register(map, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartRideable");
      p_registerEntities_1_.register(map, "MinecartChest", (p_206663_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartFurnace");
      registerMinecart(p_registerEntities_1_, map, "MinecartTNT");
      p_registerEntities_1_.register(map, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "MinecartHopper", (p_210752_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartCommandBlock");
      registerEntity(p_registerEntities_1_, map, "ArmorStand");
      registerEntity(p_registerEntities_1_, map, "Creeper");
      registerEntity(p_registerEntities_1_, map, "Skeleton");
      registerEntity(p_registerEntities_1_, map, "Spider");
      registerEntity(p_registerEntities_1_, map, "Giant");
      registerEntity(p_registerEntities_1_, map, "Zombie");
      registerEntity(p_registerEntities_1_, map, "Slime");
      registerEntity(p_registerEntities_1_, map, "Ghast");
      registerEntity(p_registerEntities_1_, map, "PigZombie");
      p_registerEntities_1_.register(map, "Enderman", (p_206686_1_) -> {
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
      p_registerEntities_1_.register(map, "EntityHorse", (p_206670_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, map, "Rabbit");
      p_registerEntities_1_.register(map, "Villager", (p_206656_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "EnderCrystal");
      p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
      p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
      registerEntity(p_registerEntities_1_, map, "Shulker");
      return map;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(p_registerBlockEntities_1_, map, "Furnace");
      registerInventory(p_registerBlockEntities_1_, map, "Chest");
      p_registerBlockEntities_1_.registerSimple(map, "EnderChest");
      p_registerBlockEntities_1_.register(map, "RecordPlayer", (p_206684_1_) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_));
      });
      registerInventory(p_registerBlockEntities_1_, map, "Trap");
      registerInventory(p_registerBlockEntities_1_, map, "Dropper");
      p_registerBlockEntities_1_.registerSimple(map, "Sign");
      p_registerBlockEntities_1_.register(map, "MobSpawner", (p_206667_1_) -> {
         return TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_);
      });
      p_registerBlockEntities_1_.registerSimple(map, "Music");
      p_registerBlockEntities_1_.registerSimple(map, "Piston");
      registerInventory(p_registerBlockEntities_1_, map, "Cauldron");
      p_registerBlockEntities_1_.registerSimple(map, "EnchantTable");
      p_registerBlockEntities_1_.registerSimple(map, "Airportal");
      p_registerBlockEntities_1_.registerSimple(map, "Control");
      p_registerBlockEntities_1_.registerSimple(map, "Beacon");
      p_registerBlockEntities_1_.registerSimple(map, "Skull");
      p_registerBlockEntities_1_.registerSimple(map, "DLDetector");
      registerInventory(p_registerBlockEntities_1_, map, "Hopper");
      p_registerBlockEntities_1_.registerSimple(map, "Comparator");
      p_registerBlockEntities_1_.register(map, "FlowerPot", (p_206653_1_) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerBlockEntities_1_)));
      });
      p_registerBlockEntities_1_.registerSimple(map, "Banner");
      p_registerBlockEntities_1_.registerSimple(map, "Structure");
      p_registerBlockEntities_1_.registerSimple(map, "EndGateway");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      p_registerTypes_1_.registerType(false, TypeReferences.LEVEL, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))));
      });
      p_registerTypes_1_.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_3_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TYPE, () -> {
         return DSL.optionalFields("Riding", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), TypeReferences.ENTITY.in(p_registerTypes_1_));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_2_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerTypes_1_)), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206691_b, HookFunction.IDENTITY);
      });
      p_registerTypes_1_.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString()));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ITEM_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STATS, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(p_registerTypes_1_)), "Teams", DSL.list(TypeReferences.TEAM.in(p_registerTypes_1_))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.TEAM, DSL::remainder);
      p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
   }

   protected static <T> T func_209869_a(Dynamic<T> p_209869_0_, Map<String, String> p_209869_1_, String p_209869_2_) {
      return p_209869_0_.update("tag", (p_209868_3_) -> {
         return p_209868_3_.update("BlockEntityTag", (p_209870_2_) -> {
            String s = p_209869_0_.get("id").asString("");
            String s1 = p_209869_1_.get(NamespacedSchema.ensureNamespaced(s));
            if (s1 == null) {
               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)s);
               return p_209870_2_;
            } else {
               return p_209870_2_.set("id", p_209869_0_.createString(s1));
            }
         }).update("EntityTag", (p_209866_2_) -> {
            String s = p_209869_0_.get("id").asString("");
            return Objects.equals(NamespacedSchema.ensureNamespaced(s), "minecraft:armor_stand") ? p_209866_2_.set("id", p_209869_0_.createString(p_209869_2_)) : p_209866_2_;
         });
      }).getValue();
   }
}