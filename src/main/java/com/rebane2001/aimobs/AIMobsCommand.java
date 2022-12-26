package com.rebane2001.aimobs;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

//import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class AIMobsCommand {

    public static void setupAIMobsCommand(ClientCommandManager manager) {
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
                .then(ClientCommandManager.literal("enable").executes(context -> setEnabled(context, true)))
                .then(ClientCommandManager.literal("disable").executes(context -> setEnabled(context, false))
                );
    }
    public static int setEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
        AIMobsConfig.config.enabled = enabled;
        AIMobsConfig.saveConfig();
        context.getSource().sendFeedback(new LiteralText("AIMobs " + (enabled ? "enabled" : "disabled")));
        return 1;
    }

    public static int status(CommandContext<FabricClientCommandSource> context) {
        boolean hasKey = AIMobsConfig.config.apiKey.length() > 0;
        Text yes = new LiteralText("Yes").formatted(Formatting.GREEN);
        Text no = new LiteralText("No").formatted(Formatting.RED);
        Text helpText = new LiteralText("")
                .append(new LiteralText("AIMobs").formatted(Formatting.UNDERLINE))
                .append("").formatted(Formatting.RESET)
                .append("\nEnabled: ").append(AIMobsConfig.config.enabled ? yes : no)
                .append("\nAPI Key: ").append(hasKey ? yes : no)
                .append("\nModel: ").append(AIMobsConfig.config.model)
                .append("\nTemp: ").append(String.valueOf(AIMobsConfig.config.temperature))
                .append("\n\nUse ").append(new LiteralText("/aimobs help").formatted(Formatting.GRAY)).append(" for help");
        context.getSource().sendFeedback(helpText);
        return 1;
    }

    public static int help(CommandContext<FabricClientCommandSource> context) {
        Text helpText = new LiteralText("")
                .append("AIMobs Commands").formatted(Formatting.UNDERLINE)
                .append("").formatted(Formatting.RESET)
                .append("\n/aimobs - View configuration status")
                .append("\n/aimobs help - View commands help")
                .append("\n/aimobs enable/disable - Enable/disable the mod")
                .append("\n/aimobs setkey <key> - Set OpenAI API key")
                .append("\n/aimobs setmodel <model> - Set AI model")
                .append("\n/aimobs settemp <temperature> - Set model temperature")
                .append("\nYou can talk to mobs by shift-clicking on them!");
        context.getSource().sendFeedback(helpText);
        return 1;
    }
    public static int setAPIKey(CommandContext<FabricClientCommandSource> context) {
        String apiKey = context.getArgument("key", String.class);
        if (apiKey.length() > 0) {
            AIMobsConfig.config.apiKey = apiKey;
            AIMobsConfig.saveConfig();
            context.getSource().sendFeedback(Text.of("API key set"));
            return 1;
        }
        return 0;
    }
    public static int setModel(CommandContext<FabricClientCommandSource> context) {
        String model = context.getArgument("model", String.class);
        if (model.length() > 0) {
            AIMobsConfig.config.model = model;
            AIMobsConfig.saveConfig();
            context.getSource().sendFeedback(Text.of("Model set"));
            return 1;
        }
        return 0;
    }
    public static int setTemp(CommandContext<FabricClientCommandSource> context) {
        AIMobsConfig.config.temperature = context.getArgument("temperature", float.class);
        AIMobsConfig.saveConfig();
        context.getSource().sendFeedback(Text.of("Temperature set"));
        return 1;
    }
}
