package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.base.data.CRTagGen;
import com.railwayteam.railways.base.data.lang.CRLangGen;
import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import com.railwayteam.railways.base.data.recipe.RailwaysStandardRecipeGen;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.registry.CRCommands;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.tterrag.registrate.providers.ProviderType;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Railways {
  public static final String MODID = "railways";
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final String VERSION = findVersion();
  public static final int DATA_FIXER_VERSION = 1; // Only used for datafixers, bump whenever a block changes id etc (should not be bumped multiple times within a release)

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
          .creativeModeTab(() -> CRItems.mainCreativeTab, "Create Steam 'n' Rails");

  static {
    REGISTRATE.setTooltipModifierFactory(
            item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
    );
  }

  private static void migrateConfig(Path path, Function<String, String> converter) {
    Convert: try {

      String str = new String(Files.readAllBytes(path));
      if (str.contains("#General settings") || str.contains("[general]")) { // we found a legacy config
        String migrated;
        try {
          migrated = converter.apply(new String(Files.readAllBytes(path)));
        } catch (IOException e) {
          break Convert;
        }
        try (FileWriter writer = new FileWriter(path.toFile())) {
          writer.write(migrated);
        }
      }
    } catch (IOException ignored) {}
  }

  public static void init() {
    Path configDir = Utils.configDir();
    Path clientConfigDir = configDir.resolve(MODID + "-client.toml");
    migrateConfig(clientConfigDir, CRConfigs::migrateClient);

    Path commonConfigDir = configDir.resolve(MODID + "-common.toml");
    migrateConfig(commonConfigDir, CRConfigs::migrateCommon);

    ModSetup.register();
    finalizeRegistrate();

    registerCommands(CRCommands::register);
    CRPackets.PACKETS.registerC2SListener();

/*    RegistrationListening.whenBothRegistered(
            Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("create", "track"),
            Registry.BLOCK, CRBlocks.MONORAIL_TRACK.getId(), // last track
            (type, block) -> TrackMaterial.addCustomValidTracks(type)
    );*/

    if (Utils.isDevEnv() && !Mods.BYG.isLoaded && !Mods.SODIUM.isLoaded) // force all mixins to load in dev
      MixinEnvironment.getCurrentEnvironment().audit();
  }

  public static ResourceLocation asResource(String name) {
    return new ResourceLocation(MODID, name);
  }

  public static void gatherData(DataGenerator gen) {
    REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CRTagGen::generateBlockTags);
    REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CRTagGen::generateItemTags);
    REGISTRATE.addDataGenerator(ProviderType.LANG, CRLangGen::generate);
    gen.addProvider(true, RailwaysSequencedAssemblyRecipeGen.create(gen));
    gen.addProvider(true, RailwaysStandardRecipeGen.create(gen));
    PonderLocalization.provideRegistrateLang(REGISTRATE);
  }

  public static CreateRegistrate registrate() {
    return REGISTRATE;
  }

  @ExpectPlatform
  public static String findVersion() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void finalizeRegistrate() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
    throw new AssertionError();
  }

  // All the below are helper variables for switch mixins
  @ApiStatus.Internal
  public static boolean trackEdgeTemporarilyFlipped = false;

  @ApiStatus.Internal
  public static boolean trackEdgeCarriageTravelling = false;

  @ApiStatus.Internal
  public static boolean temporarilySkipSwitches = false;

  @ApiStatus.Internal
  public static int signalPropagatorCallDepth = 0;

  @ApiStatus.Internal
  public static int navigationCallDepth = 0;

  @ApiStatus.Internal
  public static boolean skipUprightCalculation = false;
}
