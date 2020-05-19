package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> extends net.minecraftforge.registries.ForgeRegistryEntry<EntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, EntityClassification.MISC).immuneToFire().size(6.0F, 0.5F));
   public static final EntityType<ArmorStandEntity> ARMOR_STAND = register("armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, EntityClassification.MISC).size(0.5F, 1.975F));
   public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.create(BatEntity::new, EntityClassification.AMBIENT).size(0.5F, 0.9F));
   public static final EntityType<BeeEntity> BEE = register("bee", EntityType.Builder.create(BeeEntity::new, EntityClassification.CREATURE).size(0.7F, 0.6F));
   public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.Builder.create(BlazeEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.8F));
   public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, EntityClassification.MISC).size(1.375F, 0.5625F));
   public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.create(CatEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
   public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register("cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, EntityClassification.MONSTER).size(0.7F, 0.5F));
   public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.Builder.create(ChickenEntity::new, EntityClassification.CREATURE).size(0.4F, 0.7F));
   public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.create(CodEntity::new, EntityClassification.WATER_CREATURE).size(0.5F, 0.3F));
   public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.create(CowEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F));
   public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.Builder.create(CreeperEntity::new, EntityClassification.MONSTER).size(0.6F, 1.7F));
   public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.Builder.create(DonkeyEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.5F));
   public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.Builder.create(DolphinEntity::new, EntityClassification.WATER_CREATURE).size(0.9F, 0.6F));
   public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F));
   public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.Builder.create(DrownedEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, EntityClassification.MONSTER).size(1.9975F, 1.9975F));
   public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register("end_crystal", EntityType.Builder.<EnderCrystalEntity>create(EnderCrystalEntity::new, EntityClassification.MISC).size(2.0F, 2.0F));
   public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, EntityClassification.MONSTER).immuneToFire().size(16.0F, 8.0F));
   public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.Builder.create(EndermanEntity::new, EntityClassification.MONSTER).size(0.6F, 2.9F));
   public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.Builder.create(EndermiteEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F));
   public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, EntityClassification.MISC).size(0.5F, 0.8F));
   public static final EntityType<EvokerEntity> EVOKER = register("evoker", EntityType.Builder.create(EvokerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.<EyeOfEnderEntity>create(EyeOfEnderEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, EntityClassification.MISC).size(0.98F, 0.98F));
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.<FireworkRocketEntity>create(FireworkRocketEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<FoxEntity> FOX = register("fox", EntityType.Builder.create(FoxEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
   public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.Builder.create(GhastEntity::new, EntityClassification.MONSTER).immuneToFire().size(4.0F, 4.0F));
   public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.Builder.create(GiantEntity::new, EntityClassification.MONSTER).size(3.6F, 12.0F));
   public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.create(GuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F));
   public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.create(HorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
   public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.Builder.create(HuskEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<IllusionerEntity> ILLUSIONER = register("illusioner", EntityType.Builder.create(IllusionerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.<ItemEntity>create(ItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<ItemFrameEntity> ITEM_FRAME = register("item_frame", EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.Builder.<FireballEntity>create(FireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F));
   public static final EntityType<LeashKnotEntity> LEASH_KNOT = register("leash_knot", EntityType.Builder.<LeashKnotEntity>create(LeashKnotEntity::new, EntityClassification.MISC).disableSerialization().size(0.5F, 0.5F));
   public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.Builder.create(LlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F));
   public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register("llama_spit", EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register("magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, EntityClassification.MONSTER).immuneToFire().size(2.04F, 2.04F));
   public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register("chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<MinecartCommandBlockEntity> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.<MinecartCommandBlockEntity>create(MinecartCommandBlockEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.<SpawnerMinecartEntity>create(SpawnerMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<TNTMinecartEntity> TNT_MINECART = register("tnt_minecart", EntityType.Builder.<TNTMinecartEntity>create(TNTMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
   public static final EntityType<MuleEntity> MULE = register("mule", EntityType.Builder.create(MuleEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
   public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.Builder.create(MooshroomEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F));
   public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.Builder.create(OcelotEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
   public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<PandaEntity> PANDA = register("panda", EntityType.Builder.create(PandaEntity::new, EntityClassification.CREATURE).size(1.3F, 1.25F));
   public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.Builder.create(ParrotEntity::new, EntityClassification.CREATURE).size(0.5F, 0.9F));
   public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.create(PigEntity::new, EntityClassification.CREATURE).size(0.9F, 0.9F));
   public static final EntityType<PufferfishEntity> PUFFERFISH = register("pufferfish", EntityType.Builder.create(PufferfishEntity::new, EntityClassification.WATER_CREATURE).size(0.7F, 0.7F));
   public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN = register("zombie_pigman", EntityType.Builder.create(ZombiePigmanEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.95F));
   public static final EntityType<PolarBearEntity> POLAR_BEAR = register("polar_bear", EntityType.Builder.create(PolarBearEntity::new, EntityClassification.CREATURE).size(1.4F, 1.4F));
   public static final EntityType<TNTEntity> TNT = register("tnt", EntityType.Builder.<TNTEntity>create(TNTEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F));
   public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.Builder.create(RabbitEntity::new, EntityClassification.CREATURE).size(0.4F, 0.5F));
   public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.Builder.create(SalmonEntity::new, EntityClassification.WATER_CREATURE).size(0.7F, 0.4F));
   public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.Builder.create(SheepEntity::new, EntityClassification.CREATURE).size(0.9F, 1.3F));
   public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.Builder.create(ShulkerEntity::new, EntityClassification.MONSTER).immuneToFire().func_225435_d().size(1.0F, 1.0F));
   public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
   public static final EntityType<SilverfishEntity> SILVERFISH = register("silverfish", EntityType.Builder.create(SilverfishEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F));
   public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.Builder.create(SkeletonEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F));
   public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
   public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.Builder.create(SlimeEntity::new, EntityClassification.MONSTER).size(2.04F, 2.04F));
   public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
   public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register("snow_golem", EntityType.Builder.create(SnowGolemEntity::new, EntityClassification.MISC).size(0.7F, 1.9F));
   public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.create(SpiderEntity::new, EntityClassification.MONSTER).size(1.4F, 0.9F));
   public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.Builder.create(SquidEntity::new, EntityClassification.WATER_CREATURE).size(0.8F, 0.8F));
   public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.Builder.create(StrayEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F));
   public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register("trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F));
   public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, EntityClassification.WATER_CREATURE).size(0.5F, 0.4F));
   public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.Builder.create(TurtleEntity::new, EntityClassification.CREATURE).size(1.2F, 0.4F));
   public static final EntityType<EggEntity> EGG = register("egg", EntityType.Builder.<EggEntity>create(EggEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<EnderPearlEntity> ENDER_PEARL = register("ender_pearl", EntityType.Builder.<EnderPearlEntity>create(EnderPearlEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.<ExperienceBottleEntity>create(ExperienceBottleEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<PotionEntity> POTION = register("potion", EntityType.Builder.<PotionEntity>create(PotionEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
   public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.Builder.<TridentEntity>create(TridentEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
   public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.create(VexEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.4F, 0.8F));
   public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, EntityClassification.MISC).size(0.6F, 1.95F));
   public static final EntityType<IronGolemEntity> IRON_GOLEM = register("iron_golem", EntityType.Builder.create(IronGolemEntity::new, EntityClassification.MISC).size(1.4F, 2.7F));
   public static final EntityType<VindicatorEntity> VINDICATOR = register("vindicator", EntityType.Builder.create(VindicatorEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<PillagerEntity> PILLAGER = register("pillager", EntityType.Builder.create(PillagerEntity::new, EntityClassification.MONSTER).func_225435_d().size(0.6F, 1.95F));
   public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register("wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F));
   public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.Builder.create(WitchEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<WitherEntity> WITHER = register("wither", EntityType.Builder.create(WitherEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.9F, 3.5F));
   public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.7F, 2.4F));
   public static final EntityType<WitherSkullEntity> WITHER_SKULL = register("wither_skull", EntityType.Builder.<WitherSkullEntity>create(WitherSkullEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
   public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.Builder.create(WolfEntity::new, EntityClassification.CREATURE).size(0.6F, 0.85F));
   public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
   public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
   public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.Builder.create(PhantomEntity::new, EntityClassification.MONSTER).size(0.9F, 0.5F));
   public static final EntityType<RavagerEntity> RAVAGER = register("ravager", EntityType.Builder.create(RavagerEntity::new, EntityClassification.MONSTER).size(1.95F, 2.2F));
   public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.<LightningBoltEntity>create(EntityClassification.MISC).disableSerialization().size(0.0F, 0.0F));
   public static final EntityType<PlayerEntity> PLAYER = register("player", EntityType.Builder.<PlayerEntity>create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.6F, 1.8F));
   public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<FishingBobberEntity>create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.25F, 0.25F));
   private final EntityType.IFactory<T> factory;
   private final EntityClassification classification;
   private final boolean serializable;
   private final boolean summonable;
   private final boolean immuneToFire;
   private final boolean field_225438_be;
   @Nullable
   private String translationKey;
   @Nullable
   private ITextComponent name;
   @Nullable
   private ResourceLocation lootTable;
   private final EntitySize size;

   private final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier;
   private final java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory;
   private final net.minecraftforge.common.util.ReverseTagWrapper<EntityType<?>> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, net.minecraft.tags.EntityTypeTags::getGeneration, net.minecraft.tags.EntityTypeTags::getCollection);

   private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
      return Registry.register(Registry.ENTITY_TYPE, key, builder.build(key));
   }

   public static ResourceLocation getKey(EntityType<?> entityTypeIn) {
      return Registry.ENTITY_TYPE.getKey(entityTypeIn);
   }

   /**
    * Tries to get the entity type assosiated by the key.
    */
   public static Optional<EntityType<?>> byKey(String key) {
      return Registry.ENTITY_TYPE.getValue(ResourceLocation.tryCreate(key));
   }


   public EntityType(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn, boolean serializableIn, boolean summonableIn, boolean immuneToFireIn, boolean p_i51559_6_, EntitySize sizeIn) {
      this(factoryIn, classificationIn, serializableIn, summonableIn, immuneToFireIn, p_i51559_6_, sizeIn, EntityType::defaultVelocitySupplier, EntityType::defaultTrackingRangeSupplier, EntityType::defaultUpdateIntervalSupplier, null);
   }
   public EntityType(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn, boolean serializableIn, boolean summonableIn, boolean immuneToFireIn, boolean p_i51559_6_, EntitySize sizeIn, final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier, final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier, final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier, final java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
      this.factory = factoryIn;
      this.classification = classificationIn;
      this.field_225438_be = p_i51559_6_;
      this.serializable = serializableIn;
      this.summonable = summonableIn;
      this.immuneToFire = immuneToFireIn;
      this.size = sizeIn;
      this.velocityUpdateSupplier = velocityUpdateSupplier;
      this.trackingRangeSupplier = trackingRangeSupplier;
      this.updateIntervalSupplier = updateIntervalSupplier;
      this.customClientFactory = customClientFactory;
   }

   @Nullable
   public Entity spawn(World worldIn, @Nullable ItemStack stack, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220331_6_, boolean p_220331_7_) {
      return this.spawn(worldIn, stack == null ? null : stack.getTag(), stack != null && stack.hasDisplayName() ? stack.getDisplayName() : null, playerIn, pos, reason, p_220331_6_, p_220331_7_);
   }

   @Nullable
   public T spawn(World worldIn, @Nullable CompoundNBT compound, @Nullable ITextComponent customName, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220342_7_, boolean p_220342_8_) {
      T t = this.create(worldIn, compound, customName, playerIn, pos, reason, p_220342_7_, p_220342_8_);
      if (t instanceof net.minecraft.entity.MobEntity && net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn((net.minecraft.entity.MobEntity) t, worldIn, pos.getX(), pos.getY(), pos.getZ(), null, reason)) return null;
      worldIn.addEntity(t);
      return t;
   }

   @Nullable
   public T create(World worldIn, @Nullable CompoundNBT compound, @Nullable ITextComponent customName, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220349_7_, boolean p_220349_8_) {
      T t = this.create(worldIn);
      if (t == null) {
         return (T)null;
      } else {
         double d0;
         if (p_220349_7_) {
            t.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
            d0 = func_208051_a(worldIn, pos, p_220349_8_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
         if (t instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)t;
            mobentity.rotationYawHead = mobentity.rotationYaw;
            mobentity.renderYawOffset = mobentity.rotationYaw;
            mobentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(mobentity)), reason, (ILivingEntityData)null, compound);
            mobentity.playAmbientSound();
         }

         if (customName != null && t instanceof LivingEntity) {
            t.setCustomName(customName);
         }

         applyItemNBT(worldIn, playerIn, t, compound);
         return t;
      }
   }

   protected static double func_208051_a(IWorldReader worldReader, BlockPos pos, boolean p_208051_2_, AxisAlignedBB p_208051_3_) {
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos);
      if (p_208051_2_) {
         axisalignedbb = axisalignedbb.expand(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> stream = worldReader.getCollisionShapes((Entity)null, axisalignedbb, Collections.emptySet());
      return 1.0D + VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
   }

   public static void applyItemNBT(World worldIn, @Nullable PlayerEntity player, @Nullable Entity spawnedEntity, @Nullable CompoundNBT itemNBT) {
      if (itemNBT != null && itemNBT.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = worldIn.getServer();
         if (minecraftserver != null && spawnedEntity != null) {
            if (worldIn.isRemote || !spawnedEntity.ignoreItemEntityData() || player != null && minecraftserver.getPlayerList().canSendCommands(player.getGameProfile())) {
               CompoundNBT compoundnbt = spawnedEntity.writeWithoutTypeId(new CompoundNBT());
               UUID uuid = spawnedEntity.getUniqueID();
               compoundnbt.merge(itemNBT.getCompound("EntityTag"));
               spawnedEntity.setUniqueId(uuid);
               spawnedEntity.read(compoundnbt);
            }
         }
      }
   }

   public boolean isSerializable() {
      return this.serializable;
   }

   public boolean isSummonable() {
      return this.summonable;
   }

   public boolean isImmuneToFire() {
      return this.immuneToFire;
   }

   public boolean func_225437_d() {
      return this.field_225438_be;
   }

   public EntityClassification getClassification() {
      return this.classification;
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.translationKey;
   }

   public ITextComponent getName() {
      if (this.name == null) {
         this.name = new TranslationTextComponent(this.getTranslationKey());
      }

      return this.name;
   }

   public ResourceLocation getLootTable() {
      if (this.lootTable == null) {
         ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.size.width;
   }

   public float getHeight() {
      return this.size.height;
   }

   @Nullable
   public T create(World worldIn) {
      return this.factory.create(this, worldIn);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Entity create(int id, World worldIn) {
      return create(worldIn, Registry.ENTITY_TYPE.getByValue(id));
   }

   public static Optional<Entity> loadEntityUnchecked(CompoundNBT compound, World worldIn) {
      return Util.acceptOrElse(readEntityType(compound).map((p_220337_1_) -> {
         return p_220337_1_.create(worldIn);
      }), (p_220329_1_) -> {
         p_220329_1_.read(compound);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", (Object)compound.getString("id"));
      });
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static Entity create(World worldIn, @Nullable EntityType<?> type) {
      return type == null ? null : type.create(worldIn);
   }

   public AxisAlignedBB func_220328_a(double p_220328_1_, double p_220328_3_, double p_220328_5_) {
      float f = this.getWidth() / 2.0F;
      return new AxisAlignedBB(p_220328_1_ - (double)f, p_220328_3_, p_220328_5_ - (double)f, p_220328_1_ + (double)f, p_220328_3_ + (double)this.getHeight(), p_220328_5_ + (double)f);
   }

   public EntitySize getSize() {
      return this.size;
   }

   public static Optional<EntityType<?>> readEntityType(CompoundNBT compound) {
      return Registry.ENTITY_TYPE.getValue(new ResourceLocation(compound.getString("id")));
   }

   @Nullable
   public static Entity func_220335_a(CompoundNBT compound, World worldIn, Function<Entity, Entity> p_220335_2_) {
      return loadEntity(compound, worldIn).map(p_220335_2_).map((p_220346_3_) -> {
         if (compound.contains("Passengers", 9)) {
            ListNBT listnbt = compound.getList("Passengers", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
               Entity entity = func_220335_a(listnbt.getCompound(i), worldIn, p_220335_2_);
               if (entity != null) {
                  entity.startRiding(p_220346_3_, true);
               }
            }
         }

         return p_220346_3_;
      }).orElse((Entity)null);
   }

   private static Optional<Entity> loadEntity(CompoundNBT compound, World worldIn) {
      try {
         return loadEntityUnchecked(compound, worldIn);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
         return Optional.empty();
      }
   }

   public int getTrackingRange() {
      return trackingRangeSupplier.applyAsInt(this);
   }
   private int defaultTrackingRangeSupplier() {
      if (this == PLAYER) {
         return 32;
      } else if (this == END_CRYSTAL) {
         return 16;
      } else if (this != ENDER_DRAGON && this != TNT && this != FALLING_BLOCK && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != ARMOR_STAND && this != EXPERIENCE_ORB && this != AREA_EFFECT_CLOUD && this != EVOKER_FANGS) {
         return this != FISHING_BOBBER && this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EYE_OF_ENDER && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != ITEM ? 5 : 4;
      } else {
         return 10;
      }
   }

   public int getUpdateFrequency() {
      return updateIntervalSupplier.applyAsInt(this);
   }
   private int defaultUpdateIntervalSupplier() {
      if (this != PLAYER && this != EVOKER_FANGS) {
         if (this == EYE_OF_ENDER) {
            return 4;
         } else if (this == FISHING_BOBBER) {
            return 5;
         } else if (this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != TNT) {
            if (this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != ITEM && this != FALLING_BLOCK && this != EXPERIENCE_ORB) {
               return this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != AREA_EFFECT_CLOUD && this != END_CRYSTAL ? 3 : Integer.MAX_VALUE;
            } else {
               return 20;
            }
         } else {
            return 10;
         }
      } else {
         return 2;
      }
   }

   public boolean shouldSendVelocityUpdates() {
      return velocityUpdateSupplier.test(this);
   }
   private boolean defaultVelocitySupplier() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   /**
    * Checks if this entity type is contained in the tag
    */
   public boolean isContained(Tag<EntityType<?>> tagIn) {
      return tagIn.contains(this);
   }

   public T customClientSpawn(net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity packet, World world) {
      if (customClientFactory == null) return this.create(world);
      return customClientFactory.apply(packet, world);
   }

   /**
    * Retrieves a list of tags names this is known to be associated with.
    * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
    */
   public java.util.Set<ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public static class Builder<T extends Entity> {
      private final EntityType.IFactory<T> factory;
      private final EntityClassification classification;
      private boolean serializable = true;
      private boolean summonable = true;
      private boolean immuneToFire;
      private java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier = EntityType::defaultVelocitySupplier;
      private java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier = EntityType::defaultTrackingRangeSupplier;
      private java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier = EntityType::defaultUpdateIntervalSupplier;
      private java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory;

      private boolean field_225436_f;
      private EntitySize size = EntitySize.flexible(0.6F, 1.8F);

      private Builder(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn) {
         this.factory = factoryIn;
         this.classification = classificationIn;
         this.field_225436_f = classificationIn == EntityClassification.CREATURE || classificationIn == EntityClassification.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> create(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn) {
         return new EntityType.Builder<>(factoryIn, classificationIn);
      }

      public static <T extends Entity> EntityType.Builder<T> create(EntityClassification classificationIn) {
         return new EntityType.Builder<>((p_220323_0_, p_220323_1_) -> {
            return (T)null;
         }, classificationIn);
      }

      public EntityType.Builder<T> size(float width, float height) {
         this.size = EntitySize.flexible(width, height);
         return this;
      }

      public EntityType.Builder<T> disableSummoning() {
         this.summonable = false;
         return this;
      }

      public EntityType.Builder<T> disableSerialization() {
         this.serializable = false;
         return this;
      }

      public EntityType.Builder<T> immuneToFire() {
         this.immuneToFire = true;
         return this;
      }

      public EntityType.Builder<T> func_225435_d() {
         this.field_225436_f = true;
         return this;
      }

      public EntityType.Builder<T> setUpdateInterval(int interval) {
         this.updateIntervalSupplier = t->interval;
         return this;
      }

      public EntityType.Builder<T> setTrackingRange(int range) {
         this.trackingRangeSupplier = t->range;
         return this;
      }

      public EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value) {
         this.velocityUpdateSupplier = t->value;
         return this;
      }

      /**
       * By default, entities are spawned clientside via {@link EntityType#create(World)}.
       * If you need finer control over the spawning process, use this to get read access to the spawn packet.
       */
      public EntityType.Builder<T> setCustomClientFactory(java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
         this.customClientFactory = customClientFactory;
         return this;
      }

      public EntityType<T> build(String id) {
         if (this.serializable) {
            try {
               DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TYPE, id);
            } catch (IllegalArgumentException illegalstateexception) { // Forge: fix catching wrong exception
               if (SharedConstants.developmentMode) {
                  throw illegalstateexception;
               }

               EntityType.LOGGER.warn("No data fixer registered for entity {}", (Object)id);
            }
         }

         return new EntityType<>(this.factory, this.classification, this.serializable, this.summonable, this.immuneToFire, this.field_225436_f, this.size, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
      }
   }

   public interface IFactory<T extends Entity> {
      T create(EntityType<T> p_create_1_, World p_create_2_);
   }
}