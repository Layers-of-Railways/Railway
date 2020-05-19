package net.minecraft.client.network.play;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BeeAngrySound;
import net.minecraft.client.audio.BeeFlightSound;
import net.minecraft.client.audio.BeeSound;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadTerrainScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.EntityAIDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateDebugRenderer;
import net.minecraft.client.renderer.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptsDebugRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.NBTQueryManager;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientPlayNetHandler implements IClientPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   /**
    * The NetworkManager instance used to communicate with the server, used to respond to various packets (primarilly
    * movement and plugin channel related ones) and check the status of the network connection externally
    */
   private final NetworkManager netManager;
   private final GameProfile profile;
   /**
    * Seems to be either null (integrated server) or an instance of either GuiMultiplayer (when connecting to a server)
    * or GuiScreenReamlsTOS (when connecting to MCO server)
    */
   private final Screen guiScreenServer;
   /** Reference to the Minecraft instance, which many handler methods operate on */
   private Minecraft client;
   /** Reference to the current ClientWorld instance, which many handler methods operate on */
   private ClientWorld world;
   /**
    * True if the client has finished downloading terrain and may spawn. Set upon receipt of S08PacketPlayerPosLook,
    * reset upon respawning
    */
   private boolean doneLoadingTerrain;
   /** A mapping from player names to their respective GuiPlayerInfo (specifies the clients response time to the server) */
   private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   private final ClientAdvancementManager advancementManager;
   private final ClientSuggestionProvider clientSuggestionProvider;
   private NetworkTagManager networkTagManager = new NetworkTagManager();
   private final NBTQueryManager nbtQueryManager = new NBTQueryManager(this);
   private int viewDistance = 3;
   /**
    * Just an ordinary random number generator, used to randomize audio pitch of item/orb pickup and randomize both
    * particlespawn offset and velocity
    */
   private final Random avRandomizer = new Random();
   private CommandDispatcher<ISuggestionProvider> commandDispatcher = new CommandDispatcher<>();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID sessionId = UUID.randomUUID();

   public ClientPlayNetHandler(Minecraft mcIn, Screen previousGuiScreen, NetworkManager networkManagerIn, GameProfile profileIn) {
      this.client = mcIn;
      this.guiScreenServer = previousGuiScreen;
      this.netManager = networkManagerIn;
      this.profile = profileIn;
      this.advancementManager = new ClientAdvancementManager(mcIn);
      this.clientSuggestionProvider = new ClientSuggestionProvider(this, mcIn);
   }

   public ClientSuggestionProvider getSuggestionProvider() {
      return this.clientSuggestionProvider;
   }

   /**
    * Clears the WorldClient instance associated with this NetHandlerPlayClient
    */
   public void cleanup() {
      this.world = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   /**
    * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
    * WorldClient and sets the player initial dimension
    */
   public void handleJoinGame(SJoinGamePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.playerController = new PlayerController(this.client, this);
      this.viewDistance = packetIn.getViewDistance();
      this.world = new ClientWorld(this, new WorldSettings(packetIn.getHashedSeed(), packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), this.viewDistance, this.client.getProfiler(), this.client.worldRenderer);
      this.client.loadWorld(this.world);
      if (this.client.player == null) {
         this.client.player = this.client.playerController.createPlayer(this.world, new StatisticsManager(), new ClientRecipeBook(this.world.getRecipeManager()));
         this.client.player.rotationYaw = -180.0F;
         if (this.client.getIntegratedServer() != null) {
            this.client.getIntegratedServer().setPlayerUuid(this.client.player.getUniqueID());
         }
      }

      this.client.debugRenderer.clear();
      this.client.player.preparePlayerToSpawn();
      net.minecraftforge.fml.client.ClientHooks.firePlayerLogin(this.client.playerController, this.client.player, this.client.getConnection().getNetworkManager());
      int i = packetIn.getPlayerId();
      this.world.addPlayer(i, this.client.player);
      this.client.player.movementInput = new MovementInputFromOptions(this.client.gameSettings);
      this.client.playerController.setPlayerCapabilities(this.client.player);
      this.client.renderViewEntity = this.client.player;
      this.client.player.dimension = packetIn.getDimension();
      this.client.displayGuiScreen(new DownloadTerrainScreen());
      this.client.player.setEntityId(i);
      this.client.player.setReducedDebug(packetIn.isReducedDebugInfo());
      this.client.player.setShowDeathScreen(packetIn.func_229743_k_());
      this.client.playerController.setGameType(packetIn.getGameType());
      net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(netManager, "PLAY_TO_SERVER");
      this.client.gameSettings.sendSettingsToServer();
      this.netManager.sendPacket(new CCustomPayloadPacket(CCustomPayloadPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
      this.client.getMinecraftGame().startGameSession();
   }

   /**
    * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
    */
   public void handleSpawnObject(SSpawnObjectPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      double d0 = packetIn.getX();
      double d1 = packetIn.getY();
      double d2 = packetIn.getZ();
      EntityType<?> entitytype = packetIn.getType();
      Entity entity;
      if (entitytype == EntityType.CHEST_MINECART) {
         entity = new ChestMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.FURNACE_MINECART) {
         entity = new FurnaceMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.TNT_MINECART) {
         entity = new TNTMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.SPAWNER_MINECART) {
         entity = new SpawnerMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.HOPPER_MINECART) {
         entity = new HopperMinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.COMMAND_BLOCK_MINECART) {
         entity = new MinecartCommandBlockEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.MINECART) {
         entity = new MinecartEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.FISHING_BOBBER) {
         Entity entity1 = this.world.getEntityByID(packetIn.getData());
         if (entity1 instanceof PlayerEntity) {
            entity = new FishingBobberEntity(this.world, (PlayerEntity)entity1, d0, d1, d2);
         } else {
            entity = null;
         }
      } else if (entitytype == EntityType.ARROW) {
         entity = new ArrowEntity(this.world, d0, d1, d2);
         Entity entity2 = this.world.getEntityByID(packetIn.getData());
         if (entity2 != null) {
            ((AbstractArrowEntity)entity).setShooter(entity2);
         }
      } else if (entitytype == EntityType.SPECTRAL_ARROW) {
         entity = new SpectralArrowEntity(this.world, d0, d1, d2);
         Entity entity3 = this.world.getEntityByID(packetIn.getData());
         if (entity3 != null) {
            ((AbstractArrowEntity)entity).setShooter(entity3);
         }
      } else if (entitytype == EntityType.TRIDENT) {
         entity = new TridentEntity(this.world, d0, d1, d2);
         Entity entity4 = this.world.getEntityByID(packetIn.getData());
         if (entity4 != null) {
            ((AbstractArrowEntity)entity).setShooter(entity4);
         }
      } else if (entitytype == EntityType.SNOWBALL) {
         entity = new SnowballEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.LLAMA_SPIT) {
         entity = new LlamaSpitEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.ITEM_FRAME) {
         entity = new ItemFrameEntity(this.world, new BlockPos(d0, d1, d2), Direction.byIndex(packetIn.getData()));
      } else if (entitytype == EntityType.LEASH_KNOT) {
         entity = new LeashKnotEntity(this.world, new BlockPos(d0, d1, d2));
      } else if (entitytype == EntityType.ENDER_PEARL) {
         entity = new EnderPearlEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.EYE_OF_ENDER) {
         entity = new EyeOfEnderEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.FIREWORK_ROCKET) {
         entity = new FireworkRocketEntity(this.world, d0, d1, d2, ItemStack.EMPTY);
      } else if (entitytype == EntityType.FIREBALL) {
         entity = new FireballEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.DRAGON_FIREBALL) {
         entity = new DragonFireballEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.SMALL_FIREBALL) {
         entity = new SmallFireballEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.WITHER_SKULL) {
         entity = new WitherSkullEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.SHULKER_BULLET) {
         entity = new ShulkerBulletEntity(this.world, d0, d1, d2, packetIn.func_218693_g(), packetIn.func_218695_h(), packetIn.func_218692_i());
      } else if (entitytype == EntityType.EGG) {
         entity = new EggEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.EVOKER_FANGS) {
         entity = new EvokerFangsEntity(this.world, d0, d1, d2, 0.0F, 0, (LivingEntity)null);
      } else if (entitytype == EntityType.POTION) {
         entity = new PotionEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.EXPERIENCE_BOTTLE) {
         entity = new ExperienceBottleEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.BOAT) {
         entity = new BoatEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.TNT) {
         entity = new TNTEntity(this.world, d0, d1, d2, (LivingEntity)null);
      } else if (entitytype == EntityType.ARMOR_STAND) {
         entity = new ArmorStandEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.END_CRYSTAL) {
         entity = new EnderCrystalEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.ITEM) {
         entity = new ItemEntity(this.world, d0, d1, d2);
      } else if (entitytype == EntityType.FALLING_BLOCK) {
         entity = new FallingBlockEntity(this.world, d0, d1, d2, Block.getStateById(packetIn.getData()));
      } else if (entitytype == EntityType.AREA_EFFECT_CLOUD) {
         entity = new AreaEffectCloudEntity(this.world, d0, d1, d2);
      } else {
         entity = null;
      }

      if (entity != null) {
         int i = packetIn.getEntityID();
         entity.setPacketCoordinates(d0, d1, d2);
         entity.rotationPitch = (float)(packetIn.getPitch() * 360) / 256.0F;
         entity.rotationYaw = (float)(packetIn.getYaw() * 360) / 256.0F;
         entity.setEntityId(i);
         entity.setUniqueId(packetIn.getUniqueId());
         this.world.addEntity(i, entity);
         if (entity instanceof AbstractMinecartEntity) {
            this.client.getSoundHandler().play(new MinecartTickableSound((AbstractMinecartEntity)entity));
         }
      }

   }

   /**
    * Spawns an experience orb and sets its value (amount of XP)
    */
   public void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      double d0 = packetIn.getX();
      double d1 = packetIn.getY();
      double d2 = packetIn.getZ();
      Entity entity = new ExperienceOrbEntity(this.world, d0, d1, d2, packetIn.getXPValue());
      entity.setPacketCoordinates(d0, d1, d2);
      entity.rotationYaw = 0.0F;
      entity.rotationPitch = 0.0F;
      entity.setEntityId(packetIn.getEntityID());
      this.world.addEntity(packetIn.getEntityID(), entity);
   }

   /**
    * Handles globally visible entities. Used in vanilla for lightning bolts
    */
   public void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      double d0 = packetIn.getX();
      double d1 = packetIn.getY();
      double d2 = packetIn.getZ();
      if (packetIn.getType() == 1) {
         LightningBoltEntity lightningboltentity = new LightningBoltEntity(this.world, d0, d1, d2, false);
         lightningboltentity.setPacketCoordinates(d0, d1, d2);
         lightningboltentity.rotationYaw = 0.0F;
         lightningboltentity.rotationPitch = 0.0F;
         lightningboltentity.setEntityId(packetIn.getEntityId());
         this.world.addLightning(lightningboltentity);
      }

   }

   /**
    * Handles the spawning of a painting object
    */
   public void handleSpawnPainting(SSpawnPaintingPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PaintingEntity paintingentity = new PaintingEntity(this.world, packetIn.getPosition(), packetIn.getFacing(), packetIn.getType());
      paintingentity.setEntityId(packetIn.getEntityID());
      paintingentity.setUniqueId(packetIn.getUniqueId());
      this.world.addEntity(packetIn.getEntityID(), paintingentity);
   }

   /**
    * Sets the velocity of the specified entity to the specified value
    */
   public void handleEntityVelocity(SEntityVelocityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         entity.setVelocity((double)packetIn.getMotionX() / 8000.0D, (double)packetIn.getMotionY() / 8000.0D, (double)packetIn.getMotionZ() / 8000.0D);
      }
   }

   /**
    * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
    * changed -> Registers any changes locally
    */
   public void handleEntityMetadata(SEntityMetadataPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity != null && packetIn.getDataManagerEntries() != null) {
         entity.getDataManager().setEntryValues(packetIn.getDataManagerEntries());
      }

   }

   /**
    * Handles the creation of a nearby player entity, sets the position and held item
    */
   public void handleSpawnPlayer(SSpawnPlayerPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      double d0 = packetIn.getX();
      double d1 = packetIn.getY();
      double d2 = packetIn.getZ();
      float f = (float)(packetIn.getYaw() * 360) / 256.0F;
      float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
      int i = packetIn.getEntityID();
      RemoteClientPlayerEntity remoteclientplayerentity = new RemoteClientPlayerEntity(this.client.world, this.getPlayerInfo(packetIn.getUniqueId()).getGameProfile());
      remoteclientplayerentity.setEntityId(i);
      remoteclientplayerentity.forceSetPosition(d0, d1, d2);
      remoteclientplayerentity.setPacketCoordinates(d0, d1, d2);
      remoteclientplayerentity.setPositionAndRotation(d0, d1, d2, f, f1);
      this.world.addPlayer(i, remoteclientplayerentity);
   }

   /**
    * Updates an entity's position and rotation as specified by the packet
    */
   public void handleEntityTeleport(SEntityTeleportPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity != null) {
         double d0 = packetIn.getX();
         double d1 = packetIn.getY();
         double d2 = packetIn.getZ();
         entity.setPacketCoordinates(d0, d1, d2);
         if (!entity.canPassengerSteer()) {
            float f = (float)(packetIn.getYaw() * 360) / 256.0F;
            float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
            if (!(Math.abs(entity.getPosX() - d0) >= 0.03125D) && !(Math.abs(entity.getPosY() - d1) >= 0.015625D) && !(Math.abs(entity.getPosZ() - d2) >= 0.03125D)) {
               entity.setPositionAndRotationDirect(entity.getPosX(), entity.getPosY(), entity.getPosZ(), f, f1, 3, true);
            } else {
               entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = packetIn.isOnGround();
         }

      }
   }

   /**
    * Updates which hotbar slot of the player is currently selected
    */
   public void handleHeldItemChange(SHeldItemChangePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (PlayerInventory.isHotbar(packetIn.getHeldItemHotbarIndex())) {
         this.client.player.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
      }

   }

   /**
    * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
    * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
    * rotation or both).
    */
   public void handleEntityMovement(SEntityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = packetIn.getEntity(this.world);
      if (entity != null) {
         if (!entity.canPassengerSteer()) {
            if (packetIn.func_229745_h_()) {
               entity.serverPosX += (long)packetIn.getX();
               entity.serverPosY += (long)packetIn.getY();
               entity.serverPosZ += (long)packetIn.getZ();
               Vec3d vec3d = SEntityPacket.func_218744_a(entity.serverPosX, entity.serverPosY, entity.serverPosZ);
               float f = packetIn.isRotating() ? (float)(packetIn.getYaw() * 360) / 256.0F : entity.rotationYaw;
               float f1 = packetIn.isRotating() ? (float)(packetIn.getPitch() * 360) / 256.0F : entity.rotationPitch;
               entity.setPositionAndRotationDirect(vec3d.x, vec3d.y, vec3d.z, f, f1, 3, false);
            } else if (packetIn.isRotating()) {
               float f2 = (float)(packetIn.getYaw() * 360) / 256.0F;
               float f3 = (float)(packetIn.getPitch() * 360) / 256.0F;
               entity.setPositionAndRotationDirect(entity.getPosX(), entity.getPosY(), entity.getPosZ(), f2, f3, 3, false);
            }

            entity.onGround = packetIn.getOnGround();
         }

      }
   }

   /**
    * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
    * rotation of the entity itself
    */
   public void handleEntityHeadLook(SEntityHeadLookPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = packetIn.getEntity(this.world);
      if (entity != null) {
         float f = (float)(packetIn.getYaw() * 360) / 256.0F;
         entity.setHeadRotation(f, 3);
      }
   }

   /**
    * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
    * longer registered as required to monitor them. The latter  happens when distance between the player and item
    * increases beyond a certain treshold (typically the viewing distance)
    */
   public void handleDestroyEntities(SDestroyEntitiesPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

      for(int i = 0; i < packetIn.getEntityIDs().length; ++i) {
         int j = packetIn.getEntityIDs()[i];
         this.world.removeEntityFromWorld(j);
      }

   }

   public void handlePlayerPosLook(SPlayerPositionLookPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      Vec3d vec3d = playerentity.getMotion();
      boolean flag = packetIn.getFlags().contains(SPlayerPositionLookPacket.Flags.X);
      boolean flag1 = packetIn.getFlags().contains(SPlayerPositionLookPacket.Flags.Y);
      boolean flag2 = packetIn.getFlags().contains(SPlayerPositionLookPacket.Flags.Z);
      double d0;
      double d1;
      if (flag) {
         d0 = vec3d.getX();
         d1 = playerentity.getPosX() + packetIn.getX();
         playerentity.lastTickPosX += packetIn.getX();
      } else {
         d0 = 0.0D;
         d1 = packetIn.getX();
         playerentity.lastTickPosX = d1;
      }

      double d2;
      double d3;
      if (flag1) {
         d2 = vec3d.getY();
         d3 = playerentity.getPosY() + packetIn.getY();
         playerentity.lastTickPosY += packetIn.getY();
      } else {
         d2 = 0.0D;
         d3 = packetIn.getY();
         playerentity.lastTickPosY = d3;
      }

      double d4;
      double d5;
      if (flag2) {
         d4 = vec3d.getZ();
         d5 = playerentity.getPosZ() + packetIn.getZ();
         playerentity.lastTickPosZ += packetIn.getZ();
      } else {
         d4 = 0.0D;
         d5 = packetIn.getZ();
         playerentity.lastTickPosZ = d5;
      }

      playerentity.setRawPosition(d1, d3, d5);
      playerentity.prevPosX = d1;
      playerentity.prevPosY = d3;
      playerentity.prevPosZ = d5;
      playerentity.setMotion(d0, d2, d4);
      float f = packetIn.getYaw();
      float f1 = packetIn.getPitch();
      if (packetIn.getFlags().contains(SPlayerPositionLookPacket.Flags.X_ROT)) {
         f1 += playerentity.rotationPitch;
      }

      if (packetIn.getFlags().contains(SPlayerPositionLookPacket.Flags.Y_ROT)) {
         f += playerentity.rotationYaw;
      }

      playerentity.setPositionAndRotation(d1, d3, d5, f, f1);
      this.netManager.sendPacket(new CConfirmTeleportPacket(packetIn.getTeleportId()));
      this.netManager.sendPacket(new CPlayerPacket.PositionRotationPacket(playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), playerentity.rotationYaw, playerentity.rotationPitch, false));
      if (!this.doneLoadingTerrain) {
         this.doneLoadingTerrain = true;
         this.client.displayGuiScreen((Screen)null);
      }

   }

   /**
    * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
    * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
    * S21PacketChunkData
    */
   public void handleMultiBlockChange(SMultiBlockChangePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

      for(SMultiBlockChangePacket.UpdateData smultiblockchangepacket$updatedata : packetIn.getChangedBlocks()) {
         this.world.invalidateRegionAndSetBlock(smultiblockchangepacket$updatedata.getPos(), smultiblockchangepacket$updatedata.getBlockState());
      }

   }

   /**
    * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
    */
   public void handleChunkData(SChunkDataPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      int i = packetIn.getChunkX();
      int j = packetIn.getChunkZ();
      Chunk chunk = this.world.getChunkProvider().loadChunk(i, j, packetIn.getBiomes(), packetIn.getReadBuffer(), packetIn.getHeightmapTags(), packetIn.getAvailableSections());
      if (chunk != null && packetIn.isFullChunk()) {
         this.world.addEntitiesToChunk(chunk);
      }

      for(int k = 0; k < 16; ++k) {
         this.world.markSurroundingsForRerender(i, k, j);
      }

      for(CompoundNBT compoundnbt : packetIn.getTileEntityTags()) {
         BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity != null) {
            tileentity.handleUpdateTag(compoundnbt);
         }
      }

   }

   public void processChunkUnload(SUnloadChunkPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      int i = packetIn.getX();
      int j = packetIn.getZ();
      ClientChunkProvider clientchunkprovider = this.world.getChunkProvider();
      clientchunkprovider.unloadChunk(i, j);
      WorldLightManager worldlightmanager = clientchunkprovider.getLightManager();

      for(int k = 0; k < 16; ++k) {
         this.world.markSurroundingsForRerender(i, k, j);
         worldlightmanager.updateSectionStatus(SectionPos.of(i, k, j), true);
      }

      worldlightmanager.enableLightSources(new ChunkPos(i, j), false);
   }

   /**
    * Updates the block and metadata and generates a blockupdate (and notify the clients)
    */
   public void handleBlockChange(SChangeBlockPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.world.invalidateRegionAndSetBlock(packetIn.getPos(), packetIn.getState());
   }

   /**
    * Closes the network channel
    */
   public void handleDisconnect(SDisconnectPacket packetIn) {
      this.netManager.closeChannel(packetIn.getReason());
   }

   /**
    * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
    */
   public void onDisconnect(ITextComponent reason) {
      this.client.unloadWorld();
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof RealmsScreenProxy) {
            this.client.displayGuiScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.guiScreenServer).getScreen(), "disconnect.lost", reason)).getProxy());
         } else {
            this.client.displayGuiScreen(new DisconnectedScreen(this.guiScreenServer, "disconnect.lost", reason));
         }
      } else {
         this.client.displayGuiScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), "disconnect.lost", reason));
      }

   }

   public void sendPacket(IPacket<?> packetIn) {
      this.netManager.sendPacket(packetIn);
   }

   public void handleCollectItem(SCollectItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getCollectedItemEntityID());
      LivingEntity livingentity = (LivingEntity)this.world.getEntityByID(packetIn.getEntityID());
      if (livingentity == null) {
         livingentity = this.client.player;
      }

      if (entity != null) {
         if (entity instanceof ExperienceOrbEntity) {
            this.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
         }

         if (entity instanceof ItemEntity) {
            ((ItemEntity)entity).getItem().setCount(packetIn.getAmount());
         }

         this.client.particles.addEffect(new ItemPickupParticle(this.client.getRenderManager(), this.client.getRenderTypeBuffers(), this.world, entity, livingentity));
         this.world.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
      }

   }

   /**
    * Prints a chatmessage in the chat GUI
    */
   public void handleChat(SChatPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      net.minecraft.util.text.ITextComponent message = net.minecraftforge.event.ForgeEventFactory.onClientChat(packetIn.getType(), packetIn.getChatComponent());
      if (message == null) return;
      this.client.ingameGUI.addChatMessage(packetIn.getType(), message);
   }

   /**
    * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt or
    * receiving a critical hit by normal or magical means
    */
   public void handleAnimation(SAnimateHandPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         if (packetIn.getAnimationType() == 0) {
            LivingEntity livingentity = (LivingEntity)entity;
            livingentity.swingArm(Hand.MAIN_HAND);
         } else if (packetIn.getAnimationType() == 3) {
            LivingEntity livingentity1 = (LivingEntity)entity;
            livingentity1.swingArm(Hand.OFF_HAND);
         } else if (packetIn.getAnimationType() == 1) {
            entity.performHurtAnimation();
         } else if (packetIn.getAnimationType() == 2) {
            PlayerEntity playerentity = (PlayerEntity)entity;
            playerentity.stopSleepInBed(false, false);
         } else if (packetIn.getAnimationType() == 4) {
            this.client.particles.addParticleEmitter(entity, ParticleTypes.CRIT);
         } else if (packetIn.getAnimationType() == 5) {
            this.client.particles.addParticleEmitter(entity, ParticleTypes.ENCHANTED_HIT);
         }

      }
   }

   /**
    * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
    * entities Datawatchers with the entity metadata specified in the packet
    */
   public void handleSpawnMob(SSpawnMobPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      double d0 = packetIn.getX();
      double d1 = packetIn.getY();
      double d2 = packetIn.getZ();
      float f = (float)(packetIn.getYaw() * 360) / 256.0F;
      float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
      LivingEntity livingentity = (LivingEntity)EntityType.create(packetIn.getEntityType(), this.client.world);
      if (livingentity != null) {
         livingentity.setPacketCoordinates(d0, d1, d2);
         livingentity.renderYawOffset = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
         livingentity.rotationYawHead = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
         if (livingentity instanceof EnderDragonEntity) {
            EnderDragonPartEntity[] aenderdragonpartentity = ((EnderDragonEntity)livingentity).getDragonParts();

            for(int i = 0; i < aenderdragonpartentity.length; ++i) {
               aenderdragonpartentity[i].setEntityId(i + packetIn.getEntityID());
            }
         }

         livingentity.setEntityId(packetIn.getEntityID());
         livingentity.setUniqueId(packetIn.getUniqueId());
         livingentity.setPositionAndRotation(d0, d1, d2, f, f1);
         livingentity.setMotion((double)((float)packetIn.getVelocityX() / 8000.0F), (double)((float)packetIn.getVelocityY() / 8000.0F), (double)((float)packetIn.getVelocityZ() / 8000.0F));
         this.world.addEntity(packetIn.getEntityID(), livingentity);
         if (livingentity instanceof BeeEntity) {
            boolean flag = ((BeeEntity)livingentity).isAngry();
            BeeSound beesound;
            if (flag) {
               beesound = new BeeAngrySound((BeeEntity)livingentity);
            } else {
               beesound = new BeeFlightSound((BeeEntity)livingentity);
            }

            this.client.getSoundHandler().play(beesound);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", (int)packetIn.getEntityType());
      }

   }

   public void handleTimeUpdate(SUpdateTimePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.world.setGameTime(packetIn.getTotalWorldTime());
      this.client.world.setDayTime(packetIn.getWorldTime());
   }

   public void handleSpawnPosition(SSpawnPositionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.player.setRespawnPosition(packetIn.getSpawnPos(), true, false);
      this.client.world.getWorldInfo().setSpawn(packetIn.getSpawnPos());
   }

   public void handleSetPassengers(SSetPassengersPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean flag = entity.isRidingOrBeingRiddenBy(this.client.player);
         entity.removePassengers();

         for(int i : packetIn.getPassengerIds()) {
            Entity entity1 = this.world.getEntityByID(i);
            if (entity1 != null) {
               entity1.startRiding(entity, true);
               if (entity1 == this.client.player && !flag) {
                  this.client.ingameGUI.setOverlayMessage(I18n.format("mount.onboard", this.client.gameSettings.keyBindSneak.getLocalizedName()), false);
               }
            }
         }

      }
   }

   public void handleEntityAttach(SMountEntityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity instanceof MobEntity) {
         ((MobEntity)entity).setVehicleEntityId(packetIn.getVehicleEntityId());
      }

   }

   private static ItemStack func_217282_a(PlayerEntity p_217282_0_) {
      for(Hand hand : Hand.values()) {
         ItemStack itemstack = p_217282_0_.getHeldItem(hand);
         if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            return itemstack;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   /**
    * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
    * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch (spawn
    * particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke particles),
    * Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
    */
   public void handleEntityStatus(SEntityStatusPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = packetIn.getEntity(this.world);
      if (entity != null) {
         if (packetIn.getOpCode() == 21) {
            this.client.getSoundHandler().play(new GuardianSound((GuardianEntity)entity));
         } else if (packetIn.getOpCode() == 35) {
            int i = 40;
            this.client.particles.emitParticleAtEntity(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            this.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
            if (entity == this.client.player) {
               this.client.gameRenderer.displayItemActivation(func_217282_a(this.client.player));
            }
         } else {
            entity.handleStatusUpdate(packetIn.getOpCode());
         }
      }

   }

   public void handleUpdateHealth(SUpdateHealthPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.player.setPlayerSPHealth(packetIn.getHealth());
      this.client.player.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
      this.client.player.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
   }

   public void handleSetExperience(SSetExperiencePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.player.setXPStats(packetIn.getExperienceBar(), packetIn.getTotalExperience(), packetIn.getLevel());
   }

   public void handleRespawn(SRespawnPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      DimensionType dimensiontype = packetIn.getDimension();
      ClientPlayerEntity clientplayerentity = this.client.player;
      int i = clientplayerentity.getEntityId();
      if (dimensiontype != clientplayerentity.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.world.getScoreboard();
         this.world = new ClientWorld(this, new WorldSettings(packetIn.getHashedSeed(), packetIn.getGameType(), false, this.client.world.getWorldInfo().isHardcore(), packetIn.getWorldType()), packetIn.getDimension(), this.viewDistance, this.client.getProfiler(), this.client.worldRenderer);
         this.world.setScoreboard(scoreboard);
         this.client.loadWorld(this.world);
         this.client.displayGuiScreen(new DownloadTerrainScreen());
      }

      this.world.setInitialSpawnLocation();
      this.world.removeAllEntities();
      String s = clientplayerentity.getServerBrand();
      this.client.renderViewEntity = null;
      ClientPlayerEntity clientplayerentity1 = this.client.playerController.createPlayer(this.world, clientplayerentity.getStats(), clientplayerentity.getRecipeBook());
      clientplayerentity1.setEntityId(i);
      clientplayerentity1.dimension = dimensiontype;
      this.client.player = clientplayerentity1;
      this.client.renderViewEntity = clientplayerentity1;
      clientplayerentity1.getDataManager().setEntryValues(clientplayerentity.getDataManager().getAll());
      clientplayerentity1.getAttributes().func_226303_a_(clientplayerentity.getAttributes());
      clientplayerentity1.updateSyncFields(clientplayerentity); // Forge: fix MC-10657
      clientplayerentity1.preparePlayerToSpawn();
      clientplayerentity1.setServerBrand(s);
      net.minecraftforge.fml.client.ClientHooks.firePlayerRespawn(this.client.playerController, clientplayerentity, clientplayerentity1, clientplayerentity1.connection.getNetworkManager());
      this.world.addPlayer(i, clientplayerentity1);
      clientplayerentity1.rotationYaw = -180.0F;
      clientplayerentity1.movementInput = new MovementInputFromOptions(this.client.gameSettings);
      this.client.playerController.setPlayerCapabilities(clientplayerentity1);
      clientplayerentity1.setReducedDebug(clientplayerentity.hasReducedDebug());
      clientplayerentity1.setShowDeathScreen(clientplayerentity.isShowDeathScreen());
      if (this.client.currentScreen instanceof DeathScreen) {
         this.client.displayGuiScreen((Screen)null);
      }

      this.client.playerController.setGameType(packetIn.getGameType());
   }

   /**
    * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
    */
   public void handleExplosion(SExplosionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Explosion explosion = new Explosion(this.client.world, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
      explosion.doExplosionB(true);
      this.client.player.setMotion(this.client.player.getMotion().add((double)packetIn.getMotionX(), (double)packetIn.getMotionY(), (double)packetIn.getMotionZ()));
   }

   public void handleOpenHorseWindow(SOpenHorseWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.func_218703_d());
      if (entity instanceof AbstractHorseEntity) {
         ClientPlayerEntity clientplayerentity = this.client.player;
         AbstractHorseEntity abstracthorseentity = (AbstractHorseEntity)entity;
         Inventory inventory = new Inventory(packetIn.func_218702_c());
         HorseInventoryContainer horseinventorycontainer = new HorseInventoryContainer(packetIn.func_218704_b(), clientplayerentity.inventory, inventory, abstracthorseentity);
         clientplayerentity.openContainer = horseinventorycontainer;
         this.client.displayGuiScreen(new HorseInventoryScreen(horseinventorycontainer, clientplayerentity.inventory, abstracthorseentity));
      }

   }

   public void handleOpenWindowPacket(SOpenWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      ScreenManager.openScreen(packetIn.getContainerType(), this.client, packetIn.getWindowId(), packetIn.getTitle());
   }

   /**
    * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
    */
   public void handleSetSlot(SSetSlotPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      ItemStack itemstack = packetIn.getStack();
      int i = packetIn.getSlot();
      this.client.getTutorial().handleSetSlot(itemstack);
      if (packetIn.getWindowId() == -1) {
         if (!(this.client.currentScreen instanceof CreativeScreen)) {
            playerentity.inventory.setItemStack(itemstack);
         }
      } else if (packetIn.getWindowId() == -2) {
         playerentity.inventory.setInventorySlotContents(i, itemstack);
      } else {
         boolean flag = false;
         if (this.client.currentScreen instanceof CreativeScreen) {
            CreativeScreen creativescreen = (CreativeScreen)this.client.currentScreen;
            flag = creativescreen.getSelectedTabIndex() != ItemGroup.INVENTORY.getIndex();
         }

         if (packetIn.getWindowId() == 0 && packetIn.getSlot() >= 36 && i < 45) {
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = playerentity.container.getSlot(i).getStack();
               if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                  itemstack.setAnimationsToGo(5);
               }
            }

            playerentity.container.putStackInSlot(i, itemstack);
         } else if (packetIn.getWindowId() == playerentity.openContainer.windowId && (packetIn.getWindowId() != 0 || !flag)) {
            playerentity.openContainer.putStackInSlot(i, itemstack);
         }
      }

   }

   /**
    * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
    * and confirms if it is the case.
    */
   public void handleConfirmTransaction(SConfirmTransactionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Container container = null;
      PlayerEntity playerentity = this.client.player;
      if (packetIn.getWindowId() == 0) {
         container = playerentity.container;
      } else if (packetIn.getWindowId() == playerentity.openContainer.windowId) {
         container = playerentity.openContainer;
      }

      if (container != null && !packetIn.wasAccepted()) {
         this.sendPacket(new CConfirmTransactionPacket(packetIn.getWindowId(), packetIn.getActionNumber(), true));
      }

   }

   /**
    * Handles the placement of a specified ItemStack in a specified container/inventory slot
    */
   public void handleWindowItems(SWindowItemsPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      if (packetIn.getWindowId() == 0) {
         playerentity.container.setAll(packetIn.getItemStacks());
      } else if (packetIn.getWindowId() == playerentity.openContainer.windowId) {
         playerentity.openContainer.setAll(packetIn.getItemStacks());
      }

   }

   /**
    * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
    */
   public void handleSignEditorOpen(SOpenSignMenuPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      TileEntity tileentity = this.world.getTileEntity(packetIn.getSignPosition());
      if (!(tileentity instanceof SignTileEntity)) {
         tileentity = new SignTileEntity();
         tileentity.setWorldAndPos(this.world, packetIn.getSignPosition());
      }

      this.client.player.openSignEditor((SignTileEntity)tileentity);
   }

   /**
    * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
    * beacons, skulls, flowerpot
    */
   public void handleUpdateTileEntity(SUpdateTileEntityPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (this.client.world.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileentity = this.client.world.getTileEntity(packetIn.getPos());
         int i = packetIn.getTileEntityType();
         boolean flag = i == 2 && tileentity instanceof CommandBlockTileEntity;
         if (i == 1 && tileentity instanceof MobSpawnerTileEntity || flag || i == 3 && tileentity instanceof BeaconTileEntity || i == 4 && tileentity instanceof SkullTileEntity || i == 6 && tileentity instanceof BannerTileEntity || i == 7 && tileentity instanceof StructureBlockTileEntity || i == 8 && tileentity instanceof EndGatewayTileEntity || i == 9 && tileentity instanceof SignTileEntity || i == 11 && tileentity instanceof BedTileEntity || i == 5 && tileentity instanceof ConduitTileEntity || i == 12 && tileentity instanceof JigsawTileEntity || i == 13 && tileentity instanceof CampfireTileEntity || i == 14 && tileentity instanceof BeehiveTileEntity) {
            tileentity.read(packetIn.getNbtCompound());
         } else {
            if(tileentity == null) {
               LOGGER.error("Received invalid update packet for null tile entity at {} with data: {}", packetIn.getPos(), packetIn.getNbtCompound());
               return;
            }
            tileentity.onDataPacket(netManager, packetIn);
         }

         if (flag && this.client.currentScreen instanceof CommandBlockScreen) {
            ((CommandBlockScreen)this.client.currentScreen).updateGui();
         }
      }

   }

   /**
    * Sets the progressbar of the opened window to the specified value
    */
   public void handleWindowProperty(SWindowPropertyPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      if (playerentity.openContainer != null && playerentity.openContainer.windowId == packetIn.getWindowId()) {
         playerentity.openContainer.updateProgressBar(packetIn.getProperty(), packetIn.getValue());
      }

   }

   public void handleEntityEquipment(SEntityEquipmentPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityID());
      if (entity != null) {
         entity.setItemStackToSlot(packetIn.getEquipmentSlot(), packetIn.getItemStack());
      }

   }

   /**
    * Resets the ItemStack held in hand and closes the window that is opened
    */
   public void handleCloseWindow(SCloseWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.player.closeScreenAndDropStack();
   }

   /**
    * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
    * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
    * accessing a (Ender)Chest
    */
   public void handleBlockAction(SBlockActionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.world.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
   }

   /**
    * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
    */
   public void handleBlockBreakAnim(SAnimateBlockBreakPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.world.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
   }

   public void handleChangeGameState(SChangeGameStatePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      int i = packetIn.getGameState();
      float f = packetIn.getValue();
      int j = MathHelper.floor(f + 0.5F);
      if (i >= 0 && i < SChangeGameStatePacket.MESSAGE_NAMES.length && SChangeGameStatePacket.MESSAGE_NAMES[i] != null) {
         playerentity.sendStatusMessage(new TranslationTextComponent(SChangeGameStatePacket.MESSAGE_NAMES[i]), false);
      }

      if (i == 1) {
         this.world.getWorldInfo().setRaining(true);
         this.world.setRainStrength(0.0F);
      } else if (i == 2) {
         this.world.getWorldInfo().setRaining(false);
         this.world.setRainStrength(1.0F);
      } else if (i == 3) {
         this.client.playerController.setGameType(GameType.getByID(j));
      } else if (i == 4) {
         if (j == 0) {
            this.client.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            this.client.displayGuiScreen(new DownloadTerrainScreen());
         } else if (j == 1) {
            this.client.displayGuiScreen(new WinGameScreen(true, () -> {
               this.client.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            }));
         }
      } else if (i == 5) {
         GameSettings gamesettings = this.client.gameSettings;
         if (f == 0.0F) {
            this.client.displayGuiScreen(new DemoScreen());
         } else if (f == 101.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.movement", gamesettings.keyBindForward.getLocalizedName(), gamesettings.keyBindLeft.getLocalizedName(), gamesettings.keyBindBack.getLocalizedName(), gamesettings.keyBindRight.getLocalizedName()));
         } else if (f == 102.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.jump", gamesettings.keyBindJump.getLocalizedName()));
         } else if (f == 103.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.help.inventory", gamesettings.keyBindInventory.getLocalizedName()));
         } else if (f == 104.0F) {
            this.client.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("demo.day.6", gamesettings.keyBindScreenshot.getLocalizedName()));
         }
      } else if (i == 6) {
         this.world.playSound(playerentity, playerentity.getPosX(), playerentity.getPosYEye(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (i == 7) {
         this.world.setRainStrength(f);
      } else if (i == 8) {
         this.world.setThunderStrength(f);
      } else if (i == 9) {
         this.world.playSound(playerentity, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (i == 10) {
         this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), 0.0D, 0.0D, 0.0D);
         this.world.playSound(playerentity, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
      } else if (i == 11) {
         this.client.player.setShowDeathScreen(f == 0.0F);
      }

   }

   /**
    * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
    * MapItemRenderer for it
    */
   public void handleMaps(SMapDataPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      MapItemRenderer mapitemrenderer = this.client.gameRenderer.getMapItemRenderer();
      String s = FilledMapItem.getMapName(packetIn.getMapId());
      MapData mapdata = this.client.world.getMapData(s);
      if (mapdata == null) {
         mapdata = new MapData(s);
         if (mapitemrenderer.getMapInstanceIfExists(s) != null) {
            MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));
            if (mapdata1 != null) {
               mapdata = mapdata1;
            }
         }

         this.client.world.registerMapData(mapdata);
      }

      packetIn.setMapdataTo(mapdata);
      mapitemrenderer.updateMapTexture(mapdata);
   }

   public void handleEffect(SPlaySoundEventPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (packetIn.isSoundServerwide()) {
         this.client.world.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      } else {
         this.client.world.playEvent(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      }

   }

   public void handleAdvancementInfo(SAdvancementInfoPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.advancementManager.read(packetIn);
   }

   public void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      ResourceLocation resourcelocation = packetIn.getTab();
      if (resourcelocation == null) {
         this.advancementManager.setSelectedTab((Advancement)null, false);
      } else {
         Advancement advancement = this.advancementManager.getAdvancementList().getAdvancement(resourcelocation);
         this.advancementManager.setSelectedTab(advancement, false);
      }

   }

   public void handleCommandList(SCommandListPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.commandDispatcher = new CommandDispatcher<>(packetIn.getRoot());
   }

   public void handleStopSound(SStopSoundPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.getSoundHandler().stop(packetIn.getName(), packetIn.getCategory());
   }

   /**
    * This method is only called for manual tab-completion (the {@link
    * net.minecraft.command.arguments.SuggestionProviders#ASK_SERVER minecraft:ask_server} suggestion provider).
    */
   public void handleTabComplete(STabCompletePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.clientSuggestionProvider.handleResponse(packetIn.getTransactionId(), packetIn.getSuggestions());
   }

   public void handleUpdateRecipes(SUpdateRecipesPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.recipeManager.func_223389_a(packetIn.getRecipes());
      IMutableSearchTree<RecipeList> imutablesearchtree = this.client.getSearchTree(SearchTreeManager.RECIPES);
      imutablesearchtree.clear();
      ClientRecipeBook clientrecipebook = this.client.player.getRecipeBook();
      clientrecipebook.rebuildTable();
      clientrecipebook.getRecipes().forEach(imutablesearchtree::func_217872_a);
      imutablesearchtree.recalculate();
      net.minecraftforge.client.ForgeHooksClient.onRecipesUpdated(this.recipeManager);
   }

   public void handlePlayerLook(SPlayerLookPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Vec3d vec3d = packetIn.getTargetPosition(this.world);
      if (vec3d != null) {
         this.client.player.lookAt(packetIn.getSourceAnchor(), vec3d);
      }

   }

   public void handleNBTQueryResponse(SQueryNBTResponsePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (!this.nbtQueryManager.handleResponse(packetIn.getTransactionId(), packetIn.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", (int)packetIn.getTransactionId());
      }

   }

   /**
    * Updates the players statistics or achievements
    */
   public void handleStatistics(SStatisticsPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

      for(Entry<Stat<?>, Integer> entry : packetIn.getStatisticMap().entrySet()) {
         Stat<?> stat = entry.getKey();
         int i = entry.getValue();
         this.client.player.getStats().setValue(this.client.player, stat, i);
      }

      if (this.client.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.client.currentScreen).onStatsUpdated();
      }

   }

   public void handleRecipeBook(SRecipeBookPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      ClientRecipeBook clientrecipebook = this.client.player.getRecipeBook();
      clientrecipebook.setGuiOpen(packetIn.isGuiOpen());
      clientrecipebook.setFilteringCraftable(packetIn.isFilteringCraftable());
      clientrecipebook.setFurnaceGuiOpen(packetIn.isFurnaceGuiOpen());
      clientrecipebook.setFurnaceFilteringCraftable(packetIn.isFurnaceFilteringCraftable());
      SRecipeBookPacket.State srecipebookpacket$state = packetIn.getState();
      switch(srecipebookpacket$state) {
      case REMOVE:
         for(ResourceLocation resourcelocation3 : packetIn.getRecipes()) {
            this.recipeManager.getRecipe(resourcelocation3).ifPresent(clientrecipebook::lock);
         }
         break;
      case INIT:
         for(ResourceLocation resourcelocation1 : packetIn.getRecipes()) {
            this.recipeManager.getRecipe(resourcelocation1).ifPresent(clientrecipebook::unlock);
         }

         for(ResourceLocation resourcelocation2 : packetIn.getDisplayedRecipes()) {
            this.recipeManager.getRecipe(resourcelocation2).ifPresent(clientrecipebook::markNew);
         }
         break;
      case ADD:
         for(ResourceLocation resourcelocation : packetIn.getRecipes()) {
            this.recipeManager.getRecipe(resourcelocation).ifPresent((p_217278_2_) -> {
               clientrecipebook.unlock(p_217278_2_);
               clientrecipebook.markNew(p_217278_2_);
               RecipeToast.addOrUpdate(this.client.getToastGui(), p_217278_2_);
            });
         }
      }

      clientrecipebook.getRecipes().forEach((p_199527_1_) -> {
         p_199527_1_.updateKnownRecipes(clientrecipebook);
      });
      if (this.client.currentScreen instanceof IRecipeShownListener) {
         ((IRecipeShownListener)this.client.currentScreen).recipesUpdated();
      }

   }

   public void handleEntityEffect(SPlayEntityEffectPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity instanceof LivingEntity) {
         Effect effect = Effect.get(packetIn.getEffectId() & 0xFF);
         if (effect != null) {
            EffectInstance effectinstance = new EffectInstance(effect, packetIn.getDuration(), packetIn.getAmplifier(), packetIn.getIsAmbient(), packetIn.doesShowParticles(), packetIn.shouldShowIcon());
            effectinstance.setPotionDurationMax(packetIn.isMaxDuration());
            ((LivingEntity)entity).addPotionEffect(effectinstance);
         }
      }
   }

   public void handleTags(STagsListPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.networkTagManager = packetIn.getTags();
      if (!this.netManager.isLocalChannel()) {
         BlockTags.setCollection(this.networkTagManager.getBlocks());
         ItemTags.setCollection(this.networkTagManager.getItems());
         FluidTags.setCollection(this.networkTagManager.getFluids());
         EntityTypeTags.setCollection(this.networkTagManager.getEntityTypes());
      }

      this.client.getSearchTree(SearchTreeManager.TAGS).recalculate();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent(this.networkTagManager));
   }

   public void handleCombatEvent(SCombatPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (packetIn.eventType == SCombatPacket.Event.ENTITY_DIED) {
         Entity entity = this.world.getEntityByID(packetIn.playerId);
         if (entity == this.client.player) {
            if (this.client.player.isShowDeathScreen()) {
               this.client.displayGuiScreen(new DeathScreen(packetIn.deathMessage, this.world.getWorldInfo().isHardcore()));
            } else {
               this.client.player.respawnPlayer();
            }
         }
      }

   }

   public void handleServerDifficulty(SServerDifficultyPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.world.getWorldInfo().setDifficulty(packetIn.getDifficulty());
      this.client.world.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
   }

   public void handleCamera(SCameraPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = packetIn.getEntity(this.world);
      if (entity != null) {
         this.client.setRenderViewEntity(entity);
      }

   }

   public void handleWorldBorder(SWorldBorderPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      packetIn.apply(this.world.getWorldBorder());
   }

   public void handleTitle(STitlePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      STitlePacket.Type stitlepacket$type = packetIn.getType();
      String s = null;
      String s1 = null;
      String s2 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
      switch(stitlepacket$type) {
      case TITLE:
         s = s2;
         break;
      case SUBTITLE:
         s1 = s2;
         break;
      case ACTIONBAR:
         this.client.ingameGUI.setOverlayMessage(s2, false);
         return;
      case RESET:
         this.client.ingameGUI.displayTitle("", "", -1, -1, -1);
         this.client.ingameGUI.setDefaultTitlesTimes();
         return;
      }

      this.client.ingameGUI.displayTitle(s, s1, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
   }

   public void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket packetIn) {
      this.client.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().isEmpty() ? null : packetIn.getHeader());
      this.client.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().isEmpty() ? null : packetIn.getFooter());
   }

   public void handleRemoveEntityEffect(SRemoveEntityEffectPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = packetIn.getEntity(this.world);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).removeActivePotionEffect(packetIn.getPotion());
      }

   }

   public void handlePlayerListItem(SPlayerListItemPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

      for(SPlayerListItemPacket.AddPlayerData splayerlistitempacket$addplayerdata : packetIn.getEntries()) {
         if (packetIn.getAction() == SPlayerListItemPacket.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(splayerlistitempacket$addplayerdata.getProfile().getId());
         } else {
            NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(splayerlistitempacket$addplayerdata.getProfile().getId());
            if (packetIn.getAction() == SPlayerListItemPacket.Action.ADD_PLAYER) {
               networkplayerinfo = new NetworkPlayerInfo(splayerlistitempacket$addplayerdata);
               this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
            }

            if (networkplayerinfo != null) {
               switch(packetIn.getAction()) {
               case ADD_PLAYER:
                  networkplayerinfo.setGameType(splayerlistitempacket$addplayerdata.getGameMode());
                  networkplayerinfo.setResponseTime(splayerlistitempacket$addplayerdata.getPing());
                  networkplayerinfo.setDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
                  break;
               case UPDATE_GAME_MODE:
                  networkplayerinfo.setGameType(splayerlistitempacket$addplayerdata.getGameMode());
                  break;
               case UPDATE_LATENCY:
                  networkplayerinfo.setResponseTime(splayerlistitempacket$addplayerdata.getPing());
                  break;
               case UPDATE_DISPLAY_NAME:
                  networkplayerinfo.setDisplayName(splayerlistitempacket$addplayerdata.getDisplayName());
               }
            }
         }
      }

   }

   public void handleKeepAlive(SKeepAlivePacket packetIn) {
      this.sendPacket(new CKeepAlivePacket(packetIn.getId()));
   }

   public void handlePlayerAbilities(SPlayerAbilitiesPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      PlayerEntity playerentity = this.client.player;
      playerentity.abilities.isFlying = packetIn.isFlying();
      playerentity.abilities.isCreativeMode = packetIn.isCreativeMode();
      playerentity.abilities.disableDamage = packetIn.isInvulnerable();
      playerentity.abilities.allowFlying = packetIn.isAllowFlying();
      playerentity.abilities.setFlySpeed(packetIn.getFlySpeed());
      playerentity.abilities.setWalkSpeed(packetIn.getWalkSpeed());
   }

   public void handleSoundEffect(SPlaySoundEffectPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.world.playSound(this.client.player, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSound(), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch());
   }

   public void handleSpawnMovingSoundEffect(SSpawnMovingSoundEffectPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.func_218762_d());
      if (entity != null) {
         this.client.world.playMovingSound(this.client.player, entity, packetIn.func_218763_b(), packetIn.func_218760_c(), packetIn.func_218764_e(), packetIn.func_218761_f());
      }
   }

   public void handleCustomSound(SPlaySoundPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.getSoundHandler().play(new SimpleSound(packetIn.getSoundName(), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch(), false, 0, ISound.AttenuationType.LINEAR, (float)packetIn.getX(), (float)packetIn.getY(), (float)packetIn.getZ(), false));
   }

   public void handleResourcePack(SSendResourcePackPacket packetIn) {
      String s = packetIn.getURL();
      String s1 = packetIn.getHash();
      if (this.validateResourcePackUrl(s)) {
         if (s.startsWith("level://")) {
            try {
               String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File file1 = new File(this.client.gameDir, "saves");
               File file2 = new File(file1, s2);
               if (file2.isFile()) {
                  this.sendResourcePackStatus(CResourcePackStatusPacket.Action.ACCEPTED);
                  CompletableFuture<?> completablefuture = this.client.getPackFinder().setServerPack(file2);
                  this.handlePackFuture(completablefuture);
                  return;
               }
            } catch (UnsupportedEncodingException var8) {
               ;
            }

            this.sendResourcePackStatus(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData serverdata = this.client.getCurrentServerData();
            if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
               this.sendResourcePackStatus(CResourcePackStatusPacket.Action.ACCEPTED);
               this.handlePackFuture(this.client.getPackFinder().downloadResourcePack(s, s1));
            } else if (serverdata != null && serverdata.getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
               this.sendResourcePackStatus(CResourcePackStatusPacket.Action.DECLINED);
            } else {
               this.client.execute(() -> {
                  this.client.displayGuiScreen(new ConfirmScreen((p_217274_3_) -> {
                     this.client = Minecraft.getInstance();
                     ServerData serverdata1 = this.client.getCurrentServerData();
                     if (p_217274_3_) {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.ENABLED);
                        }

                        this.sendResourcePackStatus(CResourcePackStatusPacket.Action.ACCEPTED);
                        this.handlePackFuture(this.client.getPackFinder().downloadResourcePack(s, s1));
                     } else {
                        if (serverdata1 != null) {
                           serverdata1.setResourceMode(ServerData.ServerResourceMode.DISABLED);
                        }

                        this.sendResourcePackStatus(CResourcePackStatusPacket.Action.DECLINED);
                     }

                     ServerList.saveSingleServer(serverdata1);
                     this.client.displayGuiScreen((Screen)null);
                  }, new TranslationTextComponent("multiplayer.texturePrompt.line1"), new TranslationTextComponent("multiplayer.texturePrompt.line2")));
               });
            }

         }
      }
   }

   private boolean validateResourcePackUrl(String url) {
      try {
         URI uri = new URI(url);
         String s = uri.getScheme();
         boolean flag = "level".equals(s);
         if (!"http".equals(s) && !"https".equals(s) && !flag) {
            throw new URISyntaxException(url, "Wrong protocol");
         } else if (!flag || !url.contains("..") && url.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException var5) {
         this.sendResourcePackStatus(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return false;
      }
   }

   private void handlePackFuture(CompletableFuture<?> futureIn) {
      futureIn.thenRun(() -> {
         this.sendResourcePackStatus(CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED);
      }).exceptionally((p_217276_1_) -> {
         this.sendResourcePackStatus(CResourcePackStatusPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void sendResourcePackStatus(CResourcePackStatusPacket.Action action) {
      this.netManager.sendPacket(new CResourcePackStatusPacket(action));
   }

   public void handleUpdateBossInfo(SUpdateBossInfoPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.ingameGUI.getBossOverlay().read(packetIn);
   }

   public void handleCooldown(SCooldownPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (packetIn.getTicks() == 0) {
         this.client.player.getCooldownTracker().removeCooldown(packetIn.getItem());
      } else {
         this.client.player.getCooldownTracker().setCooldown(packetIn.getItem(), packetIn.getTicks());
      }

   }

   public void handleMoveVehicle(SMoveVehiclePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.client.player.getLowestRidingEntity();
      if (entity != this.client.player && entity.canPassengerSteer()) {
         entity.setPositionAndRotation(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getYaw(), packetIn.getPitch());
         this.netManager.sendPacket(new CMoveVehiclePacket(entity));
      }

   }

   public void handleOpenBookPacket(SOpenBookWindowPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      ItemStack itemstack = this.client.player.getHeldItem(packetIn.getHand());
      if (itemstack.getItem() == Items.WRITTEN_BOOK) {
         this.client.displayGuiScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(itemstack)));
      }

   }

   /**
    * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
    * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the player
    * instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
    * resourcepack for the client to load.
    */
   public void handleCustomPayload(SCustomPayloadPlayPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      ResourceLocation resourcelocation = packetIn.getChannelName();
      PacketBuffer packetbuffer = null;

      try {
         packetbuffer = packetIn.getBufferData();
         if (SCustomPayloadPlayPacket.BRAND.equals(resourcelocation)) {
            this.client.player.setServerBrand(packetbuffer.readString(32767));
         } else if (SCustomPayloadPlayPacket.DEBUG_PATH.equals(resourcelocation)) {
            int i = packetbuffer.readInt();
            float f = packetbuffer.readFloat();
            Path path = Path.read(packetbuffer);
            this.client.debugRenderer.pathfinding.addPath(i, path, f);
         } else if (SCustomPayloadPlayPacket.DEBUG_NEIGHBORS_UPDATE.equals(resourcelocation)) {
            long l1 = packetbuffer.readVarLong();
            BlockPos blockpos8 = packetbuffer.readBlockPos();
            ((NeighborsUpdateDebugRenderer)this.client.debugRenderer.neighborsUpdate).addUpdate(l1, blockpos8);
         } else if (SCustomPayloadPlayPacket.DEBUG_CAVES.equals(resourcelocation)) {
            BlockPos blockpos1 = packetbuffer.readBlockPos();
            int k2 = packetbuffer.readInt();
            List<BlockPos> list1 = Lists.newArrayList();
            List<Float> list = Lists.newArrayList();

            for(int j = 0; j < k2; ++j) {
               list1.add(packetbuffer.readBlockPos());
               list.add(packetbuffer.readFloat());
            }

            this.client.debugRenderer.cave.addCave(blockpos1, list1, list);
         } else if (SCustomPayloadPlayPacket.DEBUG_STRUCTURES.equals(resourcelocation)) {
            DimensionType dimensiontype = DimensionType.getById(packetbuffer.readInt());
            MutableBoundingBox mutableboundingbox = new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());
            int i4 = packetbuffer.readInt();
            List<MutableBoundingBox> list2 = Lists.newArrayList();
            List<Boolean> list4 = Lists.newArrayList();

            for(int k = 0; k < i4; ++k) {
               list2.add(new MutableBoundingBox(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt()));
               list4.add(packetbuffer.readBoolean());
            }

            this.client.debugRenderer.structure.func_223454_a(mutableboundingbox, list2, list4, dimensiontype);
         } else if (SCustomPayloadPlayPacket.DEBUG_WORLDGEN_ATTEMPT.equals(resourcelocation)) {
            ((WorldGenAttemptsDebugRenderer)this.client.debugRenderer.worldGenAttempts).addAttempt(packetbuffer.readBlockPos(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat(), packetbuffer.readFloat());
         } else if (SCustomPayloadPlayPacket.DEBUG_VILLAGE_SECTIONS.equals(resourcelocation)) {
            int i2 = packetbuffer.readInt();

            for(int l2 = 0; l2 < i2; ++l2) {
               this.client.debugRenderer.field_217741_m.func_217701_a(packetbuffer.readSectionPos());
            }

            int i3 = packetbuffer.readInt();

            for(int j4 = 0; j4 < i3; ++j4) {
               this.client.debugRenderer.field_217741_m.func_217700_b(packetbuffer.readSectionPos());
            }
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_ADDED.equals(resourcelocation)) {
            BlockPos blockpos2 = packetbuffer.readBlockPos();
            String s8 = packetbuffer.readString();
            int k4 = packetbuffer.readInt();
            PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = new PointOfInterestDebugRenderer.POIInfo(blockpos2, s8, k4);
            this.client.debugRenderer.field_217741_m.func_217691_a(pointofinterestdebugrenderer$poiinfo);
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_REMOVED.equals(resourcelocation)) {
            BlockPos blockpos3 = packetbuffer.readBlockPos();
            this.client.debugRenderer.field_217741_m.func_217698_a(blockpos3);
         } else if (SCustomPayloadPlayPacket.DEBUG_POI_TICKET_COUNT.equals(resourcelocation)) {
            BlockPos blockpos4 = packetbuffer.readBlockPos();
            int j3 = packetbuffer.readInt();
            this.client.debugRenderer.field_217741_m.func_217706_a(blockpos4, j3);
         } else if (SCustomPayloadPlayPacket.DEBUG_GOAL_SELECTOR.equals(resourcelocation)) {
            BlockPos blockpos5 = packetbuffer.readBlockPos();
            int k3 = packetbuffer.readInt();
            int l4 = packetbuffer.readInt();
            List<EntityAIDebugRenderer.Entry> list3 = Lists.newArrayList();

            for(int i6 = 0; i6 < l4; ++i6) {
               int j6 = packetbuffer.readInt();
               boolean flag = packetbuffer.readBoolean();
               String s = packetbuffer.readString(255);
               list3.add(new EntityAIDebugRenderer.Entry(blockpos5, j6, s, flag));
            }

            this.client.debugRenderer.field_217742_n.func_217682_a(k3, list3);
         } else if (SCustomPayloadPlayPacket.DEBUG_RAIDS.equals(resourcelocation)) {
            int j2 = packetbuffer.readInt();
            Collection<BlockPos> collection = Lists.newArrayList();

            for(int i5 = 0; i5 < j2; ++i5) {
               collection.add(packetbuffer.readBlockPos());
            }

            this.client.debugRenderer.field_222927_n.func_222906_a(collection);
         } else if (SCustomPayloadPlayPacket.DEBUG_BRAIN.equals(resourcelocation)) {
            double d0 = packetbuffer.readDouble();
            double d2 = packetbuffer.readDouble();
            double d4 = packetbuffer.readDouble();
            IPosition iposition = new Position(d0, d2, d4);
            UUID uuid = packetbuffer.readUniqueId();
            int l = packetbuffer.readInt();
            String s1 = packetbuffer.readString();
            String s2 = packetbuffer.readString();
            int i1 = packetbuffer.readInt();
            String s3 = packetbuffer.readString();
            boolean flag1 = packetbuffer.readBoolean();
            Path path1;
            if (flag1) {
               path1 = Path.read(packetbuffer);
            } else {
               path1 = null;
            }

            boolean flag2 = packetbuffer.readBoolean();
            PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo = new PointOfInterestDebugRenderer.BrainInfo(uuid, l, s1, s2, i1, iposition, s3, path1, flag2);
            int j1 = packetbuffer.readInt();

            for(int k1 = 0; k1 < j1; ++k1) {
               String s4 = packetbuffer.readString();
               pointofinterestdebugrenderer$braininfo.field_217751_e.add(s4);
            }

            int j7 = packetbuffer.readInt();

            for(int i8 = 0; i8 < j7; ++i8) {
               String s5 = packetbuffer.readString();
               pointofinterestdebugrenderer$braininfo.field_217752_f.add(s5);
            }

            int j8 = packetbuffer.readInt();

            for(int l8 = 0; l8 < j8; ++l8) {
               String s6 = packetbuffer.readString();
               pointofinterestdebugrenderer$braininfo.field_217753_g.add(s6);
            }

            int i9 = packetbuffer.readInt();

            for(int j9 = 0; j9 < i9; ++j9) {
               BlockPos blockpos = packetbuffer.readBlockPos();
               pointofinterestdebugrenderer$braininfo.field_217754_h.add(blockpos);
            }

            int k9 = packetbuffer.readInt();

            for(int l9 = 0; l9 < k9; ++l9) {
               String s7 = packetbuffer.readString();
               pointofinterestdebugrenderer$braininfo.field_223457_m.add(s7);
            }

            this.client.debugRenderer.field_217741_m.func_217692_a(pointofinterestdebugrenderer$braininfo);
         } else if (SCustomPayloadPlayPacket.field_229727_m_.equals(resourcelocation)) {
            double d1 = packetbuffer.readDouble();
            double d3 = packetbuffer.readDouble();
            double d5 = packetbuffer.readDouble();
            IPosition iposition1 = new Position(d1, d3, d5);
            UUID uuid1 = packetbuffer.readUniqueId();
            int k6 = packetbuffer.readInt();
            boolean flag4 = packetbuffer.readBoolean();
            BlockPos blockpos9 = null;
            if (flag4) {
               blockpos9 = packetbuffer.readBlockPos();
            }

            boolean flag5 = packetbuffer.readBoolean();
            BlockPos blockpos10 = null;
            if (flag5) {
               blockpos10 = packetbuffer.readBlockPos();
            }

            int l6 = packetbuffer.readInt();
            boolean flag6 = packetbuffer.readBoolean();
            Path path2 = null;
            if (flag6) {
               path2 = Path.read(packetbuffer);
            }

            BeeDebugRenderer.Bee beedebugrenderer$bee = new BeeDebugRenderer.Bee(uuid1, k6, iposition1, path2, blockpos9, blockpos10, l6);
            int i7 = packetbuffer.readInt();

            for(int k7 = 0; k7 < i7; ++k7) {
               String s11 = packetbuffer.readString();
               beedebugrenderer$bee.field_229005_h_.add(s11);
            }

            int l7 = packetbuffer.readInt();

            for(int k8 = 0; k8 < l7; ++k8) {
               BlockPos blockpos11 = packetbuffer.readBlockPos();
               beedebugrenderer$bee.field_229006_i_.add(blockpos11);
            }

            this.client.debugRenderer.field_229017_n_.func_228964_a_(beedebugrenderer$bee);
         } else if (SCustomPayloadPlayPacket.field_229728_n_.equals(resourcelocation)) {
            BlockPos blockpos6 = packetbuffer.readBlockPos();
            String s9 = packetbuffer.readString();
            int j5 = packetbuffer.readInt();
            int k5 = packetbuffer.readInt();
            boolean flag3 = packetbuffer.readBoolean();
            BeeDebugRenderer.Hive beedebugrenderer$hive = new BeeDebugRenderer.Hive(blockpos6, s9, j5, k5, flag3, this.world.getGameTime());
            this.client.debugRenderer.field_229017_n_.func_228966_a_(beedebugrenderer$hive);
         } else if (SCustomPayloadPlayPacket.field_229730_p_.equals(resourcelocation)) {
            this.client.debugRenderer.field_229018_q_.clear();
         } else if (SCustomPayloadPlayPacket.field_229729_o_.equals(resourcelocation)) {
            BlockPos blockpos7 = packetbuffer.readBlockPos();
            int l3 = packetbuffer.readInt();
            String s10 = packetbuffer.readString();
            int l5 = packetbuffer.readInt();
            this.client.debugRenderer.field_229018_q_.func_229022_a_(blockpos7, l3, s10, l5);
         } else {
            if (!net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(packetIn, this.netManager))
            LOGGER.warn("Unknown custom packet identifier: {}", (Object)resourcelocation);
         }
      } finally {
         if (packetbuffer != null) {
            if (false) // Forge: let packet handle releasing buffer
            packetbuffer.release();
         }

      }

   }

   /**
    * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
    */
   public void handleScoreboardObjective(SScoreboardObjectivePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = packetIn.getObjectiveName();
      if (packetIn.getAction() == 0) {
         scoreboard.addObjective(s, ScoreCriteria.DUMMY, packetIn.getDisplayName(), packetIn.getRenderType());
      } else if (scoreboard.hasObjective(s)) {
         ScoreObjective scoreobjective = scoreboard.getObjective(s);
         if (packetIn.getAction() == 1) {
            scoreboard.removeObjective(scoreobjective);
         } else if (packetIn.getAction() == 2) {
            scoreobjective.setRenderType(packetIn.getRenderType());
            scoreobjective.setDisplayName(packetIn.getDisplayName());
         }
      }

   }

   /**
    * Either updates the score with a specified value or removes the score for an objective
    */
   public void handleUpdateScore(SUpdateScorePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = packetIn.getObjectiveName();
      switch(packetIn.getAction()) {
      case CHANGE:
         ScoreObjective scoreobjective = scoreboard.getOrCreateObjective(s);
         Score score = scoreboard.getOrCreateScore(packetIn.getPlayerName(), scoreobjective);
         score.setScorePoints(packetIn.getScoreValue());
         break;
      case REMOVE:
         scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), scoreboard.getObjective(s));
      }

   }

   /**
    * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below name)
    */
   public void handleDisplayObjective(SDisplayObjectivePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      String s = packetIn.getName();
      ScoreObjective scoreobjective = s == null ? null : scoreboard.getOrCreateObjective(s);
      scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), scoreobjective);
   }

   /**
    * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
    * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
    */
   public void handleTeams(STeamsPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Scoreboard scoreboard = this.world.getScoreboard();
      ScorePlayerTeam scoreplayerteam;
      if (packetIn.getAction() == 0) {
         scoreplayerteam = scoreboard.createTeam(packetIn.getName());
      } else {
         scoreplayerteam = scoreboard.getTeam(packetIn.getName());
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {
         scoreplayerteam.setDisplayName(packetIn.getDisplayName());
         scoreplayerteam.setColor(packetIn.getColor());
         scoreplayerteam.setFriendlyFlags(packetIn.getFriendlyFlags());
         Team.Visible team$visible = Team.Visible.getByName(packetIn.getNameTagVisibility());
         if (team$visible != null) {
            scoreplayerteam.setNameTagVisibility(team$visible);
         }

         Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(packetIn.getCollisionRule());
         if (team$collisionrule != null) {
            scoreplayerteam.setCollisionRule(team$collisionrule);
         }

         scoreplayerteam.setPrefix(packetIn.getPrefix());
         scoreplayerteam.setSuffix(packetIn.getSuffix());
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
         for(String s : packetIn.getPlayers()) {
            scoreboard.addPlayerToTeam(s, scoreplayerteam);
         }
      }

      if (packetIn.getAction() == 4) {
         for(String s1 : packetIn.getPlayers()) {
            scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
         }
      }

      if (packetIn.getAction() == 1) {
         scoreboard.removeTeam(scoreplayerteam);
      }

   }

   /**
    * Spawns a specified number of particles at the specified location with a randomized displacement according to
    * specified bounds
    */
   public void handleParticles(SSpawnParticlePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      if (packetIn.getParticleCount() == 0) {
         double d0 = (double)(packetIn.getParticleSpeed() * packetIn.getXOffset());
         double d2 = (double)(packetIn.getParticleSpeed() * packetIn.getYOffset());
         double d4 = (double)(packetIn.getParticleSpeed() * packetIn.getZOffset());

         try {
            this.world.addParticle(packetIn.getParticle(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), d0, d2, d4);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticle());
         }
      } else {
         for(int i = 0; i < packetIn.getParticleCount(); ++i) {
            double d1 = this.avRandomizer.nextGaussian() * (double)packetIn.getXOffset();
            double d3 = this.avRandomizer.nextGaussian() * (double)packetIn.getYOffset();
            double d5 = this.avRandomizer.nextGaussian() * (double)packetIn.getZOffset();
            double d6 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
            double d7 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
            double d8 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();

            try {
               this.world.addParticle(packetIn.getParticle(), packetIn.isLongDistance(), packetIn.getXCoordinate() + d1, packetIn.getYCoordinate() + d3, packetIn.getZCoordinate() + d5, d6, d7, d8);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticle());
               return;
            }
         }
      }

   }

   /**
    * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
    * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
    * maxHealth and knockback resistance as well as reinforcement spawning chance.
    */
   public void handleEntityProperties(SEntityPropertiesPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Entity entity = this.world.getEntityByID(packetIn.getEntityId());
      if (entity != null) {
         if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
         } else {
            AbstractAttributeMap abstractattributemap = ((LivingEntity)entity).getAttributes();

            for(SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot : packetIn.getSnapshots()) {
               IAttributeInstance iattributeinstance = abstractattributemap.getAttributeInstanceByName(sentitypropertiespacket$snapshot.getName());
               if (iattributeinstance == null) {
                  iattributeinstance = abstractattributemap.registerAttribute(new RangedAttribute((IAttribute)null, sentitypropertiespacket$snapshot.getName(), 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE)); // FORGE: fix invalid value range (MC-150405)
               }

               iattributeinstance.setBaseValue(sentitypropertiespacket$snapshot.getBaseValue());
               iattributeinstance.removeAllModifiers();

               for(AttributeModifier attributemodifier : sentitypropertiespacket$snapshot.getModifiers()) {
                  iattributeinstance.applyModifier(attributemodifier);
               }
            }

         }
      }
   }

   public void handlePlaceGhostRecipe(SPlaceGhostRecipePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Container container = this.client.player.openContainer;
      if (container.windowId == packetIn.getWindowId() && container.getCanCraft(this.client.player)) {
         this.recipeManager.getRecipe(packetIn.getRecipeId()).ifPresent((p_217285_2_) -> {
            if (this.client.currentScreen instanceof IRecipeShownListener) {
               RecipeBookGui recipebookgui = ((IRecipeShownListener)this.client.currentScreen).getRecipeGui();
               recipebookgui.setupGhostRecipe(p_217285_2_, container.inventorySlots);
            }

         });
      }
   }

   public void handleUpdateLight(SUpdateLightPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      int i = packetIn.getChunkX();
      int j = packetIn.getChunkZ();
      WorldLightManager worldlightmanager = this.world.getChunkProvider().getLightManager();
      int k = packetIn.getSkyLightUpdateMask();
      int l = packetIn.getSkyLightResetMask();
      Iterator<byte[]> iterator = packetIn.getSkyLightData().iterator();
      this.setLightData(i, j, worldlightmanager, LightType.SKY, k, l, iterator);
      int i1 = packetIn.getBlockLightUpdateMask();
      int j1 = packetIn.getBlockLightResetMask();
      Iterator<byte[]> iterator1 = packetIn.getBlockLightData().iterator();
      this.setLightData(i, j, worldlightmanager, LightType.BLOCK, i1, j1, iterator1);
   }

   public void handleMerchantOffers(SMerchantOffersPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      Container container = this.client.player.openContainer;
      if (packetIn.getContainerId() == container.windowId && container instanceof MerchantContainer) {
         ((MerchantContainer)container).setClientSideOffers(new MerchantOffers(packetIn.getOffers().write()));
         ((MerchantContainer)container).setXp(packetIn.getExp());
         ((MerchantContainer)container).setMerchantLevel(packetIn.getLevel());
         ((MerchantContainer)container).func_217045_a(packetIn.func_218735_f());
         ((MerchantContainer)container).func_223431_b(packetIn.func_223477_g());
      }

   }

   public void handleUpdateViewDistancePacket(SUpdateViewDistancePacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.viewDistance = packetIn.getViewDistance();
      this.world.getChunkProvider().setViewDistance(packetIn.getViewDistance());
   }

   public void handleChunkPositionPacket(SUpdateChunkPositionPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.world.getChunkProvider().setCenter(packetIn.func_218755_b(), packetIn.func_218754_c());
   }

   public void handleAcknowledgePlayerDigging(SPlayerDiggingPacket packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
      this.client.playerController.acknowledgePlayerDiggingReceived(this.world, packetIn.getPosition(), packetIn.getBlockState(), packetIn.getAction(), packetIn.wasSuccessful());
   }

   private void setLightData(int chunkX, int chunkZ, WorldLightManager lightManager, LightType type, int p_217284_5_, int p_217284_6_, Iterator<byte[]> p_217284_7_) {
      for(int i = 0; i < 18; ++i) {
         int j = -1 + i;
         boolean flag = (p_217284_5_ & 1 << i) != 0;
         boolean flag1 = (p_217284_6_ & 1 << i) != 0;
         if (flag || flag1) {
            lightManager.setData(type, SectionPos.of(chunkX, j, chunkZ), flag ? new NibbleArray((byte[])p_217284_7_.next().clone()) : new NibbleArray());
            this.world.markSurroundingsForRerender(chunkX, j, chunkZ);
         }
      }

   }

   /**
    * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
    */
   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   @Nullable
   public NetworkPlayerInfo getPlayerInfo(UUID uniqueId) {
      return this.playerInfoMap.get(uniqueId);
   }

   /**
    * Gets the client's description information about another player on the server.
    */
   @Nullable
   public NetworkPlayerInfo getPlayerInfo(String name) {
      for(NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values()) {
         if (networkplayerinfo.getGameProfile().getName().equals(name)) {
            return networkplayerinfo;
         }
      }

      return null;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   public ClientAdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public CommandDispatcher<ISuggestionProvider> getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public ClientWorld getWorld() {
      return this.world;
   }

   public NetworkTagManager getTags() {
      return this.networkTagManager;
   }

   public NBTQueryManager getNBTQueryManager() {
      return this.nbtQueryManager;
   }

   public UUID getSessionId() {
      return this.sessionId;
   }
}