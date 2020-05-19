package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   /** A map containing the key-value pairs for UUIDs and their EntityPlayerMP objects. */
   private final Map<UUID, ServerPlayerEntity> uuidToPlayerMap = Maps.newHashMap();
   private final BanList bannedPlayers = new BanList(FILE_PLAYERBANS);
   private final IPBanList bannedIPs = new IPBanList(FILE_IPBANS);
   private final OpList ops = new OpList(FILE_OPS);
   private final WhiteList whiteListedPlayers = new WhiteList(FILE_WHITELIST);
   private final Map<UUID, ServerStatisticsManager> playerStatFiles = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private IPlayerFileData playerDataManager;
   private boolean whiteListEnforced;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType gameType;
   private boolean commandsAllowedForAll;
   private int playerPingIndex;
   private final List<ServerPlayerEntity> playersView = java.util.Collections.unmodifiableList(players);

   public PlayerList(MinecraftServer p_i50688_1_, int p_i50688_2_) {
      this.server = p_i50688_1_;
      this.maxPlayers = p_i50688_2_;
      this.getBannedPlayers().setLanServer(true);
      this.getBannedIPs().setLanServer(true);
   }

   public void initializeConnectionToPlayer(NetworkManager netManager, ServerPlayerEntity playerIn) {
      GameProfile gameprofile = playerIn.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
      GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.addEntry(gameprofile);
      CompoundNBT compoundnbt = this.readPlayerDataFromFile(playerIn);

      //Forge: Make sure the dimension hasn't been deleted, if so stick them in the overworld.
      ServerWorld serverworld = playerIn.dimension != null ? this.server.getWorld(playerIn.dimension) : null ;
      if (serverworld == null) {
         playerIn.dimension = DimensionType.OVERWORLD;
         serverworld = this.server.getWorld(playerIn.dimension);
         playerIn.setPosition(serverworld.getWorldInfo().getSpawnX(), serverworld.getWorldInfo().getSpawnY(), serverworld.getWorldInfo().getSpawnZ());
      }

      playerIn.setWorld(serverworld);
      playerIn.interactionManager.setWorld((ServerWorld)playerIn.world);
      String s1 = "local";
      if (netManager.getRemoteAddress() != null) {
         s1 = netManager.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName().getString(), s1, playerIn.getEntityId(), playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());
      WorldInfo worldinfo = serverworld.getWorldInfo();
      this.setPlayerGameTypeBasedOnOther(playerIn, (ServerPlayerEntity)null, serverworld);
      ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, netManager, playerIn);
      net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(netManager, "PLAY_TO_CLIENT");
      net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(netManager, playerIn);
      GameRules gamerules = serverworld.getGameRules();
      boolean flag = gamerules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
      boolean flag1 = gamerules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
      serverplaynethandler.sendPacket(new SJoinGamePacket(playerIn.getEntityId(), playerIn.interactionManager.getGameType(), WorldInfo.byHashing(worldinfo.getSeed()), worldinfo.isHardcore(), serverworld.dimension.getType(), this.getMaxPlayers(), worldinfo.getGenerator(), this.viewDistance, flag1, !flag));
      serverplaynethandler.sendPacket(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServer().getServerModName())));
      serverplaynethandler.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplaynethandler.sendPacket(new SPlayerAbilitiesPacket(playerIn.abilities));
      serverplaynethandler.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
      serverplaynethandler.sendPacket(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      serverplaynethandler.sendPacket(new STagsListPacket(this.server.getNetworkTagManager()));
      this.updatePermissionLevel(playerIn);
      playerIn.getStats().markAllDirty();
      playerIn.getRecipeBook().init(playerIn);
      this.sendScoreboard(serverworld.getScoreboard(), playerIn);
      this.server.refreshStatusNextTick();
      ITextComponent itextcomponent;
      if (playerIn.getGameProfile().getName().equalsIgnoreCase(s)) {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined", playerIn.getDisplayName());
      } else {
         itextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", playerIn.getDisplayName(), s);
      }

      this.sendMessage(itextcomponent.applyTextStyle(TextFormatting.YELLOW));
      serverplaynethandler.setPlayerLocation(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), playerIn.rotationYaw, playerIn.rotationPitch);
      this.addPlayer(playerIn);
      this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
      this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, playerIn));

      for(int i = 0; i < this.players.size(); ++i) {
         playerIn.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
      }

      serverworld.addNewPlayer(playerIn);
      this.server.getCustomBossEvents().onPlayerLogin(playerIn);
      this.sendWorldInfo(playerIn, serverworld);
      if (!this.server.getResourcePackUrl().isEmpty()) {
         playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
      }

      for(EffectInstance effectinstance : playerIn.getActivePotionEffects()) {
         serverplaynethandler.sendPacket(new SPlayEntityEffectPacket(playerIn.getEntityId(), effectinstance));
      }

      if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
         final ServerWorld worldf = serverworld;
         Entity entity1 = EntityType.func_220335_a(compoundnbt1.getCompound("Entity"), serverworld, (p_217885_1_) -> {
            return !worldf.summonEntity(p_217885_1_) ? null : p_217885_1_;
         });
         if (entity1 != null) {
            UUID uuid = compoundnbt1.getUniqueId("Attach");
            if (entity1.getUniqueID().equals(uuid)) {
               playerIn.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getRecursivePassengers()) {
                  if (entity.getUniqueID().equals(uuid)) {
                     playerIn.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!playerIn.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               serverworld.removeEntity(entity1);

               for(Entity entity2 : entity1.getRecursivePassengers()) {
                  serverworld.removeEntity(entity2);
               }
            }
         }
      }

      playerIn.addSelfToInternalCraftingInventory();
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedIn( playerIn );
   }

   protected void sendScoreboard(ServerScoreboard scoreboardIn, ServerPlayerEntity playerIn) {
      Set<ScoreObjective> set = Sets.newHashSet();

      for(ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams()) {
         playerIn.connection.sendPacket(new STeamsPacket(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            for(IPacket<?> ipacket : scoreboardIn.getCreatePackets(scoreobjective)) {
               playerIn.connection.sendPacket(ipacket);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void func_212504_a(ServerWorld p_212504_1_) {
      this.playerDataManager = p_212504_1_.getSaveHandler();
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder border, double newSize) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_SIZE));
         }

         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.LERP_SIZE));
         }

         public void onCenterChanged(WorldBorder border, double x, double z) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_CENTER));
         }

         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_WARNING_TIME));
         }

         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_WARNING_BLOCKS));
         }

         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
         }

         public void onDamageBufferChanged(WorldBorder border, double newSize) {
         }
      });
   }

   /**
    * called during player login. reads the player information from disk.
    */
   @Nullable
   public CompoundNBT readPlayerDataFromFile(ServerPlayerEntity playerIn) {
      CompoundNBT compoundnbt = this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().getPlayerNBTTagCompound();
      CompoundNBT compoundnbt1;
      if (playerIn.getName().getString().equals(this.server.getServerOwner()) && compoundnbt != null) {
         compoundnbt1 = compoundnbt;
         playerIn.read(compoundnbt);
         LOGGER.debug("loading single player");
         net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(playerIn, this.playerDataManager, playerIn.getUniqueID().toString());
      } else {
         compoundnbt1 = this.playerDataManager.readPlayerData(playerIn);
      }

      return compoundnbt1;
   }

   /**
    * also stores the NBTTags if this is an intergratedPlayerList
    */
   protected void writePlayerData(ServerPlayerEntity playerIn) {
      if (playerIn.connection == null) return;
      this.playerDataManager.writePlayerData(playerIn);
      ServerStatisticsManager serverstatisticsmanager = this.playerStatFiles.get(playerIn.getUniqueID());
      if (serverstatisticsmanager != null) {
         serverstatisticsmanager.saveStatFile();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(playerIn.getUniqueID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   /**
    * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
    */
   public void playerLoggedOut(ServerPlayerEntity playerIn) {
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedOut(playerIn);
      ServerWorld serverworld = playerIn.getServerWorld();
      playerIn.addStat(Stats.LEAVE_GAME);
      this.writePlayerData(playerIn);
      if (playerIn.isPassenger()) {
         Entity entity = playerIn.getLowestRidingEntity();
         if (entity.isOnePlayerRiding()) {
            LOGGER.debug("Removing player mount");
            playerIn.stopRiding();
            serverworld.removeEntity(entity);

            for(Entity entity1 : entity.getRecursivePassengers()) {
               serverworld.removeEntity(entity1);
            }

            serverworld.getChunk(playerIn.chunkCoordX, playerIn.chunkCoordZ).markDirty();
         }
      }

      playerIn.detach();
      serverworld.removePlayer(playerIn);
      playerIn.getAdvancements().dispose();
      this.removePlayer(playerIn);
      this.server.getCustomBossEvents().onPlayerLogout(playerIn);
      UUID uuid = playerIn.getUniqueID();
      ServerPlayerEntity serverplayerentity = this.uuidToPlayerMap.get(uuid);
      if (serverplayerentity == playerIn) {
         this.uuidToPlayerMap.remove(uuid);
         this.playerStatFiles.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, playerIn));
   }

   @Nullable
   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      if (this.bannedPlayers.isBanned(p_206258_2_)) {
         ProfileBanEntry profilebanentry = this.bannedPlayers.getEntry(p_206258_2_);
         ITextComponent itextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.banned.reason", profilebanentry.getBanReason());
         if (profilebanentry.getBanEndDate() != null) {
            itextcomponent1.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", DATE_FORMAT.format(profilebanentry.getBanEndDate())));
         }

         return itextcomponent1;
      } else if (!this.canJoin(p_206258_2_)) {
         return new TranslationTextComponent("multiplayer.disconnect.not_whitelisted");
      } else if (this.bannedIPs.isBanned(p_206258_1_)) {
         IPBanEntry ipbanentry = this.bannedIPs.getBanEntry(p_206258_1_);
         ITextComponent itextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", ipbanentry.getBanReason());
         if (ipbanentry.getBanEndDate() != null) {
            itextcomponent.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", DATE_FORMAT.format(ipbanentry.getBanEndDate())));
         }

         return itextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(p_206258_2_) ? new TranslationTextComponent("multiplayer.disconnect.server_full") : null;
      }
   }

   /**
    * also checks for multiple logins across servers
    */
   public ServerPlayerEntity createPlayerForUser(GameProfile profile) {
      UUID uuid = PlayerEntity.getUUID(profile);
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.getUniqueID().equals(uuid)) {
            list.add(serverplayerentity);
         }
      }

      ServerPlayerEntity serverplayerentity2 = this.uuidToPlayerMap.get(profile.getId());
      if (serverplayerentity2 != null && !list.contains(serverplayerentity2)) {
         list.add(serverplayerentity2);
      }

      for(ServerPlayerEntity serverplayerentity1 : list) {
         serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
      }

      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      }

      return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), profile, playerinteractionmanager);
   }

   /**
    * Destroys the given player entity and recreates another in the given dimension. Used when respawning after death or
    * returning from the End
    */
   public ServerPlayerEntity recreatePlayerEntity(ServerPlayerEntity playerIn, DimensionType dimension, boolean conqueredEnd) {
      ServerWorld world = server.getWorld(dimension);
      if (world == null)
         dimension = playerIn.getSpawnDimension();
      else if (!world.getDimension().canRespawnHere())
         dimension = world.getDimension().getRespawnDimension(playerIn);
      if (server.getWorld(dimension) == null)
         dimension = DimensionType.OVERWORLD;

      this.removePlayer(playerIn);
      playerIn.getServerWorld().removePlayer(playerIn, true); // Forge: keep data until copyFrom called
      BlockPos blockpos = playerIn.getBedLocation(dimension);
      boolean flag = playerIn.isSpawnForced(dimension);
      playerIn.dimension = dimension;
      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(playerIn.dimension));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(playerIn.dimension));
      }

      ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, this.server.getWorld(playerIn.dimension), playerIn.getGameProfile(), playerinteractionmanager);
      serverplayerentity.connection = playerIn.connection;
      serverplayerentity.copyFrom(playerIn, conqueredEnd);
      playerIn.remove(false); // Forge: clone event had a chance to see old data, now discard it
      serverplayerentity.dimension = dimension;
      serverplayerentity.setEntityId(playerIn.getEntityId());
      serverplayerentity.setPrimaryHand(playerIn.getPrimaryHand());

      for(String s : playerIn.getTags()) {
         serverplayerentity.addTag(s);
      }

      ServerWorld serverworld = this.server.getWorld(playerIn.dimension);
      this.setPlayerGameTypeBasedOnOther(serverplayerentity, playerIn, serverworld);
      if (blockpos != null) {
         Optional<Vec3d> optional = PlayerEntity.checkBedValidRespawnPosition(this.server.getWorld(playerIn.dimension), blockpos, flag);
         if (optional.isPresent()) {
            Vec3d vec3d = optional.get();
            serverplayerentity.setLocationAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
            serverplayerentity.setSpawnPoint(blockpos, flag, false, dimension);
         } else {
            serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(0, 0.0F));
         }
      }

      while(!serverworld.hasNoCollisions(serverplayerentity) && serverplayerentity.getPosY() < 256.0D) {
         serverplayerentity.setPosition(serverplayerentity.getPosX(), serverplayerentity.getPosY() + 1.0D, serverplayerentity.getPosZ());
      }

      WorldInfo worldinfo = serverplayerentity.world.getWorldInfo();
      net.minecraftforge.fml.network.NetworkHooks.sendDimensionDataPacket(serverplayerentity.connection.netManager, serverplayerentity);
      serverplayerentity.connection.sendPacket(new SRespawnPacket(serverplayerentity.dimension, WorldInfo.byHashing(worldinfo.getSeed()), worldinfo.getGenerator(), serverplayerentity.interactionManager.getGameType()));
      BlockPos blockpos1 = serverworld.getSpawnPoint();
      serverplayerentity.connection.setPlayerLocation(serverplayerentity.getPosX(), serverplayerentity.getPosY(), serverplayerentity.getPosZ(), serverplayerentity.rotationYaw, serverplayerentity.rotationPitch);
      serverplayerentity.connection.sendPacket(new SSpawnPositionPacket(blockpos1));
      serverplayerentity.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      serverplayerentity.connection.sendPacket(new SSetExperiencePacket(serverplayerentity.experience, serverplayerentity.experienceTotal, serverplayerentity.experienceLevel));
      this.sendWorldInfo(serverplayerentity, serverworld);
      this.updatePermissionLevel(serverplayerentity);
      serverworld.addRespawnedPlayer(serverplayerentity);
      this.addPlayer(serverplayerentity);
      this.uuidToPlayerMap.put(serverplayerentity.getUniqueID(), serverplayerentity);
      serverplayerentity.addSelfToInternalCraftingInventory();
      serverplayerentity.setHealth(serverplayerentity.getHealth());
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerRespawnEvent(serverplayerentity, conqueredEnd);
      return serverplayerentity;
   }

   public void updatePermissionLevel(ServerPlayerEntity player) {
      GameProfile gameprofile = player.getGameProfile();
      int i = this.server.getPermissionLevel(gameprofile);
      this.sendPlayerPermissionLevel(player, i);
   }

   /**
    * self explanitory
    */
   public void tick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_LATENCY, this.players));
         this.playerPingIndex = 0;
      }

   }

   public void sendPacketToAllPlayers(IPacket<?> packetIn) {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.sendPacket(packetIn);
      }

   }

   public void sendPacketToAllPlayersInDimension(IPacket<?> packetIn, DimensionType dimension) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity.dimension == dimension) {
            serverplayerentity.connection.sendPacket(packetIn);
         }
      }

   }

   public void sendMessageToAllTeamMembers(PlayerEntity player, ITextComponent message) {
      Team team = player.getTeam();
      if (team != null) {
         for(String s : team.getMembershipCollection()) {
            ServerPlayerEntity serverplayerentity = this.getPlayerByUsername(s);
            if (serverplayerentity != null && serverplayerentity != player) {
               serverplayerentity.sendMessage(message);
            }
         }

      }
   }

   public void sendMessageToTeamOrAllPlayers(PlayerEntity player, ITextComponent message) {
      Team team = player.getTeam();
      if (team == null) {
         this.sendMessage(message);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverplayerentity = this.players.get(i);
            if (serverplayerentity.getTeam() != team) {
               serverplayerentity.sendMessage(message);
            }
         }

      }
   }

   /**
    * Returns an array of the usernames of all the connected players.
    */
   public String[] getOnlinePlayerNames() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public BanList getBannedPlayers() {
      return this.bannedPlayers;
   }

   public IPBanList getBannedIPs() {
      return this.bannedIPs;
   }

   public void addOp(GameProfile profile) {
      this.ops.addEntry(new OpEntry(profile, this.server.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
      ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(profile.getId());
      if (serverplayerentity != null) {
         this.updatePermissionLevel(serverplayerentity);
      }

   }

   public void removeOp(GameProfile profile) {
      this.ops.removeEntry(profile);
      ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(profile.getId());
      if (serverplayerentity != null) {
         this.updatePermissionLevel(serverplayerentity);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayerEntity player, int permLevel) {
      if (player.connection != null) {
         byte b0;
         if (permLevel <= 0) {
            b0 = 24;
         } else if (permLevel >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + permLevel);
         }

         player.connection.sendPacket(new SEntityStatusPacket(player, b0));
      }

      this.server.getCommandManager().send(player);
   }

   public boolean canJoin(GameProfile profile) {
      return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
   }

   public boolean canSendCommands(GameProfile profile) {
      return this.ops.hasEntry(profile) || this.server.isServerOwner(profile) && this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().areCommandsAllowed() || this.commandsAllowedForAll;
   }

   @Nullable
   public ServerPlayerEntity getPlayerByUsername(String username) {
      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getGameProfile().getName().equalsIgnoreCase(username)) {
            return serverplayerentity;
         }
      }

      return null;
   }

   /**
    * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer, but all other players within the
    * search radius
    */
   public void sendToAllNearExcept(@Nullable PlayerEntity except, double x, double y, double z, double radius, DimensionType dimension, IPacket<?> packetIn) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverplayerentity = this.players.get(i);
         if (serverplayerentity != except && serverplayerentity.dimension == dimension) {
            double d0 = x - serverplayerentity.getPosX();
            double d1 = y - serverplayerentity.getPosY();
            double d2 = z - serverplayerentity.getPosZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
               serverplayerentity.connection.sendPacket(packetIn);
            }
         }
      }

   }

   /**
    * Saves all of the players' current states.
    */
   public void saveAllPlayerData() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.writePlayerData(this.players.get(i));
      }

   }

   public WhiteList getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public OpList getOppedPlayers() {
      return this.ops;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public void reloadWhitelist() {
   }

   /**
    * Updates the time and weather for the given player to those of the given world
    */
   public void sendWorldInfo(ServerPlayerEntity playerIn, ServerWorld worldIn) {
      WorldBorder worldborder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
      playerIn.connection.sendPacket(new SWorldBorderPacket(worldborder, SWorldBorderPacket.Action.INITIALIZE));
      playerIn.connection.sendPacket(new SUpdateTimePacket(worldIn.getGameTime(), worldIn.getDayTime(), worldIn.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
      BlockPos blockpos = worldIn.getSpawnPoint();
      playerIn.connection.sendPacket(new SSpawnPositionPacket(blockpos));
      if (worldIn.isRaining()) {
         playerIn.connection.sendPacket(new SChangeGameStatePacket(1, 0.0F));
         playerIn.connection.sendPacket(new SChangeGameStatePacket(7, worldIn.getRainStrength(1.0F)));
         playerIn.connection.sendPacket(new SChangeGameStatePacket(8, worldIn.getThunderStrength(1.0F)));
      }

   }

   /**
    * sends the players inventory to himself
    */
   public void sendInventory(ServerPlayerEntity playerIn) {
      playerIn.sendContainerToPlayer(playerIn.container);
      playerIn.setPlayerHealthUpdated();
      playerIn.connection.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
   }

   /**
    * Returns the number of players currently on the server.
    */
   public int getCurrentPlayerCount() {
      return this.players.size();
   }

   /**
    * Returns the maximum number of players allowed on the server.
    */
   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isWhiteListEnabled() {
      return this.whiteListEnforced;
   }

   public void setWhiteListEnabled(boolean whitelistEnabled) {
      this.whiteListEnforced = whitelistEnabled;
   }

   public List<ServerPlayerEntity> getPlayersMatchingAddress(String address) {
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.getPlayerIP().equals(address)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   /**
    * Gets the view distance, in chunks.
    */
   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   /**
    * On integrated servers, returns the host's player data to be written to level.dat.
    */
   public CompoundNBT getHostPlayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setGameType(GameType gameModeIn) {
      this.gameType = gameModeIn;
   }

   private void setPlayerGameTypeBasedOnOther(ServerPlayerEntity target, ServerPlayerEntity source, IWorld worldIn) {
      if (source != null) {
         target.interactionManager.setGameType(source.interactionManager.getGameType());
      } else if (this.gameType != null) {
         target.interactionManager.setGameType(this.gameType);
      }

      target.interactionManager.initializeGameType(worldIn.getWorldInfo().getGameType());
   }

   /**
    * Sets whether all players are allowed to use commands (cheats) on the server.
    */
   @OnlyIn(Dist.CLIENT)
   public void setCommandsAllowedForAll(boolean p_72387_1_) {
      this.commandsAllowedForAll = p_72387_1_;
   }

   /**
    * Kicks everyone with "Server closed" as reason.
    */
   public void removeAllPlayers() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void sendMessage(ITextComponent component, boolean isSystem) {
      this.server.sendMessage(component);
      ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
      this.sendPacketToAllPlayers(new SChatPacket(component, chattype));
   }

   /**
    * Sends the given string to every player as chat message.
    */
   public void sendMessage(ITextComponent component) {
      this.sendMessage(component, true);
   }

   public ServerStatisticsManager getPlayerStats(PlayerEntity playerIn) {
      UUID uuid = playerIn.getUniqueID();
      ServerStatisticsManager serverstatisticsmanager = uuid == null ? null : this.playerStatFiles.get(uuid);
      if (serverstatisticsmanager == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, playerIn.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         serverstatisticsmanager = new ServerStatisticsManager(this.server, file2);
         this.playerStatFiles.put(uuid, serverstatisticsmanager);
      }

      return serverstatisticsmanager;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayerEntity p_192054_1_) {
      UUID uuid = p_192054_1_.getUniqueID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "advancements");
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server, file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int viewDistanceIn) {
      this.viewDistance = viewDistanceIn;
      this.sendPacketToAllPlayers(new SUpdateViewDistancePacket(viewDistanceIn));

      for(ServerWorld serverworld : this.server.getWorlds()) {
         if (serverworld != null) {
            serverworld.getChunkProvider().setViewDistance(viewDistanceIn);
         }
      }

   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.playersView; //Unmodifiable view, we don't want people removing things without us knowing.
   }

   /**
    * Get's the EntityPlayerMP object representing the player with the UUID.
    */
   @Nullable
   public ServerPlayerEntity getPlayerByUUID(UUID playerUUID) {
      return this.uuidToPlayerMap.get(playerUUID);
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.reload();
      }

      this.sendPacketToAllPlayers(new STagsListPacket(this.server.getNetworkTagManager()));
      SUpdateRecipesPacket supdaterecipespacket = new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

      for(ServerPlayerEntity serverplayerentity : this.players) {
         serverplayerentity.connection.sendPacket(supdaterecipespacket);
         serverplayerentity.getRecipeBook().init(serverplayerentity);
      }

   }

   public boolean commandsAllowedForAll() {
      return this.commandsAllowedForAll;
   }

   public boolean addPlayer(ServerPlayerEntity player) {
      return net.minecraftforge.common.DimensionManager.rebuildPlayerMap(this, this.players.add(player));
   }

   public boolean removePlayer(ServerPlayerEntity player) {
       return net.minecraftforge.common.DimensionManager.rebuildPlayerMap(this, this.players.remove(player));
   }
}