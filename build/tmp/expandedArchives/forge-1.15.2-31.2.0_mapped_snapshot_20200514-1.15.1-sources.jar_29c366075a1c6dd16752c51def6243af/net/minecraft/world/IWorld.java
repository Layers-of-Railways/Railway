package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IEntityReader, IWorldReader, IWorldGenerationReader {
   /**
    * gets the random world seed
    */
   long getSeed();

   /**
    * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
    */
   default float getCurrentMoonPhaseFactor() {
      return this.getDimension().getCurrentMoonPhaseFactor(this.getWorld().getDayTime());
   }

   /**
    * calls calculateCelestialAngle
    */
   default float getCelestialAngle(float partialTicks) {
      return this.getDimension().calculateCelestialAngle(this.getWorld().getDayTime(), partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getWorld().getDayTime());
   }

   ITickList<Block> getPendingBlockTicks();

   ITickList<Fluid> getPendingFluidTicks();

   World getWorld();

   /**
    * Returns the world's WorldInfo object
    */
   WorldInfo getWorldInfo();

   DifficultyInstance getDifficultyForLocation(BlockPos pos);

   default Difficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   /**
    * Gets the world's chunk provider
    */
   AbstractChunkProvider getChunkProvider();

   default boolean chunkExists(int chunkX, int chunkZ) {
      return this.getChunkProvider().chunkExists(chunkX, chunkZ);
   }

   Random getRandom();

   void notifyNeighbors(BlockPos pos, Block blockIn);

   /**
    * Gets the spawn point in the world
    */
   @OnlyIn(Dist.CLIENT)
   BlockPos getSpawnPoint();

   /**
    * Plays a sound. On the server, the sound is broadcast to all nearby <em>except</em> the given player. On the
    * client, the sound only plays if the given player is the client player. Thus, this method is intended to be called
    * from code running on both sides. The client plays it locally and the server plays it for everyone else.
    */
   void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch);

   void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

   void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data);

   default void playEvent(int type, BlockPos pos, int data) {
      this.playEvent((PlayerEntity)null, type, pos, data);
   }

   default Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity entityIn, AxisAlignedBB aabb, Set<Entity> entitiesToIgnore) {
      return IEntityReader.super.getEmptyCollisionShapes(entityIn, aabb, entitiesToIgnore);
   }

   default boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      return IEntityReader.super.checkNoEntityCollision(entityIn, shape);
   }

   default BlockPos getHeight(Heightmap.Type heightmapType, BlockPos pos) {
      return IWorldReader.super.getHeight(heightmapType, pos);
   }
}