package com.exosomnia.exoadvadditions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.PrimaryLevelData;

public class LowerDifficulty {

    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(Commands.literal("lowerdifficulty")
                .requires(sourceStack -> sourceStack.hasPermission(1))
                .executes(context -> execute(context.getSource())));
    }

    private static int execute(CommandSourceStack sourceStack) {
        MinecraftServer server = sourceStack.getServer();
        ServerLevel level = sourceStack.getServer().getLevel(Level.OVERWORLD);
        GameRules rules = level.getGameRules();

        if (level.getLevelData() instanceof PrimaryLevelData levelData) {
            Difficulty current = levelData.getDifficulty();
            if (current == Difficulty.PEACEFUL || current == Difficulty.EASY) {
                server.getPlayerList().broadcastSystemMessage(Component.translatable("command.exoadvadditions.lowerdifficulty.error"), false);
            }
            else {
                Difficulty lowered = Difficulty.byId(current.getId() - 1);
                levelData.setDifficulty(lowered);
                server.getPlayerList().broadcastSystemMessage(Component.literal(
                        I18n.get("command.exoadvadditions.lowerdifficulty.success", current.toString(), lowered.toString())), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
