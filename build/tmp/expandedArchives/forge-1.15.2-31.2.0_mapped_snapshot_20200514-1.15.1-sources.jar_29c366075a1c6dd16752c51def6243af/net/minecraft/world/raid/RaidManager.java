package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class RaidManager extends WorldSavedData {
   private final Map<Integer, Raid> byId = Maps.newHashMap();
   private final ServerWorld world;
   private int nextAvailableId;
   private int tick;

   public RaidManager(ServerWorld p_i50142_1_) {
      super(getFileId(p_i50142_1_.dimension));
      this.world = p_i50142_1_;
      this.nextAvailableId = 1;
      this.markDirty();
   }

   public Raid get(int id) {
      return this.byId.get(id);
   }

   public void tick() {
      ++this.tick;
      Iterator<Raid> iterator = this.byId.values().iterator();

      while(iterator.hasNext()) {
         Raid raid = iterator.next();
         if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            raid.stop();
         }

         if (raid.isStopped()) {
            iterator.remove();
            this.markDirty();
         } else {
            raid.tick();
         }
      }

      if (this.tick % 200 == 0) {
         this.markDirty();
      }

      DebugPacketSender.sendRaids(this.world, this.byId.values());
   }

   public static boolean canJoinRaid(AbstractRaiderEntity p_215165_0_, Raid p_215165_1_) {
      if (p_215165_0_ != null && p_215165_1_ != null && p_215165_1_.getWorld() != null) {
         return p_215165_0_.isAlive() && p_215165_0_.func_213658_ej() && p_215165_0_.getIdleTime() <= 2400 && p_215165_0_.world.getDimension().getType() == p_215165_1_.getWorld().getDimension().getType();
      } else {
         return false;
      }
   }

   @Nullable
   public Raid badOmenTick(ServerPlayerEntity p_215170_1_) {
      if (p_215170_1_.isSpectator()) {
         return null;
      } else if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
         return null;
      } else {
         DimensionType dimensiontype = p_215170_1_.world.getDimension().getType();
         if (dimensiontype == DimensionType.THE_NETHER) {
            return null;
         } else {
            BlockPos blockpos = new BlockPos(p_215170_1_);
            List<PointOfInterest> list = this.world.getPointOfInterestManager().func_219146_b(PointOfInterestType.MATCH_ANY, blockpos, 64, PointOfInterestManager.Status.IS_OCCUPIED).collect(Collectors.toList());
            int i = 0;
            Vec3d vec3d = Vec3d.ZERO;

            for(PointOfInterest pointofinterest : list) {
               BlockPos blockpos2 = pointofinterest.getPos();
               vec3d = vec3d.add((double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ());
               ++i;
            }

            BlockPos blockpos1;
            if (i > 0) {
               vec3d = vec3d.scale(1.0D / (double)i);
               blockpos1 = new BlockPos(vec3d);
            } else {
               blockpos1 = blockpos;
            }

            Raid raid = this.findOrCreateRaid(p_215170_1_.getServerWorld(), blockpos1);
            boolean flag = false;
            if (!raid.isStarted()) {
               if (!this.byId.containsKey(raid.getId())) {
                  this.byId.put(raid.getId(), raid);
               }

               flag = true;
            } else if (raid.getBadOmenLevel() < raid.getMaxLevel()) {
               flag = true;
            } else {
               p_215170_1_.removePotionEffect(Effects.BAD_OMEN);
               p_215170_1_.connection.sendPacket(new SEntityStatusPacket(p_215170_1_, (byte)43));
            }

            if (flag) {
               raid.increaseLevel(p_215170_1_);
               p_215170_1_.connection.sendPacket(new SEntityStatusPacket(p_215170_1_, (byte)43));
               if (!raid.func_221297_c()) {
                  p_215170_1_.addStat(Stats.RAID_TRIGGER);
                  CriteriaTriggers.VOLUNTARY_EXILE.trigger(p_215170_1_);
               }
            }

            this.markDirty();
            return raid;
         }
      }
   }

   private Raid findOrCreateRaid(ServerWorld p_215168_1_, BlockPos p_215168_2_) {
      Raid raid = p_215168_1_.findRaid(p_215168_2_);
      return raid != null ? raid : new Raid(this.incrementNextId(), p_215168_1_, p_215168_2_);
   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(CompoundNBT nbt) {
      this.nextAvailableId = nbt.getInt("NextAvailableID");
      this.tick = nbt.getInt("Tick");
      ListNBT listnbt = nbt.getList("Raids", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         Raid raid = new Raid(this.world, compoundnbt);
         this.byId.put(raid.getId(), raid);
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      compound.putInt("NextAvailableID", this.nextAvailableId);
      compound.putInt("Tick", this.tick);
      ListNBT listnbt = new ListNBT();

      for(Raid raid : this.byId.values()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         raid.write(compoundnbt);
         listnbt.add(compoundnbt);
      }

      compound.put("Raids", listnbt);
      return compound;
   }

   public static String getFileId(Dimension p_215172_0_) {
      return "raids" + p_215172_0_.getType().getSuffix();
   }

   private int incrementNextId() {
      return ++this.nextAvailableId;
   }

   @Nullable
   public Raid findRaid(BlockPos p_215174_1_, int distance) {
      Raid raid = null;
      double d0 = (double)distance;

      for(Raid raid1 : this.byId.values()) {
         double d1 = raid1.getCenter().distanceSq(p_215174_1_);
         if (raid1.isActive() && d1 < d0) {
            raid = raid1;
            d0 = d1;
         }
      }

      return raid;
   }
}