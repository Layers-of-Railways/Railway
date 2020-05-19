package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class Effects {
   public static final Effect SPEED = register(1, "speed", (new Effect(EffectType.BENEFICIAL, 8171462)).addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", (double)0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
   public static final Effect SLOWNESS = register(2, "slowness", (new Effect(EffectType.HARMFUL, 5926017)).addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL));
   public static final Effect HASTE = register(3, "haste", (new Effect(EffectType.BENEFICIAL, 14270531)).addAttributesModifier(SharedMonsterAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", (double)0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL));
   public static final Effect MINING_FATIGUE = register(4, "mining_fatigue", (new Effect(EffectType.HARMFUL, 4866583)).addAttributesModifier(SharedMonsterAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", (double)-0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL));
   public static final Effect STRENGTH = register(5, "strength", (new AttackDamageEffect(EffectType.BENEFICIAL, 9643043, 3.0D)).addAttributesModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.ADDITION));
   public static final Effect INSTANT_HEALTH = register(6, "instant_health", new InstantEffect(EffectType.BENEFICIAL, 16262179));
   public static final Effect INSTANT_DAMAGE = register(7, "instant_damage", new InstantEffect(EffectType.HARMFUL, 4393481));
   public static final Effect JUMP_BOOST = register(8, "jump_boost", new Effect(EffectType.BENEFICIAL, 2293580));
   public static final Effect NAUSEA = register(9, "nausea", new Effect(EffectType.HARMFUL, 5578058));
   public static final Effect REGENERATION = register(10, "regeneration", new Effect(EffectType.BENEFICIAL, 13458603));
   public static final Effect RESISTANCE = register(11, "resistance", new Effect(EffectType.BENEFICIAL, 10044730));
   public static final Effect FIRE_RESISTANCE = register(12, "fire_resistance", new Effect(EffectType.BENEFICIAL, 14981690));
   public static final Effect WATER_BREATHING = register(13, "water_breathing", new Effect(EffectType.BENEFICIAL, 3035801));
   public static final Effect INVISIBILITY = register(14, "invisibility", new Effect(EffectType.BENEFICIAL, 8356754));
   public static final Effect BLINDNESS = register(15, "blindness", new Effect(EffectType.HARMFUL, 2039587));
   public static final Effect NIGHT_VISION = register(16, "night_vision", new Effect(EffectType.BENEFICIAL, 2039713));
   public static final Effect HUNGER = register(17, "hunger", new Effect(EffectType.HARMFUL, 5797459));
   public static final Effect WEAKNESS = register(18, "weakness", (new AttackDamageEffect(EffectType.HARMFUL, 4738376, -4.0D)).addAttributesModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION));
   public static final Effect POISON = register(19, "poison", new Effect(EffectType.HARMFUL, 5149489));
   public static final Effect WITHER = register(20, "wither", new Effect(EffectType.HARMFUL, 3484199));
   public static final Effect HEALTH_BOOST = register(21, "health_boost", (new HealthBoostEffect(EffectType.BENEFICIAL, 16284963)).addAttributesModifier(SharedMonsterAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, AttributeModifier.Operation.ADDITION));
   public static final Effect ABSORPTION = register(22, "absorption", new AbsorptionEffect(EffectType.BENEFICIAL, 2445989));
   public static final Effect SATURATION = register(23, "saturation", new InstantEffect(EffectType.BENEFICIAL, 16262179));
   public static final Effect GLOWING = register(24, "glowing", new Effect(EffectType.NEUTRAL, 9740385));
   public static final Effect LEVITATION = register(25, "levitation", new Effect(EffectType.HARMFUL, 13565951));
   public static final Effect LUCK = register(26, "luck", (new Effect(EffectType.BENEFICIAL, 3381504)).addAttributesModifier(SharedMonsterAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, AttributeModifier.Operation.ADDITION));
   public static final Effect UNLUCK = register(27, "unluck", (new Effect(EffectType.HARMFUL, 12624973)).addAttributesModifier(SharedMonsterAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, AttributeModifier.Operation.ADDITION));
   public static final Effect SLOW_FALLING = register(28, "slow_falling", new Effect(EffectType.BENEFICIAL, 16773073));
   public static final Effect CONDUIT_POWER = register(29, "conduit_power", new Effect(EffectType.BENEFICIAL, 1950417));
   public static final Effect DOLPHINS_GRACE = register(30, "dolphins_grace", new Effect(EffectType.BENEFICIAL, 8954814));
   public static final Effect BAD_OMEN = register(31, "bad_omen", new Effect(EffectType.NEUTRAL, 745784) {
      /**
       * checks if Potion effect is ready to be applied this tick.
       */
      public boolean isReady(int duration, int amplifier) {
         return true;
      }

      public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
         if (entityLivingBaseIn instanceof ServerPlayerEntity && !entityLivingBaseIn.isSpectator()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLivingBaseIn;
            ServerWorld serverworld = serverplayerentity.getServerWorld();
            if (serverworld.getDifficulty() == Difficulty.PEACEFUL) {
               return;
            }

            if (serverworld.isVillage(new BlockPos(entityLivingBaseIn))) {
               serverworld.getRaids().badOmenTick(serverplayerentity);
            }
         }

      }
   });
   public static final Effect HERO_OF_THE_VILLAGE = register(32, "hero_of_the_village", new Effect(EffectType.BENEFICIAL, 4521796));

   private static Effect register(int id, String key, Effect effectIn) {
      return Registry.register(Registry.EFFECTS, id, key, effectIn);
   }
}