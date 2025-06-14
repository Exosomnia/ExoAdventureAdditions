package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SkillXPScrollRandomItem extends Item {

    public SkillXPScrollRandomItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_skill_xp_random.info.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_skill_xp_random.info.2"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_skill_xp_random.info.3"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        if (level != null && level.isClientSide) {
            Player player = ExoArmory.DIST_HELPER.getDefaultPlayer();
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_skill_xp_random.info.4", player.totalExperience),
                    ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.INFO_HEADER.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.level().isClientSide) { return InteractionResultHolder.consume(itemStack); }

        int value = Math.min(player.totalExperience, 25000);
        if (value > 0) {
            player.giveExperiencePoints(-value);

            ItemStack scroll = new ItemStack(Registry.xpScrolls[level.random.nextInt(Registry.xpScrolls.length)]);
            scroll.getOrCreateTag().putInt("value", (value * 4));

            itemStack.shrink(1);
            player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.75F);
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_skill_xp_random.used").withStyle(ChatFormatting.GOLD));

            if (itemStack.isEmpty()) {
                return InteractionResultHolder.consume(scroll);
            }
            if (!player.getInventory().add(scroll.copy())) {
                player.drop(scroll, false);
            }
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
