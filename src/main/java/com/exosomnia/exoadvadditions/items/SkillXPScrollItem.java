package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exostats.ExoStats;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

public class SkillXPScrollItem extends Item {

    public enum Skill {
        COMBAT,
        MINING,
        FISHING,
        HUSBANDRY,
        OCCULT,
        EXPLORATION
    }

    private final Skill skill;

    public SkillXPScrollItem(Skill skill) {
        super(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
        this.skill = skill;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public Component getName(ItemStack itemStack) { return Component.translatable("item.exoadvadditions.scroll_of_skill_xp"); }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag itemTag = itemStack.getTag();
        if (itemTag == null || !itemTag.contains("value")) { return; }

        int value = itemTag.getInt("value");
        if (value <= 0) { return; }
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_skill_xp.info.1", value, skill.toString().toLowerCase()),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.level().isClientSide) { return InteractionResultHolder.consume(itemStack); }

        CompoundTag itemTag = itemStack.getTag();
        if (itemTag == null || !itemTag.contains("value")) { return InteractionResultHolder.consume(itemStack); }

        int value = itemTag.getInt("value");
        if (value <= 0) { return InteractionResultHolder.consume(itemStack); }

        switch(skill) {
            case COMBAT -> player.awardStat(ExoStats.COMBAT_SCORE.get(), value);
            case MINING -> player.awardStat(ExoStats.MINING_SCORE.get(), value);
            case FISHING -> player.awardStat(ExoStats.FISHING_SCORE.get(), value);
            case HUSBANDRY -> player.awardStat(ExoStats.HUSBANDRY_SCORE.get(), value);
            case OCCULT -> player.awardStat(ExoStats.OCCULT_SCORE.get(), value);
            case EXPLORATION -> player.awardStat(ExoStats.EXPLORATION_SCORE.get(), value);
        }

        itemStack.shrink(1);
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.75F);
        player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_skill_xp.used", skill.toString().toLowerCase()).withStyle(ChatFormatting.GOLD));

        return InteractionResultHolder.consume(itemStack);
    }
}
