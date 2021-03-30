package com.railwayteam.railways.blocks;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;

public class LargeSwitchTrackBlock extends LargeTrackBlock {
  public static final String name = "large_switch";

  public LargeSwitchTrackBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.ENABLED, false)); // tracking whether it's turning or straight
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return super.getStateForPlacement(context).with(BlockStateProperties.ENABLED, false);
    //return checkForConnections(getDefaultState(), context.getWorld(), context.getPos());
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(BlockStateProperties.ENABLED); }
  /*
  public static ModelFile partialModel (DataGenContext<?,?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    StringBuilder loc = new StringBuilder("block/wide_gauge/" + ctx.getName());
    loc.append("_");
    for (String suf : suffix) {
      if ()
    }
    //for (String suf : suffix) loc.append("_" + suf);
    return prov.models().getExistingFile(prov.modLoc(loc.toString()));
  }
  */
  public boolean isTurning (BlockState state) {
    return state.get(BlockStateProperties.ENABLED);
  }

}
