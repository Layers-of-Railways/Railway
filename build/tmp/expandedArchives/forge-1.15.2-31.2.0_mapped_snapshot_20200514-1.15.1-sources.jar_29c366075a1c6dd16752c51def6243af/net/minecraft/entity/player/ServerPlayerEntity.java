package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   public ServerPlayNetHandler connection;
   public final MinecraftServer server;
   public final PlayerInteractionManager interactionManager;
   private final List<Integer> entityRemoveQueue = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatisticsManager stats;
   /** the total health of the player, includes actual health and absorption health. Updated every tick. */
   private float lastHealthScore = Float.MIN_VALUE;
   private int lastFoodScore = Integer.MIN_VALUE;
   private int lastAirScore = Integer.MIN_VALUE;
   private int lastArmorScore = Integer.MIN_VALUE;
   private int lastLevelScore = Integer.MIN_VALUE;
   private int lastExperienceScore = Integer.MIN_VALUE;
   private float lastHealth = -1.0E8F;
   private int lastFoodLevel = -99999999;
   private boolean wasHungry = true;
   private int lastExperience = -99999999;
   private int respawnInvulnerabilityTicks = 60;
   private ChatVisibility chatVisibility;
   private boolean chatColours = true;
   private long playerLastActiveTime = Util.milliTime();
   /** The entity the player is currently spectating through. */
   private Entity spectatingEntity;
   private boolean invulnerableDimensionChange;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3d levitationStartPos;
   private int levitatingSince;
   private boolean disconnected;
   @Nullable
   private Vec3d enteredNetherPosition;
   /** Player section position as last updated by TicketManager, used by ChunkManager */
   private SectionPos managedSectionPos = SectionPos.of(0, 0, 0);
   public int currentWindowId;
   public boolean isChangingQuantityOnly;
   public int ping;
   public boolean queuedEndExit;

   public ServerPlayerEntity(MinecraftServer server, ServerWorld worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn) {
      super(worldIn, profile);
      interactionManagerIn.player = this;
      this.interactionManager = interactionManagerIn;
      this.server = server;
      this.recipeBook = new ServerRecipeBook(server.getRecipeManager());
      this.stats = server.getPlayerList().getPlayerStats(this);
      this.advancements = server.getPlayerList().getPlayerAdvancements(this);
      this.stepHeight = 1.0F;
      this.func_205734_a(worldIn);
   }

   private void func_205734_a(ServerWorld worldIn) {
      BlockPos blockpos = worldIn.getSpawnPoint();
      if (worldIn.dimension.hasSkyLight() && worldIn.getWorldInfo().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(worldIn));
         int j = MathHelper.floor(worldIn.getWorldBorder().getClosestDistance((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         long k = (long)(i * 2 + 1);
         long l = k * k;
         int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int)l;
         int j1 = this.func_205735_q(i1);
         int k1 = (new Random()).nextInt(i1);

         for(int l1 = 0; l1 < i1; ++l1) {
            int i2 = (k1 + j1 * l1) % i1;
            int j2 = i2 % (i * 2 + 1);
            int k2 = i2 / (i * 2 + 1);
            BlockPos blockpos1 = worldIn.getDimension().findSpawn(blockpos.getX() + j2 - i, blockpos.getZ() + k2 - i, false);
            if (blockpos1 != null) {
               this.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
               if (worldIn.hasNoCollisions(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);

         while(!worldIn.hasNoCollisions(this) && this.getPosY() < 255.0D) {
            this.setPosition(this.getPosX(), this.getPosY() + 1.0D, this.getPosZ());
         }
      }

   }

   private int func_205735_q(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("playerGameType", 99)) {
         if (this.getServer().getForceGamemode()) {
            this.interactionManager.setGameType(this.getServer().getGameType());
         } else {
            this.interactionManager.setGameType(GameType.getByID(compound.getInt("playerGameType")));
         }
      }

      if (compound.contains("enteredNetherPosition", 10)) {
         CompoundNBT compoundnbt = compound.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3d(compoundnbt.getDouble("x"), compoundnbt.getDouble("y"), compoundnbt.getDouble("z"));
      }

      this.seenCredits = compound.getBoolean("seenCredits");
      if (compound.contains("recipeBook", 10)) {
         this.recipeBook.read(compound.getCompound("recipeBook"));
      }

      if (this.isSleeping()) {
         this.wakeUp();
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("playerGameType", this.interactionManager.getGameType().getID());
      compound.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putDouble("x", this.enteredNetherPosition.x);
         compoundnbt.putDouble("y", this.enteredNetherPosition.y);
         compoundnbt.putDouble("z", this.enteredNetherPosition.z);
         compound.put("enteredNetherPosition", compoundnbt);
      }

      Entity entity1 = this.getLowestRidingEntity();
      Entity entity = this.getRidingEntity();
      if (entity != null && entity1 != this && entity1.isOnePlayerRiding()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         CompoundNBT compoundnbt2 = new CompoundNBT();
         entity1.writeUnlessPassenger(compoundnbt2);
         compoundnbt1.putUniqueId("Attach", entity.getUniqueID());
         compoundnbt1.put("Entity", compoundnbt2);
         compound.put("RootVehicle", compoundnbt1);
      }

      compound.put("recipeBook", this.recipeBook.write());
   }

   public void func_195394_a(int p_195394_1_) {
      float f = (float)this.xpBarCap();
      float f1 = (f - 1.0F) / f;
      this.experience = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastExperience = -1;
   }

   public void setExperienceLevel(int level) {
      this.experienceLevel = level;
      this.lastExperience = -1;
   }

   /**
    * Add experience levels to this player.
    */
   public void addExperienceLevel(int levels) {
      super.addExperienceLevel(levels);
      this.lastExperience = -1;
   }

   public void onEnchant(ItemStack enchantedItem, int cost) {
      super.onEnchant(enchantedItem, cost);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.addListener(this);
   }

   /**
    * Sends an ENTER_COMBAT packet to the client
    */
   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTER_COMBAT));
   }

   /**
    * Sends an END_COMBAT packet to the client
    */
   public void sendEndCombat() {
      super.sendEndCombat();
      this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createCooldownTracker() {
      return new ServerCooldownTracker(this);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.interactionManager.tick();
      --this.respawnInvulnerabilityTicks;
      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.world.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.container;
      }

      while(!this.entityRemoveQueue.isEmpty()) {
         int i = Math.min(this.entityRemoveQueue.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entityRemoveQueue.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = iterator.next();
            iterator.remove();
         }

         this.connection.sendPacket(new SDestroyEntitiesPacket(aint));
      }

      Entity entity = this.getSpectatingEntity();
      if (entity != this) {
         if (entity.isAlive()) {
            this.setPositionAndRotation(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.rotationYaw, entity.rotationPitch);
            this.getServerWorld().getChunkProvider().updatePlayerPosition(this);
            if (this.wantsToStopRiding()) {
               this.setSpectatingEntity(this);
            }
         } else {
            this.setSpectatingEntity(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.ticksExisted - this.levitatingSince);
      }

      this.advancements.flushDirty(this);
   }

   public void playerTick() {
      try {
         if (!this.isSpectator() || this.world.isBlockLoaded(new BlockPos(this))) {
            super.tick();
         }

         for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.getItem().isComplex()) {
               IPacket<?> ipacket = ((AbstractMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.world, this);
               if (ipacket != null) {
                  this.connection.sendPacket(ipacket);
               }
            }
         }

         if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.connection.sendPacket(new SUpdateHealthPacket(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
            this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
            this.updateScorePoints(ScoreCriteria.HEALTH, MathHelper.ceil(this.lastHealthScore));
         }

         if (this.foodStats.getFoodLevel() != this.lastFoodScore) {
            this.lastFoodScore = this.foodStats.getFoodLevel();
            this.updateScorePoints(ScoreCriteria.FOOD, MathHelper.ceil((float)this.lastFoodScore));
         }

         if (this.getAir() != this.lastAirScore) {
            this.lastAirScore = this.getAir();
            this.updateScorePoints(ScoreCriteria.AIR, MathHelper.ceil((float)this.lastAirScore));
         }

         if (this.getTotalArmorValue() != this.lastArmorScore) {
            this.lastArmorScore = this.getTotalArmorValue();
            this.updateScorePoints(ScoreCriteria.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
         }

         if (this.experienceTotal != this.lastExperienceScore) {
            this.lastExperienceScore = this.experienceTotal;
            this.updateScorePoints(ScoreCriteria.XP, MathHelper.ceil((float)this.lastExperienceScore));
         }

         if (this.experienceLevel != this.lastLevelScore) {
            this.lastLevelScore = this.experienceLevel;
            this.updateScorePoints(ScoreCriteria.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.connection.sendPacket(new SSetExperiencePacket(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void updateScorePoints(ScoreCriteria criteria, int points) {
      this.getWorldScoreboard().forAllObjectives(criteria, this.getScoreboardName(), (p_195397_1_) -> {
         p_195397_1_.setScorePoints(points);
      });
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, cause)) return;
      boolean flag = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
      if (flag) {
         ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
         this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {
            if (!p_212356_2_.isSuccess()) {
               int i = 256;
               String s = itextcomponent.getStringTruncated(256);
               ITextComponent itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long", (new StringTextComponent(s)).applyTextStyle(TextFormatting.YELLOW));
               ITextComponent itextcomponent2 = (new TranslationTextComponent("death.attack.even_more_magic", this.getDisplayName())).applyTextStyle((p_212357_1_) -> {
                  p_212357_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
               });
               this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
            }

         });
         Team team = this.getTeam();
         if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().sendMessageToAllTeamMembers(this, itextcomponent);
            } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().sendMessageToTeamOrAllPlayers(this, itextcomponent);
            }
         } else {
            this.server.getPlayerList().sendMessage(itextcomponent);
         }
      } else {
         this.connection.sendPacket(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED));
      }

      this.spawnShoulderEntities();
      if (!this.isSpectator()) {
         this.spawnDrops(cause);
      }

      this.getWorldScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, this.getScoreboardName(), Score::incrementScore);
      LivingEntity livingentity = this.getAttackingEntity();
      if (livingentity != null) {
         this.addStat(Stats.ENTITY_KILLED_BY.get(livingentity.getType()));
         livingentity.awardKillScore(this, this.scoreValue, cause);
         this.createWitherRose(livingentity);
      }

      this.world.setEntityState(this, (byte)3);
      this.addStat(Stats.DEATHS);
      this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.extinguish();
      this.setFlag(0, false);
      this.getCombatTracker().reset();
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.addScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getWorldScoreboard().forAllObjectives(ScoreCriteria.TOTAL_KILL_COUNT, s, Score::incrementScore);
         if (p_191956_1_ instanceof PlayerEntity) {
            this.addStat(Stats.PLAYER_KILLS);
            this.getWorldScoreboard().forAllObjectives(ScoreCriteria.PLAYER_KILL_COUNT, s, Score::incrementScore);
         } else {
            this.addStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(s, s1, ScoreCriteria.TEAM_KILL);
         this.handleTeamKill(s1, s, ScoreCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }
   }

   private void handleTeamKill(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getWorldScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getColorIndex();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getWorldScoreboard().forAllObjectives(p_195398_3_[i], p_195398_1_, Score::incrementScore);
         }
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);
         if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (source instanceof EntityDamageSource) {
               Entity entity = source.getTrueSource();
               if (entity instanceof PlayerEntity && !this.canAttackPlayer((PlayerEntity)entity)) {
                  return false;
               }

               if (entity instanceof AbstractArrowEntity) {
                  AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
                  Entity entity1 = abstractarrowentity.getShooter();
                  if (entity1 instanceof PlayerEntity && !this.canAttackPlayer((PlayerEntity)entity1)) {
                     return false;
                  }
               }
            }

            return super.attackEntityFrom(source, amount);
         }
      }
   }

   public boolean canAttackPlayer(PlayerEntity other) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
   }

   /**
    * Returns if other players can attack this player
    */
   private boolean canPlayersAttack() {
      return this.server.isPVPEnabled();
   }

   @Override
   @Nullable
   public Entity changeDimension(DimensionType destination, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, destination)) return null;
      this.invulnerableDimensionChange = true;
      DimensionType dimensiontype = this.dimension;
      if (dimensiontype == DimensionType.THE_END && destination == DimensionType.OVERWORLD && teleporter instanceof net.minecraft.world.Teleporter) { //Forge: Fix non-vanilla teleporters triggering end credits
         this.detach();
         this.getServerWorld().removePlayer(this, true); //Forge: The player entity is cloned so keep the data until after cloning calls copyFrom
         if (!this.queuedEndExit) {
            this.queuedEndExit = true;
            this.connection.sendPacket(new SChangeGameStatePacket(4, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         ServerWorld serverworld = this.server.getWorld(dimensiontype);
         this.dimension = destination;
         ServerWorld serverworld1 = this.server.getWorld(destination);
         WorldInfo worldinfo = serverworld1.getWorldInfo();
         net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(this.connection.netManager, this);
         this.connection.sendPacket(new SRespawnPacket(destination, WorldInfo.byHashing(worldinfo.getSeed()), worldinfo.getGenerator(), this.interactionManager.getGameType()));
         this.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
         PlayerList playerlist = this.server.getPlayerList();
         playerlist.updatePermissionLevel(this);
         serverworld.removeEntity(this, true); //Forge: the player entity is moved to the new world, NOT cloned. So keep the data alive with no matching invalidate call.
         this.revive();
         Entity e = teleporter.placeEntity(this, serverworld, serverworld1, this.rotationYaw, spawnPortal -> {//Forge: Start vanilla logic
         double d0 = this.getPosX();
         double d1 = this.getPosY();
         double d2 = this.getPosZ();
         float f = this.rotationPitch;
         float f1 = this.rotationYaw;
         double d3 = 8.0D;
         float f2 = f1;
         serverworld.getProfiler().startSection("moving");
         double moveFactor = serverworld.getDimension().getMovementFactor() / serverworld1.getDimension().getMovementFactor();
         d0 *= moveFactor;
         d2 *= moveFactor;
         if (dimensiontype == DimensionType.OVERWORLD && destination == DimensionType.THE_NETHER) {
            this.enteredNetherPosition = this.getPositionVec();
         } else if (dimensiontype == DimensionType.OVERWORLD && destination == DimensionType.THE_END) {
            BlockPos blockpos = serverworld1.getSpawnCoordinate();
            d0 = (double)blockpos.getX();
            d1 = (double)blockpos.getY();
            d2 = (double)blockpos.getZ();
            f1 = 90.0F;
            f = 0.0F;
         }

         this.setLocationAndAngles(d0, d1, d2, f1, f);
         serverworld.getProfiler().endSection();
         serverworld.getProfiler().startSection("placing");
         double d7 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minX() + 16.0D);
         double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minZ() + 16.0D);
         double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxX() - 16.0D);
         double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxZ() - 16.0D);
         d0 = MathHelper.clamp(d0, d7, d5);
         d2 = MathHelper.clamp(d2, d4, d6);
         this.setLocationAndAngles(d0, d1, d2, f1, f);
         if (destination == DimensionType.THE_END) {
            int i = MathHelper.floor(this.getPosX());
            int j = MathHelper.floor(this.getPosY()) - 1;
            int k = MathHelper.floor(this.getPosZ());
            int l = 1;
            int i1 = 0;

            for(int j1 = -2; j1 <= 2; ++j1) {
               for(int k1 = -2; k1 <= 2; ++k1) {
                  for(int l1 = -1; l1 < 3; ++l1) {
                     int i2 = i + k1 * 1 + j1 * 0;
                     int j2 = j + l1;
                     int k2 = k + k1 * 0 - j1 * 1;
                     boolean flag = l1 < 0;
                     serverworld1.setBlockState(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                  }
               }
            }

            this.setLocationAndAngles((double)i, (double)j, (double)k, f1, 0.0F);
            this.setMotion(Vec3d.ZERO);
         } else if (spawnPortal && !serverworld1.getDefaultTeleporter().placeInPortal(this, f2)) {
            serverworld1.getDefaultTeleporter().makePortal(this);
            serverworld1.getDefaultTeleporter().placeInPortal(this, f2);
         }

         serverworld.getProfiler().endSection();
         this.setWorld(serverworld1);
         serverworld1.addDuringPortalTeleport(this);
         this.func_213846_b(serverworld);
         this.connection.setPlayerLocation(this.getPosX(), this.getPosY(), this.getPosZ(), f1, f);
         return this;//forge: this is part of the ITeleporter patch
         });//Forge: End vanilla logic
         if (e != this) throw new java.lang.IllegalArgumentException(String.format("Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
         this.interactionManager.setWorld(serverworld1);
         this.connection.sendPacket(new SPlayerAbilitiesPacket(this.abilities));
         playerlist.sendWorldInfo(this, serverworld1);
         playerlist.sendInventory(this);

         for(EffectInstance effectinstance : this.getActivePotionEffects()) {
            this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), effectinstance));
         }

         this.connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
         net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, dimensiontype, destination);
         return this;
      }
   }

   private void func_213846_b(ServerWorld p_213846_1_) {
      DimensionType dimensiontype = p_213846_1_.dimension.getType();
      DimensionType dimensiontype1 = this.world.dimension.getType();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, dimensiontype, dimensiontype1);
      if (dimensiontype == DimensionType.THE_NETHER && dimensiontype1 == DimensionType.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (dimensiontype1 != DimensionType.THE_NETHER) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean isSpectatedByPlayer(ServerPlayerEntity player) {
      if (player.isSpectator()) {
         return this.getSpectatingEntity() == this;
      } else {
         return this.isSpectator() ? false : super.isSpectatedByPlayer(player);
      }
   }

   private void sendTileEntityUpdate(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = p_147097_1_.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.connection.sendPacket(supdatetileentitypacket);
         }
      }

   }

   /**
    * Called when the entity picks up an item.
    */
   public void onItemPickup(Entity entityIn, int quantity) {
      super.onItemPickup(entityIn, quantity);
      this.openContainer.detectAndSendChanges();
   }

   public Either<PlayerEntity.SleepResult, Unit> trySleep(BlockPos at) {
      return super.trySleep(at).ifRight((p_213849_1_) -> {
         this.addStat(Stats.SLEEP_IN_BED);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      });
   }

   public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
      if (this.isSleeping()) {
         this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(this, 2));
      }

      super.stopSleepInBed(p_225652_1_, p_225652_2_);
      if (this.connection != null) {
         this.connection.setPlayerLocation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
      }

   }

   public boolean startRiding(Entity entityIn, boolean force) {
      Entity entity = this.getRidingEntity();
      if (!super.startRiding(entityIn, force)) {
         return false;
      } else {
         Entity entity1 = this.getRidingEntity();
         if (entity1 != entity && this.connection != null) {
            this.connection.setPlayerLocation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
         }

         return true;
      }
   }

   /**
    * Dismounts this entity from the entity it is riding.
    */
   public void stopRiding() {
      Entity entity = this.getRidingEntity();
      super.stopRiding();
      Entity entity1 = this.getRidingEntity();
      if (entity1 != entity && this.connection != null) {
         this.connection.setPlayerLocation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
      }

   }

   /**
    * Returns whether this Entity is invulnerable to the given DamageSource.
    */
   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || this.isInvulnerableDimensionChange() || this.abilities.disableDamage && source == DamageSource.WITHER;
   }

   protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void frostWalk(BlockPos pos) {
      if (!this.isSpectator()) {
         super.frostWalk(pos);
      }

   }

   /**
    * process player falling based on movement packet
    */
   public void handleFalling(double y, boolean onGroundIn) {
      BlockPos blockpos = this.getOnPosition();
      if (this.world.isBlockLoaded(blockpos)) {
         BlockState blockstate = this.world.getBlockState(blockpos);
         super.updateFallState(y, onGroundIn, blockstate, blockpos);
      }
   }

   public void openSignEditor(SignTileEntity signTile) {
      signTile.setPlayer(this);
      this.connection.sendPacket(new SOpenSignMenuPacket(signTile.getPos()));
   }

   /**
    * get the next window id to use
    */
   public void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   public OptionalInt openContainer(@Nullable INamedContainerProvider p_213829_1_) {
      if (p_213829_1_ == null) {
         return OptionalInt.empty();
      } else {
         if (this.openContainer != this.container) {
            this.closeScreen();
         }

         this.getNextWindowId();
         Container container = p_213829_1_.createMenu(this.currentWindowId, this.inventory, this);
         if (container == null) {
            if (this.isSpectator()) {
               this.sendStatusMessage((new TranslationTextComponent("container.spectatorCantOpen")).applyTextStyle(TextFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.sendPacket(new SOpenWindowPacket(container.windowId, container.getType(), p_213829_1_.getDisplayName()));
            container.addListener(this);
            this.openContainer = container;
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
            return OptionalInt.of(this.currentWindowId);
         }
      }
   }

   public void openMerchantContainer(int containerId, MerchantOffers offers, int level, int xp, boolean p_213818_5_, boolean p_213818_6_) {
      this.connection.sendPacket(new SMerchantOffersPacket(containerId, offers, level, xp, p_213818_5_, p_213818_6_));
   }

   public void openHorseInventory(AbstractHorseEntity horse, IInventory inventoryIn) {
      if (this.openContainer != this.container) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.connection.sendPacket(new SOpenHorseWindowPacket(this.currentWindowId, inventoryIn.getSizeInventory(), horse.getEntityId()));
      this.openContainer = new HorseInventoryContainer(this.currentWindowId, this.inventory, inventoryIn, horse);
      this.openContainer.addListener(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.openContainer));
   }

   public void openBook(ItemStack stack, Hand hand) {
      Item item = stack.getItem();
      if (item == Items.WRITTEN_BOOK) {
         if (WrittenBookItem.resolveContents(stack, this.getCommandSource(), this)) {
            this.openContainer.detectAndSendChanges();
         }

         this.connection.sendPacket(new SOpenBookWindowPacket(hand));
      }

   }

   public void openCommandBlock(CommandBlockTileEntity commandBlock) {
      commandBlock.setSendToClient(true);
      this.sendTileEntityUpdate(commandBlock);
   }

   /**
    * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
    * contents of that slot.
    */
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (!(containerToSend.getSlot(slotInd) instanceof CraftingResultSlot)) {
         if (containerToSend == this.container) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if (!this.isChangingQuantityOnly) {
            this.connection.sendPacket(new SSetSlotPacket(containerToSend.windowId, slotInd, stack));
         }
      }
   }

   public void sendContainerToPlayer(Container containerIn) {
      this.sendAllContents(containerIn, containerIn.getInventory());
   }

   /**
    * update the crafting window inventory with the items in the list
    */
   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
      this.connection.sendPacket(new SWindowItemsPacket(containerToSend.windowId, itemsList));
      this.connection.sendPacket(new SSetSlotPacket(-1, -1, this.inventory.getItemStack()));
   }

   /**
    * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
    * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
    * value. Both are truncated to shorts in non-local SMP.
    */
   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
      this.connection.sendPacket(new SWindowPropertyPacket(containerIn.windowId, varToUpdate, newValue));
   }

   /**
    * set current crafting inventory back to the 2x2 square
    */
   public void closeScreen() {
      this.connection.sendPacket(new SCloseWindowPacket(this.openContainer.windowId));
      this.closeContainer();
   }

   /**
    * updates item held by mouse
    */
   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.connection.sendPacket(new SSetSlotPacket(-1, -1, this.inventory.getItemStack()));
      }
   }

   /**
    * Closes the container the player currently has open.
    */
   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Close(this, this.openContainer));
      this.openContainer = this.container;
   }

   public void setEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking) {
      if (this.isPassenger()) {
         if (strafe >= -1.0F && strafe <= 1.0F) {
            this.moveStrafing = strafe;
         }

         if (forward >= -1.0F && forward <= 1.0F) {
            this.moveForward = forward;
         }

         this.isJumping = jumping;
         this.setSneaking(sneaking);
      }

   }

   /**
    * Adds a value to a statistic field.
    */
   public void addStat(Stat<?> stat, int amount) {
      this.stats.increment(this, stat, amount);
      this.getWorldScoreboard().forAllObjectives(stat, this.getScoreboardName(), (p_195396_1_) -> {
         p_195396_1_.increaseScore(amount);
      });
   }

   public void takeStat(Stat<?> stat) {
      this.stats.setValue(this, stat, 0);
      this.getWorldScoreboard().forAllObjectives(stat, this.getScoreboardName(), Score::reset);
   }

   public int unlockRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return this.recipeBook.add(p_195065_1_, this);
   }

   public void unlockRecipes(ResourceLocation[] p_193102_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : p_193102_1_) {
         this.server.getRecipeManager().getRecipe(resourcelocation).ifPresent(list::add);
      }

      this.unlockRecipes(list);
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return this.recipeBook.remove(p_195069_1_, this);
   }

   public void giveExperiencePoints(int p_195068_1_) {
      super.giveExperiencePoints(p_195068_1_);
      this.lastExperience = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.removePassengers();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   /**
    * this function is called when a players inventory is sent to him, lastHealth is updated on any dimension
    * transitions, then reset.
    */
   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
      this.connection.sendPacket(new SChatPacket(chatComponent, actionBar ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   /**
    * Used for when item use count runs out, ie: eating completed
    */
   protected void onItemUseFinish() {
      if (!this.activeItemStack.isEmpty() && this.isHandActive()) {
         this.connection.sendPacket(new SEntityStatusPacket(this, (byte)9));
         super.onItemUseFinish();
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.connection.sendPacket(new SPlayerLookPacket(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void lookAt(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vec3d vec3d = p_200618_3_.apply(p_200618_2_);
      super.lookAt(p_200618_1_, vec3d);
      this.connection.sendPacket(new SPlayerLookPacket(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void copyFrom(ServerPlayerEntity that, boolean keepEverything) {
      if (keepEverything) {
         this.inventory.copyInventory(that.inventory);
         this.setHealth(that.getHealth());
         this.foodStats = that.foodStats;
         this.experienceLevel = that.experienceLevel;
         this.experienceTotal = that.experienceTotal;
         this.experience = that.experience;
         this.setScore(that.getScore());
         this.lastPortalPos = that.lastPortalPos;
         this.lastPortalVec = that.lastPortalVec;
         this.teleportDirection = that.teleportDirection;
      } else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || that.isSpectator()) {
         this.inventory.copyInventory(that.inventory);
         this.experienceLevel = that.experienceLevel;
         this.experienceTotal = that.experienceTotal;
         this.experience = that.experience;
         this.setScore(that.getScore());
      }

      this.xpSeed = that.xpSeed;
      this.enterChestInventory = that.enterChestInventory;
      this.getDataManager().set(PLAYER_MODEL_FLAG, that.getDataManager().get(PLAYER_MODEL_FLAG));
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.recipeBook.copyFrom(that.recipeBook);
      this.entityRemoveQueue.addAll(that.entityRemoveQueue);
      this.seenCredits = that.seenCredits;
      this.enteredNetherPosition = that.enteredNetherPosition;
      this.setLeftShoulderEntity(that.getLeftShoulderEntity());
      this.setRightShoulderEntity(that.getRightShoulderEntity());

      this.spawnPosMap = that.spawnPosMap;
      this.spawnForcedMap = that.spawnForcedMap;
      if(that.dimension != DimensionType.OVERWORLD) {
          this.spawnPos = that.spawnPos;
          this.spawnForced = that.spawnForced;
      }

      //Copy over a section of the Entity Data from the old player.
      //Allows mods to specify data that persists after players respawn.
      CompoundNBT old = that.getPersistentData();
      if (old.contains(PERSISTED_NBT_TAG))
          getPersistentData().put(PERSISTED_NBT_TAG, old.get(PERSISTED_NBT_TAG));
      net.minecraftforge.event.ForgeEventFactory.onPlayerClone(this, that, !keepEverything);
   }

   protected void onNewPotionEffect(EffectInstance id) {
      super.onNewPotionEffect(id);
      this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), id));
      if (id.getPotion() == Effects.LEVITATION) {
         this.levitatingSince = this.ticksExisted;
         this.levitationStartPos = this.getPositionVec();
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onChangedPotionEffect(EffectInstance id, boolean reapply) {
      super.onChangedPotionEffect(id, reapply);
      this.connection.sendPacket(new SPlayEntityEffectPacket(this.getEntityId(), id));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onFinishedPotionEffect(EffectInstance effect) {
      super.onFinishedPotionEffect(effect);
      this.connection.sendPacket(new SRemoveEntityEffectPacket(this.getEntityId(), effect.getPotion()));
      if (effect.getPotion() == Effects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   /**
    * Sets the position of the entity and updates the 'last' variables
    */
   public void setPositionAndUpdate(double x, double y, double z) {
      this.connection.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   public void moveForced(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.connection.setPlayerLocation(p_225653_1_, p_225653_3_, p_225653_5_, this.rotationYaw, this.rotationPitch);
      this.connection.captureCurrentPosition();
   }

   /**
    * Called when the entity is dealt a critical hit.
    */
   public void onCriticalHit(Entity entityHit) {
      this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(entityHit, 4));
   }

   public void onEnchantmentCritical(Entity entityHit) {
      this.getServerWorld().getChunkProvider().sendToTrackingAndSelf(this, new SAnimateHandPacket(entityHit, 5));
   }

   /**
    * Sends the player's abilities to the server (if there is one).
    */
   public void sendPlayerAbilities() {
      if (this.connection != null) {
         this.connection.sendPacket(new SPlayerAbilitiesPacket(this.abilities));
         this.updatePotionMetadata();
      }
   }

   public ServerWorld getServerWorld() {
      return (ServerWorld)this.world;
   }

   /**
    * Sets the player's game mode and sends it to them.
    */
   public void setGameType(GameType gameType) {
      this.interactionManager.setGameType(gameType);
      this.connection.sendPacket(new SChangeGameStatePacket(3, (float)gameType.getID()));
      if (gameType == GameType.SPECTATOR) {
         this.spawnShoulderEntities();
         this.stopRiding();
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   /**
    * Returns true if the player is in spectator mode.
    */
   public boolean isSpectator() {
      return this.interactionManager.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.interactionManager.getGameType() == GameType.CREATIVE;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      this.sendMessage(component, ChatType.SYSTEM);
   }

   public void sendMessage(ITextComponent textComponent, ChatType chatTypeIn) {
      this.connection.sendPacket(new SChatPacket(textComponent, chatTypeIn), (p_211144_3_) -> {
         if (!p_211144_3_.isSuccess() && (chatTypeIn == ChatType.GAME_INFO || chatTypeIn == ChatType.SYSTEM)) {
            int i = 256;
            String s = textComponent.getStringTruncated(256);
            ITextComponent itextcomponent = (new StringTextComponent(s)).applyTextStyle(TextFormatting.YELLOW);
            this.connection.sendPacket(new SChatPacket((new TranslationTextComponent("multiplayer.message_not_delivered", itextcomponent)).applyTextStyle(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   /**
    * Gets the player's IP address. Used in /banip.
    */
   public String getPlayerIP() {
      String s = this.connection.netManager.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      s = s.substring(0, s.indexOf(":"));
      return s;
   }

   public void handleClientSettings(CClientSettingsPacket packetIn) {
      this.language = packetIn.getLang();
      this.chatVisibility = packetIn.getChatVisibility();
      this.chatColours = packetIn.isColorsEnabled();
      this.getDataManager().set(PLAYER_MODEL_FLAG, (byte)packetIn.getModelPartFlags());
      this.getDataManager().set(MAIN_HAND, (byte)(packetIn.getMainHand() == HandSide.LEFT ? 0 : 1));
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String url, String hash) {
      this.connection.sendPacket(new SSendResourcePackPacket(url, hash));
   }

   protected int getPermissionLevel() {
      return this.server.getPermissionLevel(this.getGameProfile());
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = Util.milliTime();
   }

   public ServerStatisticsManager getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   /**
    * Sends a packet to the player to remove an entity.
    */
   public void removeEntity(Entity entityIn) {
      if (entityIn instanceof PlayerEntity) {
         this.connection.sendPacket(new SDestroyEntitiesPacket(entityIn.getEntityId()));
      } else {
         this.entityRemoveQueue.add(entityIn.getEntityId());
      }

   }

   public void addEntity(Entity entityIn) {
      this.entityRemoveQueue.remove(Integer.valueOf(entityIn.getEntityId()));
   }

   /**
    * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
    * ambience, and invisibility metadata values
    */
   protected void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

   }

   public Entity getSpectatingEntity() {
      return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
   }

   public void setSpectatingEntity(Entity entityToSpectate) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = (Entity)(entityToSpectate == null ? this : entityToSpectate);
      if (entity != this.spectatingEntity) {
         this.connection.sendPacket(new SCameraPacket(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.getPosX(), this.spectatingEntity.getPosY(), this.spectatingEntity.getPosZ());
      }

   }

   /**
    * Decrements the counter for the remaining time until the entity may use a portal again.
    */
   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0 && !this.invulnerableDimensionChange) {
         --this.timeUntilPortal;
      }

   }

   /**
    * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
    * called on it. Args: targetEntity
    */
   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (this.interactionManager.getGameType() == GameType.SPECTATOR) {
         this.setSpectatingEntity(targetEntity);
      } else {
         super.attackTargetEntityWithCurrentItem(targetEntity);
      }

   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   /**
    * Returns null which indicates the tab list should just display the player's name, return a different value to
    * display the specified text instead of the player's name
    */
   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swingArm(Hand hand) {
      super.swingArm(hand);
      this.resetCooldown();
   }

   public boolean isInvulnerableDimensionChange() {
      return this.invulnerableDimensionChange;
   }

   public void clearInvulnerableDimensionChange() {
      this.invulnerableDimensionChange = false;
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleport(ServerWorld newWorld, double x, double y, double z, float yaw, float pitch) {
      this.setSpectatingEntity(this);
      this.stopRiding();
      if (newWorld == this.world) {
         this.connection.setPlayerLocation(x, y, z, yaw, pitch);
      } else if (net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, newWorld.dimension.getType())) {
         DimensionType oldDimension = this.dimension;
         ServerWorld serverworld = this.getServerWorld();
         this.dimension = newWorld.dimension.getType();
         WorldInfo worldinfo = newWorld.getWorldInfo();
         net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(this.connection.netManager, this);
         this.connection.sendPacket(new SRespawnPacket(this.dimension, WorldInfo.byHashing(worldinfo.getSeed()), worldinfo.getGenerator(), this.interactionManager.getGameType()));
         this.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
         this.server.getPlayerList().updatePermissionLevel(this);
         serverworld.removePlayer(this, true); //Forge: The player entity itself is moved, and not cloned. So we need to keep the data alive with no matching invalidate call later.
         this.revive();
         this.setLocationAndAngles(x, y, z, yaw, pitch);
         this.setWorld(newWorld);
         newWorld.addDuringCommandTeleport(this);
         this.func_213846_b(serverworld);
         this.connection.setPlayerLocation(x, y, z, yaw, pitch);
         this.interactionManager.setWorld(newWorld);
         this.server.getPlayerList().sendWorldInfo(this, newWorld);
         this.server.getPlayerList().sendInventory(this);
         net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, oldDimension, this.dimension);
      }

   }

   public void sendChunkLoad(ChunkPos p_213844_1_, IPacket<?> p_213844_2_, IPacket<?> p_213844_3_) {
      this.connection.sendPacket(p_213844_3_);
      this.connection.sendPacket(p_213844_2_);
   }

   public void sendChunkUnload(ChunkPos p_213845_1_) {
      if (this.isAlive()) {
         this.connection.sendPacket(new SUnloadChunkPacket(p_213845_1_.x, p_213845_1_.z));
      }

   }

   public SectionPos getManagedSectionPos() {
      return this.managedSectionPos;
   }

   public void setManagedSectionPos(SectionPos sectionPosIn) {
      this.managedSectionPos = sectionPosIn;
   }

   public void playSound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.connection.sendPacket(new SPlaySoundEffectPacket(p_213823_1_, p_213823_2_, this.getPosX(), this.getPosY(), this.getPosZ(), p_213823_3_, p_213823_4_));
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnPlayerPacket(this);
   }

   /**
    * Creates and drops the provided item. Depending on the dropAround, it will drop teh item around the player, instead
    * of dropping the item from where the player is pointing at. Likewise, if traceItem is true, the dropped item entity
    * will have the thrower set as the player.
    */
   public ItemEntity dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
      ItemEntity itementity = super.dropItem(droppedItem, dropAround, traceItem);
      if (itementity == null) {
         return null;
      } else {
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.world.addEntity(itementity);
         ItemStack itemstack = itementity.getItem();
         if (traceItem) {
            if (!itemstack.isEmpty()) {
               this.addStat(Stats.ITEM_DROPPED.get(itemstack.getItem()), droppedItem.getCount());
            }

            this.addStat(Stats.DROP);
         }

         return itementity;
      }
   }
}