package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VillagerData {
   private static final int[] field_221136_a = new int[]{0, 10, 70, 150, 250};
   private final IVillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(IVillagerType type, VillagerProfession profession, int level) {
      this.type = type;
      this.profession = profession;
      this.level = Math.max(1, level);
   }

   public VillagerData(Dynamic<?> p_i50181_1_) {
      this(Registry.VILLAGER_TYPE.getOrDefault(ResourceLocation.tryCreate(p_i50181_1_.get("type").asString(""))), Registry.VILLAGER_PROFESSION.getOrDefault(ResourceLocation.tryCreate(p_i50181_1_.get("profession").asString(""))), p_i50181_1_.get("level").asInt(1));
   }

   public IVillagerType getType() {
      return this.type;
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public int getLevel() {
      return this.level;
   }

   public VillagerData withType(IVillagerType typeIn) {
      return new VillagerData(typeIn, this.profession, this.level);
   }

   public VillagerData withProfession(VillagerProfession professionIn) {
      return new VillagerData(this.type, professionIn, this.level);
   }

   public VillagerData withLevel(int levelIn) {
      return new VillagerData(this.type, this.profession, levelIn);
   }

   public <T> T serialize(DynamicOps<T> p_221131_1_) {
      return p_221131_1_.createMap(ImmutableMap.of(p_221131_1_.createString("type"), p_221131_1_.createString(Registry.VILLAGER_TYPE.getKey(this.type).toString()), p_221131_1_.createString("profession"), p_221131_1_.createString(Registry.VILLAGER_PROFESSION.getKey(this.profession).toString()), p_221131_1_.createString("level"), p_221131_1_.createInt(this.level)));
   }

   @OnlyIn(Dist.CLIENT)
   public static int func_221133_b(int p_221133_0_) {
      return func_221128_d(p_221133_0_) ? field_221136_a[p_221133_0_ - 1] : 0;
   }

   public static int func_221127_c(int p_221127_0_) {
      return func_221128_d(p_221127_0_) ? field_221136_a[p_221127_0_] : 0;
   }

   public static boolean func_221128_d(int p_221128_0_) {
      return p_221128_0_ >= 1 && p_221128_0_ < 5;
   }
}