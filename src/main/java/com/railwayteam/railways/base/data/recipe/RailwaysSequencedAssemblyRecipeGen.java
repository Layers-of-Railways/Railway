package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Locale;
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

  HashMap<DyeColor, GeneratedRecipe> CONDUCTOR_CAPS = new HashMap<>();
  {
    for (DyeColor color : DyeColor.values()) {
      String colorName = TextUtils.titleCaseConversion(color.getName().replace("_", " "));
      String colorReg  = color.getName().toLowerCase(Locale.ROOT);
      CONDUCTOR_CAPS.put(color, create(colorReg + "_conductor_cap", b -> b.require(CRItems.woolByColor(color))
          .transitionTo(CRItems.ITEM_INCOMPLETE_CONDUCTOR_CAP.get(color).get())
          .addOutput(CRItems.ITEM_CONDUCTOR_CAP.get(color).get(), 1)
          .loops(1)
          .addStep(CuttingRecipe::new, rb -> rb)
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(I.precisionMechanism()))
          .addStep(DeployerApplicationRecipe::new, rb -> rb.require(I.string()))
      ));
    }
  }

  @Override
  public String getName() {
    return "Railways' Sequenced Assembly Recipes";
  }
}
