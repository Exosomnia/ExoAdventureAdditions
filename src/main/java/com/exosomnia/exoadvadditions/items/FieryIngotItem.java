package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class FieryIngotItem extends Item {
    public FieryIngotItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && level.random.nextInt(200) == 0) {
            entity.hurt(level.damageSources().inFire(), 2);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.fiery_ingot.info"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}
