package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleManager implements IFutureReloadListener {
   private static final List<IParticleRenderType> TYPES = ImmutableList.of(IParticleRenderType.TERRAIN_SHEET, IParticleRenderType.PARTICLE_SHEET_OPAQUE, IParticleRenderType.PARTICLE_SHEET_LIT, IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, IParticleRenderType.CUSTOM);
   protected World world;
   private final Map<IParticleRenderType, Queue<Particle>> byType = Maps.newIdentityHashMap();
   private final Queue<EmitterParticle> particleEmitters = Queues.newArrayDeque();
   private final TextureManager renderer;
   private final Random rand = new Random();
   private final Map<ResourceLocation, IParticleFactory<?>> factories = new java.util.HashMap<>();
   private final Queue<Particle> queue = Queues.newArrayDeque();
   private final Map<ResourceLocation, ParticleManager.AnimatedSpriteImpl> sprites = Maps.newHashMap();
   private final AtlasTexture atlas = new AtlasTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);

   public ParticleManager(World worldIn, TextureManager rendererIn) {
      rendererIn.loadTexture(this.atlas.getTextureLocation(), this.atlas);
      this.world = worldIn;
      this.renderer = rendererIn;
      this.registerFactories();
   }

   private void registerFactories() {
      this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobFactory::new);
      this.registerFactory(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerFactory::new);
      this.registerFactory(ParticleTypes.BARRIER, new BarrierParticle.Factory());
      this.registerFactory(ParticleTypes.BLOCK, new DiggingParticle.Factory());
      this.registerFactory(ParticleTypes.BUBBLE, BubbleParticle.Factory::new);
      this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
      this.registerFactory(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
      this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireParticle.CozySmokeFactory::new);
      this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireParticle.SignalSmokeFactory::new);
      this.registerFactory(ParticleTypes.CLOUD, CloudParticle.Factory::new);
      this.registerFactory(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFactory::new);
      this.registerFactory(ParticleTypes.CRIT, CritParticle.Factory::new);
      this.registerFactory(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
      this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorFactory::new);
      this.registerFactory(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
      this.registerFactory(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedFactory::new);
      this.registerFactory(ParticleTypes.DRIPPING_LAVA, DripParticle.DrippingLavaFactory::new);
      this.registerFactory(ParticleTypes.FALLING_LAVA, DripParticle.FallingLavaFactory::new);
      this.registerFactory(ParticleTypes.LANDING_LAVA, DripParticle.LandingLavaFactory::new);
      this.registerFactory(ParticleTypes.DRIPPING_WATER, DripParticle.DrippingWaterFactory::new);
      this.registerFactory(ParticleTypes.FALLING_WATER, DripParticle.FallingWaterFactory::new);
      this.registerFactory(ParticleTypes.DUST, RedstoneParticle.Factory::new);
      this.registerFactory(ParticleTypes.EFFECT, SpellParticle.Factory::new);
      this.registerFactory(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Factory());
      this.registerFactory(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicFactory::new);
      this.registerFactory(ParticleTypes.ENCHANT, EnchantmentTableParticle.EnchantmentTable::new);
      this.registerFactory(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
      this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobFactory::new);
      this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionParticle.Factory());
      this.registerFactory(ParticleTypes.EXPLOSION, LargeExplosionParticle.Factory::new);
      this.registerFactory(ParticleTypes.FALLING_DUST, FallingDustParticle.Factory::new);
      this.registerFactory(ParticleTypes.FIREWORK, FireworkParticle.SparkFactory::new);
      this.registerFactory(ParticleTypes.FISHING, WaterWakeParticle.Factory::new);
      this.registerFactory(ParticleTypes.FLAME, FlameParticle.Factory::new);
      this.registerFactory(ParticleTypes.FLASH, FireworkParticle.OverlayFactory::new);
      this.registerFactory(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerFactory::new);
      this.registerFactory(ParticleTypes.HEART, HeartParticle.Factory::new);
      this.registerFactory(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
      this.registerFactory(ParticleTypes.ITEM, new BreakingParticle.Factory());
      this.registerFactory(ParticleTypes.ITEM_SLIME, new BreakingParticle.SlimeFactory());
      this.registerFactory(ParticleTypes.ITEM_SNOWBALL, new BreakingParticle.SnowballFactory());
      this.registerFactory(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Factory::new);
      this.registerFactory(ParticleTypes.LAVA, LavaParticle.Factory::new);
      this.registerFactory(ParticleTypes.MYCELIUM, SuspendedTownParticle.Factory::new);
      this.registerFactory(ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusFactory::new);
      this.registerFactory(ParticleTypes.NOTE, NoteParticle.Factory::new);
      this.registerFactory(ParticleTypes.POOF, PoofParticle.Factory::new);
      this.registerFactory(ParticleTypes.PORTAL, PortalParticle.Factory::new);
      this.registerFactory(ParticleTypes.RAIN, RainParticle.Factory::new);
      this.registerFactory(ParticleTypes.SMOKE, SmokeParticle.Factory::new);
      this.registerFactory(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
      this.registerFactory(ParticleTypes.SPIT, SpitParticle.Factory::new);
      this.registerFactory(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
      this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, TotemOfUndyingParticle.Factory::new);
      this.registerFactory(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
      this.registerFactory(ParticleTypes.UNDERWATER, UnderwaterParticle.Factory::new);
      this.registerFactory(ParticleTypes.SPLASH, SplashParticle.Factory::new);
      this.registerFactory(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
      this.registerFactory(ParticleTypes.DRIPPING_HONEY, DripParticle.DrippingHoneyFactory::new);
      this.registerFactory(ParticleTypes.FALLING_HONEY, DripParticle.FallingHoneyFactory::new);
      this.registerFactory(ParticleTypes.LANDING_HONEY, DripParticle.LandingHoneyFactory::new);
      this.registerFactory(ParticleTypes.FALLING_NECTAR, DripParticle.FallingNectarFactory::new);
   }

   public <T extends IParticleData> void registerFactory(ParticleType<T> particleTypeIn, IParticleFactory<T> particleFactoryIn) {
      this.factories.put(Registry.PARTICLE_TYPE.getKey(particleTypeIn), particleFactoryIn);
   }

   public <T extends IParticleData> void registerFactory(ParticleType<T> particleTypeIn, ParticleManager.IParticleMetaFactory<T> particleMetaFactoryIn) {
      ParticleManager.AnimatedSpriteImpl particlemanager$animatedspriteimpl = new ParticleManager.AnimatedSpriteImpl();
      this.sprites.put(Registry.PARTICLE_TYPE.getKey(particleTypeIn), particlemanager$animatedspriteimpl);
      this.factories.put(Registry.PARTICLE_TYPE.getKey(particleTypeIn), particleMetaFactoryIn.create(particlemanager$animatedspriteimpl));
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      Map<ResourceLocation, List<ResourceLocation>> map = Maps.newConcurrentMap();
      CompletableFuture<?>[] completablefuture = Registry.PARTICLE_TYPE.keySet().stream().map((p_215228_4_) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadTextureLists(resourceManager, p_215228_4_, map);
         }, backgroundExecutor);
      }).toArray((p_215239_0_) -> {
         return new CompletableFuture[p_215239_0_];
      });
      return CompletableFuture.allOf(completablefuture).thenApplyAsync((p_228344_4_) -> {
         preparationsProfiler.startTick();
         preparationsProfiler.startSection("stitching");
         AtlasTexture.SheetData atlastexture$sheetdata = this.atlas.stitch(resourceManager, map.values().stream().flatMap(Collection::stream), preparationsProfiler, 0);
         preparationsProfiler.endSection();
         preparationsProfiler.endTick();
         return atlastexture$sheetdata;
      }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_215229_3_) -> {
         this.byType.clear();
         reloadProfiler.startTick();
         reloadProfiler.startSection("upload");
         this.atlas.upload(p_215229_3_);
         reloadProfiler.endStartSection("bindSpriteSets");
         TextureAtlasSprite textureatlassprite = this.atlas.getSprite(MissingTextureSprite.getLocation());
         map.forEach((p_215227_2_, p_215227_3_) -> {
            ImmutableList<TextureAtlasSprite> immutablelist = p_215227_3_.isEmpty() ? ImmutableList.of(textureatlassprite) : p_215227_3_.stream().map(this.atlas::getSprite).collect(ImmutableList.toImmutableList());
            this.sprites.get(p_215227_2_).setSprites(immutablelist);
         });
         reloadProfiler.endSection();
         reloadProfiler.endTick();
      }, gameExecutor);
   }

   public void close() {
      this.atlas.clear();
   }

   private void loadTextureLists(IResourceManager manager, ResourceLocation particleId, Map<ResourceLocation, List<ResourceLocation>> textures) {
      ResourceLocation resourcelocation = new ResourceLocation(particleId.getNamespace(), "particles/" + particleId.getPath() + ".json");

      try (
         IResource iresource = manager.getResource(resourcelocation);
         Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
      ) {
         TexturesParticle texturesparticle = TexturesParticle.deserialize(JSONUtils.fromJson(reader));
         List<ResourceLocation> list = texturesparticle.getTextures();
         boolean flag = this.sprites.containsKey(particleId);
         if (list == null) {
            if (flag) {
               throw new IllegalStateException("Missing texture list for particle " + particleId);
            }
         } else {
            if (!flag) {
               throw new IllegalStateException("Redundant texture list for particle " + particleId);
            }

            textures.put(particleId, list.stream().map((p_228349_0_) -> {
               return new ResourceLocation(p_228349_0_.getNamespace(), "particle/" + p_228349_0_.getPath());
            }).collect(Collectors.toList()));
         }

      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load description for particle " + particleId, ioexception);
      }
   }

   public void addParticleEmitter(Entity entityIn, IParticleData particleData) {
      this.particleEmitters.add(new EmitterParticle(this.world, entityIn, particleData));
   }

   public void emitParticleAtEntity(Entity entityIn, IParticleData dataIn, int lifetimeIn) {
      this.particleEmitters.add(new EmitterParticle(this.world, entityIn, dataIn, lifetimeIn));
   }

   @Nullable
   public Particle addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      Particle particle = this.makeParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
      if (particle != null) {
         this.addEffect(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends IParticleData> Particle makeParticle(T particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      IParticleFactory<T> iparticlefactory = (IParticleFactory<T>) this.factories.get(Registry.PARTICLE_TYPE.getKey(particleData.getType()));
      return iparticlefactory == null ? null : iparticlefactory.makeParticle(particleData, this.world, x, y, z, xSpeed, ySpeed, zSpeed);
   }

   public void addEffect(Particle effect) {
      if (effect == null) return; //Forge: Prevent modders from being bad and adding nulls causing untraceable NPEs.
      this.queue.add(effect);
   }

   public void tick() {
      this.byType.forEach((p_228347_1_, p_228347_2_) -> {
         this.world.getProfiler().startSection(p_228347_1_.toString());
         this.tickParticleList(p_228347_2_);
         this.world.getProfiler().endSection();
      });
      if (!this.particleEmitters.isEmpty()) {
         List<EmitterParticle> list = Lists.newArrayList();

         for(EmitterParticle emitterparticle : this.particleEmitters) {
            emitterparticle.tick();
            if (!emitterparticle.isAlive()) {
               list.add(emitterparticle);
            }
         }

         this.particleEmitters.removeAll(list);
      }

      Particle particle;
      if (!this.queue.isEmpty()) {
         while((particle = this.queue.poll()) != null) {
            this.byType.computeIfAbsent(particle.getRenderType(), (p_228346_0_) -> {
               return EvictingQueue.create(16384);
            }).add(particle);
         }
      }

   }

   private void tickParticleList(Collection<Particle> particlesIn) {
      if (!particlesIn.isEmpty()) {
         Iterator<Particle> iterator = particlesIn.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               iterator.remove();
            }
         }
      }

   }

   private void tickParticle(Particle particle) {
      try {
         particle.tick();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
         crashreportcategory.addDetail("Particle", particle::toString);
         crashreportcategory.addDetail("Particle Type", particle.getRenderType()::toString);
         throw new ReportedException(crashreport);
      }
   }

   public void renderParticles(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, LightTexture lightTextureIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks) {
      lightTextureIn.enableLightmap();
      Runnable enable = () -> {
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableDepthTest();
      RenderSystem.enableFog();
      };
      RenderSystem.pushMatrix();
      RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());

      for(IParticleRenderType iparticlerendertype : this.byType.keySet()) { // Forge: allow custom IParticleRenderType's
         if (iparticlerendertype == IParticleRenderType.NO_RENDER) continue;
         enable.run(); //Forge: MC-168672 Make sure all render types have the correct GL state.
         Iterable<Particle> iterable = this.byType.get(iparticlerendertype);
         if (iterable != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            iparticlerendertype.beginRender(bufferbuilder, this.renderer);

            for(Particle particle : iterable) {
               try {
                  particle.renderParticle(bufferbuilder, activeRenderInfoIn, partialTicks);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                  crashreportcategory.addDetail("Particle", particle::toString);
                  crashreportcategory.addDetail("Particle Type", iparticlerendertype::toString);
                  throw new ReportedException(crashreport);
               }
            }

            iparticlerendertype.finishRender(tessellator);
         }
      }

      RenderSystem.popMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      RenderSystem.defaultAlphaFunc();
      lightTextureIn.disableLightmap();
      RenderSystem.disableFog();
   }

   public void clearEffects(@Nullable World worldIn) {
      this.world = worldIn;
      this.byType.clear();
      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos pos, BlockState state) {
      if (!state.isAir(this.world, pos) && !state.addDestroyEffects(this.world, pos, this)) {
         VoxelShape voxelshape = state.getShape(this.world, pos);
         double d0 = 0.25D;
         voxelshape.forEachBox((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
            double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
            double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
            double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25D));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25D));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 < j; ++i1) {
                  for(int j1 = 0; j1 < k; ++j1) {
                     double d4 = ((double)l + 0.5D) / (double)i;
                     double d5 = ((double)i1 + 0.5D) / (double)j;
                     double d6 = ((double)j1 + 0.5D) / (double)k;
                     double d7 = d4 * d1 + p_228348_3_;
                     double d8 = d5 * d2 + p_228348_5_;
                     double d9 = d6 * d3 + p_228348_7_;
                     this.addEffect((new DiggingParticle(this.world, (double)pos.getX() + d7, (double)pos.getY() + d8, (double)pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)).setBlockPos(pos));
                  }
               }
            }

         });
      }
   }

   /**
    * Adds block hit particles for the specified block
    */
   public void addBlockHitEffects(BlockPos pos, Direction side) {
      BlockState blockstate = this.world.getBlockState(pos);
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         float f = 0.1F;
         AxisAlignedBB axisalignedbb = blockstate.getShape(this.world, pos).getBoundingBox();
         double d0 = (double)i + this.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2F) + (double)0.1F + axisalignedbb.minX;
         double d1 = (double)j + this.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2F) + (double)0.1F + axisalignedbb.minY;
         double d2 = (double)k + this.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2F) + (double)0.1F + axisalignedbb.minZ;
         if (side == Direction.DOWN) {
            d1 = (double)j + axisalignedbb.minY - (double)0.1F;
         }

         if (side == Direction.UP) {
            d1 = (double)j + axisalignedbb.maxY + (double)0.1F;
         }

         if (side == Direction.NORTH) {
            d2 = (double)k + axisalignedbb.minZ - (double)0.1F;
         }

         if (side == Direction.SOUTH) {
            d2 = (double)k + axisalignedbb.maxZ + (double)0.1F;
         }

         if (side == Direction.WEST) {
            d0 = (double)i + axisalignedbb.minX - (double)0.1F;
         }

         if (side == Direction.EAST) {
            d0 = (double)i + axisalignedbb.maxX + (double)0.1F;
         }

         this.addEffect((new DiggingParticle(this.world, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate)).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
      }
   }

   public String getStatistics() {
      return String.valueOf(this.byType.values().stream().mapToInt(Collection::size).sum());
   }

   public void addBlockHitEffects(BlockPos pos, net.minecraft.util.math.BlockRayTraceResult target) {
      BlockState state = world.getBlockState(pos);
      if (!state.addHitEffects(world, target, this))
         addBlockHitEffects(pos, target.getFace());
   }

   @OnlyIn(Dist.CLIENT)
   class AnimatedSpriteImpl implements IAnimatedSprite {
      private List<TextureAtlasSprite> sprites;

      private AnimatedSpriteImpl() {
      }

      public TextureAtlasSprite get(int particleAge, int particleMaxAge) {
         return this.sprites.get(particleAge * (this.sprites.size() - 1) / particleMaxAge);
      }

      public TextureAtlasSprite get(Random p_217590_1_) {
         return this.sprites.get(p_217590_1_.nextInt(this.sprites.size()));
      }

      public void setSprites(List<TextureAtlasSprite> p_217592_1_) {
         this.sprites = ImmutableList.copyOf(p_217592_1_);
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface IParticleMetaFactory<T extends IParticleData> {
      IParticleFactory<T> create(IAnimatedSprite p_create_1_);
   }
}