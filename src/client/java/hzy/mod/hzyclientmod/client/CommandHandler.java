package hzy.mod.hzyclientmod.client;


import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class CommandHandler {
	public static Map<String, Text> errorMap;

	public static void init() {
		errorMap = new HashMap<String, Text>();
		errorMap.put("commandCalculateSyntax", Text.literal("Invalid arguments! Usage: /calc <number> <operation> <number>").formatted(Formatting.RED));
		errorMap.put("commandCalculateZero", Text.literal("Division by zero error!").formatted(Formatting.RED));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder.<FabricClientCommandSource>literal("calc")
				.then(RequiredArgumentBuilder.<FabricClientCommandSource, Double>argument("num1", DoubleArgumentType.doubleArg())
					.then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("operation", StringArgumentType.string())
						.then(RequiredArgumentBuilder.<FabricClientCommandSource, Double>argument("num2", DoubleArgumentType.doubleArg())
							.executes(ctx -> {
								double num1 = DoubleArgumentType.getDouble(ctx, "num1");
								String operation = StringArgumentType.getString(ctx, "operation");
								double num2 = DoubleArgumentType.getDouble(ctx, "num2");
								return CommandHandler.commandCalculate(ctx, num1, operation, num2);
							})
						)
					)
				);

			dispatcher.register(command);
		});
	}

	public static int commandCalculate(CommandContext<FabricClientCommandSource> ctx, double value1, String operation, double value2) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			HzyclientmodClient.LOGGER.info("You can only run this command as a player");
			return 0;
		}

		double result = 0;


		switch (operation) {
			case "+", "add", "plus":
				result = value1 + value2;
				break;
			case "-", "minus", "subtract":
				result = value1 - value2;
				break;
			case "*", "mul", "multiply":
				result = value1 * value2;
				break;
			case "/", "divide":
				if (value1 == 0 && value2 == 0) {
					ctx.getSource().sendFeedback(errorMap.get("commandCalculateZero"));
					return 0;
				}
				result = value1 / value2;
				break;
			case "**", "pow":
				result = Math.pow(value1, value2);
				break;

			case "/=", "floor_divide":
				if (value1 == 0 && value2 == 0) {
					player.sendMessage(errorMap.get("commandCalculateZero"));
					return 0;
				}

				result = Math.floorDiv((int) value1, (int) value2);
				break;

			case "%", "modulus":
				if (value1 == 0 && value2 == 0) {
					player.sendMessage(errorMap.get("commandCalculateZero"));
					return 0;
				}

				result = value1 % value2;
				break;

			default:
				player.sendMessage(errorMap.get("commandCalculateSyntax"));
				return 0;
		}

		player.sendMessage(Text.literal(StringUtils.stripEnd(String.format("Result: %f", result), ".0")).formatted(Formatting.GREEN));
		return SINGLE_SUCCESS;
	}
}
