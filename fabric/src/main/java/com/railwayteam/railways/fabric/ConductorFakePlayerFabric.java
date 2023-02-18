package com.railwayteam.railways.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.foundation.utility.Components;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

// NOTICE: changes must be replicated in ConductorFakePlayerForge.
// annoying that we can't merge them any further.
public class ConductorFakePlayerFabric extends FakePlayer {
	public ConductorFakePlayerFabric(ServerLevel world) {
		super(world, ConductorEntity.FAKE_PLAYER_PROFILE);
	}

	@Override
	@NotNull
	public OptionalInt openMenu(MenuProvider container) {
		return OptionalInt.empty();
	}

	@Override
	@NotNull
	public Component getDisplayName() {
		return Components.translatable(Railways.MODID + "." + "conductor_name");
	}

	@Override
	public float getEyeHeight(@NotNull Pose pose) {
		return 0;
	}

	@Override
	public Vec3 position() {
		return new Vec3(getX(), getY(), getZ());
	}

	@Override
	public float getCurrentItemAttackStrengthDelay() {
		return 1 / 64f;
	}

	@Override
	public boolean canEat(boolean ignoreHunger) {
		return false;
	}

	@Override
	@NotNull
	public ItemStack eat(@NotNull Level world, ItemStack stack) {
		stack.shrink(1);
		return stack;
	}
}
