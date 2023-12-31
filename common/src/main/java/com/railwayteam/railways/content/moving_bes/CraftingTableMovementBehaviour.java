package com.railwayteam.railways.content.moving_bes;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public class CraftingTableMovementBehaviour extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = contraptionEntity.getContraption();
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);

        Component name = info != null ? info.state.getBlock().getName() : Component.translatable("container.crafting");
        player.openMenu(new SimpleMenuProvider((syncId, inventory, player1) -> new CraftingMenu(syncId, inventory), name));

        Vec3 soundPos = contraption.entity.toGlobalVector(Vec3.atCenterOf(localPos), 0);
        player.level.playSound(null, BlockPos.containing(soundPos), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.75f, 1f);
        return true;
    }
}
