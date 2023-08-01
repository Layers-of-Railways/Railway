package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlocks;
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
import java.util.function.Function;
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

    protected GeneratedRecipe create(String name, Function<RailwaysSequencedAssemblyRecipeBuilder, SequencedAssemblyRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
            c -> transform.apply(new RailwaysSequencedAssemblyRecipeBuilder(Railways.asResource(name)))
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
            if (material.railsIngredient.isEmpty() || material.sleeperIngredient.isEmpty()) {
                if (material.trackType == CRTrackMaterials.CRTrackType.WIDE_GAUGE) {
                    TrackMaterial baseMaterial = CRTrackMaterials.getBaseFromWide(material);
                    if (baseMaterial == null)
                        continue;
                    Ingredient sleeperIngredient;
                    if (material == CRTrackMaterials.WIDE_GAUGE_ANDESITE) {
                        sleeperIngredient = Ingredient.of(AllTags.AllItemTags.SLEEPERS.tag);
                    } else {
                        sleeperIngredient = baseMaterial.sleeperIngredient;
                    }
                    if (sleeperIngredient.isEmpty()) continue;
                    TRACKS.put(material, create(
                        "track_" + (material.id.getNamespace().equals(Railways.MODID)
                            ? "" : material.id.getNamespace()+"_") + material.resourceName(),
                        b -> b.conditionalMaterial(material).require(baseMaterial.getBlock())
                            .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(material).get())
                            .addOutput(material.getBlock(), 1)
                            .loops(1)
                            .addStep(CuttingRecipe::new, rb -> rb)
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(sleeperIngredient))
                            .addStep(PressingRecipe::new, rb -> rb)
                    ));
                } else if (material.trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE) {
                    TrackMaterial baseMaterial = CRTrackMaterials.getBaseFromWide(material);
                    if (baseMaterial == null)
                        continue;
                    Ingredient sleeperIngredient;
                    if (material == CRTrackMaterials.WIDE_GAUGE_ANDESITE) {
                        sleeperIngredient = Ingredient.of(AllTags.AllItemTags.SLEEPERS.tag);
                    } else {
                        sleeperIngredient = baseMaterial.sleeperIngredient;
                    }
                    if (sleeperIngredient.isEmpty()) continue;
                    if (baseMaterial.railsIngredient.isEmpty()) continue;

                    Ingredient railsIngredient = material.railsIngredient;
                    if (railsIngredient.values.length == 2 && Arrays.stream(railsIngredient.values).allMatch((value) -> {
                        return value instanceof Ingredient.TagValue tagValue
                            && (tagValue.tag.equals(AllTags.forgeItemTag("nuggets/iron"))
                            || tagValue.tag.equals(AllTags.forgeItemTag("nuggets/zinc"))
                            || tagValue.tag.equals(AllTags.forgeItemTag("iron_nuggets"))
                            || tagValue.tag.equals(AllTags.forgeItemTag("zinc_nuggets"))); // TODO wait until create fabric merge such difference between 1.18 and 1.19
                    })) {
                        railsIngredient = Ingredient.fromValues(Stream.of(
                            TagValueAccessor.createTagValue(Ingredients.ironNugget()),
                            TagValueAccessor.createTagValue(Ingredients.zincNugget())));
                    }

                    Ingredient finalRailsIngredient = railsIngredient;
                    TRACKS.put(material, create( // fixme can this even be crafted?
                        "track_" + (material.id.getNamespace().equals(Railways.MODID)
                            ? "" : material.id.getNamespace()+"_") + material.resourceName(),
                        b -> b.conditionalMaterial(material).require(baseMaterial.sleeperIngredient)
                            .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(material).get())
                            .addOutput(material.getBlock(), 1)
                            .loops(1)
                            .addStep(CuttingRecipe::new, rb -> rb)
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(finalRailsIngredient))
                            .addStep(PressingRecipe::new, rb -> rb)
                    ));
                }
                continue;
            }

            Ingredient railsIngredient = material.railsIngredient;
            if (railsIngredient.values.length == 2 && Arrays.stream(railsIngredient.values).allMatch((value) -> {
                return value instanceof Ingredient.TagValue tagValue
                    && (tagValue.tag.equals(AllTags.forgeItemTag("nuggets/iron"))
                    || tagValue.tag.equals(AllTags.forgeItemTag("nuggets/zinc"))
                    || tagValue.tag.equals(AllTags.forgeItemTag("iron_nuggets"))
                    || tagValue.tag.equals(AllTags.forgeItemTag("zinc_nuggets"))); // TODO wait until create fabric merge such difference between 1.18 and 1.19
            })) {
                railsIngredient = Ingredient.fromValues(Stream.of(
                    TagValueAccessor.createTagValue(Ingredients.ironNugget()),
                    TagValueAccessor.createTagValue(Ingredients.zincNugget())));
            }

            Ingredient finalRailsIngredient = railsIngredient;

            TRACKS.put(material, create(
                "track_" + (material.id.getNamespace().equals(Railways.MODID)
                    ? "" : material.id.getNamespace()+"_") + material.resourceName(),
                b -> b.conditionalMaterial(material).require(material.sleeperIngredient)
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

        TRACKS.put(CRTrackMaterials.getWide(CRTrackMaterials.PHANTOM), create("track_phantom_wide", b -> b.require(CRBlocks.PHANTOM_TRACK.get())
            .transitionTo(CRItems.ITEM_INCOMPLETE_TRACK.get(CRTrackMaterials.getWide(CRTrackMaterials.PHANTOM)).get())
            .addOutput(new ItemStack(CRTrackMaterials.getWide(CRTrackMaterials.PHANTOM).getBlock()), 1)
            .loops(1)
            .addStep(CuttingRecipe::new, rb -> rb)
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
