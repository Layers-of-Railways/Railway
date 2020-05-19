package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class BlockListReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BlockListReport(DataGenerator generatorIn) {
      this.generator = generatorIn;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      JsonObject jsonobject = new JsonObject();

      for(Block block : Registry.BLOCK) {
         ResourceLocation resourcelocation = Registry.BLOCK.getKey(block);
         JsonObject jsonobject1 = new JsonObject();
         StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
         if (!statecontainer.getProperties().isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();

            for(IProperty<?> iproperty : statecontainer.getProperties()) {
               JsonArray jsonarray = new JsonArray();

               for(Comparable<?> comparable : iproperty.getAllowedValues()) {
                  jsonarray.add(Util.getValueName(iproperty, comparable));
               }

               jsonobject2.add(iproperty.getName(), jsonarray);
            }

            jsonobject1.add("properties", jsonobject2);
         }

         JsonArray jsonarray1 = new JsonArray();

         for(BlockState blockstate : statecontainer.getValidStates()) {
            JsonObject jsonobject3 = new JsonObject();
            JsonObject jsonobject4 = new JsonObject();

            for(IProperty<?> iproperty1 : statecontainer.getProperties()) {
               jsonobject4.addProperty(iproperty1.getName(), Util.getValueName(iproperty1, blockstate.get(iproperty1)));
            }

            if (jsonobject4.size() > 0) {
               jsonobject3.add("properties", jsonobject4);
            }

            jsonobject3.addProperty("id", Block.getStateId(blockstate));
            if (blockstate == block.getDefaultState()) {
               jsonobject3.addProperty("default", true);
            }

            jsonarray1.add(jsonobject3);
         }

         jsonobject1.add("states", jsonarray1);
         jsonobject.add(resourcelocation.toString(), jsonobject1);
      }

      Path path = this.generator.getOutputFolder().resolve("reports/blocks.json");
      IDataProvider.save(GSON, cache, jsonobject, path);
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Block List";
   }
}