package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin_interfaces.ICarriageConductors;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(value = StationBlock.class, remap = false)
public abstract class MixinStationBlock {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
    private void autoWhistle(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        if (CRBlocks.CONDUCTOR_WHISTLE_FLAG.asStack().getItem().equals(itemInHand.getItem())) {
            if (!pLevel.isClientSide && pPlayer instanceof DeployerFakePlayer && pLevel.getBlockEntity(pPos) instanceof StationBlockEntity stationBe) {
                cir.setReturnValue(InteractionResult.CONSUME);
                GlobalStation station = stationBe.getStation();
                if (station != null && station.getPresentTrain() == null) {
                    CompoundTag stackTag = itemInHand.getTag();
                    if (stackTag == null || !stackTag.hasUUID("SelectedTrain") || !stackTag.hasUUID("SelectedConductor"))
                        cir.setReturnValue(InteractionResult.FAIL);

                    BlockPos pos = stationBe.edgePoint.getPos();
                    Level level = pPlayer.level();

                    UUID trainId = stackTag.getUUID("SelectedTrain");
                    UUID conductorId = stackTag.getUUID("SelectedConductor");
                    Train train = Create.RAILWAYS.trains.get(trainId);
                    if (!Create.RAILWAYS.trains.containsKey(trainId))
                        return;

                    boolean foundConductor = false;
                    Carriage conductorCarriage = null;
                    if(train==null)return;
                    for (Carriage carriage : train.carriages) {
                        if (((ICarriageConductors) carriage).getControllingConductors().contains(conductorId)) {
                            foundConductor = true;
                            conductorCarriage = carriage;
                            break;
                        }
                    }

                    if (!foundConductor)
                        return;

                    stackTag.put("SelectedPos", NbtUtils.writeBlockPos(pos));
                    stackTag.remove("Bezier");
                    itemInHand.setTag(stackTag);

                    if (Config.CONDUCTOR_WHISTLE_REQUIRES_OWNING.get() && train.runtime.getSchedule() != null && !train.runtime.completed && !train.runtime.isAutoSchedule && train.getOwner(level) != pPlayer) {
                        stackTag.remove("SelectedPos");
                        itemInHand.setTag(stackTag);
                        return;
                    }

                    System.out.println("DB1");

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
                            if (!scheduleStack.isEmpty() && !pPlayer.addItem(scheduleStack)) // fallback, probably should never be called. Keep it *just in case*
                                pPlayer.drop(scheduleStack, false);
                        }
                    }

                    Schedule schedule = new Schedule();
                    ScheduleEntry entry = new ScheduleEntry();
                    DestinationInstruction instruction = new DestinationInstruction();
                    ScheduledDelay condition = new ScheduledDelay();
                    condition.getData().putInt("Value", 0);
                    instruction.getData().putString("Text", station.name);
                    entry.instruction = instruction;
                    if (entry.conditions.size() == 0)
                        entry.conditions.add(new ArrayList<>());
                    entry.conditions.get(0).add(condition);
                    schedule.entries.add(entry);
                    schedule.cyclic = false;
                    train.runtime.setSchedule(schedule, true);
                    ((AccessorScheduleRuntime) train.runtime).setCooldown(10);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
                else if(station != null && station.getPresentTrain() != null) {
                    UUID trainId = station.getPresentTrain().id;
                    Train train = Create.RAILWAYS.trains.get(trainId);
                    if(train==null) return;

                    AtomicBoolean found = new AtomicBoolean(false);
                    for (Carriage carriage : train.carriages)
                        carriage.forEachPresentEntity(e -> e.getIndirectPassengers()
                                .forEach(p -> {
                                    if (p instanceof ConductorEntity conductor && !found.get()) {
                                        CompoundTag stackTag = itemInHand.getOrCreateTag();
                                        stackTag.putUUID("SelectedTrain", train.id);
                                        stackTag.putUUID("SelectedConductor", conductor.getUUID());
                                        stackTag.putByte("SelectedColor", conductor.getEntityData().get(ConductorEntity.COLOR));
                                        itemInHand.setTag(stackTag);
                                        pPlayer.setItemInHand(pHand, itemInHand);
                                        cir.setReturnValue(InteractionResult.SUCCESS);
                                        found.set(true);
                                    }
                                }));
                }
            }
        }
    }

    @Inject(method = "use", at = @At(value = "RETURN", ordinal = 1), cancellable = true, remap = true)
    private void deployersAssemble(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!pLevel.isClientSide && pPlayer instanceof DeployerFakePlayer deployerFakePlayer && pLevel.getBlockEntity(pPos) instanceof StationBlockEntity stationBe) {
            cir.setReturnValue(InteractionResult.CONSUME);
            GlobalStation station = stationBe.getStation();
            boolean isAssemblyMode = pState.getValue(StationBlock.ASSEMBLING);
            if (station != null && station.getPresentTrain() == null) {
                //assemble
                if (stationBe.isAssembling() || stationBe.tryEnterAssemblyMode()) {
                    //Need to fix blockstate
                    stationBe.assemble(deployerFakePlayer.getUUID());
                    cir.setReturnValue(InteractionResult.SUCCESS);

                    if (isAssemblyMode) {
                        pLevel.setBlock(pPos, pState.setValue(StationBlock.ASSEMBLING, false), 3);
                        stationBe.refreshBlockState();
                    }
                }
                return;
            }
            BlockState newState = null;
            if (!isAssemblyMode) {
                newState = pState.setValue(StationBlock.ASSEMBLING, true);
            }
            if (disassembleAndEnterMode(deployerFakePlayer, stationBe)) {
                if (newState != null) {
                    pLevel.setBlock(pPos, newState, 3);
                    stationBe.refreshBlockState();

                    stationBe.refreshAssemblyInfo();
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    private boolean disassembleAndEnterMode(ServerPlayer sender, StationBlockEntity te) {
        GlobalStation station = te.getStation();
        if (station != null) {
            Train train = station.getPresentTrain();
            BlockPos trackPosition = te.edgePoint.getGlobalPosition();
            ItemStack schedule = train == null ? ItemStack.EMPTY : train.runtime.returnSchedule();
            if (train != null && !train.disassemble(te.getAssemblyDirection(), trackPosition.above()))
                return false;
            dropSchedule(sender, te, schedule);
        }
        return te.tryEnterAssemblyMode();
    }

    private void dropSchedule(ServerPlayer sender, StationBlockEntity te, ItemStack schedule) {
        if (schedule.isEmpty())
            return;
        if (sender.getMainHandItem()
            .isEmpty()) {
            sender.getInventory()
                .placeItemBackInInventory(schedule);
            return;
        }
        Vec3 v = VecHelper.getCenterOf(te.getBlockPos());
        ItemEntity itemEntity = new ItemEntity(te.getLevel(), v.x, v.y, v.z, schedule);
        itemEntity.setDeltaMovement(Vec3.ZERO);
        te.getLevel()
            .addFreshEntity(itemEntity);
    }
}
