package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.registry.Registry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class Enchantments {
   private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
   public static final Enchantment PROTECTION = register("protection", new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR_SLOTS));
   public static final Enchantment FIRE_PROTECTION = register("fire_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR_SLOTS));
   public static final Enchantment FEATHER_FALLING = register("feather_falling", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, ARMOR_SLOTS));
   public static final Enchantment BLAST_PROTECTION = register("blast_protection", new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR_SLOTS));
   public static final Enchantment PROJECTILE_PROTECTION = register("projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR_SLOTS));
   public static final Enchantment RESPIRATION = register("respiration", new RespirationEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment THORNS = register("thorns", new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment DEPTH_STRIDER = register("depth_strider", new DepthStriderEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment FROST_WALKER = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.FEET));
   public static final Enchantment BINDING_CURSE = register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment SHARPNESS = register("sharpness", new DamageEnchantment(Enchantment.Rarity.COMMON, 0, EquipmentSlotType.MAINHAND));
   public static final Enchantment SMITE = register("smite", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, EquipmentSlotType.MAINHAND));
   public static final Enchantment BANE_OF_ARTHROPODS = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, EquipmentSlotType.MAINHAND));
   public static final Enchantment KNOCKBACK = register("knockback", new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment FIRE_ASPECT = register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment LOOTING = register("looting", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND));
   public static final Enchantment SWEEPING = register("sweeping", new SweepingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment EFFICIENCY = register("efficiency", new EfficiencyEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment SILK_TOUCH = register("silk_touch", new SilkTouchEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment UNBREAKING = register("unbreaking", new UnbreakingEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment FORTUNE = register("fortune", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND));
   public static final Enchantment POWER = register("power", new PowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment PUNCH = register("punch", new PunchEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment FLAME = register("flame", new FlameEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment INFINITY = register("infinity", new InfinityEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment LUCK_OF_THE_SEA = register("luck_of_the_sea", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND));
   public static final Enchantment LURE = register("lure", new LureEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND));
   public static final Enchantment LOYALTY = register("loyalty", new LoyaltyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment IMPALING = register("impaling", new ImpalingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment RIPTIDE = register("riptide", new RiptideEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment CHANNELING = register("channeling", new ChannelingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment MULTISHOT = register("multishot", new MultishotEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment QUICK_CHARGE = register("quick_charge", new QuickChargeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment PIERCING = register("piercing", new PiercingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment MENDING = register("mending", new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.values()));
   public static final Enchantment VANISHING_CURSE = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.values()));

   private static Enchantment register(String key, Enchantment p_222191_1_) {
      return Registry.register(Registry.ENCHANTMENT, key, p_222191_1_);
   }
}