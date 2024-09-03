/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.semaphore;

import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SemaphoreItem extends BlockItem {
    public SemaphoreItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {

        //return super.place(pContext);
        IPlacementHelper placementHelper = PlacementHelpers.get(SemaphoreBlock.placementHelperId);

        Level world = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos pos = pContext.getClickedPos();
        if (!pContext.replacingClickedOnBlock()) {
            pos = pos.offset(pContext.getClickedFace().getOpposite().getNormal());
        }
        BlockState state = world.getBlockState(pos);
        double pHitDistance = EntityUtils.getReachDistance(player);
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(0);
        Vec3 endPos = eyePos.add(viewVec.x * pHitDistance, viewVec.y * pHitDistance, viewVec.z * pHitDistance);
        BlockHitResult ray = pContext.getLevel().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
//        BlockHitResult ray = (BlockHitResult)Minecraft.getInstance().hitResult;
        if (!placementHelper.matchesState(state))
            return super.place(pContext);

        InteractionResult result = placementHelper.getOffset(player, world, state, pos, ray)
                .placeInWorld(world, this, player, pContext.getHand(), ray);

        if (result.consumesAction())
            return result;
        else
            return super.place(pContext);
    }
}
