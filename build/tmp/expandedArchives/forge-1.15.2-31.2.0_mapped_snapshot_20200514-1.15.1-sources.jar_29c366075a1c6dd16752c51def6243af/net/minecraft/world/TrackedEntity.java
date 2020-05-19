package net.minecraft.world;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackedEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerWorld world;
   private final Entity trackedEntity;
   private final int updateFrequency;
   private final boolean sendVelocityUpdates;
   private final Consumer<IPacket<?>> packetConsumer;
   private long encodedPosX;
   private long encodedPosY;
   private long encodedPosZ;
   private int encodedRotationYaw;
   private int encodedRotationPitch;
   private int encodedRotationYawHead;
   private Vec3d velocity = Vec3d.ZERO;
   private int updateCounter;
   private int ticksSinceAbsoluteTeleport;
   private List<Entity> passengers = Collections.emptyList();
   private boolean riding;
   private boolean onGround;

   public TrackedEntity(ServerWorld p_i50704_1_, Entity p_i50704_2_, int p_i50704_3_, boolean p_i50704_4_, Consumer<IPacket<?>> p_i50704_5_) {
      this.world = p_i50704_1_;
      this.packetConsumer = p_i50704_5_;
      this.trackedEntity = p_i50704_2_;
      this.updateFrequency = p_i50704_3_;
      this.sendVelocityUpdates = p_i50704_4_;
      this.updateEncodedPosition();
      this.encodedRotationYaw = MathHelper.floor(p_i50704_2_.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor(p_i50704_2_.rotationPitch * 256.0F / 360.0F);
      this.encodedRotationYawHead = MathHelper.floor(p_i50704_2_.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = p_i50704_2_.onGround;
   }

   public void tick() {
      List<Entity> list = this.trackedEntity.getPassengers();
      if (!list.equals(this.passengers)) {
         this.passengers = list;
         this.packetConsumer.accept(new SSetPassengersPacket(this.trackedEntity));
      }

      if (this.trackedEntity instanceof ItemFrameEntity && this.updateCounter % 10 == 0) {
         ItemFrameEntity itemframeentity = (ItemFrameEntity)this.trackedEntity;
         ItemStack itemstack = itemframeentity.getDisplayedItem();
         MapData mapdata = FilledMapItem.getMapData(itemstack, this.world);
         if (mapdata != null) {
            for(ServerPlayerEntity serverplayerentity : this.world.getPlayers()) {
               mapdata.updateVisiblePlayers(serverplayerentity, itemstack);
               IPacket<?> ipacket = ((FilledMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.world, serverplayerentity);
               if (ipacket != null) {
                  serverplayerentity.connection.sendPacket(ipacket);
               }
            }
         }

         this.sendMetadata();
      }

      if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataManager().isDirty()) {
         if (this.trackedEntity.isPassenger()) {
            int i1 = MathHelper.floor(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int l1 = MathHelper.floor(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean flag2 = Math.abs(i1 - this.encodedRotationYaw) >= 1 || Math.abs(l1 - this.encodedRotationPitch) >= 1;
            if (flag2) {
               this.packetConsumer.accept(new SEntityPacket.LookPacket(this.trackedEntity.getEntityId(), (byte)i1, (byte)l1, this.trackedEntity.onGround));
               this.encodedRotationYaw = i1;
               this.encodedRotationPitch = l1;
            }

            this.updateEncodedPosition();
            this.sendMetadata();
            this.riding = true;
         } else {
            ++this.ticksSinceAbsoluteTeleport;
            int l = MathHelper.floor(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int k1 = MathHelper.floor(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            Vec3d vec3d = this.trackedEntity.getPositionVec().subtract(SEntityPacket.func_218744_a(this.encodedPosX, this.encodedPosY, this.encodedPosZ));
            boolean flag3 = vec3d.lengthSquared() >= (double)7.6293945E-6F;
            IPacket<?> ipacket1 = null;
            boolean flag4 = flag3 || this.updateCounter % 60 == 0;
            boolean flag = Math.abs(l - this.encodedRotationYaw) >= 1 || Math.abs(k1 - this.encodedRotationPitch) >= 1;
            if (this.updateCounter > 0 || this.trackedEntity instanceof AbstractArrowEntity) {
               long i = SEntityPacket.func_218743_a(vec3d.x);
               long j = SEntityPacket.func_218743_a(vec3d.y);
               long k = SEntityPacket.func_218743_a(vec3d.z);
               boolean flag1 = i < -32768L || i > 32767L || j < -32768L || j > 32767L || k < -32768L || k > 32767L;
               if (!flag1 && this.ticksSinceAbsoluteTeleport <= 400 && !this.riding && this.onGround == this.trackedEntity.onGround) {
                  if ((!flag4 || !flag) && !(this.trackedEntity instanceof AbstractArrowEntity)) {
                     if (flag4) {
                        ipacket1 = new SEntityPacket.RelativeMovePacket(this.trackedEntity.getEntityId(), (short)((int)i), (short)((int)j), (short)((int)k), this.trackedEntity.onGround);
                     } else if (flag) {
                        ipacket1 = new SEntityPacket.LookPacket(this.trackedEntity.getEntityId(), (byte)l, (byte)k1, this.trackedEntity.onGround);
                     }
                  } else {
                     ipacket1 = new SEntityPacket.MovePacket(this.trackedEntity.getEntityId(), (short)((int)i), (short)((int)j), (short)((int)k), (byte)l, (byte)k1, this.trackedEntity.onGround);
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceAbsoluteTeleport = 0;
                  ipacket1 = new SEntityTeleportPacket(this.trackedEntity);
               }
            }

            if ((this.sendVelocityUpdates || this.trackedEntity.isAirBorne || this.trackedEntity instanceof LivingEntity && ((LivingEntity)this.trackedEntity).isElytraFlying()) && this.updateCounter > 0) {
               Vec3d vec3d1 = this.trackedEntity.getMotion();
               double d0 = vec3d1.squareDistanceTo(this.velocity);
               if (d0 > 1.0E-7D || d0 > 0.0D && vec3d1.lengthSquared() == 0.0D) {
                  this.velocity = vec3d1;
                  this.packetConsumer.accept(new SEntityVelocityPacket(this.trackedEntity.getEntityId(), this.velocity));
               }
            }

            if (ipacket1 != null) {
               this.packetConsumer.accept(ipacket1);
            }

            this.sendMetadata();
            if (flag4) {
               this.updateEncodedPosition();
            }

            if (flag) {
               this.encodedRotationYaw = l;
               this.encodedRotationPitch = k1;
            }

            this.riding = false;
         }

         int j1 = MathHelper.floor(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(j1 - this.encodedRotationYawHead) >= 1) {
            this.packetConsumer.accept(new SEntityHeadLookPacket(this.trackedEntity, (byte)j1));
            this.encodedRotationYawHead = j1;
         }

         this.trackedEntity.isAirBorne = false;
      }

      ++this.updateCounter;
      if (this.trackedEntity.velocityChanged) {
         this.sendPacket(new SEntityVelocityPacket(this.trackedEntity));
         this.trackedEntity.velocityChanged = false;
      }

   }

   public void untrack(ServerPlayerEntity player) {
      this.trackedEntity.removeTrackingPlayer(player);
      player.removeEntity(this.trackedEntity);
      net.minecraftforge.event.ForgeEventFactory.onStopEntityTracking(this.trackedEntity, player);
   }

   public void track(ServerPlayerEntity player) {
      this.sendSpawnPackets(player.connection::sendPacket);
      this.trackedEntity.addTrackingPlayer(player);
      player.addEntity(this.trackedEntity);
      net.minecraftforge.event.ForgeEventFactory.onStartEntityTracking(this.trackedEntity, player);
   }

   public void sendSpawnPackets(Consumer<IPacket<?>> p_219452_1_) {
      if (this.trackedEntity.removed) {
         LOGGER.warn("Fetching packet for removed entity " + this.trackedEntity);
      }

      IPacket<?> ipacket = this.trackedEntity.createSpawnPacket();
      this.encodedRotationYawHead = MathHelper.floor(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
      p_219452_1_.accept(ipacket);
      if (!this.trackedEntity.getDataManager().isEmpty()) {
         p_219452_1_.accept(new SEntityMetadataPacket(this.trackedEntity.getEntityId(), this.trackedEntity.getDataManager(), true));
      }

      boolean flag = this.sendVelocityUpdates;
      if (this.trackedEntity instanceof LivingEntity) {
         AttributeMap attributemap = (AttributeMap)((LivingEntity)this.trackedEntity).getAttributes();
         Collection<IAttributeInstance> collection = attributemap.getWatchedAttributes();
         if (!collection.isEmpty()) {
            p_219452_1_.accept(new SEntityPropertiesPacket(this.trackedEntity.getEntityId(), collection));
         }

         if (((LivingEntity)this.trackedEntity).isElytraFlying()) {
            flag = true;
         }
      }

      this.velocity = this.trackedEntity.getMotion();
      if (flag && !(ipacket instanceof SSpawnMobPacket)) {
         p_219452_1_.accept(new SEntityVelocityPacket(this.trackedEntity.getEntityId(), this.velocity));
      }

      if (this.trackedEntity instanceof LivingEntity) {
         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            ItemStack itemstack = ((LivingEntity)this.trackedEntity).getItemStackFromSlot(equipmentslottype);
            if (!itemstack.isEmpty()) {
               p_219452_1_.accept(new SEntityEquipmentPacket(this.trackedEntity.getEntityId(), equipmentslottype, itemstack));
            }
         }
      }

      if (this.trackedEntity instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)this.trackedEntity;

         for(EffectInstance effectinstance : livingentity.getActivePotionEffects()) {
            p_219452_1_.accept(new SPlayEntityEffectPacket(this.trackedEntity.getEntityId(), effectinstance));
         }
      }

      if (!this.trackedEntity.getPassengers().isEmpty()) {
         p_219452_1_.accept(new SSetPassengersPacket(this.trackedEntity));
      }

      if (this.trackedEntity.isPassenger()) {
         p_219452_1_.accept(new SSetPassengersPacket(this.trackedEntity.getRidingEntity()));
      }

      if (this.trackedEntity instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.trackedEntity;
         if (mobentity.getLeashed()) {
            p_219452_1_.accept(new SMountEntityPacket(mobentity, mobentity.getLeashHolder()));
         }
      }

   }

   private void sendMetadata() {
      EntityDataManager entitydatamanager = this.trackedEntity.getDataManager();
      if (entitydatamanager.isDirty()) {
         this.sendPacket(new SEntityMetadataPacket(this.trackedEntity.getEntityId(), entitydatamanager, false));
      }

      if (this.trackedEntity instanceof LivingEntity) {
         AttributeMap attributemap = (AttributeMap)((LivingEntity)this.trackedEntity).getAttributes();
         Set<IAttributeInstance> set = attributemap.getDirtyInstances();
         if (!set.isEmpty()) {
            this.sendPacket(new SEntityPropertiesPacket(this.trackedEntity.getEntityId(), set));
         }

         set.clear();
      }

   }

   private void updateEncodedPosition() {
      this.encodedPosX = SEntityPacket.func_218743_a(this.trackedEntity.getPosX());
      this.encodedPosY = SEntityPacket.func_218743_a(this.trackedEntity.getPosY());
      this.encodedPosZ = SEntityPacket.func_218743_a(this.trackedEntity.getPosZ());
   }

   public Vec3d func_219456_b() {
      return SEntityPacket.func_218744_a(this.encodedPosX, this.encodedPosY, this.encodedPosZ);
   }

   private void sendPacket(IPacket<?> p_219451_1_) {
      this.packetConsumer.accept(p_219451_1_);
      if (this.trackedEntity instanceof ServerPlayerEntity) {
         ((ServerPlayerEntity)this.trackedEntity).connection.sendPacket(p_219451_1_);
      }

   }
}