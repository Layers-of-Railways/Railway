package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiggingParticle extends SpriteTexturedParticle {
   private final BlockState sourceState;
   private BlockPos sourcePos;
   private final float field_217587_G;
   private final float field_217588_H;

   public DiggingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, BlockState state) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.sourceState = state;
      this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
      this.particleGravity = 1.0F;
      this.particleRed = 0.6F;
      this.particleGreen = 0.6F;
      this.particleBlue = 0.6F;
      this.particleScale /= 2.0F;
      this.field_217587_G = this.rand.nextFloat() * 3.0F;
      this.field_217588_H = this.rand.nextFloat() * 3.0F;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   /**
    * Sets the position of the block that this particle came from. Used for calculating texture and color multiplier.
    */
   public DiggingParticle setBlockPos(BlockPos pos) {
      updateSprite(pos);
      this.sourcePos = pos;
      if (this.sourceState.getBlock() == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(pos);
         return this;
      }
   }

   public DiggingParticle init() {
      this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
      Block block = this.sourceState.getBlock();
      if (block == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(this.sourcePos);
         return this;
      }
   }

   protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
      int i = Minecraft.getInstance().getBlockColors().getColor(this.sourceState, this.world, p_187154_1_, 0);
      this.particleRed *= (float)(i >> 16 & 255) / 255.0F;
      this.particleGreen *= (float)(i >> 8 & 255) / 255.0F;
      this.particleBlue *= (float)(i & 255) / 255.0F;
   }

   protected float getMinU() {
      return this.sprite.getInterpolatedU((double)((this.field_217587_G + 1.0F) / 4.0F * 16.0F));
   }

   protected float getMaxU() {
      return this.sprite.getInterpolatedU((double)(this.field_217587_G / 4.0F * 16.0F));
   }

   protected float getMinV() {
      return this.sprite.getInterpolatedV((double)(this.field_217588_H / 4.0F * 16.0F));
   }

   protected float getMaxV() {
      return this.sprite.getInterpolatedV((double)((this.field_217588_H + 1.0F) / 4.0F * 16.0F));
   }

   public int getBrightnessForRender(float partialTick) {
      int i = super.getBrightnessForRender(partialTick);
      int j = 0;
      if (this.world.isBlockLoaded(this.sourcePos)) {
         j = WorldRenderer.getCombinedLight(this.world, this.sourcePos);
      }

      return i == 0 ? j : i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      public Particle makeParticle(BlockParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         BlockState blockstate = typeIn.getBlockState();
         return !blockstate.isAir() && blockstate.getBlock() != Blocks.MOVING_PISTON ? (new DiggingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, blockstate)).init().updateSprite(typeIn.getPos()) : null;
      }
   }

   private Particle updateSprite(BlockPos pos) { //FORGE: we cannot assume that the x y z of the particles match the block pos of the block.
      if (pos != null) // There are cases where we are not able to obtain the correct source pos, and need to fallback to the non-model data version
         this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(sourceState, world, pos));
      return this;
   }
}