package com.railwayteam.railways.content.custom_tracks.monorail;

import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class MonorailBlockStateGenerator extends CustomTrackBlockStateGenerator {
    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return prov.models()
            .getExistingFile(prov.modLoc("block/monorail/monorail"));
    }
}
