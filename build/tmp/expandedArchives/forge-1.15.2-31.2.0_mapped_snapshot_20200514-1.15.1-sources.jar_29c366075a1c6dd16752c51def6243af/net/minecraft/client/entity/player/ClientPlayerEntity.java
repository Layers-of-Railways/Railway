package net.minecraft.client.entity.player;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.RidingMinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditMinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.screen.JigsawScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity {
   public final ClientPlayNetHandler connection;
   private final StatisticsManager stats;
   private final ClientRecipeBook recipeBook;
   private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   /**
    * The last X position which was transmitted to the server, used to determine when the X position changes and needs
    * to be re-trasmitted
    */
   private double lastReportedPosX;
   /**
    * The last Y position which was transmitted to the server, used to determine when the Y position changes and needs
    * to be re-transmitted
    */
   private double lastReportedPosY;
   /**
    * The last Z position which was transmitted to the server, used to determine when the Z position changes and needs
    * to be re-transmitted
    */
   private double lastReportedPosZ;
   /**
    * The last yaw value which was transmitted to the server, used to determine when the yaw changes and needs to be re-
    * transmitted
    */
   private float lastReportedYaw;
   /**
    * The last pitch value which was transmitted to the server, used to determine when the pitch changes and needs to be
    * re-transmitted
    */
   private float lastReportedPitch;
   private boolean prevOnGround;
   private boolean field_228351_cj_;
   /** the last sprinting state sent to the server */
   private boolean serverSprintState;
   /**
    * Reset to 0 every time position is sent to the server, used to send periodic updates every 20 ticks even when the
    * player is not moving.
    */
   private int positionUpdateTicks;
   private boolean hasValidHealth;
   private String serverBrand;
   public MovementInput movementInput;
   protected final Minecraft mc;
   protected int sprintToggleTimer;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int horseJumpPowerCounter;
   private float horseJumpPower;
   public float timeInPortal;
   public float prevTimeInPortal;
   private boolean handActive;
   private Hand activeHand;
   private boolean rowingBoat;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int counterInWater;
   private boolean showDeathScreen = true;

   public ClientPlayerEntity(Minecraft minecraftIn, ClientWorld clientWorldIn, ClientPlayNetHandler connectionIn, StatisticsManager statsManager, ClientRecipeBook recipes) {
      super(clientWorldIn, connectionIn.getGameProfile());
      this.connection = connectionIn;
      this.stats = statsManager;
      this.recipeBook = recipes;
      this.mc = minecraftIn;
      this.dimension = DimensionType.OVERWORLD;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, minecraftIn.getSoundHandler()));
      this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
   }

   public boolean isGlowing() {
      return super.isGlowing() || this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown();
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, source, amount);
      return false;
   }

   /**
    * Heal living entity (param: amount of half-hearts)
    */
   public void heal(float healAmount) {
   }

   public boolean startRiding(Entity entityIn, boolean force) {
      if (!super.startRiding(entityIn, force)) {
         return false;
      } else {
         if (entityIn instanceof AbstractMinecartEntity) {
            this.mc.getSoundHandler().play(new RidingMinecartTickableSound(this, (AbstractMinecartEntity)entityIn));
         }

         if (entityIn instanceof BoatEntity) {
            this.prevRotationYaw = entityIn.rotationYaw;
            this.rotationYaw = entityIn.rotationYaw;
            this.setRotationYawHead(entityIn.rotationYaw);
         }

         return true;
      }
   }

   /**
    * Dismounts this entity from the entity it is riding.
    */
   public void stopRiding() {
      super.stopRiding();
      this.rowingBoat = false;
   }

   /**
    * Gets the current pitch of the entity.
    */
   public float getPitch(float partialTicks) {
      return this.rotationPitch;
   }

   /**
    * Gets the current yaw of the entity
    */
   public float getYaw(float partialTicks) {
      return this.isPassenger() ? super.getYaw(partialTicks) : this.rotationYaw;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.world.isBlockLoaded(new BlockPos(this.getPosX(), 0.0D, this.getPosZ()))) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.sendPacket(new CPlayerPacket.RotationPacket(this.rotationYaw, this.rotationPitch, this.onGround));
            this.connection.sendPacket(new CInputPacket(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneaking));
            Entity entity = this.getLowestRidingEntity();
            if (entity != this && entity.canPassengerSteer()) {
               this.connection.sendPacket(new CMoveVehiclePacket(entity));
            }
         } else {
            this.onUpdateWalkingPlayer();
         }

         for(IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
            iambientsoundhandler.tick();
         }

      }
   }

   /**
    * called every tick when the player is on foot. Performs all the things that normally happen during movement.
    */
   private void onUpdateWalkingPlayer() {
      boolean flag = this.isSprinting();
      if (flag != this.serverSprintState) {
         CEntityActionPacket.Action centityactionpacket$action = flag ? CEntityActionPacket.Action.START_SPRINTING : CEntityActionPacket.Action.STOP_SPRINTING;
         this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action));
         this.serverSprintState = flag;
      }

      boolean flag3 = this.isSneaking();
      if (flag3 != this.field_228351_cj_) {
         CEntityActionPacket.Action centityactionpacket$action1 = flag3 ? CEntityActionPacket.Action.PRESS_SHIFT_KEY : CEntityActionPacket.Action.RELEASE_SHIFT_KEY;
         this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action1));
         this.field_228351_cj_ = flag3;
      }

      if (this.isCurrentViewEntity()) {
         double d4 = this.getPosX() - this.lastReportedPosX;
         double d0 = this.getPosY() - this.lastReportedPosY;
         double d1 = this.getPosZ() - this.lastReportedPosZ;
         double d2 = (double)(this.rotationYaw - this.lastReportedYaw);
         double d3 = (double)(this.rotationPitch - this.lastReportedPitch);
         ++this.positionUpdateTicks;
         boolean flag1 = d4 * d4 + d0 * d0 + d1 * d1 > 9.0E-4D || this.positionUpdateTicks >= 20;
         boolean flag2 = d2 != 0.0D || d3 != 0.0D;
         if (this.isPassenger()) {
            Vec3d vec3d = this.getMotion();
            this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(vec3d.x, -999.0D, vec3d.z, this.rotationYaw, this.rotationPitch, this.onGround));
            flag1 = false;
         } else if (flag1 && flag2) {
            this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (flag1) {
            this.connection.sendPacket(new CPlayerPacket.PositionPacket(this.getPosX(), this.getPosY(), this.getPosZ(), this.onGround));
         } else if (flag2) {
            this.connection.sendPacket(new CPlayerPacket.RotationPacket(this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (this.prevOnGround != this.onGround) {
            this.connection.sendPacket(new CPlayerPacket(this.onGround));
         }

         if (flag1) {
            this.lastReportedPosX = this.getPosX();
            this.lastReportedPosY = this.getPosY();
            this.lastReportedPosZ = this.getPosZ();
            this.positionUpdateTicks = 0;
         }

         if (flag2) {
            this.lastReportedYaw = this.rotationYaw;
            this.lastReportedPitch = this.rotationPitch;
         }

         this.prevOnGround = this.onGround;
         this.autoJumpEnabled = this.mc.gameSettings.autoJump;
      }

   }

   public boolean drop(boolean p_225609_1_) {
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_225609_1_ ? CPlayerDiggingPacket.Action.DROP_ALL_ITEMS : CPlayerDiggingPacket.Action.DROP_ITEM;
      this.connection.sendPacket(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
      return this.inventory.decrStackSize(this.inventory.currentItem, p_225609_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1) != ItemStack.EMPTY;
   }

   /**
    * Sends a chat message from the player.
    */
   public void sendChatMessage(String message) {
      this.connection.sendPacket(new CChatMessagePacket(message));
   }

   public void swingArm(Hand hand) {
      super.swingArm(hand);
      this.connection.sendPacket(new CAnimateHandPacket(hand));
   }

   public void respawnPlayer() {
      this.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
   }

   /**
    * Deals damage to the entity. This will take the armor of the entity into consideration before damaging the health
    * bar.
    */
   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isInvulnerableTo(damageSrc)) {
         this.setHealth(this.getHealth() - damageAmount);
      }
   }

   /**
    * set current crafting inventory back to the 2x2 square
    */
   public void closeScreen() {
      this.connection.sendPacket(new CCloseWindowPacket(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack(ItemStack.EMPTY);
      super.closeScreen();
      this.mc.displayGuiScreen((Screen)null);
   }

   /**
    * Updates health locally.
    */
   public void setPlayerSPHealth(float health) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - health;
         if (f <= 0.0F) {
            this.setHealth(health);
            if (f < 0.0F) {
               this.hurtResistantTime = 10;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = 20;
            this.damageEntity(DamageSource.GENERIC, f);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(health);
         this.hasValidHealth = true;
      }

   }

   /**
    * Sends the player's abilities to the server (if there is one).
    */
   public void sendPlayerAbilities() {
      this.connection.sendPacket(new CPlayerAbilitiesPacket(this.abilities));
   }

   /**
    * returns true if this is an EntityPlayerSP, or the logged in player.
    */
   public boolean isUser() {
      return true;
   }

   protected void sendHorseJump() {
      this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_RIDING_JUMP, MathHelper.floor(this.getHorseJumpPower() * 100.0F)));
   }

   public void sendHorseInventory() {
      this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.OPEN_INVENTORY));
   }

   /**
    * Sets the brand of the currently connected server. Server brand information is sent over the {@code MC|Brand}
    * plugin channel, and is used to identify modded servers in crash reports.
    */
   public void setServerBrand(String brand) {
      this.serverBrand = brand;
   }

   /**
    * Gets the brand of the currently connected server. May be null if the server hasn't yet sent brand information.
    * Server brand information is sent over the {@code MC|Brand} plugin channel, and is used to identify modded servers
    * in crash reports.
    */
   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatisticsManager getStats() {
      return this.stats;
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(IRecipe<?> p_193103_1_) {
      if (this.recipeBook.isNew(p_193103_1_)) {
         this.recipeBook.markSeen(p_193103_1_);
         this.connection.sendPacket(new CRecipeInfoPacket(p_193103_1_));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int p_184839_1_) {
      this.permissionLevel = p_184839_1_;
   }

   public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
      if (actionBar) {
         this.mc.ingameGUI.setOverlayMessage(chatComponent, false);
      } else {
         this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
      }

   }

   protected void pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      if (this.shouldBlockPushPlayer(blockpos)) {
         double d0 = x - (double)blockpos.getX();
         double d1 = z - (double)blockpos.getZ();
         Direction direction = null;
         double d2 = 9999.0D;
         if (!this.shouldBlockPushPlayer(blockpos.west()) && d0 < d2) {
            d2 = d0;
            direction = Direction.WEST;
         }

         if (!this.shouldBlockPushPlayer(blockpos.east()) && 1.0D - d0 < d2) {
            d2 = 1.0D - d0;
            direction = Direction.EAST;
         }

         if (!this.shouldBlockPushPlayer(blockpos.north()) && d1 < d2) {
            d2 = d1;
            direction = Direction.NORTH;
         }

         if (!this.shouldBlockPushPlayer(blockpos.south()) && 1.0D - d1 < d2) {
            d2 = 1.0D - d1;
            direction = Direction.SOUTH;
         }

         if (direction != null) {
            Vec3d vec3d = this.getMotion();
            switch(direction) {
            case WEST:
               this.setMotion(-0.1D, vec3d.y, vec3d.z);
               break;
            case EAST:
               this.setMotion(0.1D, vec3d.y, vec3d.z);
               break;
            case NORTH:
               this.setMotion(vec3d.x, vec3d.y, -0.1D);
               break;
            case SOUTH:
               this.setMotion(vec3d.x, vec3d.y, 0.1D);
            }
         }
      }

   }

   private boolean shouldBlockPushPlayer(BlockPos p_205027_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_205027_1_);

      for(int i = MathHelper.floor(axisalignedbb.minY); i < MathHelper.ceil(axisalignedbb.maxY); ++i) {
         blockpos$mutable.setY(i);
         if (!this.isNormalCube(blockpos$mutable)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Set sprinting switch for Entity.
    */
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingTicksLeft = 0;
   }

   /**
    * Sets the current XP, total XP, and level number.
    */
   public void setXPStats(float currentXP, int maxXP, int level) {
      this.experience = currentXP;
      this.experienceTotal = maxXP;
      this.experienceLevel = level;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(component);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   public void handleStatusUpdate(byte id) {
      if (id >= 24 && id <= 28) {
         this.setPermissionLevel(id - 24);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void setShowDeathScreen(boolean p_228355_1_) {
      this.showDeathScreen = p_228355_1_;
   }

   public boolean isShowDeathScreen() {
      return this.showDeathScreen;
   }

   public void playSound(SoundEvent soundIn, float volume, float pitch) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(this, soundIn, this.getSoundCategory(), volume, pitch);
      if (event.isCanceled() || event.getSound() == null) return;
      soundIn = event.getSound();
      volume = event.getVolume();
      pitch = event.getPitch();
      this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch, false);
   }

   public void playSound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), p_213823_1_, p_213823_2_, p_213823_3_, p_213823_4_, false);
   }

   /**
    * Returns whether the entity is in a server world
    */
   public boolean isServerWorld() {
      return true;
   }

   public void setActiveHand(Hand hand) {
      ItemStack itemstack = this.getHeldItem(hand);
      if (!itemstack.isEmpty() && !this.isHandActive()) {
         super.setActiveHand(hand);
         this.handActive = true;
         this.activeHand = hand;
      }
   }

   public boolean isHandActive() {
      return this.handActive;
   }

   public void resetActiveHand() {
      super.resetActiveHand();
      this.handActive = false;
   }

   public Hand getActiveHand() {
      return this.activeHand;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (LIVING_FLAGS.equals(key)) {
         boolean flag = (this.dataManager.get(LIVING_FLAGS) & 1) > 0;
         Hand hand = (this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (flag && !this.handActive) {
            this.setActiveHand(hand);
         } else if (!flag && this.handActive) {
            this.resetActiveHand();
         }
      }

      if (FLAGS.equals(key) && this.isElytraFlying() && !this.wasFallFlying) {
         this.mc.getSoundHandler().play(new ElytraSound(this));
      }

   }

   public boolean isRidingHorse() {
      Entity entity = this.getRidingEntity();
      return this.isPassenger() && entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   public void openSignEditor(SignTileEntity signTile) {
      this.mc.displayGuiScreen(new EditSignScreen(signTile));
   }

   public void openMinecartCommandBlock(CommandBlockLogic commandBlock) {
      this.mc.displayGuiScreen(new EditMinecartCommandBlockScreen(commandBlock));
   }

   public void openCommandBlock(CommandBlockTileEntity commandBlock) {
      this.mc.displayGuiScreen(new CommandBlockScreen(commandBlock));
   }

   public void openStructureBlock(StructureBlockTileEntity structure) {
      this.mc.displayGuiScreen(new EditStructureScreen(structure));
   }

   public void openJigsaw(JigsawTileEntity p_213826_1_) {
      this.mc.displayGuiScreen(new JigsawScreen(p_213826_1_));
   }

   public void openBook(ItemStack stack, Hand hand) {
      Item item = stack.getItem();
      if (item == Items.WRITABLE_BOOK) {
         this.mc.displayGuiScreen(new EditBookScreen(this, stack, hand));
      }

   }

   /**
    * Called when the entity is dealt a critical hit.
    */
   public void onCriticalHit(Entity entityHit) {
      this.mc.particles.addParticleEmitter(entityHit, ParticleTypes.CRIT);
   }

   public void onEnchantmentCritical(Entity entityHit) {
      this.mc.particles.addParticleEmitter(entityHit, ParticleTypes.ENCHANTED_HIT);
   }

   public boolean isSneaking() {
      return this.movementInput != null && this.movementInput.sneaking;
   }

   public boolean isCrouching() {
      if (!this.abilities.isFlying && !this.isSwimming() && this.isPoseClear(Pose.CROUCHING)) {
         return this.isSneaking() || !this.isSleeping() && !this.isPoseClear(Pose.STANDING);
      } else {
         return false;
      }
   }

   public boolean func_228354_I_() {
      return this.isCrouching() || this.isVisuallySwimming();
   }

   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
         this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
      }

   }

   protected boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      ++this.sprintingTicksLeft;
      if (this.sprintToggleTimer > 0) {
         --this.sprintToggleTimer;
      }

      this.func_213839_ed();
      boolean flag = this.movementInput.jump;
      boolean flag1 = this.movementInput.sneaking;
      boolean flag2 = this.func_223110_ee();
      this.movementInput.func_225607_a_(this.func_228354_I_());
      net.minecraftforge.client.ForgeHooksClient.onInputUpdate(this, this.movementInput);
      this.mc.getTutorial().handleMovement(this.movementInput);
      if (this.isHandActive() && !this.isPassenger()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.sprintToggleTimer = 0;
      }

      boolean flag3 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         flag3 = true;
         this.movementInput.jump = true;
      }

      net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent event = new net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent(this);
      if (!this.noClip && !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
         this.pushOutOfBlocks(this.getPosX() - (double)this.getWidth() * 0.35D, event.getMinY(), this.getPosZ() + (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.getPosX() - (double)this.getWidth() * 0.35D, event.getMinY(), this.getPosZ() - (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.getPosX() + (double)this.getWidth() * 0.35D, event.getMinY(), this.getPosZ() - (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.getPosX() + (double)this.getWidth() * 0.35D, event.getMinY(), this.getPosZ() + (double)this.getWidth() * 0.35D);
      }

      boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
      if ((this.onGround || this.canSwim()) && !flag1 && !flag2 && this.func_223110_ee() && !this.isSprinting() && flag4 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS)) {
         if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.sprintToggleTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.canSwim()) && this.func_223110_ee() && flag4 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag5 = !this.movementInput.func_223135_b() || !flag4;
         boolean flag6 = flag5 || this.collidedHorizontally || this.isInWater() && !this.canSwim();
         if (this.isSwimming()) {
            if (!this.onGround && !this.movementInput.sneaking && flag5 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      boolean flag7 = false;
      if (this.abilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.abilities.isFlying) {
               this.abilities.isFlying = true;
               flag7 = true;
               this.sendPlayerAbilities();
            }
         } else if (!flag && this.movementInput.jump && !flag3) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else if (!this.isSwimming()) {
               this.abilities.isFlying = !this.abilities.isFlying;
               flag7 = true;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.movementInput.jump && !flag7 && !flag && !this.abilities.isFlying && !this.isPassenger() && !this.isOnLadder()) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemstack) && this.tryToStartFallFlying()) {
            this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isElytraFlying();
      if (this.isInWater() && this.movementInput.sneaking) {
         this.handleFluidSneak();
      }

      if (this.areEyesInFluid(FluidTags.WATER)) {
         int i = this.isSpectator() ? 10 : 1;
         this.counterInWater = MathHelper.clamp(this.counterInWater + i, 0, 600);
      } else if (this.counterInWater > 0) {
         this.areEyesInFluid(FluidTags.WATER);
         this.counterInWater = MathHelper.clamp(this.counterInWater - 10, 0, 600);
      }

      if (this.abilities.isFlying && this.isCurrentViewEntity()) {
         int j = 0;
         if (this.movementInput.sneaking) {
            --j;
         }

         if (this.movementInput.jump) {
            ++j;
         }

         if (j != 0) {
            this.setMotion(this.getMotion().add(0.0D, (double)((float)j * this.abilities.getFlySpeed() * 3.0F), 0.0D));
         }
      }

      if (this.isRidingHorse()) {
         IJumpingMount ijumpingmount = (IJumpingMount)this.getRidingEntity();
         if (this.horseJumpPowerCounter < 0) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (flag && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            ijumpingmount.setJumpPower(MathHelper.floor(this.getHorseJumpPower() * 100.0F));
            this.sendHorseJump();
         } else if (!flag && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (flag) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.livingTick();
      if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.abilities.isFlying = false;
         this.sendPlayerAbilities();
      }

   }

   private void func_213839_ed() {
      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.isPauseScreen()) {
            if (this.mc.currentScreen instanceof ContainerScreen) {
               this.closeScreen();
            }

            this.mc.displayGuiScreen((Screen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.BLOCK_PORTAL_TRIGGER, this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(Effects.NAUSEA) && this.getActivePotionEffect(Effects.NAUSEA).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      this.decrementTimeUntilPortal();
   }

   /**
    * Handles updating while riding another entity
    */
   public void updateRidden() {
      super.updateRidden();
      this.rowingBoat = false;
      if (this.getRidingEntity() instanceof BoatEntity) {
         BoatEntity boatentity = (BoatEntity)this.getRidingEntity();
         boatentity.updateInputs(this.movementInput.leftKeyDown, this.movementInput.rightKeyDown, this.movementInput.forwardKeyDown, this.movementInput.backKeyDown);
         this.rowingBoat |= this.movementInput.leftKeyDown || this.movementInput.rightKeyDown || this.movementInput.forwardKeyDown || this.movementInput.backKeyDown;
      }

   }

   public boolean isRowingBoat() {
      return this.rowingBoat;
   }

   /**
    * Removes the given potion effect from the active potion map and returns it. Does not call cleanup callbacks for the
    * end of the potion effect.
    */
   @Nullable
   public EffectInstance removeActivePotionEffect(@Nullable Effect potioneffectin) {
      if (potioneffectin == Effects.NAUSEA) {
         this.prevTimeInPortal = 0.0F;
         this.timeInPortal = 0.0F;
      }

      return super.removeActivePotionEffect(potioneffectin);
   }

   public void move(MoverType typeIn, Vec3d pos) {
      double d0 = this.getPosX();
      double d1 = this.getPosZ();
      super.move(typeIn, pos);
      this.updateAutoJump((float)(this.getPosX() - d0), (float)(this.getPosZ() - d1));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float p_189810_1_, float p_189810_2_) {
      if (this.func_228356_eG_()) {
         Vec3d vec3d = this.getPositionVec();
         Vec3d vec3d1 = vec3d.add((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         Vec3d vec3d2 = new Vec3d((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         float f = this.getAIMoveSpeed();
         float f1 = (float)vec3d2.lengthSquared();
         if (f1 <= 0.001F) {
            Vec2f vec2f = this.movementInput.getMoveVector();
            float f2 = f * vec2f.x;
            float f3 = f * vec2f.y;
            float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
            float f5 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
            vec3d2 = new Vec3d((double)(f2 * f5 - f3 * f4), vec3d2.y, (double)(f3 * f5 + f2 * f4));
            f1 = (float)vec3d2.lengthSquared();
            if (f1 <= 0.001F) {
               return;
            }
         }

         float f12 = MathHelper.fastInvSqrt(f1);
         Vec3d vec3d12 = vec3d2.scale((double)f12);
         Vec3d vec3d13 = this.getForward();
         float f13 = (float)(vec3d13.x * vec3d12.x + vec3d13.z * vec3d12.z);
         if (!(f13 < -0.15F)) {
            ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
            BlockPos blockpos = new BlockPos(this.getPosX(), this.getBoundingBox().maxY, this.getPosZ());
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
               blockpos = blockpos.up();
               BlockState blockstate1 = this.world.getBlockState(blockpos);
               if (blockstate1.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                  float f6 = 7.0F;
                  float f7 = 1.2F;
                  if (this.isPotionActive(Effects.JUMP_BOOST)) {
                     f7 += (float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
                  }

                  float f8 = Math.max(f * 7.0F, 1.0F / f12);
                  Vec3d vec3d4 = vec3d1.add(vec3d12.scale((double)f8));
                  float f9 = this.getWidth();
                  float f10 = this.getHeight();
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vec3d, vec3d4.add(0.0D, (double)f10, 0.0D))).grow((double)f9, 0.0D, (double)f9);
                  Vec3d lvt_19_1_ = vec3d.add(0.0D, (double)0.51F, 0.0D);
                  vec3d4 = vec3d4.add(0.0D, (double)0.51F, 0.0D);
                  Vec3d vec3d5 = vec3d12.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
                  Vec3d vec3d6 = vec3d5.scale((double)(f9 * 0.5F));
                  Vec3d vec3d7 = lvt_19_1_.subtract(vec3d6);
                  Vec3d vec3d8 = vec3d4.subtract(vec3d6);
                  Vec3d vec3d9 = lvt_19_1_.add(vec3d6);
                  Vec3d vec3d10 = vec3d4.add(vec3d6);
                  Iterator<AxisAlignedBB> iterator = this.world.getCollisionShapes(this, axisalignedbb, Collections.emptySet()).flatMap((p_212329_0_) -> {
                     return p_212329_0_.toBoundingBoxList().stream();
                  }).iterator();
                  float f11 = Float.MIN_VALUE;

                  while(iterator.hasNext()) {
                     AxisAlignedBB axisalignedbb1 = iterator.next();
                     if (axisalignedbb1.intersects(vec3d7, vec3d8) || axisalignedbb1.intersects(vec3d9, vec3d10)) {
                        f11 = (float)axisalignedbb1.maxY;
                        Vec3d vec3d11 = axisalignedbb1.getCenter();
                        BlockPos blockpos1 = new BlockPos(vec3d11);

                        for(int i = 1; (float)i < f7; ++i) {
                           BlockPos blockpos2 = blockpos1.up(i);
                           BlockState blockstate2 = this.world.getBlockState(blockpos2);
                           VoxelShape voxelshape;
                           if (!(voxelshape = blockstate2.getCollisionShape(this.world, blockpos2, iselectioncontext)).isEmpty()) {
                              f11 = (float)voxelshape.getEnd(Direction.Axis.Y) + (float)blockpos2.getY();
                              if ((double)f11 - this.getPosY() > (double)f7) {
                                 return;
                              }
                           }

                           if (i > 1) {
                              blockpos = blockpos.up();
                              BlockState blockstate3 = this.world.getBlockState(blockpos);
                              if (!blockstate3.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                                 return;
                              }
                           }
                        }
                        break;
                     }
                  }

                  if (f11 != Float.MIN_VALUE) {
                     float f14 = (float)((double)f11 - this.getPosY());
                     if (!(f14 <= 0.5F) && !(f14 > f7)) {
                        this.autoJumpTime = 1;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean func_228356_eG_() {
      return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.func_228357_eH_() && (double)this.getJumpFactor() >= 1.0D;
   }

   private boolean func_228357_eH_() {
      Vec2f vec2f = this.movementInput.getMoveVector();
      return vec2f.x != 0.0F || vec2f.y != 0.0F;
   }

   private boolean func_223110_ee() {
      double d0 = 0.8D;
      return this.canSwim() ? this.movementInput.func_223135_b() : (double)this.movementInput.moveForward >= 0.8D;
   }

   public float getWaterBrightness() {
      if (!this.areEyesInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float f = 600.0F;
         float f1 = 100.0F;
         if ((float)this.counterInWater >= 600.0F) {
            return 1.0F;
         } else {
            float f2 = MathHelper.clamp((float)this.counterInWater / 100.0F, 0.0F, 1.0F);
            float f3 = (float)this.counterInWater < 100.0F ? 0.0F : MathHelper.clamp(((float)this.counterInWater - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
         }
      }
   }

   public boolean canSwim() {
      return this.eyesInWaterPlayer;
   }

   protected boolean updateEyesInWaterPlayer() {
      boolean flag = this.eyesInWaterPlayer;
      boolean flag1 = super.updateEyesInWaterPlayer();
      if (this.isSpectator()) {
         return this.eyesInWaterPlayer;
      } else {
         if (!flag && flag1) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.mc.getSoundHandler().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (flag && !flag1) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.eyesInWaterPlayer;
      }
   }

   public void updateSyncFields(ClientPlayerEntity old) {
      this.lastReportedPosX = old.lastReportedPosX;
      this.lastReportedPosY = old.lastReportedPosY;
      this.lastReportedPosZ = old.lastReportedPosZ;
      this.lastReportedYaw = old.lastReportedYaw;
      this.lastReportedPitch = old.lastReportedPitch;
      this.prevOnGround = old.prevOnGround;
      this.field_228351_cj_ = old.field_228351_cj_;
      this.serverSprintState = old.serverSprintState;
      this.positionUpdateTicks = old.positionUpdateTicks;
   }
}