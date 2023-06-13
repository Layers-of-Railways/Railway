package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.trains.track.TrackMaterial;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.TagValueAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.TRACK_COMPAT_MODS;

public abstract class RailwaysSequencedAssemblyRecipeGen extends RailwaysRecipeProvider {
  protected RailwaysSequencedAssemblyRecipeGen(DataGenerator pGenerator) {
    super(pGenerator);
  }

  @ExpectPlatform
  public static RecipeProvider create(DataGenerator gen) {
    throw new AssertionError();
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

    List<TrackMaterial> trackMaterials = new ArrayList<>(TrackMaterial.allFromMod(Railways.MODID));

    // Add all mod compat tracks
    for (String mod : TRACK_COMPAT_MODS)
      trackMaterials.addAll(TrackMaterial.allFromMod(mod));

    for (TrackMaterial material : trackMaterials) {
      if (material.railsIngredient.isEmpty() || material.sleeperIngredient.isEmpty()) continue;

      Ingredient railsIngredient = material.railsIngredient;
      if (railsIngredient.values.length == 2 && Arrays.stream(railsIngredient.values).allMatch((value) -> {
        return value instanceof Ingredient.TagValue tagValue
            && (tagValue.tag.equals(AllTags.forgeItemTag("nuggets/iron"))
                || tagValue.tag.equals(AllTags.forgeItemTag("nuggets/zinc")));
      })) {
        railsIngredient = Ingredient.fromValues(Stream.of(
            TagValueAccessor.createTagValue(Ingredients.ironNugget()),
            TagValueAccessor.createTagValue(Ingredients.zincNugget())));
      }

      Ingredient finalRailsIngredient = railsIngredient;

      TRACKS.put(material, create(
          "track_" + (material.id.getNamespace().equals(Railways.MODID)
              ? "" : material.id.getNamespace()+"_") + material.resourceName(),
          b -> b.require(material.sleeperIngredient)
          .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(material).get())
          .addOutput(material.getBlock(), 1)
          .loops(1)
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(finalRailsIngredient))
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(finalRailsIngredient))
          .addStep(PressingRecipe::new, rb -> rb)
      ));
    }

    TRACKS.put(CRTrackMaterials.PHANTOM, create("track_phantom", b -> b.require(Ingredients.phantomMembrane())
        .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(CRTrackMaterials.PHANTOM).get())
        .addOutput(new ItemStack(CRTrackMaterials.PHANTOM.getBlock(), 32), 1)
        .loops(1)
        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.ironIngot()))
        .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Ingredients.ironIngot()))
        .addStep(PressingRecipe::new, rb -> rb)
    ));

    TRACKS.put(CRTrackMaterials.MONORAIL, create("track_monorail", b -> b.require(Ingredients.girder())
        .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(CRTrackMaterials.MONORAIL).get())
        .addOutput(new ItemStack(CRTrackMaterials.MONORAIL.getBlock(), 6), 1)
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
