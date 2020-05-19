package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {
   public static void main(String[] p_main_0_) throws IOException {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
      OptionSpec<Void> optionspec1 = optionparser.accepts("server", "Include server generators");
      OptionSpec<Void> optionspec2 = optionparser.accepts("client", "Include client generators");
      OptionSpec<Void> optionspec3 = optionparser.accepts("dev", "Include development tools");
      OptionSpec<Void> optionspec4 = optionparser.accepts("reports", "Include data reports");
      OptionSpec<Void> optionspec5 = optionparser.accepts("validate", "Validate inputs");
      OptionSpec<Void> optionspec6 = optionparser.accepts("all", "Include all generators");
      OptionSpec<String> optionspec7 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
      OptionSpec<String> optionspec8 = optionparser.accepts("input", "Input folder").withRequiredArg();
      OptionSpec<String> existing = optionparser.accepts("existing", "Existing resource packs that generated resources can reference").withRequiredArg();
      OptionSpec<java.io.File> gameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(java.io.File.class).defaultsTo(new java.io.File(".")).required(); //Need by modlauncher, so lets just eat it
      OptionSpec<String> mod = optionparser.accepts("mod", "A modid to dump").withRequiredArg().withValuesSeparatedBy(",");
      OptionSet optionset = optionparser.parse(p_main_0_);
      if (!optionset.has(optionspec) && optionset.hasOptions() && !(optionset.specs().size() == 1 && optionset.has(gameDir))) {
         Path path = Paths.get(optionspec7.value(optionset));
         boolean flag = optionset.has(optionspec6);
         boolean flag1 = flag || optionset.has(optionspec2);
         boolean flag2 = flag || optionset.has(optionspec1);
         boolean flag3 = flag || optionset.has(optionspec3);
         boolean flag4 = flag || optionset.has(optionspec4);
         boolean flag5 = flag || optionset.has(optionspec5);
         Collection<Path> inputs = optionset.valuesOf(optionspec8).stream().map(Paths::get).collect(Collectors.toList());
         Collection<Path> existingPacks = optionset.valuesOf(existing).stream().map(Paths::get).collect(Collectors.toList());
         java.util.Set<String> mods = new java.util.HashSet<>(optionset.valuesOf(mod));
         net.minecraftforge.fml.ModLoader.get().runDataGenerator(mods, path, inputs, existingPacks, flag2, flag1, flag3, flag4, flag5);
         if (mods.contains("minecraft") || mods.isEmpty())
            makeGenerator(mods.isEmpty() ? path : path.resolve("minecraft"), inputs, flag1, flag2, flag3, flag4, flag5).run();
      } else {
         optionparser.printHelpOn(System.out);
      }
   }

   /**
    * Creates a data generator based on the given options
    */
   public static DataGenerator makeGenerator(Path output, Collection<Path> inputs, boolean client, boolean server, boolean dev, boolean reports, boolean validate) {
      DataGenerator datagenerator = new DataGenerator(output, inputs);
      if (client || server) {
         datagenerator.addProvider((new SNBTToNBTConverter(datagenerator)).func_225369_a(new StructureUpdater()));
      }

      if (server) {
         datagenerator.addProvider(new FluidTagsProvider(datagenerator));
         datagenerator.addProvider(new BlockTagsProvider(datagenerator));
         datagenerator.addProvider(new ItemTagsProvider(datagenerator));
         datagenerator.addProvider(new EntityTypeTagsProvider(datagenerator));
         datagenerator.addProvider(new RecipeProvider(datagenerator));
         datagenerator.addProvider(new AdvancementProvider(datagenerator));
         datagenerator.addProvider(new LootTableProvider(datagenerator));
      }

      if (dev) {
         datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
      }

      if (reports) {
         datagenerator.addProvider(new BlockListReport(datagenerator));
         datagenerator.addProvider(new RegistryDumpReport(datagenerator));
         datagenerator.addProvider(new CommandsReport(datagenerator));
      }

      return datagenerator;
   }
}