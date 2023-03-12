package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.track_api.TrackMaterial;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

public class RailwaysSequencedAssemblyRecipeGen extends RailwaysRecipeProvider {
  public RailwaysSequencedAssemblyRecipeGen(DataGenerator pGenerator) {
    super(pGenerator);
  }

  protected GeneratedRecipe create(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
    GeneratedRecipe generatedRecipe =
        c -> transform.apply(new SequencedAssemblyRecipeBuilder(Railways.asResource(name)))
            .build(c);
    all.add(generatedRecipe);
    return generatedRecipe;
  }

  final EnumMap<DyeColor, GeneratedRecipe> CONDUCTOR_CAPS = new EnumMap<>(DyeColor.class);
  final Map<TrackMaterial, GeneratedRecipe> TRACKS = new HashMap<>();
  {
    for (DyeColor color : DyeColor.values()) {
      String colorName = TextUtils.titleCaseConversion(color.getName().replace("_", " "));
      String colorReg  = color.getName().toLowerCase(Locale.ROOT);
      CONDUCTOR_CAPS.put(color, create(colorReg + "_conductor_cap", b -> b.require(CRItems.woolByColor(color))
          .transitionTo(CRItems.ITEM_INCOMPLETE_CONDUCTOR_CAP.get(color).get())
          .addOutput(CRItems.ITEM_CONDUCTOR_CAP.get(color).get(), 1)
          .loops(1)
          .addStep(CuttingRecipe::new, rb -> rb)
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.precisionMechanism()))
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.string()))
      ));
    }

    for (TrackMaterial material : TrackMaterial.allCustom(Railways.MODID)) { //TODO track api
      if (material.railsIngredient.isEmpty() || material.sleeperIngredient.isEmpty()) continue;

      TRACKS.put(material, create("track_" + material.resName(), b -> b.require(material.sleeperIngredient)
          .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(material).get())
          .addOutput(material.getTrackBlock().get(), 1)
          .loops(1)
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(material.railsIngredient))
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(material.railsIngredient))
          .addStep(PressingRecipe::new, rb -> rb)
      ));
    }

    TRACKS.put(TrackMaterial.MONORAIL, create("track_monorail", b -> b.require(Ingredients.girder())
        .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(TrackMaterial.MONORAIL).get())
        .addOutput(new ItemStack(TrackMaterial.MONORAIL.getTrackBlock().get(), 6), 1)
        .loops(1)
        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.metalBracket()))
        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.ironSheet()))
        .addStep(PressingRecipe::new, rb -> rb)
    ));
  }

  @Override
  public @NotNull String getName() {
    return "Railways' Sequenced Assembly Recipes";
  }
}
