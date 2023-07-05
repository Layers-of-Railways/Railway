package com.railwayteam.railways.content.schedule;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorScheduleRuntime;
import com.railwayteam.railways.mixin_interfaces.ICustomExecutableInstruction;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RedstoneLinkInstruction extends ScheduleInstruction implements ICustomExecutableInstruction {

    public static WorldAttached<List<CustomRedstoneActor>> customActors =
        new WorldAttached<>($ -> new ArrayList<>());

    public static void tick(Level world) {
        List<CustomRedstoneActor> actors = customActors.get(world);
        for (Iterator<CustomRedstoneActor> actorIterator = actors.iterator(); actorIterator.hasNext();) {
            CustomRedstoneActor actor = actorIterator.next();
            actor.decrement();
            if (!actor.isAlive()) {
                Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(world, actor);
                actorIterator.remove();
            }
        }
    }

    public Couple<RedstoneLinkNetworkHandler.Frequency> freq;

    public RedstoneLinkInstruction() {
        freq = Couple.create(() -> RedstoneLinkNetworkHandler.Frequency.EMPTY);
        data.putInt("Power", 15);
    }

    @Override
    public int slotsTargeted() {
        return 2;
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(icon(), formatted());
    }

    private MutableComponent formatted() {
        return Components.translatable("railways.schedule.instruction.redstone_link.power", intData("Power"));
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(Lang.translateDirect(slot == 0 ? "logistics.firstFrequency" : "logistics.secondFrequency")
            .withStyle(ChatFormatting.RED));
    }

    private ItemStack icon() {
        return new ItemStack(AllBlocks.REDSTONE_LINK.get());
    }

    @Override
    public ResourceLocation getId() {
        return Railways.asResource("redstone_link");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return icon();
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(
            Lang.translateDirect("schedule.condition.redstone_link.frequency_powered"),
            Components.literal(" #1 ").withStyle(ChatFormatting.GRAY)
                .append(freq.getFirst()
                    .getStack()
                    .getHoverName()
                    .copy()
                    .withStyle(ChatFormatting.DARK_AQUA)),
            Components.literal(" #2 ").withStyle(ChatFormatting.GRAY)
                .append(freq.getSecond()
                    .getStack()
                    .getHoverName()
                    .copy()
                    .withStyle(ChatFormatting.DARK_AQUA)));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        freq.set(slot == 0, RedstoneLinkNetworkHandler.Frequency.of(stack));
        super.setItem(slot, stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return freq.get(slot == 0)
            .getStack();
    }

    @Override
    protected void writeAdditional(CompoundTag tag) {
        tag.put("Frequency", freq.serializeEach(f -> f.getStack().save(new CompoundTag())));
    }

    @Override
    protected void readAdditional(CompoundTag tag) {
        if (tag.contains("Frequency", Tag.TAG_LIST))
            freq = Couple.deserializeEach(tag.getList("Frequency", Tag.TAG_COMPOUND), c -> RedstoneLinkNetworkHandler.Frequency.of(ItemStack.of(c)));
        else
            freq = Couple.create(() -> RedstoneLinkNetworkHandler.Frequency.EMPTY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addScrollInput(20, 101, (si, l) -> {
            si.withRange(1, 16)
                .withStepFunction(c -> c.shift ? 5 : 1)
                .titled(Components.translatable("railways.schedule.instruction.redstone_link.power_edit_box"));
            //l.withSuffix("%");
        }, "Power");
    }

    @Override
    public void execute(ScheduleRuntime runtime) {
        Train train = ((AccessorScheduleRuntime) runtime).getTrain();
        Carriage carriage = train.carriages.get(0);
        CarriageContraptionEntity cce = carriage.anyAvailableEntity();
        if (cce != null) {
            Level level = cce.level;
            CustomRedstoneActor actor = new CustomRedstoneActor(carriage);
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, actor);
            customActors.get(level).add(actor);
            //Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, actor);
        }
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
    }

    private final class CustomRedstoneActor implements IRedstoneLinkable {
        private final Carriage carriage;
        private long ticks = 8;

        private CustomRedstoneActor(Carriage carriage) {
            this.carriage = carriage;
        }

        public void decrement() {
            ticks--;
        }

        @Override
        public int getTransmittedStrength() {
            return isAlive() ? intData("Power") : 0;
        }

        @Override
        public void setReceivedStrength(int power) {
        }

        @Override
        public boolean isListening() {
            return false;
        }

        @Override
        public boolean isAlive() {
            return ticks > 0;
        }

        @Override
        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
            return freq;
        }

        @Override
        public BlockPos getLocation() {
            return new BlockPos(carriage.getLeadingPoint().getPosition(carriage.train.graph));
        }
    }
}
