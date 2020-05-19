package net.minecraft.world.spawner;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void spawnEntitiesInChunk(EntityClassification p_226701_0_, ServerWorld p_226701_1_, Chunk p_226701_2_, BlockPos p_226701_3_) {
      ChunkGenerator<?> chunkgenerator = p_226701_1_.getChunkProvider().getChunkGenerator();
      int i = 0;
      BlockPos blockpos = getRandomHeight(p_226701_1_, p_226701_2_);
      int j = blockpos.getX();
      int k = blockpos.getY();
      int l = blockpos.getZ();
      if (k >= 1) {
         BlockState blockstate = p_226701_2_.getBlockState(blockpos);
         if (!blockstate.isNormalCube(p_226701_2_, blockpos)) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            int i1 = 0;

            while(i1 < 3) {
               int j1 = j;
               int k1 = l;
               int l1 = 6;
               Biome.SpawnListEntry biome$spawnlistentry = null;
               ILivingEntityData ilivingentitydata = null;
               int i2 = MathHelper.ceil(Math.random() * 4.0D);
               int j2 = 0;
               int k2 = 0;

               while(true) {
                  label115: {
                     if (k2 < i2) {
                        label123: {
                           j1 += p_226701_1_.rand.nextInt(6) - p_226701_1_.rand.nextInt(6);
                           k1 += p_226701_1_.rand.nextInt(6) - p_226701_1_.rand.nextInt(6);
                           blockpos$mutable.setPos(j1, k, k1);
                           float f = (float)j1 + 0.5F;
                           float f1 = (float)k1 + 0.5F;
                           PlayerEntity playerentity = p_226701_1_.getClosestPlayer((double)f, (double)f1, -1.0D);
                           if (playerentity == null) {
                              break label115;
                           }

                           double d0 = playerentity.getDistanceSq((double)f, (double)k, (double)f1);
                           if (d0 <= 576.0D || p_226701_3_.withinDistance(new Vec3d((double)f, (double)k, (double)f1), 24.0D)) {
                              break label115;
                           }

                           ChunkPos chunkpos = new ChunkPos(blockpos$mutable);
                           if (!Objects.equals(chunkpos, p_226701_2_.getPos()) && !p_226701_1_.getChunkProvider().isChunkLoaded(chunkpos)) {
                              break label115;
                           }

                           if (biome$spawnlistentry == null) {
                              biome$spawnlistentry = getSpawnList(chunkgenerator, p_226701_0_, p_226701_1_.rand, blockpos$mutable, p_226701_1_);
                              if (biome$spawnlistentry == null) {
                                 break label123;
                              }

                              i2 = biome$spawnlistentry.minGroupCount + p_226701_1_.rand.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
                           }

                           if (biome$spawnlistentry.entityType.getClassification() == EntityClassification.MISC || !biome$spawnlistentry.entityType.func_225437_d() && d0 > 16384.0D) {
                              break label115;
                           }

                           EntityType<?> entitytype = biome$spawnlistentry.entityType;
                           if (!entitytype.isSummonable() || !getSpawnList(chunkgenerator, p_226701_0_, biome$spawnlistentry, blockpos$mutable, p_226701_1_)) {
                              break label115;
                           }

                           EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
                           if (!canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, p_226701_1_, blockpos$mutable, entitytype) || !EntitySpawnPlacementRegistry.func_223515_a(entitytype, p_226701_1_, SpawnReason.NATURAL, blockpos$mutable, p_226701_1_.rand) || !p_226701_1_.hasNoCollisions(entitytype.func_220328_a((double)f, (double)k, (double)f1))) {
                              break label115;
                           }

                           MobEntity mobentity;
                           try {
                              Entity entity = entitytype.create(p_226701_1_);
                              if (!(entity instanceof MobEntity)) {
                                 throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(entitytype));
                              }

                              mobentity = (MobEntity)entity;
                           } catch (Exception exception) {
                              LOGGER.warn("Failed to create mob", (Throwable)exception);
                              return;
                           }

                           mobentity.setLocationAndAngles((double)f, (double)k, (double)f1, p_226701_1_.rand.nextFloat() * 360.0F, 0.0F);
                           int canSpawn = net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobentity, p_226701_1_, f, k, f1, null, SpawnReason.NATURAL);
                           if (canSpawn == -1 || (canSpawn == 0 && (d0 > 16384.0D && mobentity.canDespawn(d0) || !mobentity.canSpawn(p_226701_1_, SpawnReason.NATURAL) || !mobentity.isNotColliding(p_226701_1_)))) {
                              break label115;
                           }

                           if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mobentity, p_226701_1_, f, k, f1, null, SpawnReason.NATURAL))
                           ilivingentitydata = mobentity.onInitialSpawn(p_226701_1_, p_226701_1_.getDifficultyForLocation(new BlockPos(mobentity)), SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                           ++i;
                           ++j2;
                           p_226701_1_.addEntity(mobentity);
                           if (i >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(mobentity)) {
                              return;
                           }

                           if (!mobentity.isMaxGroupSize(j2)) {
                              break label115;
                           }
                        }
                     }

                     ++i1;
                     break;
                  }

                  ++k2;
               }
            }

         }
      }
   }

   @Nullable
   private static Biome.SpawnListEntry getSpawnList(ChunkGenerator<?> p_222264_0_, EntityClassification p_222264_1_, Random p_222264_2_, BlockPos p_222264_3_, World world) {
      List<Biome.SpawnListEntry> list = p_222264_0_.getPossibleCreatures(p_222264_1_, p_222264_3_);
      list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(world, p_222264_1_, p_222264_3_, list);
      return list.isEmpty() ? null : WeightedRandom.getRandomItem(p_222264_2_, list);
   }

   private static boolean getSpawnList(ChunkGenerator<?> p_222261_0_, EntityClassification p_222261_1_, Biome.SpawnListEntry p_222261_2_, BlockPos p_222261_3_, World world) {
      List<Biome.SpawnListEntry> list = p_222261_0_.getPossibleCreatures(p_222261_1_, p_222261_3_);
      list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(world, p_222261_1_, p_222261_3_, list);
      return list.isEmpty() ? false : list.contains(p_222261_2_);
   }

   private static BlockPos getRandomHeight(World worldIn, Chunk p_222262_1_) {
      ChunkPos chunkpos = p_222262_1_.getPos();
      int i = chunkpos.getXStart() + worldIn.rand.nextInt(16);
      int j = chunkpos.getZStart() + worldIn.rand.nextInt(16);
      int k = p_222262_1_.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
      int l = worldIn.rand.nextInt(k + 1);
      return new BlockPos(i, l, j);
   }

   public static boolean isSpawnableSpace(IBlockReader worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      if (state.isCollisionShapeOpaque(worldIn, pos)) {
         return false;
      } else if (state.canProvidePower()) {
         return false;
      } else if (!fluidStateIn.isEmpty()) {
         return false;
      } else {
         return !state.isIn(BlockTags.RAILS);
      }
   }

   public static boolean canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType placeType, IWorldReader worldIn, BlockPos pos, @Nullable EntityType<?> entityTypeIn) {
      if (placeType == EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS) {
         return true;
      } else if (entityTypeIn != null && worldIn.getWorldBorder().contains(pos)) {
         return placeType.canSpawnAt(worldIn, pos, entityTypeIn);
      }
      return false;
   }

   public static boolean canSpawnAtBody(EntitySpawnPlacementRegistry.PlacementType placeType, IWorldReader worldIn, BlockPos pos, @Nullable EntityType<?> entityTypeIn) {
      {
         BlockState blockstate = worldIn.getBlockState(pos);
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         BlockPos blockpos = pos.up();
         BlockPos blockpos1 = pos.down();
         switch(placeType) {
         case IN_WATER:
            return ifluidstate.isTagged(FluidTags.WATER) && worldIn.getFluidState(blockpos1).isTagged(FluidTags.WATER) && !worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos);
         case ON_GROUND:
         default:
            BlockState blockstate1 = worldIn.getBlockState(blockpos1);
            if (!blockstate1.canCreatureSpawn(worldIn, blockpos1, placeType, entityTypeIn)) {
               return false;
            } else {
               return isSpawnableSpace(worldIn, pos, blockstate, ifluidstate) && isSpawnableSpace(worldIn, blockpos, worldIn.getBlockState(blockpos), worldIn.getFluidState(blockpos));
            }
         }
      }
   }

   /**
    * Called during chunk generation to spawn initial creatures.
    */
   public static void performWorldGenSpawning(IWorld worldIn, Biome biomeIn, int centerX, int centerZ, Random diameterX) {
      List<Biome.SpawnListEntry> list = biomeIn.getSpawns(EntityClassification.CREATURE);
      if (!list.isEmpty()) {
         int i = centerX << 4;
         int j = centerZ << 4;

         while(diameterX.nextFloat() < biomeIn.getSpawningChance()) {
            Biome.SpawnListEntry biome$spawnlistentry = WeightedRandom.getRandomItem(diameterX, list);
            int k = biome$spawnlistentry.minGroupCount + diameterX.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
            ILivingEntityData ilivingentitydata = null;
            int l = i + diameterX.nextInt(16);
            int i1 = j + diameterX.nextInt(16);
            int j1 = l;
            int k1 = i1;

            for(int l1 = 0; l1 < k; ++l1) {
               boolean flag = false;

               for(int i2 = 0; !flag && i2 < 4; ++i2) {
                  BlockPos blockpos = getTopSolidOrLiquidBlock(worldIn, biome$spawnlistentry.entityType, l, i1);
                  if (biome$spawnlistentry.entityType.isSummonable() && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, worldIn, blockpos, biome$spawnlistentry.entityType)) {
                     float f = biome$spawnlistentry.entityType.getWidth();
                     double d0 = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                     double d1 = MathHelper.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);
                     if (!worldIn.hasNoCollisions(biome$spawnlistentry.entityType.func_220328_a(d0, (double)blockpos.getY(), d1)) || !EntitySpawnPlacementRegistry.func_223515_a(biome$spawnlistentry.entityType, worldIn, SpawnReason.CHUNK_GENERATION, new BlockPos(d0, (double)blockpos.getY(), d1), worldIn.getRandom())) {
                        continue;
                     }

                     Entity entity;
                     try {
                        entity = biome$spawnlistentry.entityType.create(worldIn.getWorld());
                     } catch (Exception exception) {
                        LOGGER.warn("Failed to create mob", (Throwable)exception);
                        continue;
                     }

                     entity.setLocationAndAngles(d0, (double)blockpos.getY(), d1, diameterX.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobentity = (MobEntity)entity;
                        if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobentity, worldIn, d0, blockpos.getY(), d1, null, SpawnReason.CHUNK_GENERATION) == -1) continue;
                        if (mobentity.canSpawn(worldIn, SpawnReason.CHUNK_GENERATION) && mobentity.isNotColliding(worldIn)) {
                           ilivingentitydata = mobentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(mobentity)), SpawnReason.CHUNK_GENERATION, ilivingentitydata, (CompoundNBT)null);
                           worldIn.addEntity(mobentity);
                           flag = true;
                        }
                     }
                  }

                  l += diameterX.nextInt(5) - diameterX.nextInt(5);

                  for(i1 += diameterX.nextInt(5) - diameterX.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + diameterX.nextInt(5) - diameterX.nextInt(5)) {
                     l = j1 + diameterX.nextInt(5) - diameterX.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopSolidOrLiquidBlock(IWorldReader worldIn, @Nullable EntityType<?> p_208498_1_, int x, int z) {
      BlockPos blockpos = new BlockPos(x, worldIn.getHeight(EntitySpawnPlacementRegistry.func_209342_b(p_208498_1_), x, z), z);
      BlockPos blockpos1 = blockpos.down();
      return worldIn.getBlockState(blockpos1).allowsMovement(worldIn, blockpos1, PathType.LAND) ? blockpos1 : blockpos;
   }
}