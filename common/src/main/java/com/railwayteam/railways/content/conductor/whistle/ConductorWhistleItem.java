package com.railwayteam.railways.content.conductor.whistle;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin.AccessorCarriage;
import com.railwayteam.railways.mixin.AccessorScheduleRuntime;
import com.railwayteam.railways.mixin_interfaces.ICarriageConductors;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRSounds;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConductorWhistleItem extends TrackTargetingBlockItem {

    public static final String SPECIAL_MARKER = "<ConductorFlag>";

    public ConductorWhistleItem(Block block, Item.Properties properties) {
        super(block, properties, EdgePointType.STATION);
    }
    @Override
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) { //Not worth the effort
        return false;
    }

    private static InteractionResult fail(Player player, String message) {
        player.displayClientMessage(Components.translatable("railways.whistle.failure."+message).withStyle(ChatFormatting.RED), true);
        player.displayClientMessage(Components.translatable("railways.whistle.failure."+message).withStyle(ChatFormatting.RED), false);
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasUUID("SelectedTrain") && tag.hasUUID("SelectedConductor")) {
            UUID trainId = tag.getUUID("SelectedTrain");
            UUID conductorId = tag.getUUID("SelectedConductor");
            String trainName = "NOT FOUND";
            GlobalRailwayManager railways = Create.RAILWAYS.sided(level);
            if (railways != null && railways.trains.containsKey(trainId))
                trainName = railways.trains.get(trainId).name.getString();

            tooltip.add(Components.translatable("railways.whistle.tool.bound").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(TextUtils.translateWithFormatting("railways.whistle.tool.conductor_id", conductorId.toString().substring(0, 5)));
            tooltip.add(TextUtils.translateWithFormatting("railways.whistle.tool.train_id", trainName, trainId.toString().substring(0, 5)));
            tooltip.add(Components.translatable("railways.whistle.tool.bound_usage"));
            tooltip.add(Components.translatable("railways.whistle.tool.bound_auto_usage"));
            tooltip.add(Components.translatable("railways.whistle.tool.bound_auto_clear"));
        } else {
            tooltip.add(Components.translatable("railways.whistle.tool.not_bound").withStyle(ChatFormatting.DARK_RED));
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer,
                                                           @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pPlayer.level.isClientSide)
            return InteractionResult.PASS;
        if (pInteractionTarget instanceof ConductorEntity conductor && conductor.getVehicle() instanceof CarriageContraptionEntity cce) {
            Train train = cce.getCarriage().train;
            if (train.owner == pPlayer.getUUID() || !CRConfigs.server().conductors.whistleRequiresOwning.get()) {
                CompoundTag stackTag = pStack.getOrCreateTag();
                stackTag.putUUID("SelectedTrain", train.id);
                stackTag.putUUID("SelectedConductor", conductor.getUUID());
                stackTag.putByte("SelectedColor", conductor.getEntityData().get(ConductorEntity.COLOR));
                pPlayer.displayClientMessage(Components.translatable("railways.whistle.set"), true);
                pStack.setTag(stackTag);
                pPlayer.setItemInHand(pUsedHand, pStack);
                AllSoundEvents.PECULIAR_BELL_USE.play(pPlayer.level, null, conductor.getX(), conductor.getY(), conductor.getZ(), .5f, 1.1f);
                return InteractionResult.SUCCESS;
            } else {
                pPlayer.displayClientMessage(Components.translatable("railways.whistle.not_owner").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        CompoundTag stackTag = stack.getTag();
        if(stackTag == null) return InteractionResult.FAIL;
        UUID trainId = stackTag.getUUID("SelectedTrain");
        Train train = Create.RAILWAYS.trains.get(trainId);

        if (player == null || train == null)
            return InteractionResult.FAIL;

        if (player instanceof DeployerFakePlayer && state.getBlock() instanceof AirBlock && train.runtime.isAutoSchedule) {train.runtime.discardSchedule();}


        if (player.isSteppingCarefully() && stack.hasTag()) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            player.displayClientMessage(Components.translatable("railways.whistle.clear"), true);
            stack.setTag(null);
            AllSoundEvents.CONTROLLER_CLICK.play(level, null, pos, 1, .5f);
            return InteractionResult.SUCCESS;
        }

        if (state.getBlock() instanceof StationBlock || state.getBlock() instanceof ITrackBlock) {
            level.playSound(null, pos, CRSounds.CONDUCTOR_WHISTLE.get(), SoundSource.BLOCKS, 0.3f, 1f);
            if (level.isClientSide)
                return InteractionResult.SUCCESS;


            String stationName = "";

            if (!stackTag.hasUUID("SelectedTrain") || !stackTag.hasUUID("SelectedConductor"))
                return fail(player, "not_bound");

            UUID conductorId = stackTag.getUUID("SelectedConductor");


            if (!Create.RAILWAYS.trains.containsKey(trainId))
                return fail(player, "train_missing");


            boolean foundConductor = false;
            Carriage conductorCarriage = null;
            for (Carriage carriage : train.carriages) {
                if (((ICarriageConductors) carriage).getControllingConductors().contains(conductorId)) {
                    foundConductor = true;
                    conductorCarriage = carriage;
                    break;
                }
            }

            if (!foundConductor)
                return fail(player, "conductor_missing");


            if (state.getBlock() instanceof ITrackBlock track) {
                Vec3 lookAngle = player.getLookAngle();
                boolean front = track.getNearestTrackAxis(level, pos, state, lookAngle)
                        .getSecond() == Direction.AxisDirection.POSITIVE;

                stackTag.put("SelectedPos", NbtUtils.writeBlockPos(pos));
                stackTag.putBoolean("SelectedDirection", front);
                stackTag.remove("Bezier");
                stack.setTag(stackTag);

                EdgePointType<?> type = getType(stack);
                MutableObject<OverlapResult> result = new MutableObject<>(null);
                withGraphLocation(level, pos, front, null, type, (overlap, location) -> result.setValue(overlap));

                if (result.getValue().feedback != null) {
                    player.displayClientMessage(Lang.translateDirect(result.getValue().feedback)
                            .withStyle(ChatFormatting.RED), true);
                    AllSoundEvents.DENY.play(level, null, pos, .5f, 1);
                    return InteractionResult.FAIL;
                }

                Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
                Direction successDirection = null;
                for (Direction direction : directions) {
                    BlockPos placePos = pos.relative(direction);
                    Vec3 hitPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                            .add(direction.getStepX() * 0.5, direction.getStepY() * 0.5, direction.getStepZ() * 0.5);
                    BlockPlaceContext ctx = new BlockPlaceContext(
                            player, pContext.getHand(), stack, new BlockHitResult(hitPos, direction.getOpposite(), placePos, false)
                    );
                    if (level.getBlockState(placePos).canBeReplaced(ctx)) {
                        successDirection = direction;
                        break;
                    }
                }

                if (successDirection == null) {
                    stackTag.remove("SelectedPos");
                    stackTag.remove("SelectedDirection");
                    stack.setTag(stackTag);
                    return fail(player, "no_space");
                }

                BlockPos placePos = pos.relative(successDirection);

                stationName = SPECIAL_MARKER + placePos.toShortString();

                BlockState placeState = CRBlocks.CONDUCTOR_WHISTLE_FLAG.getDefaultState();
                level.setBlock(placePos, placeState, 11);
                CompoundTag teTag = new CompoundTag();
                teTag.putString("Name", stationName);
                teTag.putByte("SelectedColor", stackTag.getByte("SelectedColor"));
                teTag.putBoolean("TargetDirection", stackTag.getBoolean("SelectedDirection"));
                BlockPos selectedPos = NbtUtils.readBlockPos(stackTag.getCompound("SelectedPos"));
                teTag.put("TargetTrack", NbtUtils.writeBlockPos(selectedPos.subtract(placePos)));
                stackTag.put("BlockEntityTag", teTag);
                stack.setTag(stackTag);

                updateCustomBlockEntityTag(placePos, level, player, stack, placeState);
                stackTag.remove("SelectedPos");
                stackTag.remove("SelectedDirection");
                stackTag.remove("BlockEntityTag");
                stack.setTag(stackTag);

            }

            else if (level.getBlockEntity(pos) instanceof StationBlockEntity stationBe) {
                stationName = Objects.requireNonNull(stationBe.getStation()).name;
            }

            // have to own train if: non-auto schedule is in progress
            if (CRConfigs.server().conductors.whistleRequiresOwning.get() && train.runtime.getSchedule() != null && !train.runtime.completed && !train.runtime.isAutoSchedule && train.getOwner(level) != player) {
                stackTag.remove("SelectedPos");
                stackTag.remove("SelectedDirection");
                stack.setTag(stackTag);
                return fail(player, "not_owner");
            }

            if (train.runtime.getSchedule() != null && !train.runtime.isAutoSchedule) {
                ItemStack scheduleStack = train.runtime.returnSchedule();
                if (!scheduleStack.isEmpty()) {
                    for (CompoundTag passengerTag : ((AccessorCarriage) conductorCarriage).getSerialisedPassengers().values()) {
                        if (passengerTag.contains("PlayerPassenger")) continue;
                        if (passengerTag.contains("id") && CREntities.CONDUCTOR.getId().equals(new ResourceLocation(passengerTag.getString("id")))) {
                            // It is a conductor
                            if (passengerTag.hasUUID("UUID") && passengerTag.getUUID("UUID").equals(conductorId)) {
                                // It is the targeted conductor
                                // Place the schedule in the conductor
                                ListTag schedulesList;
                                if (!passengerTag.contains("heldSchedules")) {
                                    schedulesList = new ListTag();
                                    passengerTag.put("heldSchedules", schedulesList);
                                } else {
                                    schedulesList = passengerTag.getList("heldSchedules", Tag.TAG_COMPOUND);
                                }
                                schedulesList.add(scheduleStack.save(new CompoundTag()));
                                scheduleStack.setCount(0);
                                break;
                            }
                        }
                    }
                    if (!scheduleStack.isEmpty()) {
                        // Try if the conductor is simply loaded
                        conductorCarriage.forEachPresentEntity(cce -> {
                            if (!scheduleStack.isEmpty()) {
                                for (Entity passenger : cce.getPassengers()) {
                                    if (passenger instanceof ConductorEntity conductorEntity && passenger.getUUID().equals(conductorId)) {
                                        conductorEntity.addSchedule(scheduleStack);
                                        scheduleStack.setCount(0);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    if (!scheduleStack.isEmpty() && !player.addItem(scheduleStack)) // fallback, probably should never be called. Keep it *just in case*
                        player.drop(scheduleStack, false);
                }
            }


            player.displayClientMessage(Components.translatable("railways.whistle.success"), true);
            AllSoundEvents.CONTROLLER_CLICK.play(level, null, pos, 1, 1);

            Schedule schedule = new Schedule();
            ScheduleEntry entry = new ScheduleEntry();
            DestinationInstruction instruction = new DestinationInstruction();
            ScheduledDelay condition = new ScheduledDelay();
            condition.getData().putInt("Value", 0);
            instruction.getData().putString("Text", stationName);
            entry.instruction = instruction;
            if (entry.conditions.isEmpty())
                entry.conditions.add(new ArrayList<>());
            entry.conditions.get(0).add(condition);
            schedule.entries.add(entry);
            schedule.cyclic = false;
            train.runtime.discardSchedule();
            train.runtime.setSchedule(schedule, true);
            ((AccessorScheduleRuntime) train.runtime).setCooldown(10);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (isSelected && level instanceof ServerLevel serverLevel
            && (serverLevel.getGameTime() + entity.hashCode() + slotId) % CRConfigs.server().conductors.whistleRebindRate.get() == 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.hasUUID("SelectedTrain") && tag.hasUUID("SelectedConductor")) {
                UUID trainId = tag.getUUID("SelectedTrain");
                UUID conductorId = tag.getUUID("SelectedConductor");

                if (serverLevel.getEntity(conductorId) instanceof ConductorEntity conductor) {
                    if (conductor.getVehicle() instanceof CarriageContraptionEntity cce && !trainId.equals(cce.trainId)) {
                        tag.putUUID("SelectedTrain", cce.trainId);
                    }
                }
            }
        }
    }
}
