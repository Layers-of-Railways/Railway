package net.minecraft.world.spawner;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;

public class WanderingTraderSpawner {
   private final Random random = new Random();
   private final ServerWorld world;
   private int field_221248_c;
   private int field_221249_d;
   private int field_221250_e;

   public WanderingTraderSpawner(ServerWorld p_i50177_1_) {
      this.world = p_i50177_1_;
      this.field_221248_c = 1200;
      WorldInfo worldinfo = p_i50177_1_.getWorldInfo();
      this.field_221249_d = worldinfo.getWanderingTraderSpawnDelay();
      this.field_221250_e = worldinfo.getWanderingTraderSpawnChance();
      if (this.field_221249_d == 0 && this.field_221250_e == 0) {
         this.field_221249_d = 24000;
         worldinfo.setWanderingTraderSpawnDelay(this.field_221249_d);
         this.field_221250_e = 25;
         worldinfo.setWanderingTraderSpawnChance(this.field_221250_e);
      }

   }

   public void tick() {
      if (this.world.getGameRules().getBoolean(GameRules.field_230128_E_)) {
         if (--this.field_221248_c <= 0) {
            this.field_221248_c = 1200;
            WorldInfo worldinfo = this.world.getWorldInfo();
            this.field_221249_d -= 1200;
            worldinfo.setWanderingTraderSpawnDelay(this.field_221249_d);
            if (this.field_221249_d <= 0) {
               this.field_221249_d = 24000;
               if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                  int i = this.field_221250_e;
                  this.field_221250_e = MathHelper.clamp(this.field_221250_e + 25, 25, 75);
                  worldinfo.setWanderingTraderSpawnChance(this.field_221250_e);
                  if (this.random.nextInt(100) <= i) {
                     if (this.func_221245_b()) {
                        this.field_221250_e = 25;
                     }

                  }
               }
            }
         }
      }
   }

   private boolean func_221245_b() {
      PlayerEntity playerentity = this.world.getRandomPlayer();
      if (playerentity == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos blockpos = playerentity.getPosition();
         int i = 48;
         PointOfInterestManager pointofinterestmanager = this.world.getPointOfInterestManager();
         Optional<BlockPos> optional = pointofinterestmanager.find(PointOfInterestType.MEETING.getPredicate(), (p_221241_0_) -> {
            return true;
         }, blockpos, 48, PointOfInterestManager.Status.ANY);
         BlockPos blockpos1 = optional.orElse(blockpos);
         BlockPos blockpos2 = this.func_221244_a(blockpos1, 48);
         if (blockpos2 != null && this.func_226559_a_(blockpos2)) {
            if (this.world.getBiome(blockpos2) == Biomes.THE_VOID) {
               return false;
            }

            WanderingTraderEntity wanderingtraderentity = EntityType.WANDERING_TRADER.spawn(this.world, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos2, SpawnReason.EVENT, false, false);
            if (wanderingtraderentity != null) {
               for(int j = 0; j < 2; ++j) {
                  this.func_221243_a(wanderingtraderentity, 4);
               }

               this.world.getWorldInfo().setWanderingTraderId(wanderingtraderentity.getUniqueID());
               wanderingtraderentity.setDespawnDelay(48000);
               wanderingtraderentity.setWanderTarget(blockpos1);
               wanderingtraderentity.setHomePosAndDistance(blockpos1, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void func_221243_a(WanderingTraderEntity p_221243_1_, int p_221243_2_) {
      BlockPos blockpos = this.func_221244_a(new BlockPos(p_221243_1_), p_221243_2_);
      if (blockpos != null) {
         TraderLlamaEntity traderllamaentity = EntityType.TRADER_LLAMA.spawn(this.world, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos, SpawnReason.EVENT, false, false);
         if (traderllamaentity != null) {
            traderllamaentity.setLeashHolder(p_221243_1_, true);
         }
      }
   }

   @Nullable
   private BlockPos func_221244_a(BlockPos p_221244_1_, int p_221244_2_) {
      BlockPos blockpos = null;

      for(int i = 0; i < 10; ++i) {
         int j = p_221244_1_.getX() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
         int k = p_221244_1_.getZ() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
         int l = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
         BlockPos blockpos1 = new BlockPos(j, l, k);
         if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.world, blockpos1, EntityType.WANDERING_TRADER)) {
            blockpos = blockpos1;
            break;
         }
      }

      return blockpos;
   }

   private boolean func_226559_a_(BlockPos p_226559_1_) {
      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(p_226559_1_, p_226559_1_.add(1, 2, 1))) {
         if (!this.world.getBlockState(blockpos).getCollisionShape(this.world, blockpos).isEmpty()) {
            return false;
         }
      }

      return true;
   }
}