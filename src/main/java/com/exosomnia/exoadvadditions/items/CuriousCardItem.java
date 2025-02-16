package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.mixins.VillagerAccessor;
import com.exosomnia.exolib.utils.ComponentUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CuriousCardItem extends Item {
    public CuriousCardItem(Properties properties) {
        super(properties);
    }

    public void createNumber(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        int[] newNumber = new int[4];
        for (var i = 0; i < 4; i++) {
            newNumber[i] = ThreadLocalRandom.current().nextInt(1000, 10000);
        }
        tag.putIntArray("cardNumber", newNumber);
    }

    public boolean hasNumber(ItemStack itemStack) {
        return itemStack.getOrCreateTag().contains("cardNumber");
    }

    public Integer[] getCardNumber(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        int[] number = tag.getIntArray("cardNumber");
        return new Integer[]{ number[0], number[1], number[2], number[3] };
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        if (!hasNumber(itemStack)) { createNumber(itemStack); }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.curious_card.info"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        if (hasNumber(itemStack)) {
            components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.curious_card.number", getCardNumber(itemStack)),
                    ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (player.level().isClientSide || !(livingEntity instanceof Villager villager)) { return InteractionResult.PASS; }

        VillagerData data = villager.getVillagerData();
        if (data.getProfession().equals(VillagerProfession.NONE) && data.getLevel() < 4) { return InteractionResult.PASS; }

        VillagerAccessor access = ((VillagerAccessor)villager);
        int currentLvl = Math.max(data.getLevel(), 1);
        for (var i = currentLvl; i < 5; i++) {
            access.callIncreaseMerchantCareer();
        }
        access.setIncreaseProfessionLevelOnUpdate(false);
        villager.heal(villager.getMaxHealth());
        if (villager.isTrading()) { villager.getTradingPlayer().sendMerchantOffers(villager.getTradingPlayer().containerMenu.containerId, villager.getOffers(), 5, villager.getVillagerXp(), villager.showProgressBar(), villager.canRestock()); }

        itemStack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
