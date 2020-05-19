package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity extends Entity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.PARTICLE_DATA);
   private Potion potion = Potions.EMPTY;
   private final List<EffectInstance> effects = Lists.newArrayList();
   private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private boolean colorSet;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private LivingEntity owner;
   private UUID ownerUniqueId;

   public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> p_i50389_1_, World p_i50389_2_) {
      super(p_i50389_1_, p_i50389_2_);
      this.noClip = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloudEntity(World worldIn, double x, double y, double z) {
      this(EntityType.AREA_EFFECT_CLOUD, worldIn);
      this.setPosition(x, y, z);
   }

   protected void registerData() {
      this.getDataManager().register(COLOR, 0);
      this.getDataManager().register(RADIUS, 0.5F);
      this.getDataManager().register(IGNORE_RADIUS, false);
      this.getDataManager().register(PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float radiusIn) {
      if (!this.world.isRemote) {
         this.getDataManager().set(RADIUS, radiusIn);
      }

   }

   public void recalculateSize() {
      double d0 = this.getPosX();
      double d1 = this.getPosY();
      double d2 = this.getPosZ();
      super.recalculateSize();
      this.setPosition(d0, d1, d2);
   }

   public float getRadius() {
      return this.getDataManager().get(RADIUS);
   }

   public void setPotion(Potion potionIn) {
      this.potion = potionIn;
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   private void updateFixedColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getDataManager().set(COLOR, 0);
      } else {
         this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(EffectInstance effect) {
      this.effects.add(effect);
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   public int getColor() {
      return this.getDataManager().get(COLOR);
   }

   public void setColor(int colorIn) {
      this.colorSet = true;
      this.getDataManager().set(COLOR, colorIn);
   }

   public IParticleData getParticleData() {
      return this.getDataManager().get(PARTICLE);
   }

   public void setParticleData(IParticleData p_195059_1_) {
      this.getDataManager().set(PARTICLE, p_195059_1_);
   }

   /**
    * Sets if the radius should be ignored, and the effect should be shown in a single point instead of an area
    */
   protected void setIgnoreRadius(boolean ignoreRadius) {
      this.getDataManager().set(IGNORE_RADIUS, ignoreRadius);
   }

   /**
    * Returns true if the radius should be ignored, and the effect should be shown in a single point instead of an area
    */
   public boolean shouldIgnoreRadius() {
      return this.getDataManager().get(IGNORE_RADIUS);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int durationIn) {
      this.duration = durationIn;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      boolean flag = this.shouldIgnoreRadius();
      float f = this.getRadius();
      if (this.world.isRemote) {
         IParticleData iparticledata = this.getParticleData();
         if (flag) {
            if (this.rand.nextBoolean()) {
               for(int i = 0; i < 2; ++i) {
                  float f1 = this.rand.nextFloat() * ((float)Math.PI * 2F);
                  float f2 = MathHelper.sqrt(this.rand.nextFloat()) * 0.2F;
                  float f3 = MathHelper.cos(f1) * f2;
                  float f4 = MathHelper.sin(f1) * f2;
                  if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                     int j = this.rand.nextBoolean() ? 16777215 : this.getColor();
                     int k = j >> 16 & 255;
                     int l = j >> 8 & 255;
                     int i1 = j & 255;
                     this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f3, this.getPosY(), this.getPosZ() + (double)f4, (double)((float)k / 255.0F), (double)((float)l / 255.0F), (double)((float)i1 / 255.0F));
                  } else {
                     this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f3, this.getPosY(), this.getPosZ() + (double)f4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float f5 = (float)Math.PI * f * f;

            for(int k1 = 0; (float)k1 < f5; ++k1) {
               float f6 = this.rand.nextFloat() * ((float)Math.PI * 2F);
               float f7 = MathHelper.sqrt(this.rand.nextFloat()) * f;
               float f8 = MathHelper.cos(f6) * f7;
               float f9 = MathHelper.sin(f6) * f7;
               if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                  int l1 = this.getColor();
                  int i2 = l1 >> 16 & 255;
                  int j2 = l1 >> 8 & 255;
                  int j1 = l1 & 255;
                  this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f8, this.getPosY(), this.getPosZ() + (double)f9, (double)((float)i2 / 255.0F), (double)((float)j2 / 255.0F), (double)((float)j1 / 255.0F));
               } else {
                  this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f8, this.getPosY(), this.getPosZ() + (double)f9, (0.5D - this.rand.nextDouble()) * 0.15D, (double)0.01F, (0.5D - this.rand.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.ticksExisted >= this.waitTime + this.duration) {
            this.remove();
            return;
         }

         boolean flag1 = this.ticksExisted < this.waitTime;
         if (flag != flag1) {
            this.setIgnoreRadius(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            f += this.radiusPerTick;
            if (f < 0.5F) {
               this.remove();
               return;
            }

            this.setRadius(f);
         }

         if (this.ticksExisted % 5 == 0) {
            Iterator<Entry<Entity, Integer>> iterator = this.reapplicationDelayMap.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Entity, Integer> entry = iterator.next();
               if (this.ticksExisted >= entry.getValue()) {
                  iterator.remove();
               }
            }

            List<EffectInstance> effects = Lists.newArrayList();

            for(EffectInstance effectinstance1 : this.potion.getEffects()) {
               effects.add(new EffectInstance(effectinstance1.getPotion(), effectinstance1.getDuration() / 4, effectinstance1.getAmplifier(), effectinstance1.isAmbient(), effectinstance1.doesShowParticles()));
            }

            effects.addAll(this.effects);
            if (effects.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox());
               if (!list.isEmpty()) {
                  for(LivingEntity livingentity : list) {
                     if (!this.reapplicationDelayMap.containsKey(livingentity) && livingentity.canBeHitWithPotion()) {
                        double d0 = livingentity.getPosX() - this.getPosX();
                        double d1 = livingentity.getPosZ() - this.getPosZ();
                        double d2 = d0 * d0 + d1 * d1;
                        if (d2 <= (double)(f * f)) {
                           this.reapplicationDelayMap.put(livingentity, this.ticksExisted + this.reapplicationDelay);

                           for(EffectInstance effectinstance : effects) {
                              if (effectinstance.getPotion().isInstant()) {
                                 effectinstance.getPotion().affectEntity(this, this.getOwner(), livingentity, effectinstance.getAmplifier(), 0.5D);
                              } else {
                                 livingentity.addPotionEffect(new EffectInstance(effectinstance));
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              f += this.radiusOnUse;
                              if (f < 0.5F) {
                                 this.remove();
                                 return;
                              }

                              this.setRadius(f);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration += this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.remove();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float radiusOnUseIn) {
      this.radiusOnUse = radiusOnUseIn;
   }

   public void setRadiusPerTick(float radiusPerTickIn) {
      this.radiusPerTick = radiusPerTickIn;
   }

   public void setWaitTime(int waitTimeIn) {
      this.waitTime = waitTimeIn;
   }

   public void setOwner(@Nullable LivingEntity ownerIn) {
      this.owner = ownerIn;
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.world instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.ownerUniqueId);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      this.ticksExisted = compound.getInt("Age");
      this.duration = compound.getInt("Duration");
      this.waitTime = compound.getInt("WaitTime");
      this.reapplicationDelay = compound.getInt("ReapplicationDelay");
      this.durationOnUse = compound.getInt("DurationOnUse");
      this.radiusOnUse = compound.getFloat("RadiusOnUse");
      this.radiusPerTick = compound.getFloat("RadiusPerTick");
      this.setRadius(compound.getFloat("Radius"));
      this.ownerUniqueId = compound.getUniqueId("OwnerUUID");
      if (compound.contains("Particle", 8)) {
         try {
            this.setParticleData(ParticleArgument.parseParticle(new StringReader(compound.getString("Particle"))));
         } catch (CommandSyntaxException commandsyntaxexception) {
            PRIVATE_LOGGER.warn("Couldn't load custom particle {}", compound.getString("Particle"), commandsyntaxexception);
         }
      }

      if (compound.contains("Color", 99)) {
         this.setColor(compound.getInt("Color"));
      }

      if (compound.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotionTypeFromNBT(compound));
      }

      if (compound.contains("Effects", 9)) {
         ListNBT listnbt = compound.getList("Effects", 10);
         this.effects.clear();

         for(int i = 0; i < listnbt.size(); ++i) {
            EffectInstance effectinstance = EffectInstance.read(listnbt.getCompound(i));
            if (effectinstance != null) {
               this.addEffect(effectinstance);
            }
         }
      }

   }

   protected void writeAdditional(CompoundNBT compound) {
      compound.putInt("Age", this.ticksExisted);
      compound.putInt("Duration", this.duration);
      compound.putInt("WaitTime", this.waitTime);
      compound.putInt("ReapplicationDelay", this.reapplicationDelay);
      compound.putInt("DurationOnUse", this.durationOnUse);
      compound.putFloat("RadiusOnUse", this.radiusOnUse);
      compound.putFloat("RadiusPerTick", this.radiusPerTick);
      compound.putFloat("Radius", this.getRadius());
      compound.putString("Particle", this.getParticleData().getParameters());
      if (this.ownerUniqueId != null) {
         compound.putUniqueId("OwnerUUID", this.ownerUniqueId);
      }

      if (this.colorSet) {
         compound.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY && this.potion != null) {
         compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListNBT listnbt = new ListNBT();

         for(EffectInstance effectinstance : this.effects) {
            listnbt.add(effectinstance.write(new CompoundNBT()));
         }

         compound.put("Effects", listnbt);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (RADIUS.equals(key)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(key);
   }

   public PushReaction getPushReaction() {
      return PushReaction.IGNORE;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   public EntitySize getSize(Pose poseIn) {
      return EntitySize.flexible(this.getRadius() * 2.0F, 0.5F);
   }
}