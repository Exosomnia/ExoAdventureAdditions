package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TomeOfAmnesia extends TomeItem {

    public TomeOfAmnesia(Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_amnesia.info.1"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_amnesia.info.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_amnesia.info.3"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        if (eternal) {
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_amnesia.info.extra.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_amnesia.info.extra.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) { return itemStack; }
        if (player.experienceLevel >= 10 || eternal) {
            if (!level.isClientSide) {
                player.getCooldowns().addCooldown(this, 20);
                if (!eternal) { player.giveExperiencePoints(-150); }
                MinecraftServer server = ((ServerLevel) level).getServer();
                CommandSourceStack sourceStack = player.createCommandSourceStack().withPermission(2).withSource(player).withSuppressedOutput();
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:combat");
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:exploration");
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:fishing");
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:husbandry");
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:mining");
                server.getCommands().performPrefixedCommand(sourceStack, "puffish_skills skills reset @s exoadventure:occult");
            }
            else if (entity instanceof LocalPlayer localPlayer) {
                localPlayer.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_amnesia.activated").withStyle(ChatFormatting.GOLD), false);
                localPlayer.playSound(SoundEvents.PLAYER_LEVELUP, 0.75F, 0.75F);
            }
            if (!eternal) { itemStack.shrink(1); }
        }
        else if (player instanceof ServerPlayer) { player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_amnesia.invalid_xp").withStyle(ChatFormatting.RED), false);; }
        return itemStack;
    }
}
