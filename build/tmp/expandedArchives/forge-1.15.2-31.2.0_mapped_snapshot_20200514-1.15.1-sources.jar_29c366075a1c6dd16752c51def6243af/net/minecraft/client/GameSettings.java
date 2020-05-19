package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.ToggleableKeyBinding;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Type TYPE_LIST_STRING = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(':').limit(2);
   public double mouseSensitivity = 0.5D;
   public int renderDistanceChunks = -1;
   public int framerateLimit = 120;
   public CloudOption cloudOption = CloudOption.FANCY;
   public boolean fancyGraphics = true;
   public AmbientOcclusionStatus ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public ChatVisibility chatVisibility = ChatVisibility.FULL;
   public double chatOpacity = 1.0D;
   public double accessibilityTextBackgroundOpacity = 0.5D;
   @Nullable
   public String fullscreenResolution;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> setModelParts = Sets.newHashSet(PlayerModelPart.values());
   public HandSide mainHand = HandSide.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public int mipmapLevels = 4;
   private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
   public boolean useNativeTransport = true;
   public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public int biomeBlendRadius = 2;
   public double mouseWheelSensitivity = 1.0D;
   public boolean rawMouseInput = true;
   public int glDebugVerbosity = 1;
   public boolean autoJump = true;
   public boolean autoSuggestCommands = true;
   public boolean chatColor = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public boolean vsync = true;
   public boolean entityShadows = true;
   public boolean forceUnicodeFont;
   public boolean invertMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications = true;
   public boolean reducedDebugInfo;
   public boolean snooper = true;
   public boolean showSubtitles;
   public boolean accessibilityTextBackground = true;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean viewBobbing = true;
   public boolean toggleCrouch;
   public boolean toggleSprint;
   public boolean field_230152_Z_;
   public final KeyBinding keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
   public final KeyBinding keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
   public final KeyBinding keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
   public final KeyBinding keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
   public final KeyBinding keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
   public final KeyBinding keyBindSneak = new ToggleableKeyBinding("key.sneak", 340, "key.categories.movement", () -> {
      return this.toggleCrouch;
   });
   public final KeyBinding keyBindSprint = new ToggleableKeyBinding("key.sprint", 341, "key.categories.movement", () -> {
      return this.toggleSprint;
   });
   public final KeyBinding keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
   public final KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
   public final KeyBinding keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
   public final KeyBinding keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyBinding keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyBinding keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
   public final KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyBinding keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
   public final KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
   public final KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
   public final KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
   public final KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
   public final KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
   public final KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
   public KeyBinding[] keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
   protected Minecraft mc;
   private final File optionsFile;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer = "";
   public boolean smoothCamera;
   public double fov = 70.0D;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles = ParticleStatus.ALL;
   public NarratorStatus narrator = NarratorStatus.OFF;
   public String language = "en_us";

   public GameSettings(Minecraft mcIn, File mcDataDir) {
      setForgeKeybindProperties();
      this.mc = mcIn;
      this.optionsFile = new File(mcDataDir, "options.txt");
      if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         AbstractOption.RENDER_DISTANCE.setMaxValue(32.0F);
      } else {
         AbstractOption.RENDER_DISTANCE.setMaxValue(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
      this.loadOptions();
   }

   public float getTextBackgroundOpacity(float p_216840_1_) {
      return this.accessibilityTextBackground ? p_216840_1_ : (float)this.accessibilityTextBackgroundOpacity;
   }

   public int getTextBackgroundColor(float p_216841_1_) {
      return (int)(this.getTextBackgroundOpacity(p_216841_1_) * 255.0F) << 24 & -16777216;
   }

   public int getChatBackgroundColor(int p_216839_1_) {
      return this.accessibilityTextBackground ? p_216839_1_ : (int)(this.accessibilityTextBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKeyBindingCode(KeyBinding keyBindingIn, InputMappings.Input inputIn) {
      keyBindingIn.bind(inputIn);
      this.saveOptions();
   }

   /**
    * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
    */
   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.soundLevels.clear();
         CompoundNBT compoundnbt = new CompoundNBT();

         try (BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8)) {
            bufferedreader.lines().forEach((p_230004_1_) -> {
               try {
                  Iterator<String> iterator = KEY_VALUE_SPLITTER.split(p_230004_1_).iterator();
                  compoundnbt.putString(iterator.next(), iterator.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", (Object)p_230004_1_);
               }

            });
         }

         CompoundNBT compoundnbt1 = this.dataFix(compoundnbt);

         for(String s : compoundnbt1.keySet()) {
            String s1 = compoundnbt1.getString(s);

            try {
               if ("autoJump".equals(s)) {
                  AbstractOption.AUTO_JUMP.set(this, s1);
               }

               if ("autoSuggestions".equals(s)) {
                  AbstractOption.AUTO_SUGGEST_COMMANDS.set(this, s1);
               }

               if ("chatColors".equals(s)) {
                  AbstractOption.CHAT_COLOR.set(this, s1);
               }

               if ("chatLinks".equals(s)) {
                  AbstractOption.CHAT_LINKS.set(this, s1);
               }

               if ("chatLinksPrompt".equals(s)) {
                  AbstractOption.CHAT_LINKS_PROMPT.set(this, s1);
               }

               if ("enableVsync".equals(s)) {
                  AbstractOption.VSYNC.set(this, s1);
               }

               if ("entityShadows".equals(s)) {
                  AbstractOption.ENTITY_SHADOWS.set(this, s1);
               }

               if ("forceUnicodeFont".equals(s)) {
                  AbstractOption.FORCE_UNICODE_FONT.set(this, s1);
               }

               if ("discrete_mouse_scroll".equals(s)) {
                  AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s1);
               }

               if ("invertYMouse".equals(s)) {
                  AbstractOption.INVERT_MOUSE.set(this, s1);
               }

               if ("realmsNotifications".equals(s)) {
                  AbstractOption.REALMS_NOTIFICATIONS.set(this, s1);
               }

               if ("reducedDebugInfo".equals(s)) {
                  AbstractOption.REDUCED_DEBUG_INFO.set(this, s1);
               }

               if ("showSubtitles".equals(s)) {
                  AbstractOption.SHOW_SUBTITLES.set(this, s1);
               }

               if ("snooperEnabled".equals(s)) {
                  AbstractOption.SNOOPER.set(this, s1);
               }

               if ("touchscreen".equals(s)) {
                  AbstractOption.TOUCHSCREEN.set(this, s1);
               }

               if ("fullscreen".equals(s)) {
                  AbstractOption.FULLSCREEN.set(this, s1);
               }

               if ("bobView".equals(s)) {
                  AbstractOption.VIEW_BOBBING.set(this, s1);
               }

               if ("toggleCrouch".equals(s)) {
                  this.toggleCrouch = "true".equals(s1);
               }

               if ("toggleSprint".equals(s)) {
                  this.toggleSprint = "true".equals(s1);
               }

               if ("mouseSensitivity".equals(s)) {
                  this.mouseSensitivity = (double)parseFloat(s1);
               }

               if ("fov".equals(s)) {
                  this.fov = (double)(parseFloat(s1) * 40.0F + 70.0F);
               }

               if ("gamma".equals(s)) {
                  this.gamma = (double)parseFloat(s1);
               }

               if ("renderDistance".equals(s)) {
                  this.renderDistanceChunks = Integer.parseInt(s1);
               }

               if ("guiScale".equals(s)) {
                  this.guiScale = Integer.parseInt(s1);
               }

               if ("particles".equals(s)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(s1));
               }

               if ("maxFps".equals(s)) {
                  this.framerateLimit = Integer.parseInt(s1);
                  if (this.mc.getMainWindow() != null) {
                     this.mc.getMainWindow().setFramerateLimit(this.framerateLimit);
                  }
               }

               if ("difficulty".equals(s)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(s1));
               }

               if ("fancyGraphics".equals(s)) {
                  this.fancyGraphics = "true".equals(s1);
               }

               if ("tutorialStep".equals(s)) {
                  this.tutorialStep = TutorialSteps.byName(s1);
               }

               if ("ao".equals(s)) {
                  if ("true".equals(s1)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(s1)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.getValue(Integer.parseInt(s1));
                  }
               }

               if ("renderClouds".equals(s)) {
                  if ("true".equals(s1)) {
                     this.cloudOption = CloudOption.FANCY;
                  } else if ("false".equals(s1)) {
                     this.cloudOption = CloudOption.OFF;
                  } else if ("fast".equals(s1)) {
                     this.cloudOption = CloudOption.FAST;
                  }
               }

               if ("attackIndicator".equals(s)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s1));
               }

               if ("resourcePacks".equals(s)) {
                  this.resourcePacks = JSONUtils.fromJson(GSON, s1, TYPE_LIST_STRING);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s)) {
                  this.incompatibleResourcePacks = JSONUtils.fromJson(GSON, s1, TYPE_LIST_STRING);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s)) {
                  this.lastServer = s1;
               }

               if ("lang".equals(s)) {
                  this.language = s1;
               }

               if ("chatVisibility".equals(s)) {
                  this.chatVisibility = ChatVisibility.getValue(Integer.parseInt(s1));
               }

               if ("chatOpacity".equals(s)) {
                  this.chatOpacity = (double)parseFloat(s1);
               }

               if ("textBackgroundOpacity".equals(s)) {
                  this.accessibilityTextBackgroundOpacity = (double)parseFloat(s1);
               }

               if ("backgroundForChatOnly".equals(s)) {
                  this.accessibilityTextBackground = "true".equals(s1);
               }

               if ("fullscreenResolution".equals(s)) {
                  this.fullscreenResolution = s1;
               }

               if ("hideServerAddress".equals(s)) {
                  this.hideServerAddress = "true".equals(s1);
               }

               if ("advancedItemTooltips".equals(s)) {
                  this.advancedItemTooltips = "true".equals(s1);
               }

               if ("pauseOnLostFocus".equals(s)) {
                  this.pauseOnLostFocus = "true".equals(s1);
               }

               if ("overrideHeight".equals(s)) {
                  this.overrideHeight = Integer.parseInt(s1);
               }

               if ("overrideWidth".equals(s)) {
                  this.overrideWidth = Integer.parseInt(s1);
               }

               if ("heldItemTooltips".equals(s)) {
                  this.heldItemTooltips = "true".equals(s1);
               }

               if ("chatHeightFocused".equals(s)) {
                  this.chatHeightFocused = (double)parseFloat(s1);
               }

               if ("chatHeightUnfocused".equals(s)) {
                  this.chatHeightUnfocused = (double)parseFloat(s1);
               }

               if ("chatScale".equals(s)) {
                  this.chatScale = (double)parseFloat(s1);
               }

               if ("chatWidth".equals(s)) {
                  this.chatWidth = (double)parseFloat(s1);
               }

               if ("mipmapLevels".equals(s)) {
                  this.mipmapLevels = Integer.parseInt(s1);
               }

               if ("useNativeTransport".equals(s)) {
                  this.useNativeTransport = "true".equals(s1);
               }

               if ("mainHand".equals(s)) {
                  this.mainHand = "left".equals(s1) ? HandSide.LEFT : HandSide.RIGHT;
               }

               if ("narrator".equals(s)) {
                  this.narrator = NarratorStatus.byId(Integer.parseInt(s1));
               }

               if ("biomeBlendRadius".equals(s)) {
                  this.biomeBlendRadius = Integer.parseInt(s1);
               }

               if ("mouseWheelSensitivity".equals(s)) {
                  this.mouseWheelSensitivity = (double)parseFloat(s1);
               }

               if ("rawMouseInput".equals(s)) {
                  this.rawMouseInput = "true".equals(s1);
               }

               if ("glDebugVerbosity".equals(s)) {
                  this.glDebugVerbosity = Integer.parseInt(s1);
               }

               if ("skipMultiplayerWarning".equals(s)) {
                  this.field_230152_Z_ = "true".equals(s1);
               }

               for(KeyBinding keybinding : this.keyBindings) {
                  if (s.equals("key_" + keybinding.getKeyDescription())) {
                     if (s1.indexOf(':') != -1) {
                        String[] pts = s1.split(":");
                        keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(pts[1]), InputMappings.getInputByName(pts[0]));
                     } else
                        keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, InputMappings.getInputByName(s1));
                  }
               }

               for(SoundCategory soundcategory : SoundCategory.values()) {
                  if (s.equals("soundCategory_" + soundcategory.getName())) {
                     this.soundLevels.put(soundcategory, parseFloat(s1));
                  }
               }

               for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
                  if (s.equals("modelPart_" + playermodelpart.getPartName())) {
                     this.setModelPartEnabled(playermodelpart, "true".equals(s1));
                  }
               }
            } catch (Exception var19) {
               LOGGER.warn("Skipping bad option: {}:{}", s, s1);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } catch (Exception exception) {
         LOGGER.error("Failed to load options", (Throwable)exception);
      }

   }

   private CompoundNBT dataFix(CompoundNBT nbt) {
      int i = 0;

      try {
         i = Integer.parseInt(nbt.getString("version"));
      } catch (RuntimeException var4) {
         ;
      }

      return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, nbt, i);
   }

   /**
    * Parses a string into a float.
    */
   private static float parseFloat(String p_74305_0_) {
      if ("true".equals(p_74305_0_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_0_) ? 0.0F : Float.parseFloat(p_74305_0_);
      }
   }

   /**
    * Saves the options to the options file.
    */
   public void saveOptions() {
      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
      try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
         printwriter.println("version:" + SharedConstants.getVersion().getWorldVersion());
         printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
         printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGEST_COMMANDS.get(this));
         printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
         printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
         printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
         printwriter.println("enableVsync:" + AbstractOption.VSYNC.get(this));
         printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
         printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
         printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
         printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
         printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
         printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
         printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER.get(this));
         printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
         printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
         printwriter.println("fullscreen:" + AbstractOption.FULLSCREEN.get(this));
         printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
         printwriter.println("toggleCrouch:" + this.toggleCrouch);
         printwriter.println("toggleSprint:" + this.toggleSprint);
         printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
         printwriter.println("gamma:" + this.gamma);
         printwriter.println("renderDistance:" + this.renderDistanceChunks);
         printwriter.println("guiScale:" + this.guiScale);
         printwriter.println("particles:" + this.particles.getId());
         printwriter.println("maxFps:" + this.framerateLimit);
         printwriter.println("difficulty:" + this.difficulty.getId());
         printwriter.println("fancyGraphics:" + this.fancyGraphics);
         printwriter.println("ao:" + this.ambientOcclusionStatus.getId());
         printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
         switch(this.cloudOption) {
         case FANCY:
            printwriter.println("renderClouds:true");
            break;
         case FAST:
            printwriter.println("renderClouds:fast");
            break;
         case OFF:
            printwriter.println("renderClouds:false");
         }

         printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
         printwriter.println("lastServer:" + this.lastServer);
         printwriter.println("lang:" + this.language);
         printwriter.println("chatVisibility:" + this.chatVisibility.getId());
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("textBackgroundOpacity:" + this.accessibilityTextBackgroundOpacity);
         printwriter.println("backgroundForChatOnly:" + this.accessibilityTextBackground);
         if (this.mc.getMainWindow().getVideoMode().isPresent()) {
            printwriter.println("fullscreenResolution:" + this.mc.getMainWindow().getVideoMode().get().getSettingsString());
         }

         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("useNativeTransport:" + this.useNativeTransport);
         printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
         printwriter.println("attackIndicator:" + this.attackIndicator.getId());
         printwriter.println("narrator:" + this.narrator.getId());
         printwriter.println("tutorialStep:" + this.tutorialStep.getName());
         printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
         printwriter.println("rawMouseInput:" + AbstractOption.RAW_MOUSE_INPUT.get(this));
         printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
         printwriter.println("skipMultiplayerWarning:" + this.field_230152_Z_);

         for(KeyBinding keybinding : this.keyBindings) {
            printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey() + (keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? ":" + keybinding.getKeyModifier() : ""));
         }

         for(SoundCategory soundcategory : SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
         }

         for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
            printwriter.println("modelPart_" + playermodelpart.getPartName() + ":" + this.setModelParts.contains(playermodelpart));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to save options", (Throwable)exception);
      }

      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory category) {
      return this.soundLevels.containsKey(category) ? this.soundLevels.get(category) : 1.0F;
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      this.soundLevels.put(category, volume);
      this.mc.getSoundHandler().setSoundLevel(category, volume);
   }

   /**
    * Send a client info packet with settings information to the server
    */
   public void sendSettingsToServer() {
      if (this.mc.player != null) {
         int i = 0;

         for(PlayerModelPart playermodelpart : this.setModelParts) {
            i |= playermodelpart.getPartMask();
         }

         this.mc.player.connection.sendPacket(new CClientSettingsPacket(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColor, i, this.mainHand));
      }

   }

   public Set<PlayerModelPart> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(PlayerModelPart modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(PlayerModelPart modelPart) {
      if (this.getModelParts().contains(modelPart)) {
         this.setModelParts.remove(modelPart);
      } else {
         this.setModelParts.add(modelPart);
      }

      this.sendSettingsToServer();
   }

   public CloudOption getCloudOption() {
      return this.renderDistanceChunks >= 4 ? this.cloudOption : CloudOption.OFF;
   }

   /**
    * Return true if the client connect to a server using the native transport system
    */
   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void fillResourcePackList(ResourcePackList<ClientResourcePackInfo> resourcePackListIn) {
      resourcePackListIn.reloadPacksFromFinders();
      Set<ClientResourcePackInfo> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         ClientResourcePackInfo clientresourcepackinfo = resourcePackListIn.getPackInfo(s);
         if (clientresourcepackinfo == null && !s.startsWith("file/")) {
            clientresourcepackinfo = resourcePackListIn.getPackInfo("file/" + s);
         }

         if (clientresourcepackinfo == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
            iterator.remove();
         } else if (!clientresourcepackinfo.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
            iterator.remove();
         } else if (clientresourcepackinfo.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(clientresourcepackinfo);
         }
      }

      resourcePackListIn.setEnabledPacks(set);
   }

   private void setForgeKeybindProperties() {
      net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
      keyBindForward.setKeyConflictContext(inGame);
      keyBindLeft.setKeyConflictContext(inGame);
      keyBindBack.setKeyConflictContext(inGame);
      keyBindRight.setKeyConflictContext(inGame);
      keyBindJump.setKeyConflictContext(inGame);
      keyBindSneak.setKeyConflictContext(inGame);
      keyBindSprint.setKeyConflictContext(inGame);
      keyBindAttack.setKeyConflictContext(inGame);
      keyBindChat.setKeyConflictContext(inGame);
      keyBindPlayerList.setKeyConflictContext(inGame);
      keyBindCommand.setKeyConflictContext(inGame);
      keyBindTogglePerspective.setKeyConflictContext(inGame);
      keyBindSmoothCamera.setKeyConflictContext(inGame);
      keyBindSwapHands.setKeyConflictContext(inGame);
   }
}