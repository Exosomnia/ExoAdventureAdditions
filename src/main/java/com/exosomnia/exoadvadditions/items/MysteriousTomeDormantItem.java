package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.exosomnia.exolib.particles.shapes.ParticleShapeLine;
import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class MysteriousTomeDormantItem extends Item {
    public MysteriousTomeDormantItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level contextLevel = context.getLevel();
        if (contextLevel.isClientSide) return InteractionResult.sidedSuccess(true);

        ServerLevel level = (ServerLevel)contextLevel;
        BlockPos position = context.getClickedPos();
        ServerPlayer player = (ServerPlayer)context.getPlayer();

        if (contextLevel.getBlockState(position).is(Blocks.LECTERN) && level.structureManager().getStructureAt(context.getPlayer().blockPosition(), level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(Registry.MYSTERIOUS_TOME_STRUCTURE)).getStructure() != null) {
            level.playSound(null, position, SoundEvents.BLAZE_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);

            ItemStack itemStack = context.getItemInHand();
            if (itemStack.isEmpty()) { player.setItemInHand(context.getHand(), new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get())); }
            if (!player.getInventory().add(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()))) {
                player.drop(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()), false);
            }
            player.awardStat(Stats.ITEM_CRAFTED.get(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()), 1);
            player.getCooldowns().addCooldown(this, 200);
            itemStack.shrink(1);

            MinecraftServer server = level.getServer();
            CommandSourceStack stack = server.createCommandSourceStack().withPermission(4).withSource(server).withSuppressedOutput();
            for (var i = 0; i < 5; i++) {
                server.getCommands().performPrefixedCommand(stack, String.format("pandora %s", player.getName().getString()));
            }
        }
        return InteractionResult.sidedSuccess(false);
    }
}
