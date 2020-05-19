package net.minecraft.particles;

import net.minecraft.util.registry.Registry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class ParticleTypes {
   public static final BasicParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
   public static final BasicParticleType ANGRY_VILLAGER = register("angry_villager", false);
   public static final BasicParticleType BARRIER = register("barrier", false);
   public static final ParticleType<BlockParticleData> BLOCK = register("block", BlockParticleData.DESERIALIZER);
   public static final BasicParticleType BUBBLE = register("bubble", false);
   public static final BasicParticleType CLOUD = register("cloud", false);
   public static final BasicParticleType CRIT = register("crit", false);
   public static final BasicParticleType DAMAGE_INDICATOR = register("damage_indicator", true);
   public static final BasicParticleType DRAGON_BREATH = register("dragon_breath", false);
   public static final BasicParticleType DRIPPING_LAVA = register("dripping_lava", false);
   public static final BasicParticleType FALLING_LAVA = register("falling_lava", false);
   public static final BasicParticleType LANDING_LAVA = register("landing_lava", false);
   public static final BasicParticleType DRIPPING_WATER = register("dripping_water", false);
   public static final BasicParticleType FALLING_WATER = register("falling_water", false);
   public static final ParticleType<RedstoneParticleData> DUST = register("dust", RedstoneParticleData.DESERIALIZER);
   public static final BasicParticleType EFFECT = register("effect", false);
   public static final BasicParticleType ELDER_GUARDIAN = register("elder_guardian", true);
   public static final BasicParticleType ENCHANTED_HIT = register("enchanted_hit", false);
   public static final BasicParticleType ENCHANT = register("enchant", false);
   public static final BasicParticleType END_ROD = register("end_rod", false);
   public static final BasicParticleType ENTITY_EFFECT = register("entity_effect", false);
   public static final BasicParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
   public static final BasicParticleType EXPLOSION = register("explosion", true);
   public static final ParticleType<BlockParticleData> FALLING_DUST = register("falling_dust", BlockParticleData.DESERIALIZER);
   public static final BasicParticleType FIREWORK = register("firework", false);
   public static final BasicParticleType FISHING = register("fishing", false);
   public static final BasicParticleType FLAME = register("flame", false);
   public static final BasicParticleType FLASH = register("flash", false);
   public static final BasicParticleType HAPPY_VILLAGER = register("happy_villager", false);
   public static final BasicParticleType COMPOSTER = register("composter", false);
   public static final BasicParticleType HEART = register("heart", false);
   public static final BasicParticleType INSTANT_EFFECT = register("instant_effect", false);
   public static final ParticleType<ItemParticleData> ITEM = register("item", ItemParticleData.DESERIALIZER);
   public static final BasicParticleType ITEM_SLIME = register("item_slime", false);
   public static final BasicParticleType ITEM_SNOWBALL = register("item_snowball", false);
   public static final BasicParticleType LARGE_SMOKE = register("large_smoke", false);
   public static final BasicParticleType LAVA = register("lava", false);
   public static final BasicParticleType MYCELIUM = register("mycelium", false);
   public static final BasicParticleType NOTE = register("note", false);
   public static final BasicParticleType POOF = register("poof", true);
   public static final BasicParticleType PORTAL = register("portal", false);
   public static final BasicParticleType RAIN = register("rain", false);
   public static final BasicParticleType SMOKE = register("smoke", false);
   public static final BasicParticleType SNEEZE = register("sneeze", false);
   public static final BasicParticleType SPIT = register("spit", true);
   public static final BasicParticleType SQUID_INK = register("squid_ink", true);
   public static final BasicParticleType SWEEP_ATTACK = register("sweep_attack", true);
   public static final BasicParticleType TOTEM_OF_UNDYING = register("totem_of_undying", false);
   public static final BasicParticleType UNDERWATER = register("underwater", false);
   public static final BasicParticleType SPLASH = register("splash", false);
   public static final BasicParticleType WITCH = register("witch", false);
   public static final BasicParticleType BUBBLE_POP = register("bubble_pop", false);
   public static final BasicParticleType CURRENT_DOWN = register("current_down", false);
   public static final BasicParticleType BUBBLE_COLUMN_UP = register("bubble_column_up", false);
   public static final BasicParticleType NAUTILUS = register("nautilus", false);
   public static final BasicParticleType DOLPHIN = register("dolphin", false);
   public static final BasicParticleType CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
   public static final BasicParticleType CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
   public static final BasicParticleType DRIPPING_HONEY = register("dripping_honey", false);
   public static final BasicParticleType FALLING_HONEY = register("falling_honey", false);
   public static final BasicParticleType LANDING_HONEY = register("landing_honey", false);
   public static final BasicParticleType FALLING_NECTAR = register("falling_nectar", false);

   private static BasicParticleType register(String key, boolean alwaysShow) {
      return (BasicParticleType)Registry.<ParticleType<? extends IParticleData>>register(Registry.PARTICLE_TYPE, key, new BasicParticleType(alwaysShow));
   }

   private static <T extends IParticleData> ParticleType<T> register(String key, IParticleData.IDeserializer<T> deserializer) {
      return Registry.register(Registry.PARTICLE_TYPE, key, new ParticleType<>(false, deserializer));
   }
}