package com.exosomnia.exoadvadditions.commands;

import com.exosomnia.exoadvadditions.managers.AdventureManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.PrimaryLevelData;

public class AdventureStart {

    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(Commands.literal("exoadventurestart")
                .requires(sourceStack -> sourceStack.hasPermission(1))
                .executes(context -> execute(context.getSource())));
    }

    private static int execute(CommandSourceStack sourceStack) {
        MinecraftServer server = sourceStack.getServer();
        ServerLevel level = sourceStack.getServer().getLevel(Level.OVERWORLD);
        GameRules rules = level.getGameRules();

        if (!rules.getBoolean(AdventureManager.RULE_ADVENTURE_STARTED)) {
            WorldBorder border = level.getWorldBorder();

            rules.getRule(AdventureManager.RULE_CAN_BREAK).set(true, server);
            rules.getRule(AdventureManager.RULE_CAN_HURT).set(true, server);
            rules.getRule(AdventureManager.RULE_CAN_HUNGER_DRAIN).set(true, server);
            rules.getRule(AdventureManager.RULE_ADVENTURE_STARTED).set(true, server);
            rules.getRule(GameRules.RULE_DAYLIGHT).set(true, server);
            rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(true, server);
            rules.getRule(GameRules.RULE_RANDOMTICKING).set(3, server);

            if (level.getLevelData() instanceof PrimaryLevelData levelData) {
                if (levelData.getDifficulty().equals(Difficulty.PEACEFUL)) {
                    levelData.setDifficulty(Difficulty.EASY);
                }
                levelData.setDifficultyLocked(true);
            }

            border.setCenter(0, 0);
            border.setWarningBlocks(5);
            border.setSize(30000000);

            CommandSourceStack newCommandStack = server.createCommandSourceStack().withPermission(2).withSource(server).withSuppressedOutput();
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:combat");
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:exploration");
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:fishing");
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:husbandry");
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:mining");
            server.getCommands().performPrefixedCommand(newCommandStack, "puffish_skills category erase @a exoadventure:occult");

            for (ServerPlayer player : level.players()) {
                player.connection.send(new ClientboundChangeDifficultyPacket(level.getLevelData().getDifficulty(), level.getLevelData().isDifficultyLocked()));
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
