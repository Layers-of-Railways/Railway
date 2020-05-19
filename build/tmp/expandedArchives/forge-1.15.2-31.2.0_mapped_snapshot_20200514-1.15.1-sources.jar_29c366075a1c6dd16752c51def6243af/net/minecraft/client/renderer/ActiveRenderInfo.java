package net.minecraft.client.renderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private boolean valid;
   private IBlockReader world;
   private Entity renderViewEntity;
   private Vec3d pos = Vec3d.ZERO;
   private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
   private final Vector3f look = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
   private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
   private float pitch;
   private float yaw;
   private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private boolean thirdPerson;
   private boolean thirdPersonReverse;
   private float height;
   private float previousHeight;

   public void update(IBlockReader worldIn, Entity renderViewEntity, boolean thirdPersonIn, boolean thirdPersonReverseIn, float partialTicks) {
      this.valid = true;
      this.world = worldIn;
      this.renderViewEntity = renderViewEntity;
      this.thirdPerson = thirdPersonIn;
      this.thirdPersonReverse = thirdPersonReverseIn;
      this.setDirection(renderViewEntity.getYaw(partialTicks), renderViewEntity.getPitch(partialTicks));
      this.setPosition(MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosX, renderViewEntity.getPosX()), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosY, renderViewEntity.getPosY()) + (double)MathHelper.lerp(partialTicks, this.previousHeight, this.height), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosZ, renderViewEntity.getPosZ()));
      if (thirdPersonIn) {
         if (thirdPersonReverseIn) {
            this.setDirection(this.yaw + 180.0F, -this.pitch);
         }

         this.movePosition(-this.calcCameraDistance(4.0D), 0.0D, 0.0D);
      } else if (renderViewEntity instanceof LivingEntity && ((LivingEntity)renderViewEntity).isSleeping()) {
         Direction direction = ((LivingEntity)renderViewEntity).getBedDirection();
         this.setDirection(direction != null ? direction.getHorizontalAngle() - 180.0F : 0.0F, 0.0F);
         this.movePosition(0.0D, 0.3D, 0.0D);
      }

   }

   public void interpolateHeight() {
      if (this.renderViewEntity != null) {
         this.previousHeight = this.height;
         this.height += (this.renderViewEntity.getEyeHeight() - this.height) * 0.5F;
      }

   }

   /**
    * Checks for collision of the third person camera and returns the distance
    */
   private double calcCameraDistance(double startingDistance) {
      for(int i = 0; i < 8; ++i) {
         float f = (float)((i & 1) * 2 - 1);
         float f1 = (float)((i >> 1 & 1) * 2 - 1);
         float f2 = (float)((i >> 2 & 1) * 2 - 1);
         f = f * 0.1F;
         f1 = f1 * 0.1F;
         f2 = f2 * 0.1F;
         Vec3d vec3d = this.pos.add((double)f, (double)f1, (double)f2);
         Vec3d vec3d1 = new Vec3d(this.pos.x - (double)this.look.getX() * startingDistance + (double)f + (double)f2, this.pos.y - (double)this.look.getY() * startingDistance + (double)f1, this.pos.z - (double)this.look.getZ() * startingDistance + (double)f2);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.renderViewEntity));
         if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            double d0 = raytraceresult.getHitVec().distanceTo(this.pos);
            if (d0 < startingDistance) {
               startingDistance = d0;
            }
         }
      }

      return startingDistance;
   }

   /**
    * Moves the render position relative to the view direction, for third person camera
    */
   protected void movePosition(double distanceOffset, double verticalOffset, double horizontalOffset) {
      double d0 = (double)this.look.getX() * distanceOffset + (double)this.up.getX() * verticalOffset + (double)this.left.getX() * horizontalOffset;
      double d1 = (double)this.look.getY() * distanceOffset + (double)this.up.getY() * verticalOffset + (double)this.left.getY() * horizontalOffset;
      double d2 = (double)this.look.getZ() * distanceOffset + (double)this.up.getZ() * verticalOffset + (double)this.left.getZ() * horizontalOffset;
      this.setPostion(new Vec3d(this.pos.x + d0, this.pos.y + d1, this.pos.z + d2));
   }

   protected void setDirection(float pitchIn, float yawIn) {
      this.pitch = yawIn;
      this.yaw = pitchIn;
      this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
      this.rotation.multiply(Vector3f.YP.rotationDegrees(-pitchIn));
      this.rotation.multiply(Vector3f.XP.rotationDegrees(yawIn));
      this.look.set(0.0F, 0.0F, 1.0F);
      this.look.transform(this.rotation);
      this.up.set(0.0F, 1.0F, 0.0F);
      this.up.transform(this.rotation);
      this.left.set(1.0F, 0.0F, 0.0F);
      this.left.transform(this.rotation);
   }

   /**
    * Sets the position and blockpos of the active render
    */
   protected void setPosition(double x, double y, double z) {
      this.setPostion(new Vec3d(x, y, z));
   }

   /**
    * Sets the position and blockpos of the active render
    */
   protected void setPostion(Vec3d posIn) {
      this.pos = posIn;
      this.blockPos.setPos(posIn.x, posIn.y, posIn.z);
   }

   public Vec3d getProjectedView() {
      return this.pos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public Quaternion getRotation() {
      return this.rotation;
   }

   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public boolean isValid() {
      return this.valid;
   }

   public boolean isThirdPerson() {
      return this.thirdPerson;
   }

   public IFluidState getFluidState() {
      if (!this.valid) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         IFluidState ifluidstate = this.world.getFluidState(this.blockPos);
         return !ifluidstate.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + ifluidstate.getActualHeight(this.world, this.blockPos)) ? Fluids.EMPTY.getDefaultState() : ifluidstate;
      }
   }

   public final Vector3f getViewVector() {
      return this.look;
   }

   public final Vector3f getUpVector() {
      return this.up;
   }

   public void clear() {
      this.world = null;
      this.renderViewEntity = null;
      this.valid = false;
   }

   public void setAnglesInternal(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public net.minecraft.block.BlockState getBlockAtCamera() {
      if (!this.valid)
         return net.minecraft.block.Blocks.AIR.getDefaultState();
      else
         return this.world.getBlockState(this.blockPos).getStateAtViewpoint(this.world, this.blockPos, this.pos);
   }
}