package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MagickFeatherItem extends Item {
    public MagickFeatherItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        String name = getAttunedName(itemStack);
        if (name == null) { return Component.translatable(this.getDescriptionId()); }

        return Component.translatable(this.getDescriptionId()).append(String.format(" (%s)", I18n.get(name)));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        String name = getAttunedName(itemStack);
        if (name == null) { return; }

        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.magicked_feather.info.1", I18n.get(name)),
                        ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle()));
    }

    private String getAttunedName(ItemStack itemStack) {
        if (!itemStack.hasTag() || !itemStack.getTag().contains("LocateData")) {
            return null;
        }

        Tag tag = itemStack.getTag().get("LocateData");
        if (!(tag instanceof CompoundTag locateData)) {
            return null;
        }

        if (!locateData.contains("name")) {
            return "item.exoadvadditions.magicked_feather.info.fallback";
        }

        return locateData.getString("name");
    }
}
