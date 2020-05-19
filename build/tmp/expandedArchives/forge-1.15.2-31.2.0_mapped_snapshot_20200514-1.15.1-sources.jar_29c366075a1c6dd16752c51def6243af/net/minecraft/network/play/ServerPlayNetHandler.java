package net.minecraft.network.play;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetHandler implements IServerPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   public final NetworkManager netManager;
   private final MinecraftServer server;
   public ServerPlayerEntity player;
   private int networkTickCount;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveKey;
   /** Incremented by 20 each time a user sends a chat message, decreased by one every tick. Non-ops kicked when over 200 */
   private int chatSpamThresholdCount;
   private int itemDropThreshold;
   private final Int2ShortMap pendingTransactions = new Int2ShortOpenHashMap();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lowestRiddenEnt;
   private double lowestRiddenX;
   private double lowestRiddenY;
   private double lowestRiddenZ;
   private double lowestRiddenX1;
   private double lowestRiddenY1;
   private double lowestRiddenZ1;
   private Vec3d targetPos;
   private int teleportId;
   private int lastPositionUpdate;
   private boolean floating;
   /**
    * Used to keep track of how the player is floating while gamerules should prevent that. Surpassing 80 ticks means
    * kick
    */
   private int floatingTickCount;
   private boolean vehicleFloating;
   private int vehicleFloatingTickCount;
   private int movePacketCounter;
   private int lastMovePacketCounter;

   public ServerPlayNetHandler(MinecraftServer server, NetworkManager networkManagerIn, ServerPlayerEntity playerIn) {
      this.server = server;
      this.netManager = networkManagerIn;
      networkManagerIn.setNetHandler(this);
      this.player = playerIn;
      playerIn.connection = this;
   }

   public void tick() {
      this.captureCurrentPosition();
      this.player.prevPosX = this.player.getPosX();
      this.player.prevPosY = this.player.getPosY();
      this.player.prevPosZ = this.player.getPosZ();
      this.player.playerTick();
      this.player.setPositionAndRotation(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.rotationYaw, this.player.rotationPitch);
      ++this.networkTickCount;
      this.lastMovePacketCounter = this.movePacketCounter;
      if (this.floating) {
         if (++this.floatingTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", (Object)this.player.getName().getString());
            this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying"));
            return;
         }
      } else {
         this.floating = false;
         this.floatingTickCount = 0;
      }

      this.lowestRiddenEnt = this.player.getLowestRidingEntity();
      if (this.lowestRiddenEnt != this.player && this.lowestRiddenEnt.getControllingPassenger() == this.player) {
         this.lowestRiddenX = this.lowestRiddenEnt.getPosX();
         this.lowestRiddenY = this.lowestRiddenEnt.getPosY();
         this.lowestRiddenZ = this.lowestRiddenEnt.getPosZ();
         this.lowestRiddenX1 = this.lowestRiddenEnt.getPosX();
         this.lowestRiddenY1 = this.lowestRiddenEnt.getPosY();
         this.lowestRiddenZ1 = this.lowestRiddenEnt.getPosZ();
         if (this.vehicleFloating && this.player.getLowestRidingEntity().getControllingPassenger() == this.player) {
            if (++this.vehicleFloatingTickCount > 80) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", (Object)this.player.getName().getString());
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying"));
               return;
            }
         } else {
            this.vehicleFloating = false;
            this.vehicleFloatingTickCount = 0;
         }
      } else {
         this.lowestRiddenEnt = null;
         this.vehicleFloating = false;
         this.vehicleFloatingTickCount = 0;
      }

      this.server.getProfiler().startSection("keepAlive");
      long i = Util.milliTime();
      if (i - this.keepAliveTime >= 15000L) {
         if (this.keepAlivePending) {
            this.disconnect(new TranslationTextComponent("disconnect.timeout"));
         } else {
            this.keepAlivePending = true;
            this.keepAliveTime = i;
            this.keepAliveKey = i;
            this.sendPacket(new SKeepAlivePacket(this.keepAliveKey));
         }
      }

      this.server.getProfiler().endSection();
      if (this.chatSpamThresholdCount > 0) {
         --this.chatSpamThresholdCount;
      }

      if (this.itemDropThreshold > 0) {
         --this.itemDropThreshold;
      }

      if (this.player.getLastActiveTime() > 0L && this.server.getMaxPlayerIdleMinutes() > 0 && Util.milliTime() - this.player.getLastActiveTime() > (long)(this.server.getMaxPlayerIdleMinutes() * 1000 * 60)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.idling"));
      }

   }

   public void captureCurrentPosition() {
      this.firstGoodX = this.player.getPosX();
      this.firstGoodY = this.player.getPosY();
      this.firstGoodZ = this.player.getPosZ();
      this.lastGoodX = this.player.getPosX();
      this.lastGoodY = this.player.getPosY();
      this.lastGoodZ = this.player.getPosZ();
   }

   /**
    * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
    */
   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   private boolean func_217264_d() {
      return this.server.isServerOwner(this.player.getGameProfile());
   }

   /**
    * Disconnect the player with a specified reason
    */
   public void disconnect(ITextComponent textComponent) {
      this.netManager.sendPacket(new SDisconnectPacket(textComponent), (p_210161_2_) -> {
         this.netManager.closeChannel(textComponent);
      });
      this.netManager.disableAutoRead();
      this.server.runImmediately(this.netManager::handleDisconnection);
   }

   /**
    * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
    * flying/sprinting
    */
   public void processInput(CInputPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
   }

   private static boolean isMovePlayerPacketInvalid(CPlayerPacket packetIn) {
      if (Doubles.isFinite(packetIn.getX(0.0D)) && Doubles.isFinite(packetIn.getY(0.0D)) && Doubles.isFinite(packetIn.getZ(0.0D)) && Floats.isFinite(packetIn.getPitch(0.0F)) && Floats.isFinite(packetIn.getYaw(0.0F))) {
         return Math.abs(packetIn.getX(0.0D)) > 3.0E7D || Math.abs(packetIn.getY(0.0D)) > 3.0E7D || Math.abs(packetIn.getZ(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean isMoveVehiclePacketInvalid(CMoveVehiclePacket packetIn) {
      return !Doubles.isFinite(packetIn.getX()) || !Doubles.isFinite(packetIn.getY()) || !Doubles.isFinite(packetIn.getZ()) || !Floats.isFinite(packetIn.getPitch()) || !Floats.isFinite(packetIn.getYaw());
   }

   public void processVehicleMove(CMoveVehiclePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (isMoveVehiclePacketInvalid(packetIn)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity entity = this.player.getLowestRidingEntity();
         if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lowestRiddenEnt) {
            ServerWorld serverworld = this.player.getServerWorld();
            double d0 = entity.getPosX();
            double d1 = entity.getPosY();
            double d2 = entity.getPosZ();
            double d3 = packetIn.getX();
            double d4 = packetIn.getY();
            double d5 = packetIn.getZ();
            float f = packetIn.getYaw();
            float f1 = packetIn.getPitch();
            double d6 = d3 - this.lowestRiddenX;
            double d7 = d4 - this.lowestRiddenY;
            double d8 = d5 - this.lowestRiddenZ;
            double d9 = entity.getMotion().lengthSquared();
            double d10 = d6 * d6 + d7 * d7 + d8 * d8;
            if (d10 - d9 > 100.0D && !this.func_217264_d()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
               this.netManager.sendPacket(new SMoveVehiclePacket(entity));
               return;
            }

            boolean flag = serverworld.hasNoCollisions(entity, entity.getBoundingBox().shrink(0.0625D));
            d6 = d3 - this.lowestRiddenX1;
            d7 = d4 - this.lowestRiddenY1 - 1.0E-6D;
            d8 = d5 - this.lowestRiddenZ1;
            entity.move(MoverType.PLAYER, new Vec3d(d6, d7, d8));
            d6 = d3 - entity.getPosX();
            d7 = d4 - entity.getPosY();
            if (d7 > -0.5D || d7 < 0.5D) {
               d7 = 0.0D;
            }

            d8 = d5 - entity.getPosZ();
            d10 = d6 * d6 + d7 * d7 + d8 * d8;
            boolean flag1 = false;
            if (d10 > 0.0625D) {
               flag1 = true;
               LOGGER.warn("{} moved wrongly!", (Object)entity.getName().getString());
            }

            entity.setPositionAndRotation(d3, d4, d5, f, f1);
            this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch); // Forge - Resync player position on vehicle moving
            boolean flag2 = serverworld.hasNoCollisions(entity, entity.getBoundingBox().shrink(0.0625D));
            if (flag && (flag1 || !flag2)) {
               entity.setPositionAndRotation(d0, d1, d2, f, f1);
               this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch); // Forge - Resync player position on vehicle moving
               this.netManager.sendPacket(new SMoveVehiclePacket(entity));
               return;
            }

            this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
            this.player.addMovementStat(this.player.getPosX() - d0, this.player.getPosY() - d1, this.player.getPosZ() - d2);
            this.vehicleFloating = d7 >= -0.03125D && !this.server.isFlightAllowed() && !serverworld.checkBlockCollision(entity.getBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
            this.lowestRiddenX1 = entity.getPosX();
            this.lowestRiddenY1 = entity.getPosY();
            this.lowestRiddenZ1 = entity.getPosZ();
         }

      }
   }

   public void processConfirmTeleport(CConfirmTeleportPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (packetIn.getTeleportId() == this.teleportId) {
         this.player.setPositionAndRotation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
         this.lastGoodX = this.targetPos.x;
         this.lastGoodY = this.targetPos.y;
         this.lastGoodZ = this.targetPos.z;
         if (this.player.isInvulnerableDimensionChange()) {
            this.player.clearInvulnerableDimensionChange();
         }

         this.targetPos = null;
      }

   }

   public void handleRecipeBookUpdate(CRecipeInfoPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (packetIn.getPurpose() == CRecipeInfoPacket.Purpose.SHOWN) {
         this.server.getRecipeManager().getRecipe(packetIn.getRecipeId()).ifPresent(this.player.getRecipeBook()::markSeen);
      } else if (packetIn.getPurpose() == CRecipeInfoPacket.Purpose.SETTINGS) {
         this.player.getRecipeBook().setGuiOpen(packetIn.isGuiOpen());
         this.player.getRecipeBook().setFilteringCraftable(packetIn.isFilteringCraftable());
         this.player.getRecipeBook().setFurnaceGuiOpen(packetIn.isFurnaceGuiOpen());
         this.player.getRecipeBook().setFurnaceFilteringCraftable(packetIn.isFurnaceFilteringCraftable());
         this.player.getRecipeBook().func_216755_e(packetIn.func_218779_h());
         this.player.getRecipeBook().func_216756_f(packetIn.func_218778_i());
         this.player.getRecipeBook().func_216757_g(packetIn.func_218780_j());
         this.player.getRecipeBook().func_216760_h(packetIn.func_218781_k());
      }

   }

   public void handleSeenAdvancements(CSeenAdvancementsPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (packetIn.getAction() == CSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation resourcelocation = packetIn.getTab();
         Advancement advancement = this.server.getAdvancementManager().getAdvancement(resourcelocation);
         if (advancement != null) {
            this.player.getAdvancements().setSelectedTab(advancement);
         }
      }

   }

   /**
    * This method is only called for manual tab-completion (the {@link
    * net.minecraft.command.arguments.SuggestionProviders#ASK_SERVER minecraft:ask_server} suggestion provider).
    */
   public void processTabComplete(CTabCompletePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      StringReader stringreader = new StringReader(packetIn.getCommand());
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      ParseResults<CommandSource> parseresults = this.server.getCommandManager().getDispatcher().parse(stringreader, this.player.getCommandSource());
      this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseresults).thenAccept((p_195519_2_) -> {
         this.netManager.sendPacket(new STabCompletePacket(packetIn.getTransactionId(), p_195519_2_));
      });
   }

   public void processUpdateCommandBlock(CUpdateCommandBlockPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled"));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed"));
      } else {
         CommandBlockLogic commandblocklogic = null;
         CommandBlockTileEntity commandblocktileentity = null;
         BlockPos blockpos = packetIn.getPos();
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof CommandBlockTileEntity) {
            commandblocktileentity = (CommandBlockTileEntity)tileentity;
            commandblocklogic = commandblocktileentity.getCommandBlockLogic();
         }

         String s = packetIn.getCommand();
         boolean flag = packetIn.shouldTrackOutput();
         if (commandblocklogic != null) {
            CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
            Direction direction = this.player.world.getBlockState(blockpos).get(CommandBlockBlock.FACING);
            switch(packetIn.getMode()) {
            case SEQUENCE:
               BlockState blockstate1 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, blockstate1.with(CommandBlockBlock.FACING, direction).with(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(packetIn.isConditional())), 2);
               break;
            case AUTO:
               BlockState blockstate = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, blockstate.with(CommandBlockBlock.FACING, direction).with(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(packetIn.isConditional())), 2);
               break;
            case REDSTONE:
            default:
               BlockState blockstate2 = Blocks.COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, blockstate2.with(CommandBlockBlock.FACING, direction).with(CommandBlockBlock.CONDITIONAL, Boolean.valueOf(packetIn.isConditional())), 2);
            }

            tileentity.validate();
            this.player.world.setTileEntity(blockpos, tileentity);
            commandblocklogic.setCommand(s);
            commandblocklogic.setTrackOutput(flag);
            if (!flag) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocktileentity.setAuto(packetIn.isAuto());
            if (commandblocktileentity$mode != packetIn.getMode()) {
               commandblocktileentity.func_226987_h_();
            }

            commandblocklogic.updateCommand();
            if (!StringUtils.isNullOrEmpty(s)) {
               this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", s));
            }
         }

      }
   }

   public void processUpdateCommandMinecart(CUpdateMinecartCommandBlockPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled"));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed"));
      } else {
         CommandBlockLogic commandblocklogic = packetIn.getCommandBlock(this.player.world);
         if (commandblocklogic != null) {
            commandblocklogic.setCommand(packetIn.getCommand());
            commandblocklogic.setTrackOutput(packetIn.shouldTrackOutput());
            if (!packetIn.shouldTrackOutput()) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocklogic.updateCommand();
            this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", packetIn.getCommand()));
         }

      }
   }

   public void processPickItem(CPickItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.inventory.pickItem(packetIn.getPickIndex());
      this.player.connection.sendPacket(new SSetSlotPacket(-2, this.player.inventory.currentItem, this.player.inventory.getStackInSlot(this.player.inventory.currentItem)));
      this.player.connection.sendPacket(new SSetSlotPacket(-2, packetIn.getPickIndex(), this.player.inventory.getStackInSlot(packetIn.getPickIndex())));
      this.player.connection.sendPacket(new SHeldItemChangePacket(this.player.inventory.currentItem));
   }

   public void processRenameItem(CRenameItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.openContainer instanceof RepairContainer) {
         RepairContainer repaircontainer = (RepairContainer)this.player.openContainer;
         String s = SharedConstants.filterAllowedCharacters(packetIn.getName());
         if (s.length() <= 35) {
            repaircontainer.updateItemName(s);
         }
      }

   }

   public void processUpdateBeacon(CUpdateBeaconPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.openContainer instanceof BeaconContainer) {
         ((BeaconContainer)this.player.openContainer).func_216966_c(packetIn.getPrimaryEffect(), packetIn.getSecondaryEffect());
      }

   }

   public void processUpdateStructureBlock(CUpdateStructureBlockPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.canUseCommandBlock()) {
         BlockPos blockpos = packetIn.getPos();
         BlockState blockstate = this.player.world.getBlockState(blockpos);
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof StructureBlockTileEntity) {
            StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)tileentity;
            structureblocktileentity.setMode(packetIn.getMode());
            structureblocktileentity.setName(packetIn.getName());
            structureblocktileentity.setPosition(packetIn.getPosition());
            structureblocktileentity.setSize(packetIn.getSize());
            structureblocktileentity.setMirror(packetIn.getMirror());
            structureblocktileentity.setRotation(packetIn.getRotation());
            structureblocktileentity.setMetadata(packetIn.getMetadata());
            structureblocktileentity.setIgnoresEntities(packetIn.shouldIgnoreEntities());
            structureblocktileentity.setShowAir(packetIn.shouldShowAir());
            structureblocktileentity.setShowBoundingBox(packetIn.shouldShowBoundingBox());
            structureblocktileentity.setIntegrity(packetIn.getIntegrity());
            structureblocktileentity.setSeed(packetIn.getSeed());
            if (structureblocktileentity.hasName()) {
               String s = structureblocktileentity.getName();
               if (packetIn.func_210384_b() == StructureBlockTileEntity.UpdateCommand.SAVE_AREA) {
                  if (structureblocktileentity.save()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.save_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.save_failure", s), false);
                  }
               } else if (packetIn.func_210384_b() == StructureBlockTileEntity.UpdateCommand.LOAD_AREA) {
                  if (!structureblocktileentity.isStructureLoadable()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_not_found", s), false);
                  } else if (structureblocktileentity.load()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_prepare", s), false);
                  }
               } else if (packetIn.func_210384_b() == StructureBlockTileEntity.UpdateCommand.SCAN_AREA) {
                  if (structureblocktileentity.detectSize()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.size_success", s), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.sendStatusMessage(new TranslationTextComponent("structure_block.invalid_structure_name", packetIn.getName()), false);
            }

            structureblocktileentity.markDirty();
            this.player.world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
         }

      }
   }

   public void func_217262_a(CUpdateJigsawBlockPacket p_217262_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217262_1_, this, this.player.getServerWorld());
      if (this.player.canUseCommandBlock()) {
         BlockPos blockpos = p_217262_1_.func_218789_b();
         BlockState blockstate = this.player.world.getBlockState(blockpos);
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof JigsawTileEntity) {
            JigsawTileEntity jigsawtileentity = (JigsawTileEntity)tileentity;
            jigsawtileentity.setAttachmentType(p_217262_1_.func_218787_d());
            jigsawtileentity.setTargetPool(p_217262_1_.func_218786_c());
            jigsawtileentity.setFinalState(p_217262_1_.func_218788_e());
            jigsawtileentity.markDirty();
            this.player.world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
         }

      }
   }

   public void processSelectTrade(CSelectTradePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      int i = packetIn.func_210353_a();
      Container container = this.player.openContainer;
      if (container instanceof MerchantContainer) {
         MerchantContainer merchantcontainer = (MerchantContainer)container;
         merchantcontainer.setCurrentRecipeIndex(i);
         merchantcontainer.func_217046_g(i);
      }

   }

   public void processEditBook(CEditBookPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      ItemStack itemstack = packetIn.getStack();
      if (!itemstack.isEmpty()) {
         if (WritableBookItem.isNBTValid(itemstack.getTag())) {
            ItemStack itemstack1 = this.player.getHeldItem(packetIn.getHand());
            if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
               if (packetIn.shouldUpdateAll()) {
                  ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK);
                  CompoundNBT compoundnbt = itemstack1.getTag();
                  if (compoundnbt != null) {
                     itemstack2.setTag(compoundnbt.copy());
                  }

                  itemstack2.setTagInfo("author", StringNBT.valueOf(this.player.getName().getString()));
                  itemstack2.setTagInfo("title", StringNBT.valueOf(itemstack.getTag().getString("title")));
                  ListNBT listnbt = itemstack.getTag().getList("pages", 8);

                  for(int i = 0; i < listnbt.size(); ++i) {
                     String s = listnbt.getString(i);
                     ITextComponent itextcomponent = new StringTextComponent(s);
                     s = ITextComponent.Serializer.toJson(itextcomponent);
                     listnbt.set(i, (INBT)StringNBT.valueOf(s));
                  }

                  itemstack2.setTagInfo("pages", listnbt);
                  this.player.setHeldItem(packetIn.getHand(), itemstack2);
               } else {
                  itemstack1.setTagInfo("pages", itemstack.getTag().getList("pages", 8));
               }
            }

         }
      }
   }

   public void processNBTQueryEntity(CQueryEntityNBTPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         Entity entity = this.player.getServerWorld().getEntityByID(packetIn.getEntityId());
         if (entity != null) {
            CompoundNBT compoundnbt = entity.writeWithoutTypeId(new CompoundNBT());
            this.player.connection.sendPacket(new SQueryNBTResponsePacket(packetIn.getTransactionId(), compoundnbt));
         }

      }
   }

   public void processNBTQueryBlockEntity(CQueryTileEntityNBTPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         TileEntity tileentity = this.player.getServerWorld().getTileEntity(packetIn.getPosition());
         CompoundNBT compoundnbt = tileentity != null ? tileentity.write(new CompoundNBT()) : null;
         this.player.connection.sendPacket(new SQueryNBTResponsePacket(packetIn.getTransactionId(), compoundnbt));
      }
   }

   /**
    * Processes clients perspective on player positioning and/or orientation
    */
   public void processPlayer(CPlayerPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (isMovePlayerPacketInvalid(packetIn)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_player_movement"));
      } else {
         ServerWorld serverworld = this.server.getWorld(this.player.dimension);
         if (!this.player.queuedEndExit) {
            if (this.networkTickCount == 0) {
               this.captureCurrentPosition();
            }

            if (this.targetPos != null) {
               if (this.networkTickCount - this.lastPositionUpdate > 20) {
                  this.lastPositionUpdate = this.networkTickCount;
                  this.setPlayerLocation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
               }

            } else {
               this.lastPositionUpdate = this.networkTickCount;
               if (this.player.isPassenger()) {
                  this.player.setPositionAndRotation(this.player.getPosX(), this.player.getPosY(), this.player.getPosZ(), packetIn.getYaw(this.player.rotationYaw), packetIn.getPitch(this.player.rotationPitch));
                  this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
               } else {
                  double d0 = this.player.getPosX();
                  double d1 = this.player.getPosY();
                  double d2 = this.player.getPosZ();
                  double d3 = this.player.getPosY();
                  double d4 = packetIn.getX(this.player.getPosX());
                  double d5 = packetIn.getY(this.player.getPosY());
                  double d6 = packetIn.getZ(this.player.getPosZ());
                  float f = packetIn.getYaw(this.player.rotationYaw);
                  float f1 = packetIn.getPitch(this.player.rotationPitch);
                  double d7 = d4 - this.firstGoodX;
                  double d8 = d5 - this.firstGoodY;
                  double d9 = d6 - this.firstGoodZ;
                  double d10 = this.player.getMotion().lengthSquared();
                  double d11 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (this.player.isSleeping()) {
                     if (d11 > 1.0D) {
                        this.setPlayerLocation(this.player.getPosX(), this.player.getPosY(), this.player.getPosZ(), packetIn.getYaw(this.player.rotationYaw), packetIn.getPitch(this.player.rotationPitch));
                     }

                  } else {
                     ++this.movePacketCounter;
                     int i = this.movePacketCounter - this.lastMovePacketCounter;
                     if (i > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                        i = 1;
                     }

                     if (!this.player.isInvulnerableDimensionChange() && (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isElytraFlying())) {
                        float f2 = this.player.isElytraFlying() ? 300.0F : 100.0F;
                        if (d11 - d10 > (double)(f2 * (float)i) && !this.func_217264_d()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                           this.setPlayerLocation(this.player.getPosX(), this.player.getPosY(), this.player.getPosZ(), this.player.rotationYaw, this.player.rotationPitch);
                           return;
                        }
                     }

                     boolean flag2 = this.isPlayerNotInBlock(serverworld);
                     d7 = d4 - this.lastGoodX;
                     d8 = d5 - this.lastGoodY;
                     d9 = d6 - this.lastGoodZ;
                     if (d8 > 0.0D) {
                        this.player.fallDistance = 0.0F;
                     }

                     if (this.player.onGround && !packetIn.isOnGround() && d8 > 0.0D) {
                        this.player.jump();
                     }

                     this.player.move(MoverType.PLAYER, new Vec3d(d7, d8, d9));
                     this.player.onGround = packetIn.isOnGround();
                     d7 = d4 - this.player.getPosX();
                     d8 = d5 - this.player.getPosY();
                     if (d8 > -0.5D || d8 < 0.5D) {
                        d8 = 0.0D;
                     }

                     d9 = d6 - this.player.getPosZ();
                     d11 = d7 * d7 + d8 * d8 + d9 * d9;
                     boolean flag = false;
                     if (!this.player.isInvulnerableDimensionChange() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameType() != GameType.SPECTATOR) {
                        flag = true;
                        LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
                     }

                     this.player.setPositionAndRotation(d4, d5, d6, f, f1);
                     this.player.addMovementStat(this.player.getPosX() - d0, this.player.getPosY() - d1, this.player.getPosZ() - d2);
                     if (!this.player.noClip && !this.player.isSleeping()) {
                        boolean flag1 = this.isPlayerNotInBlock(serverworld);
                        if (flag2 && (flag || !flag1)) {
                           this.setPlayerLocation(d0, d1, d2, f, f1);
                           return;
                        }
                     }

                     this.floating = d8 >= -0.03125D && this.player.interactionManager.getGameType() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.allowFlying && !this.player.isPotionActive(Effects.LEVITATION) && !this.player.isElytraFlying() && !serverworld.checkBlockCollision(this.player.getBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
                     this.player.onGround = packetIn.isOnGround();
                     this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
                     this.player.handleFalling(this.player.getPosY() - d3, packetIn.isOnGround());
                     this.lastGoodX = this.player.getPosX();
                     this.lastGoodY = this.player.getPosY();
                     this.lastGoodZ = this.player.getPosZ();
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerNotInBlock(IWorldReader worldIn) {
      return worldIn.hasNoCollisions(this.player, this.player.getBoundingBox().shrink((double)1.0E-5F));
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
      this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
   }

   /**
    * Teleports the player position to the (relative) values specified, and syncs to the client
    */
   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<SPlayerPositionLookPacket.Flags> relativeSet) {
      double d0 = relativeSet.contains(SPlayerPositionLookPacket.Flags.X) ? this.player.getPosX() : 0.0D;
      double d1 = relativeSet.contains(SPlayerPositionLookPacket.Flags.Y) ? this.player.getPosY() : 0.0D;
      double d2 = relativeSet.contains(SPlayerPositionLookPacket.Flags.Z) ? this.player.getPosZ() : 0.0D;
      float f = relativeSet.contains(SPlayerPositionLookPacket.Flags.Y_ROT) ? this.player.rotationYaw : 0.0F;
      float f1 = relativeSet.contains(SPlayerPositionLookPacket.Flags.X_ROT) ? this.player.rotationPitch : 0.0F;
      this.targetPos = new Vec3d(x, y, z);
      if (++this.teleportId == Integer.MAX_VALUE) {
         this.teleportId = 0;
      }

      this.lastPositionUpdate = this.networkTickCount;
      this.player.setPositionAndRotation(x, y, z, yaw, pitch);
      this.player.connection.sendPacket(new SPlayerPositionLookPacket(x - d0, y - d1, z - d2, yaw - f, pitch - f1, relativeSet, this.teleportId));
   }

   /**
    * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items
    */
   public void processPlayerDigging(CPlayerDiggingPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      BlockPos blockpos = packetIn.getPosition();
      this.player.markPlayerActive();
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = packetIn.getAction();
      switch(cplayerdiggingpacket$action) {
      case SWAP_HELD_ITEMS:
         if (!this.player.isSpectator()) {
            ItemStack itemstack = this.player.getHeldItem(Hand.OFF_HAND);
            this.player.setHeldItem(Hand.OFF_HAND, this.player.getHeldItem(Hand.MAIN_HAND));
            this.player.setHeldItem(Hand.MAIN_HAND, itemstack);
         }

         return;
      case DROP_ITEM:
         if (!this.player.isSpectator()) {
            this.player.drop(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if (!this.player.isSpectator()) {
            this.player.drop(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.player.stopActiveHand();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         this.player.interactionManager.func_225416_a(blockpos, cplayerdiggingpacket$action, packetIn.getFacing(), this.server.getBuildLimit());
         return;
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Hand hand = packetIn.getHand();
      ItemStack itemstack = this.player.getHeldItem(hand);
      BlockRayTraceResult blockraytraceresult = packetIn.func_218794_c();
      BlockPos blockpos = blockraytraceresult.getPos();
      Direction direction = blockraytraceresult.getFace();
      this.player.markPlayerActive();
      if (blockpos.getY() < this.server.getBuildLimit() - 1 || direction != Direction.UP && blockpos.getY() < this.server.getBuildLimit()) {
         double dist = player.getAttribute(net.minecraft.entity.player.PlayerEntity.REACH_DISTANCE).getValue() + 3;
         dist *= dist;
         if (this.targetPos == null && this.player.getDistanceSq((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) < dist && serverworld.isBlockModifiable(this.player, blockpos)) {
            ActionResultType actionresulttype = this.player.interactionManager.func_219441_a(this.player, serverworld, itemstack, hand, blockraytraceresult);
            if (actionresulttype.isSuccess()) {
               this.player.swing(hand, true);
            }
         }
      } else {
         ITextComponent itextcomponent = (new TranslationTextComponent("build.tooHigh", this.server.getBuildLimit())).applyTextStyle(TextFormatting.RED);
         this.player.connection.sendPacket(new SChatPacket(itextcomponent, ChatType.GAME_INFO));
      }

      this.player.connection.sendPacket(new SChangeBlockPacket(serverworld, blockpos));
      this.player.connection.sendPacket(new SChangeBlockPacket(serverworld, blockpos.offset(direction)));
   }

   /**
    * Called when a client is using an item while not pointing at a block, but simply using an item
    */
   public void processTryUseItem(CPlayerTryUseItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Hand hand = packetIn.getHand();
      ItemStack itemstack = this.player.getHeldItem(hand);
      this.player.markPlayerActive();
      if (!itemstack.isEmpty()) {
         this.player.interactionManager.processRightClick(this.player, serverworld, itemstack, hand);
      }
   }

   public void handleSpectate(CSpectatePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.isSpectator()) {
         for(ServerWorld serverworld : this.server.getWorlds()) {
            Entity entity = packetIn.getEntity(serverworld);
            if (entity != null) {
               this.player.teleport(serverworld, entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.rotationYaw, entity.rotationPitch);
               return;
            }
         }
      }

   }

   public void handleResourcePackStatus(CResourcePackStatusPacket packetIn) {
   }

   public void processSteerBoat(CSteerBoatPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      Entity entity = this.player.getRidingEntity();
      if (entity instanceof BoatEntity) {
         ((BoatEntity)entity).setPaddleState(packetIn.getLeft(), packetIn.getRight());
      }

   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
      this.server.refreshStatusNextTick();
      this.server.getPlayerList().sendMessage((new TranslationTextComponent("multiplayer.player.left", this.player.getDisplayName())).applyTextStyle(TextFormatting.YELLOW));
      this.player.disconnect();
      this.server.getPlayerList().playerLoggedOut(this.player);
      if (this.func_217264_d()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.initiateShutdown(false);
      }

   }

   public void sendPacket(IPacket<?> packetIn) {
      this.sendPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>)null);
   }

   public void sendPacket(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> futureListeners) {
      if (packetIn instanceof SChatPacket) {
         SChatPacket schatpacket = (SChatPacket)packetIn;
         ChatVisibility chatvisibility = this.player.getChatVisibility();
         if (chatvisibility == ChatVisibility.HIDDEN && schatpacket.getType() != ChatType.GAME_INFO) {
            return;
         }

         if (chatvisibility == ChatVisibility.SYSTEM && !schatpacket.isSystem()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(packetIn, futureListeners);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Sending packet");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Packet being sent");
         crashreportcategory.addDetail("Packet class", () -> {
            return packetIn.getClass().getCanonicalName();
         });
         throw new ReportedException(crashreport);
      }
   }

   /**
    * Updates which quickbar slot is selected
    */
   public void processHeldItemChange(CHeldItemChangePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < PlayerInventory.getHotbarSize()) {
         this.player.inventory.currentItem = packetIn.getSlotId();
         this.player.markPlayerActive();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", (Object)this.player.getName().getString());
      }
   }

   /**
    * Process chat messages (broadcast back to clients) and commands (executes)
    */
   public void processChatMessage(CChatMessagePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.getChatVisibility() == ChatVisibility.HIDDEN) {
         this.sendPacket(new SChatPacket((new TranslationTextComponent("chat.cannotSend")).applyTextStyle(TextFormatting.RED)));
      } else {
         this.player.markPlayerActive();
         String s = packetIn.getMessage();
         s = org.apache.commons.lang3.StringUtils.normalizeSpace(s);

         for(int i = 0; i < s.length(); ++i) {
            if (!SharedConstants.isAllowedCharacter(s.charAt(i))) {
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.illegal_characters"));
               return;
            }
         }

         if (s.startsWith("/")) {
            this.handleSlashCommand(s);
         } else {
            ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", this.player.getDisplayName(), net.minecraftforge.common.ForgeHooks.newChatWithLinks(s));
            itextcomponent = net.minecraftforge.common.ForgeHooks.onServerChatEvent(this, s, itextcomponent);
            if (itextcomponent == null) return;
            this.server.getPlayerList().sendMessage(itextcomponent, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.server.getPlayerList().canSendCommands(this.player.getGameProfile())) {
            this.disconnect(new TranslationTextComponent("disconnect.spam"));
         }

      }
   }

   /**
    * Handle commands that start with a /
    */
   private void handleSlashCommand(String command) {
      this.server.getCommandManager().handleCommand(this.player.getCommandSource(), command);
   }

   public void handleAnimation(CAnimateHandPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      this.player.swingArm(packetIn.getHand());
   }

   /**
    * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
    * height of the horse the player is riding
    */
   public void processEntityAction(CEntityActionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      switch(packetIn.getAction()) {
      case PRESS_SHIFT_KEY:
         this.player.setSneaking(true);
         break;
      case RELEASE_SHIFT_KEY:
         this.player.setSneaking(false);
         break;
      case START_SPRINTING:
         this.player.setSprinting(true);
         break;
      case STOP_SPRINTING:
         this.player.setSprinting(false);
         break;
      case STOP_SLEEPING:
         if (this.player.isSleeping()) {
            this.player.stopSleepInBed(false, true);
            this.targetPos = this.player.getPositionVec();
         }
         break;
      case START_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount1 = (IJumpingMount)this.player.getRidingEntity();
            int i = packetIn.getAuxData();
            if (ijumpingmount1.canJump() && i > 0) {
               ijumpingmount1.handleStartJump(i);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            IJumpingMount ijumpingmount = (IJumpingMount)this.player.getRidingEntity();
            ijumpingmount.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if (this.player.getRidingEntity() instanceof AbstractHorseEntity) {
            ((AbstractHorseEntity)this.player.getRidingEntity()).openGUI(this.player);
         }
         break;
      case START_FALL_FLYING:
         if (!this.player.tryToStartFallFlying()) {
            this.player.stopFallFlying();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   /**
    * Processes left and right clicks on entities
    */
   public void processUseEntity(CUseEntityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Entity entity = packetIn.getEntityFromWorld(serverworld);
      this.player.markPlayerActive();
      if (entity != null) {
         boolean flag = this.player.canEntityBeSeen(entity);
         double d0 = 36.0D;
         if (!flag) {
            d0 = 9.0D;
         }

         if (this.player.getDistanceSq(entity) < d0) {
            if (packetIn.getAction() == CUseEntityPacket.Action.INTERACT) {
               Hand hand = packetIn.getHand();
               this.player.interactOn(entity, hand);
            } else if (packetIn.getAction() == CUseEntityPacket.Action.INTERACT_AT) {
               Hand hand1 = packetIn.getHand();
               if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, packetIn.getHitVec(), hand1) != null) return;
               ActionResultType actionresulttype = entity.applyPlayerInteraction(this.player, packetIn.getHitVec(), hand1);
               if (actionresulttype.isSuccess()) {
                  this.player.swing(hand1, true);
               }
            } else if (packetIn.getAction() == CUseEntityPacket.Action.ATTACK) {
               if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == this.player) {
                  this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
                  this.server.logWarning("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                  return;
               }

               this.player.attackTargetEntityWithCurrentItem(entity);
            }
         }
      }

   }

   /**
    * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or acquiring
    * 'open inventory' achievement
    */
   public void processClientStatus(CClientStatusPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      CClientStatusPacket.State cclientstatuspacket$state = packetIn.getStatus();
      switch(cclientstatuspacket$state) {
      case PERFORM_RESPAWN:
         if (this.player.queuedEndExit) {
            this.player.queuedEndExit = false;
            this.player = this.server.getPlayerList().recreatePlayerEntity(this.player, DimensionType.OVERWORLD, true);
            CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
         } else {
            if (this.player.getHealth() > 0.0F) {
               return;
            }

            this.player = this.server.getPlayerList().recreatePlayerEntity(this.player, this.player.dimension, false);
            if (this.server.isHardcore()) {
               this.player.setGameType(GameType.SPECTATOR);
               this.player.getServerWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   /**
    * Processes the client closing windows (container)
    */
   public void processCloseWindow(CCloseWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.closeContainer();
   }

   /**
    * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
    * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
    * the same open container/inventory
    */
   public void processClickWindow(CClickWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == packetIn.getWindowId() && this.player.openContainer.getCanCraft(this.player)) {
         if (this.player.isSpectator()) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.player.openContainer.inventorySlots.size(); ++i) {
               nonnulllist.add(this.player.openContainer.inventorySlots.get(i).getStack());
            }

            this.player.sendAllContents(this.player.openContainer, nonnulllist);
         } else {
            ItemStack itemstack1 = this.player.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getClickType(), this.player);
            if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), itemstack1)) {
               this.player.connection.sendPacket(new SConfirmTransactionPacket(packetIn.getWindowId(), packetIn.getActionNumber(), true));
               this.player.isChangingQuantityOnly = true;
               this.player.openContainer.detectAndSendChanges();
               this.player.updateHeldItem();
               this.player.isChangingQuantityOnly = false;
            } else {
               this.pendingTransactions.put(this.player.openContainer.windowId, packetIn.getActionNumber());
               this.player.connection.sendPacket(new SConfirmTransactionPacket(packetIn.getWindowId(), packetIn.getActionNumber(), false));
               this.player.openContainer.setCanCraft(this.player, false);
               NonNullList<ItemStack> nonnulllist1 = NonNullList.create();

               for(int j = 0; j < this.player.openContainer.inventorySlots.size(); ++j) {
                  ItemStack itemstack = this.player.openContainer.inventorySlots.get(j).getStack();
                  nonnulllist1.add(itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
               }

               this.player.sendAllContents(this.player.openContainer, nonnulllist1);
            }
         }
      }

   }

   public void processPlaceRecipe(CPlaceRecipePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (!this.player.isSpectator() && this.player.openContainer.windowId == packetIn.getWindowId() && this.player.openContainer.getCanCraft(this.player) && this.player.openContainer instanceof RecipeBookContainer) {
         this.server.getRecipeManager().getRecipe(packetIn.getRecipeId()).ifPresent((p_217265_2_) -> {
            ((RecipeBookContainer)this.player.openContainer).func_217056_a(packetIn.shouldPlaceAll(), p_217265_2_, this.player);
         });
      }
   }

   /**
    * Enchants the item identified by the packet given some convoluted conditions (matching window, which
    * should/shouldn't be in use?)
    */
   public void processEnchantItem(CEnchantItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == packetIn.getWindowId() && this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.enchantItem(this.player, packetIn.getButton());
         this.player.openContainer.detectAndSendChanges();
      }

   }

   /**
    * Update the server with an ItemStack in a slot.
    */
   public void processCreativeInventoryAction(CCreativeInventoryActionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      if (this.player.interactionManager.isCreative()) {
         boolean flag = packetIn.getSlotId() < 0;
         ItemStack itemstack = packetIn.getStack();
         CompoundNBT compoundnbt = itemstack.getChildTag("BlockEntityTag");
         if (!itemstack.isEmpty() && compoundnbt != null && compoundnbt.contains("x") && compoundnbt.contains("y") && compoundnbt.contains("z")) {
            BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
            TileEntity tileentity = this.player.world.getTileEntity(blockpos);
            if (tileentity != null) {
               CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
               compoundnbt1.remove("x");
               compoundnbt1.remove("y");
               compoundnbt1.remove("z");
               itemstack.setTagInfo("BlockEntityTag", compoundnbt1);
            }
         }

         boolean flag1 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() <= 45;
         boolean flag2 = itemstack.isEmpty() || itemstack.getDamage() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();
         if (flag1 && flag2) {
            if (itemstack.isEmpty()) {
               this.player.container.putStackInSlot(packetIn.getSlotId(), ItemStack.EMPTY);
            } else {
               this.player.container.putStackInSlot(packetIn.getSlotId(), itemstack);
            }

            this.player.container.setCanCraft(this.player, true);
            this.player.container.detectAndSendChanges();
         } else if (flag && flag2 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            this.player.dropItem(itemstack, true);
         }
      }

   }

   /**
    * Received in response to the server requesting to confirm that the client-side open container matches the servers'
    * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
    * contents
    */
   public void processConfirmTransaction(CConfirmTransactionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      int i = this.player.openContainer.windowId;
      if (i == packetIn.getWindowId() && this.pendingTransactions.getOrDefault(i, (short)(packetIn.getUid() + 1)) == packetIn.getUid() && !this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.setCanCraft(this.player, true);
      }

   }

   public void processUpdateSign(CUpdateSignPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.markPlayerActive();
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      BlockPos blockpos = packetIn.getPosition();
      if (serverworld.isBlockLoaded(blockpos)) {
         BlockState blockstate = serverworld.getBlockState(blockpos);
         TileEntity tileentity = serverworld.getTileEntity(blockpos);
         if (!(tileentity instanceof SignTileEntity)) {
            return;
         }

         SignTileEntity signtileentity = (SignTileEntity)tileentity;
         if (!signtileentity.getIsEditable() || signtileentity.getPlayer() != this.player) {
            this.server.logWarning("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
            return;
         }

         String[] astring = packetIn.getLines();

         for(int i = 0; i < astring.length; ++i) {
            signtileentity.setText(i, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(astring[i])));
         }

         signtileentity.markDirty();
         serverworld.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
      }

   }

   /**
    * Updates a players' ping statistics
    */
   public void processKeepAlive(CKeepAlivePacket packetIn) {
      if (this.keepAlivePending && packetIn.getKey() == this.keepAliveKey) {
         int i = (int)(Util.milliTime() - this.keepAliveTime);
         this.player.ping = (this.player.ping * 3 + i) / 4;
         this.keepAlivePending = false;
      } else if (!this.func_217264_d()) {
         this.disconnect(new TranslationTextComponent("disconnect.timeout"));
      }

   }

   /**
    * Processes a player starting/stopping flying
    */
   public void processPlayerAbilities(CPlayerAbilitiesPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.abilities.isFlying = packetIn.isFlying() && this.player.abilities.allowFlying;
   }

   /**
    * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
    * and whether to show the cape
    */
   public void processClientSettings(CClientSettingsPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      this.player.handleClientSettings(packetIn);
   }

   /**
    * Synchronizes serverside and clientside book contents and signing
    */
   public void processCustomPayload(CCustomPayloadPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.player.getServerWorld());
      net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(packetIn, this.netManager);
   }

   public void func_217263_a(CSetDifficultyPacket p_217263_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217263_1_, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2) || this.func_217264_d()) {
         this.server.setDifficultyForAllWorlds(p_217263_1_.func_218773_b(), false);
      }
   }

   public void func_217261_a(CLockDifficultyPacket p_217261_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217261_1_, this, this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2) || this.func_217264_d()) {
         this.server.setDifficultyLocked(p_217261_1_.func_218776_b());
      }
   }
}