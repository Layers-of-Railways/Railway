package net.minecraft.potion;

import net.minecraft.util.registry.Registry;

public class Potions {
   public static final Potion EMPTY = register("empty", new Potion());
   public static final Potion WATER = register("water", new Potion());
   public static final Potion MUNDANE = register("mundane", new Potion());
   public static final Potion THICK = register("thick", new Potion());
   public static final Potion AWKWARD = register("awkward", new Potion());
   public static final Potion NIGHT_VISION = register("night_vision", new Potion(new EffectInstance(Effects.NIGHT_VISION, 3600)));
   public static final Potion LONG_NIGHT_VISION = register("long_night_vision", new Potion("night_vision", new EffectInstance(Effects.NIGHT_VISION, 9600)));
   public static final Potion INVISIBILITY = register("invisibility", new Potion(new EffectInstance(Effects.INVISIBILITY, 3600)));
   public static final Potion LONG_INVISIBILITY = register("long_invisibility", new Potion("invisibility", new EffectInstance(Effects.INVISIBILITY, 9600)));
   public static final Potion LEAPING = register("leaping", new Potion(new EffectInstance(Effects.JUMP_BOOST, 3600)));
   public static final Potion LONG_LEAPING = register("long_leaping", new Potion("leaping", new EffectInstance(Effects.JUMP_BOOST, 9600)));
   public static final Potion STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new EffectInstance(Effects.JUMP_BOOST, 1800, 1)));
   public static final Potion FIRE_RESISTANCE = register("fire_resistance", new Potion(new EffectInstance(Effects.FIRE_RESISTANCE, 3600)));
   public static final Potion LONG_FIRE_RESISTANCE = register("long_fire_resistance", new Potion("fire_resistance", new EffectInstance(Effects.FIRE_RESISTANCE, 9600)));
   public static final Potion SWIFTNESS = register("swiftness", new Potion(new EffectInstance(Effects.SPEED, 3600)));
   public static final Potion LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new EffectInstance(Effects.SPEED, 9600)));
   public static final Potion STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new EffectInstance(Effects.SPEED, 1800, 1)));
   public static final Potion SLOWNESS = register("slowness", new Potion(new EffectInstance(Effects.SLOWNESS, 1800)));
   public static final Potion LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new EffectInstance(Effects.SLOWNESS, 4800)));
   public static final Potion STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new EffectInstance(Effects.SLOWNESS, 400, 3)));
   public static final Potion TURTLE_MASTER = register("turtle_master", new Potion("turtle_master", new EffectInstance(Effects.SLOWNESS, 400, 3), new EffectInstance(Effects.RESISTANCE, 400, 2)));
   public static final Potion LONG_TURTLE_MASTER = register("long_turtle_master", new Potion("turtle_master", new EffectInstance(Effects.SLOWNESS, 800, 3), new EffectInstance(Effects.RESISTANCE, 800, 2)));
   public static final Potion STRONG_TURTLE_MASTER = register("strong_turtle_master", new Potion("turtle_master", new EffectInstance(Effects.SLOWNESS, 400, 5), new EffectInstance(Effects.RESISTANCE, 400, 3)));
   public static final Potion WATER_BREATHING = register("water_breathing", new Potion(new EffectInstance(Effects.WATER_BREATHING, 3600)));
   public static final Potion LONG_WATER_BREATHING = register("long_water_breathing", new Potion("water_breathing", new EffectInstance(Effects.WATER_BREATHING, 9600)));
   public static final Potion HEALING = register("healing", new Potion(new EffectInstance(Effects.INSTANT_HEALTH, 1)));
   public static final Potion STRONG_HEALING = register("strong_healing", new Potion("healing", new EffectInstance(Effects.INSTANT_HEALTH, 1, 1)));
   public static final Potion HARMING = register("harming", new Potion(new EffectInstance(Effects.INSTANT_DAMAGE, 1)));
   public static final Potion STRONG_HARMING = register("strong_harming", new Potion("harming", new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1)));
   public static final Potion POISON = register("poison", new Potion(new EffectInstance(Effects.POISON, 900)));
   public static final Potion LONG_POISON = register("long_poison", new Potion("poison", new EffectInstance(Effects.POISON, 1800)));
   public static final Potion STRONG_POISON = register("strong_poison", new Potion("poison", new EffectInstance(Effects.POISON, 432, 1)));
   public static final Potion REGENERATION = register("regeneration", new Potion(new EffectInstance(Effects.REGENERATION, 900)));
   public static final Potion LONG_REGENERATION = register("long_regeneration", new Potion("regeneration", new EffectInstance(Effects.REGENERATION, 1800)));
   public static final Potion STRONG_REGENERATION = register("strong_regeneration", new Potion("regeneration", new EffectInstance(Effects.REGENERATION, 450, 1)));
   public static final Potion STRENGTH = register("strength", new Potion(new EffectInstance(Effects.STRENGTH, 3600)));
   public static final Potion LONG_STRENGTH = register("long_strength", new Potion("strength", new EffectInstance(Effects.STRENGTH, 9600)));
   public static final Potion STRONG_STRENGTH = register("strong_strength", new Potion("strength", new EffectInstance(Effects.STRENGTH, 1800, 1)));
   public static final Potion WEAKNESS = register("weakness", new Potion(new EffectInstance(Effects.WEAKNESS, 1800)));
   public static final Potion LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new EffectInstance(Effects.WEAKNESS, 4800)));
   public static final Potion LUCK = register("luck", new Potion("luck", new EffectInstance(Effects.LUCK, 6000)));
   public static final Potion SLOW_FALLING = register("slow_falling", new Potion(new EffectInstance(Effects.SLOW_FALLING, 1800)));
   public static final Potion LONG_SLOW_FALLING = register("long_slow_falling", new Potion("slow_falling", new EffectInstance(Effects.SLOW_FALLING, 4800)));

   private static Potion register(String key, Potion potionIn) {
      return Registry.register(Registry.POTION, key, potionIn);
   }
}