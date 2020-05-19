package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRendererManager {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.getEntityNoOutline(new ResourceLocation("textures/misc/shadow.png"));
   public final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();
   /** lists the various player skin types with their associated Renderer class instances. */
   private final Map<String, PlayerRenderer> skinMap = Maps.newHashMap();
   private final PlayerRenderer playerRenderer;
   private final FontRenderer textRenderer;
   public final TextureManager textureManager;
   private World world;
   public ActiveRenderInfo info;
   private Quaternion cameraOrientation;
   public Entity pointedEntity;
   public final GameSettings options;
   private boolean renderShadow = true;
   private boolean debugBoundingBox;

   public <E extends Entity> int getPackedLight(E entityIn, float partialTicks) {
      return this.getRenderer(entityIn).getPackedLight(entityIn, partialTicks);
   }

   public <T extends Entity> void register(EntityType<T> entityTypeIn, EntityRenderer<? super T> entityRendererIn) {
      this.renderers.put(entityTypeIn, entityRendererIn);
   }

   private void registerRenderers(net.minecraft.client.renderer.ItemRenderer itemRendererIn, IReloadableResourceManager resourceManagerIn) {
      this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudRenderer(this));
      this.register(EntityType.ARMOR_STAND, new ArmorStandRenderer(this));
      this.register(EntityType.ARROW, new TippedArrowRenderer(this));
      this.register(EntityType.BAT, new BatRenderer(this));
      this.register(EntityType.BEE, new BeeRenderer(this));
      this.register(EntityType.BLAZE, new BlazeRenderer(this));
      this.register(EntityType.BOAT, new BoatRenderer(this));
      this.register(EntityType.CAT, new CatRenderer(this));
      this.register(EntityType.CAVE_SPIDER, new CaveSpiderRenderer(this));
      this.register(EntityType.CHEST_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.CHICKEN, new ChickenRenderer(this));
      this.register(EntityType.COD, new CodRenderer(this));
      this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.COW, new CowRenderer(this));
      this.register(EntityType.CREEPER, new CreeperRenderer(this));
      this.register(EntityType.DOLPHIN, new DolphinRenderer(this));
      this.register(EntityType.DONKEY, new ChestedHorseRenderer<>(this, 0.87F));
      this.register(EntityType.DRAGON_FIREBALL, new DragonFireballRenderer(this));
      this.register(EntityType.DROWNED, new DrownedRenderer(this));
      this.register(EntityType.EGG, new SpriteRenderer<>(this, itemRendererIn));
      this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianRenderer(this));
      this.register(EntityType.END_CRYSTAL, new EnderCrystalRenderer(this));
      this.register(EntityType.ENDER_DRAGON, new EnderDragonRenderer(this));
      this.register(EntityType.ENDERMAN, new EndermanRenderer(this));
      this.register(EntityType.ENDERMITE, new EndermiteRenderer(this));
      this.register(EntityType.ENDER_PEARL, new SpriteRenderer<>(this, itemRendererIn));
      this.register(EntityType.EVOKER_FANGS, new EvokerFangsRenderer(this));
      this.register(EntityType.EVOKER, new EvokerRenderer<>(this));
      this.register(EntityType.EXPERIENCE_BOTTLE, new SpriteRenderer<>(this, itemRendererIn));
      this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbRenderer(this));
      this.register(EntityType.EYE_OF_ENDER, new SpriteRenderer<>(this, itemRendererIn, 1.0F, true));
      this.register(EntityType.FALLING_BLOCK, new FallingBlockRenderer(this));
      this.register(EntityType.FIREBALL, new SpriteRenderer<>(this, itemRendererIn, 3.0F, true));
      this.register(EntityType.FIREWORK_ROCKET, new FireworkRocketRenderer(this, itemRendererIn));
      this.register(EntityType.FISHING_BOBBER, new FishRenderer(this));
      this.register(EntityType.FOX, new FoxRenderer(this));
      this.register(EntityType.FURNACE_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.GHAST, new GhastRenderer(this));
      this.register(EntityType.GIANT, new GiantZombieRenderer(this, 6.0F));
      this.register(EntityType.GUARDIAN, new GuardianRenderer(this));
      this.register(EntityType.HOPPER_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.HORSE, new HorseRenderer(this));
      this.register(EntityType.HUSK, new HuskRenderer(this));
      this.register(EntityType.ILLUSIONER, new IllusionerRenderer(this));
      this.register(EntityType.IRON_GOLEM, new IronGolemRenderer(this));
      this.register(EntityType.ITEM_FRAME, new ItemFrameRenderer(this, itemRendererIn));
      this.register(EntityType.ITEM, new ItemRenderer(this, itemRendererIn));
      this.register(EntityType.LEASH_KNOT, new LeashKnotRenderer(this));
      this.register(EntityType.LIGHTNING_BOLT, new LightningBoltRenderer(this));
      this.register(EntityType.LLAMA, new LlamaRenderer(this));
      this.register(EntityType.LLAMA_SPIT, new LlamaSpitRenderer(this));
      this.register(EntityType.MAGMA_CUBE, new MagmaCubeRenderer(this));
      this.register(EntityType.MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.MOOSHROOM, new MooshroomRenderer(this));
      this.register(EntityType.MULE, new ChestedHorseRenderer<>(this, 0.92F));
      this.register(EntityType.OCELOT, new OcelotRenderer(this));
      this.register(EntityType.PAINTING, new PaintingRenderer(this));
      this.register(EntityType.PANDA, new PandaRenderer(this));
      this.register(EntityType.PARROT, new ParrotRenderer(this));
      this.register(EntityType.PHANTOM, new PhantomRenderer(this));
      this.register(EntityType.PIG, new PigRenderer(this));
      this.register(EntityType.PILLAGER, new PillagerRenderer(this));
      this.register(EntityType.POLAR_BEAR, new PolarBearRenderer(this));
      this.register(EntityType.POTION, new SpriteRenderer<>(this, itemRendererIn));
      this.register(EntityType.PUFFERFISH, new PufferfishRenderer(this));
      this.register(EntityType.RABBIT, new RabbitRenderer(this));
      this.register(EntityType.RAVAGER, new RavagerRenderer(this));
      this.register(EntityType.SALMON, new SalmonRenderer(this));
      this.register(EntityType.SHEEP, new SheepRenderer(this));
      this.register(EntityType.SHULKER_BULLET, new ShulkerBulletRenderer(this));
      this.register(EntityType.SHULKER, new ShulkerRenderer(this));
      this.register(EntityType.SILVERFISH, new SilverfishRenderer(this));
      this.register(EntityType.SKELETON_HORSE, new UndeadHorseRenderer(this));
      this.register(EntityType.SKELETON, new SkeletonRenderer(this));
      this.register(EntityType.SLIME, new SlimeRenderer(this));
      this.register(EntityType.SMALL_FIREBALL, new SpriteRenderer<>(this, itemRendererIn, 0.75F, true));
      this.register(EntityType.SNOWBALL, new SpriteRenderer<>(this, itemRendererIn));
      this.register(EntityType.SNOW_GOLEM, new SnowManRenderer(this));
      this.register(EntityType.SPAWNER_MINECART, new MinecartRenderer<>(this));
      this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowRenderer(this));
      this.register(EntityType.SPIDER, new SpiderRenderer<>(this));
      this.register(EntityType.SQUID, new SquidRenderer(this));
      this.register(EntityType.STRAY, new StrayRenderer(this));
      this.register(EntityType.TNT_MINECART, new TNTMinecartRenderer(this));
      this.register(EntityType.TNT, new TNTRenderer(this));
      this.register(EntityType.TRADER_LLAMA, new LlamaRenderer(this));
      this.register(EntityType.TRIDENT, new TridentRenderer(this));
      this.register(EntityType.TROPICAL_FISH, new TropicalFishRenderer(this));
      this.register(EntityType.TURTLE, new TurtleRenderer(this));
      this.register(EntityType.VEX, new VexRenderer(this));
      this.register(EntityType.VILLAGER, new VillagerRenderer(this, resourceManagerIn));
      this.register(EntityType.VINDICATOR, new VindicatorRenderer(this));
      this.register(EntityType.WANDERING_TRADER, new WanderingTraderRenderer(this));
      this.register(EntityType.WITCH, new WitchRenderer(this));
      this.register(EntityType.WITHER, new WitherRenderer(this));
      this.register(EntityType.WITHER_SKELETON, new WitherSkeletonRenderer(this));
      this.register(EntityType.WITHER_SKULL, new WitherSkullRenderer(this));
      this.register(EntityType.WOLF, new WolfRenderer(this));
      this.register(EntityType.ZOMBIE_HORSE, new UndeadHorseRenderer(this));
      this.register(EntityType.ZOMBIE, new ZombieRenderer(this));
      this.register(EntityType.ZOMBIE_PIGMAN, new PigZombieRenderer(this));
      this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer(this, resourceManagerIn));
   }

   public EntityRendererManager(TextureManager textureManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn, IReloadableResourceManager resourceManagerIn, FontRenderer fontRendererIn, GameSettings gameSettingsIn) {
      this.textureManager = textureManagerIn;
      this.textRenderer = fontRendererIn;
      this.options = gameSettingsIn;
      this.registerRenderers(itemRendererIn, resourceManagerIn);
      this.playerRenderer = new PlayerRenderer(this);
      this.skinMap.put("default", this.playerRenderer);
      this.skinMap.put("slim", new PlayerRenderer(this, true));
   }

   //FORGE: Validate the existence of a renderer for each entity type after modded entity renderers have been added
   public void validateRendererExistence() {
      for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
         if (entitytype != EntityType.PLAYER && !this.renderers.containsKey(entitytype)) {
            throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getKey(entitytype));
         }
      }
   }

   public <T extends Entity> EntityRenderer<? super T> getRenderer(T entityIn) {
      if (entityIn instanceof AbstractClientPlayerEntity) {
         String s = ((AbstractClientPlayerEntity)entityIn).getSkinType();
         PlayerRenderer playerrenderer = this.skinMap.get(s);
         return (EntityRenderer<? super T>)(playerrenderer != null ? playerrenderer : this.playerRenderer);
      } else {
         return (EntityRenderer<? super T>)this.renderers.get(entityIn.getType());
      }
   }

   public void cacheActiveRenderInfo(World worldIn, ActiveRenderInfo activeRenderInfoIn, Entity entityIn) {
      this.world = worldIn;
      this.info = activeRenderInfoIn;
      this.cameraOrientation = activeRenderInfoIn.getRotation();
      this.pointedEntity = entityIn;
   }

   public void setCameraOrientation(Quaternion quaternionIn) {
      this.cameraOrientation = quaternionIn;
   }

   public void setRenderShadow(boolean renderShadowIn) {
      this.renderShadow = renderShadowIn;
   }

   public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
      this.debugBoundingBox = debugBoundingBoxIn;
   }

   public boolean isDebugBoundingBox() {
      return this.debugBoundingBox;
   }

   public <E extends Entity> boolean shouldRender(E entityIn, ClippingHelperImpl frustumIn, double camX, double camY, double camZ) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(entityIn);
      return entityrenderer.shouldRender(entityIn, frustumIn, camX, camY, camZ);
   }

   public <E extends Entity> void renderEntityStatic(E entityIn, double xIn, double yIn, double zIn, float rotationYawIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(entityIn);

      try {
         Vec3d vec3d = entityrenderer.getRenderOffset(entityIn, partialTicks);
         double d2 = xIn + vec3d.getX();
         double d3 = yIn + vec3d.getY();
         double d0 = zIn + vec3d.getZ();
         matrixStackIn.push();
         matrixStackIn.translate(d2, d3, d0);
         entityrenderer.render(entityIn, rotationYawIn, partialTicks, matrixStackIn, bufferIn, packedLightIn);
         if (entityIn.canRenderOnFire()) {
            this.renderFire(matrixStackIn, bufferIn, entityIn);
         }

         matrixStackIn.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
         if (this.options.entityShadows && this.renderShadow && entityrenderer.shadowSize > 0.0F && !entityIn.isInvisible()) {
            double d1 = this.getDistanceToCamera(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
            float f = (float)((1.0D - d1 / 256.0D) * (double)entityrenderer.shadowOpaque);
            if (f > 0.0F) {
               renderShadow(matrixStackIn, bufferIn, entityIn, f, partialTicks, this.world, entityrenderer.shadowSize);
            }
         }

         if (this.debugBoundingBox && !entityIn.isInvisible() && !Minecraft.getInstance().isReducedDebug()) {
            this.renderDebugBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), entityIn, partialTicks);
         }

         matrixStackIn.pop();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
         entityIn.fillCrashReport(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
         crashreportcategory1.addDetail("Assigned renderer", entityrenderer);
         crashreportcategory1.addDetail("Location", CrashReportCategory.getCoordinateInfo(xIn, yIn, zIn));
         crashreportcategory1.addDetail("Rotation", rotationYawIn);
         crashreportcategory1.addDetail("Delta", partialTicks);
         throw new ReportedException(crashreport);
      }
   }

   private void renderDebugBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float partialTicks) {
      float f = entityIn.getWidth() / 2.0F;
      this.renderBoundingBox(matrixStackIn, bufferIn, entityIn, 1.0F, 1.0F, 1.0F);
      if (entityIn instanceof EnderDragonEntity) {
         double d0 = entityIn.getPosX() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
         double d1 = entityIn.getPosY() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
         double d2 = entityIn.getPosZ() - MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());

         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts()) {
            matrixStackIn.push();
            double d3 = d0 + MathHelper.lerp((double)partialTicks, enderdragonpartentity.lastTickPosX, enderdragonpartentity.getPosX());
            double d4 = d1 + MathHelper.lerp((double)partialTicks, enderdragonpartentity.lastTickPosY, enderdragonpartentity.getPosY());
            double d5 = d2 + MathHelper.lerp((double)partialTicks, enderdragonpartentity.lastTickPosZ, enderdragonpartentity.getPosZ());
            matrixStackIn.translate(d3, d4, d5);
            this.renderBoundingBox(matrixStackIn, bufferIn, enderdragonpartentity, 0.25F, 1.0F, 0.0F);
            matrixStackIn.pop();
         }
      }

      if (entityIn instanceof LivingEntity) {
         float f1 = 0.01F;
         WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, (double)(-f), (double)(entityIn.getEyeHeight() - 0.01F), (double)(-f), (double)f, (double)(entityIn.getEyeHeight() + 0.01F), (double)f, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Vec3d vec3d = entityIn.getLook(partialTicks);
      Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
      bufferIn.pos(matrix4f, 0.0F, entityIn.getEyeHeight(), 0.0F).color(0, 0, 255, 255).endVertex();
      bufferIn.pos(matrix4f, (float)(vec3d.x * 2.0D), (float)((double)entityIn.getEyeHeight() + vec3d.y * 2.0D), (float)(vec3d.z * 2.0D)).color(0, 0, 255, 255).endVertex();
   }

   private void renderBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float red, float green, float blue) {
      AxisAlignedBB axisalignedbb = entityIn.getBoundingBox().offset(-entityIn.getPosX(), -entityIn.getPosY(), -entityIn.getPosZ());
      WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, axisalignedbb, red, green, blue, 1.0F);
   }

   private void renderFire(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Entity entityIn) {
      TextureAtlasSprite textureatlassprite = ModelBakery.LOCATION_FIRE_0.getSprite();
      TextureAtlasSprite textureatlassprite1 = ModelBakery.LOCATION_FIRE_1.getSprite();
      matrixStackIn.push();
      float f = entityIn.getWidth() * 1.4F;
      matrixStackIn.scale(f, f, f);
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = entityIn.getHeight() / f;
      float f4 = 0.0F;
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-this.info.getYaw()));
      matrixStackIn.translate(0.0D, 0.0D, (double)(-0.3F + (float)((int)f3) * 0.02F));
      float f5 = 0.0F;
      int i = 0;
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(Atlases.getCutoutBlockType());

      for(MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast(); f3 > 0.0F; ++i) {
         TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
         float f6 = textureatlassprite2.getMinU();
         float f7 = textureatlassprite2.getMinV();
         float f8 = textureatlassprite2.getMaxU();
         float f9 = textureatlassprite2.getMaxV();
         if (i / 2 % 2 == 0) {
            float f10 = f8;
            f8 = f6;
            f6 = f10;
         }

         fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
         fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
         fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
         fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
         f3 -= 0.45F;
         f4 -= 0.45F;
         f1 *= 0.9F;
         f5 += 0.03F;
      }

      matrixStackIn.pop();
   }

   private static void fireVertex(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, float x, float y, float z, float texU, float texV) {
      bufferIn.pos(matrixEntryIn.getMatrix(), x, y, z).color(255, 255, 255, 255).tex(texU, texV).overlay(0, 10).lightmap(240).normal(matrixEntryIn.getNormal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void renderShadow(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Entity entityIn, float weightIn, float partialTicks, IWorldReader worldIn, float sizeIn) {
      float f = sizeIn;
      if (entityIn instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)entityIn;
         if (mobentity.isChild()) {
            f = sizeIn * 0.5F;
         }
      }

      double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
      double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
      double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
      int i = MathHelper.floor(d2 - (double)f);
      int j = MathHelper.floor(d2 + (double)f);
      int k = MathHelper.floor(d0 - (double)f);
      int l = MathHelper.floor(d0);
      int i1 = MathHelper.floor(d1 - (double)f);
      int j1 = MathHelper.floor(d1 + (double)f);
      MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(SHADOW_RENDER_TYPE);

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
         renderBlockShadow(matrixstack$entry, ivertexbuilder, worldIn, blockpos, d2, d0, d1, f, weightIn);
      }

   }

   private static void renderBlockShadow(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, IWorldReader worldIn, BlockPos blockPosIn, double xIn, double yIn, double zIn, float sizeIn, float weightIn) {
      BlockPos blockpos = blockPosIn.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && worldIn.getLight(blockPosIn) > 3) {
         if (blockstate.isCollisionShapeOpaque(worldIn, blockpos)) {
            VoxelShape voxelshape = blockstate.getShape(worldIn, blockPosIn.down());
            if (!voxelshape.isEmpty()) {
               float f = (float)(((double)weightIn - (yIn - (double)blockPosIn.getY()) / 2.0D) * 0.5D * (double)worldIn.getBrightness(blockPosIn));
               if (f >= 0.0F) {
                  if (f > 1.0F) {
                     f = 1.0F;
                  }

                  AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();
                  double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                  double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                  double d2 = (double)blockPosIn.getY() + axisalignedbb.minY;
                  double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                  double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                  float f1 = (float)(d0 - xIn);
                  float f2 = (float)(d1 - xIn);
                  float f3 = (float)(d2 - yIn + 0.015625D);
                  float f4 = (float)(d3 - zIn);
                  float f5 = (float)(d4 - zIn);
                  float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                  float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                  float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                  float f9 = -f5 / 2.0F / sizeIn + 0.5F;
                  shadowVertex(matrixEntryIn, bufferIn, f, f1, f3, f4, f6, f8);
                  shadowVertex(matrixEntryIn, bufferIn, f, f1, f3, f5, f6, f9);
                  shadowVertex(matrixEntryIn, bufferIn, f, f2, f3, f5, f7, f9);
                  shadowVertex(matrixEntryIn, bufferIn, f, f2, f3, f4, f7, f8);
               }

            }
         }
      }
   }

   private static void shadowVertex(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, float alphaIn, float xIn, float yIn, float zIn, float texU, float texV) {
      bufferIn.pos(matrixEntryIn.getMatrix(), xIn, yIn, zIn).color(1.0F, 1.0F, 1.0F, alphaIn).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(matrixEntryIn.getNormal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   /**
    * World sets this RenderManager's worldObj to the world provided
    */
   public void setWorld(@Nullable World worldIn) {
      this.world = worldIn;
      if (worldIn == null) {
         this.info = null;
      }

   }

   public double squareDistanceTo(Entity entityIn) {
      return this.info.getProjectedView().squareDistanceTo(entityIn.getPositionVec());
   }

   public double getDistanceToCamera(double x, double y, double z) {
      return this.info.getProjectedView().squareDistanceTo(x, y, z);
   }

   public Quaternion getCameraOrientation() {
      return this.cameraOrientation;
   }

   /**
    * Returns the font renderer
    */
   public FontRenderer getFontRenderer() {
      return this.textRenderer;
   }

   public Map<String, PlayerRenderer> getSkinMap() {
      return (Map<String, PlayerRenderer>) java.util.Collections.unmodifiableMap(skinMap);
   }
}