package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected M entityModel;
   protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();

   public LivingRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
      super(rendererManager);
      this.entityModel = entityModelIn;
      this.shadowSize = shadowSizeIn;
   }

   public final boolean addLayer(LayerRenderer<T, M> layer) {
      return this.layerRenderers.add(layer);
   }

   public M getEntityModel() {
      return this.entityModel;
   }

   public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<T, M>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))) return;
      matrixStackIn.push();
      this.entityModel.swingProgress = this.getSwingProgress(entityIn, partialTicks);

      boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
      this.entityModel.isSitting = shouldSit;
      this.entityModel.isChild = entityIn.isChild();
      float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
      float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
      float f2 = f1 - f;
      if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
         f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
         f2 = f1 - f;
         float f3 = MathHelper.wrapDegrees(f2);
         if (f3 < -85.0F) {
            f3 = -85.0F;
         }

         if (f3 >= 85.0F) {
            f3 = 85.0F;
         }

         f = f1 - f3;
         if (f3 * f3 > 2500.0F) {
            f += f3 * 0.2F;
         }

         f2 = f1 - f;
      }

      float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
      if (entityIn.getPose() == Pose.SLEEPING) {
         Direction direction = entityIn.getBedDirection();
         if (direction != null) {
            float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
            matrixStackIn.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
         }
      }

      float f7 = this.handleRotationFloat(entityIn, partialTicks);
      this.applyRotations(entityIn, matrixStackIn, f7, f, partialTicks);
      matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
      this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
      matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
      float f8 = 0.0F;
      float f5 = 0.0F;
      if (!shouldSit && entityIn.isAlive()) {
         f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
         f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
         if (entityIn.isChild()) {
            f5 *= 3.0F;
         }

         if (f8 > 1.0F) {
            f8 = 1.0F;
         }
      }

      this.entityModel.setLivingAnimations(entityIn, f5, f8, partialTicks);
      this.entityModel.setRotationAngles(entityIn, f5, f8, f7, f2, f6);
      boolean flag = this.isVisible(entityIn);
      boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
      RenderType rendertype = this.func_230042_a_(entityIn, flag, flag1);
      if (rendertype != null) {
         IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
         int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
         this.entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
      }

      if (!entityIn.isSpectator()) {
         for(LayerRenderer<T, M> layerrenderer : this.layerRenderers) {
            layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
         }
      }

      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<T, M>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
   }

   @Nullable
   protected RenderType func_230042_a_(T p_230042_1_, boolean p_230042_2_, boolean p_230042_3_) {
      ResourceLocation resourcelocation = this.getEntityTexture(p_230042_1_);
      if (p_230042_3_) {
         return RenderType.getEntityTranslucent(resourcelocation);
      } else if (p_230042_2_) {
         return this.entityModel.getRenderType(resourcelocation);
      } else {
         return p_230042_1_.isGlowing() ? RenderType.getOutline(resourcelocation) : null;
      }
   }

   public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
      return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
   }

   protected boolean isVisible(T livingEntityIn) {
      return !livingEntityIn.isInvisible();
   }

   private static float getFacingAngle(Direction facingIn) {
      switch(facingIn) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      Pose pose = entityLiving.getPose();
      if (pose != Pose.SLEEPING) {
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
      }

      if (entityLiving.deathTime > 0) {
         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
      } else if (entityLiving.isSpinAttacking()) {
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.rotationPitch));
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float)entityLiving.ticksExisted + partialTicks) * -75.0F));
      } else if (pose == Pose.SLEEPING) {
         Direction direction = entityLiving.getBedDirection();
         float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0F));
      } else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
         String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
         if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity)entityLiving).isWearing(PlayerModelPart.CAPE))) {
            matrixStackIn.translate(0.0D, (double)(entityLiving.getHeight() + 0.1F), 0.0D);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
         }
      }

   }

   /**
    * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
    */
   protected float getSwingProgress(T livingBase, float partialTickTime) {
      return livingBase.getSwingProgress(partialTickTime);
   }

   /**
    * Defines what float the third param in setRotationAngles of ModelBase is
    */
   protected float handleRotationFloat(T livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   protected float getOverlayProgress(T livingEntityIn, float partialTicks) {
      return 0.0F;
   }

   protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
   }

   protected boolean canRenderName(T entity) {
      double d0 = this.renderManager.squareDistanceTo(entity);
      float f = entity.isDiscrete() ? 32.0F : 64.0F;
      if (d0 >= (double)(f * f)) {
         return false;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         ClientPlayerEntity clientplayerentity = minecraft.player;
         boolean flag = !entity.isInvisibleToPlayer(clientplayerentity);
         if (entity != clientplayerentity) {
            Team team = entity.getTeam();
            Team team1 = clientplayerentity.getTeam();
            if (team != null) {
               Team.Visible team$visible = team.getNameTagVisibility();
               switch(team$visible) {
               case ALWAYS:
                  return flag;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
               case HIDE_FOR_OWN_TEAM:
                  return team1 == null ? flag : !team.isSameTeam(team1) && flag;
               default:
                  return true;
               }
            }
         }

         return Minecraft.isGuiEnabled() && entity != minecraft.getRenderViewEntity() && flag && !entity.isBeingRidden();
      }
   }
}