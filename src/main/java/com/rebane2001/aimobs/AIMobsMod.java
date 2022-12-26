package com.rebane2001.aimobs;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class AIMobsMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("aimobs");

	@Override
	public void onInitializeClient() {
		AIMobsConfig.loadConfig();
		ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("aimobs")
				.executes(AIMobsCommand::status)
				.then(ClientCommandManager.literal("help").executes(AIMobsCommand::help))
				.then(ClientCommandManager.literal("setkey")
						.then(argument("key", StringArgumentType.string())
								.executes(AIMobsCommand::setAPIKey)
						))
				.then(ClientCommandManager.literal("setmodel")
						.then(argument("model", StringArgumentType.string())
								.executes(AIMobsCommand::setModel)
						))
				.then(ClientCommandManager.literal("settemp")
						.then(argument("temperature", FloatArgumentType.floatArg(0,1))
								.executes(AIMobsCommand::setTemp)
						))
				.then(ClientCommandManager.literal("enable").executes(context -> AIMobsCommand.setEnabled(context, true)))
				.then(ClientCommandManager.literal("disable").executes(context -> AIMobsCommand.setEnabled(context, false))
				)
		);


		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
			if (!player.isSneaking()) {
				if (entity.getId() == ActionHandler.entityId)
					ActionHandler.handlePunch(entity, player);
				return ActionResult.PASS;
			}
			ActionHandler.startConversation(entity, player);
			return ActionResult.FAIL;
		});
	}
}
