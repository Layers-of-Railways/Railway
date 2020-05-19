package net.minecraft.entity.passive.fish;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TropicalFishEntity extends AbstractGroupFishEntity {
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(TropicalFishEntity.class, DataSerializers.VARINT);
   private static final ResourceLocation[] BODY_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
   private static final ResourceLocation[] PATTERN_TEXTURES_A = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
   private static final ResourceLocation[] PATTERN_TEXTURES_B = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
   public static final int[] SPECIAL_VARIANTS = new int[]{pack(TropicalFishEntity.Type.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), pack(TropicalFishEntity.Type.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), pack(TropicalFishEntity.Type.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), pack(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), pack(TropicalFishEntity.Type.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), pack(TropicalFishEntity.Type.KOB, DyeColor.ORANGE, DyeColor.WHITE), pack(TropicalFishEntity.Type.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), pack(TropicalFishEntity.Type.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), pack(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.RED), pack(TropicalFishEntity.Type.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), pack(TropicalFishEntity.Type.GLITTER, DyeColor.WHITE, DyeColor.GRAY), pack(TropicalFishEntity.Type.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), pack(TropicalFishEntity.Type.DASHER, DyeColor.CYAN, DyeColor.PINK), pack(TropicalFishEntity.Type.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), pack(TropicalFishEntity.Type.BETTY, DyeColor.RED, DyeColor.WHITE), pack(TropicalFishEntity.Type.SNOOPER, DyeColor.GRAY, DyeColor.RED), pack(TropicalFishEntity.Type.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), pack(TropicalFishEntity.Type.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), pack(TropicalFishEntity.Type.KOB, DyeColor.RED, DyeColor.WHITE), pack(TropicalFishEntity.Type.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), pack(TropicalFishEntity.Type.DASHER, DyeColor.CYAN, DyeColor.YELLOW), pack(TropicalFishEntity.Type.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
   private boolean field_204228_bA = true;

   private static int pack(TropicalFishEntity.Type size, DyeColor pattern, DyeColor bodyColor) {
      return size.func_212550_a() & 255 | (size.func_212551_b() & 255) << 8 | (pattern.getId() & 255) << 16 | (bodyColor.getId() & 255) << 24;
   }

   public TropicalFishEntity(EntityType<? extends TropicalFishEntity> p_i50242_1_, World p_i50242_2_) {
      super(p_i50242_1_, p_i50242_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212324_b(int p_212324_0_) {
      return "entity.minecraft.tropical_fish.predefined." + p_212324_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static DyeColor func_212326_d(int p_212326_0_) {
      return DyeColor.byId(getBodyColor(p_212326_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static DyeColor func_212323_p(int p_212323_0_) {
      return DyeColor.byId(getPatternColor(p_212323_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_212327_q(int p_212327_0_) {
      int i = func_212325_s(p_212327_0_);
      int j = getPattern(p_212327_0_);
      return "entity.minecraft.tropical_fish.type." + TropicalFishEntity.Type.func_212548_a(i, j);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VARIANT, 0);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Variant", this.getVariant());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setVariant(compound.getInt("Variant"));
   }

   public void setVariant(int p_204215_1_) {
      this.dataManager.set(VARIANT, p_204215_1_);
   }

   public boolean isMaxGroupSize(int sizeIn) {
      return !this.field_204228_bA;
   }

   public int getVariant() {
      return this.dataManager.get(VARIANT);
   }

   /**
    * Add extra data to the bucket that just picked this fish up
    */
   protected void setBucketData(ItemStack bucket) {
      super.setBucketData(bucket);
      CompoundNBT compoundnbt = bucket.getOrCreateTag();
      compoundnbt.putInt("BucketVariantTag", this.getVariant());
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
   }

   @OnlyIn(Dist.CLIENT)
   private static int getBodyColor(int p_204216_0_) {
      return (p_204216_0_ & 16711680) >> 16;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204219_dC() {
      return DyeColor.byId(getBodyColor(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPatternColor(int p_204212_0_) {
      return (p_204212_0_ & -16777216) >> 24;
   }

   @OnlyIn(Dist.CLIENT)
   public float[] func_204222_dD() {
      return DyeColor.byId(getPatternColor(this.getVariant())).getColorComponentValues();
   }

   @OnlyIn(Dist.CLIENT)
   public static int func_212325_s(int p_212325_0_) {
      return Math.min(p_212325_0_ & 255, 1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return func_212325_s(this.getVariant());
   }

   @OnlyIn(Dist.CLIENT)
   private static int getPattern(int p_204213_0_) {
      return Math.min((p_204213_0_ & '\uff00') >> 8, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getPatternTexture() {
      return func_212325_s(this.getVariant()) == 0 ? PATTERN_TEXTURES_A[getPattern(this.getVariant())] : PATTERN_TEXTURES_B[getPattern(this.getVariant())];
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBodyTexture() {
      return BODY_TEXTURES[func_212325_s(this.getVariant())];
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (dataTag != null && dataTag.contains("BucketVariantTag", 3)) {
         this.setVariant(dataTag.getInt("BucketVariantTag"));
         return spawnDataIn;
      } else {
         int i;
         int j;
         int k;
         int l;
         if (spawnDataIn instanceof TropicalFishEntity.TropicalFishData) {
            TropicalFishEntity.TropicalFishData tropicalfishentity$tropicalfishdata = (TropicalFishEntity.TropicalFishData)spawnDataIn;
            i = tropicalfishentity$tropicalfishdata.size;
            j = tropicalfishentity$tropicalfishdata.pattern;
            k = tropicalfishentity$tropicalfishdata.bodyColor;
            l = tropicalfishentity$tropicalfishdata.patternColor;
         } else if ((double)this.rand.nextFloat() < 0.9D) {
            int i1 = SPECIAL_VARIANTS[this.rand.nextInt(SPECIAL_VARIANTS.length)];
            i = i1 & 255;
            j = (i1 & '\uff00') >> 8;
            k = (i1 & 16711680) >> 16;
            l = (i1 & -16777216) >> 24;
            spawnDataIn = new TropicalFishEntity.TropicalFishData(this, i, j, k, l);
         } else {
            this.field_204228_bA = false;
            i = this.rand.nextInt(2);
            j = this.rand.nextInt(6);
            k = this.rand.nextInt(15);
            l = this.rand.nextInt(15);
         }

         this.setVariant(i | j << 8 | k << 16 | l << 24);
         return spawnDataIn;
      }
   }

   static class TropicalFishData extends AbstractGroupFishEntity.GroupData {
      private final int size;
      private final int pattern;
      private final int bodyColor;
      private final int patternColor;

      private TropicalFishData(TropicalFishEntity p_i49859_1_, int p_i49859_2_, int p_i49859_3_, int p_i49859_4_, int p_i49859_5_) {
         super(p_i49859_1_);
         this.size = p_i49859_2_;
         this.pattern = p_i49859_3_;
         this.bodyColor = p_i49859_4_;
         this.patternColor = p_i49859_5_;
      }
   }

   static enum Type {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int field_212552_m;
      private final int field_212553_n;
      private static final TropicalFishEntity.Type[] field_212554_o = values();

      private Type(int p_i49832_3_, int p_i49832_4_) {
         this.field_212552_m = p_i49832_3_;
         this.field_212553_n = p_i49832_4_;
      }

      public int func_212550_a() {
         return this.field_212552_m;
      }

      public int func_212551_b() {
         return this.field_212553_n;
      }

      @OnlyIn(Dist.CLIENT)
      public static String func_212548_a(int p_212548_0_, int p_212548_1_) {
         return field_212554_o[p_212548_1_ + 6 * p_212548_0_].func_212549_c();
      }

      @OnlyIn(Dist.CLIENT)
      public String func_212549_c() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}