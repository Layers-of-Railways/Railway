package com.railwayteam.railways.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.railwayteam.railways.registry.commands.ClearCapCacheCommand;
import com.railwayteam.railways.registry.commands.ClearCasingCacheCommand;
import com.railwayteam.railways.registry.commands.ReloadJourneymapCommand;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collections;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;

public class CRCommandsClient {
	public static void register(CommandDispatcher<SharedSuggestionProvider> dispatcher) {
		LiteralCommandNode<SharedSuggestionProvider> railwaysRoot = dispatcher.register(literal("railways_client")
				.requires(cs -> cs.hasPermission(0))
				.then(ClearCasingCacheCommand.register())
				.then(ClearCapCacheCommand.register())
				.then(ReloadJourneymapCommand.register())
		);

		CommandNode<SharedSuggestionProvider> snrc = dispatcher.findNode(Collections.singleton("snrc"));
		if (snrc != null)
			return;

		dispatcher.getRoot()
				.addChild(buildRedirect("snrc", railwaysRoot));
	}

	/**
	 * Copied from AllCommands, with generics weakened.
	 * Original source:
	 * <a href="https://github.com/VelocityPowered/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38">https://github.com/VelocityPowered/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38</a>
	 *
	 * <p>
	 * Returns a literal node that redirects its execution to
	 * the given destination node.
	 *
	 * @param alias       the command alias
	 * @param destination the destination node
	 *
	 * @return the built node
	 */
	public static <T> LiteralCommandNode<T> buildRedirect(final String alias, final LiteralCommandNode<T> destination) {
		// Redirects only work for nodes with children, but break the top argument-less command.
		// Manually adding the root command after setting the redirect doesn't fix it.
		// See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
		LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder
				.<T>literal(alias)
				.requires(destination.getRequirement())
				.forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
				.executes(destination.getCommand());
		for (CommandNode<T> child : destination.getChildren()) {
			builder.then(child);
		}
		return builder.build();
	}
}
