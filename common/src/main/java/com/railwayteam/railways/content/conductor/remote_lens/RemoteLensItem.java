package com.railwayteam.railways.content.conductor.remote_lens;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RemoteLensItem extends Item {
    public RemoteLensItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasUUID("SelectedConductor")) {
            UUID conductorId = tag.getUUID("SelectedConductor");

            tooltip.add(Components.translatable("railways.whistle.tool.bound").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(TextUtils.translateWithFormatting("railways.whistle.tool.conductor_id", conductorId.toString().substring(0, 5)));
            tooltip.add(Components.translatable("railways.remote_lens.tool.bound_usage"));
        } else {
            tooltip.add(Components.translatable("railways.remote_lens.tool.not_bound").withStyle(ChatFormatting.DARK_RED));
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer,
                                                           @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pPlayer.level.isClientSide)
            return InteractionResult.CONSUME;
        if (pInteractionTarget instanceof ConductorEntity conductor && conductor.getJob() == ConductorEntity.Job.SPY) {
            CompoundTag stackTag = pStack.getOrCreateTag();
            stackTag.putUUID("SelectedConductor", conductor.getUUID());
            pPlayer.displayClientMessage(Components.translatable("railways.remote_lens.set"), true);
            pStack.setTag(stackTag);
            pPlayer.setItemInHand(pUsedHand, pStack);
            AllSoundEvents.PECULIAR_BELL_USE.play(pPlayer.level, null, conductor.getX(), conductor.getY(), conductor.getZ(), .5f, 1.1f);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            CompoundTag stackTag = stack.getTag();
            if (stackTag == null || !stackTag.hasUUID("SelectedConductor"))
                return InteractionResultHolder.fail(stack);
            UUID conductorId = stackTag.getUUID("SelectedConductor");
            if (player.isShiftKeyDown()) {
                stack.removeTagKey("SelectedConductor");
                AllSoundEvents.CONTROLLER_CLICK.play(level, null, player.blockPosition(), .5f, 1.1f);
                player.displayClientMessage(Components.translatable("railways.remote_lens.clear"), true);
                return InteractionResultHolder.success(stack);
            }
            Entity entity = serverLevel.getEntity(conductorId);
            if (entity instanceof ConductorEntity conductor && conductor.getJob() == ConductorEntity.Job.SPY) {
                return conductor.startViewing(serverPlayer) ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
            } else {
                return InteractionResultHolder.fail(stack);
            }
        } else {
            return InteractionResultHolder.success(stack);
        }
    }
}
