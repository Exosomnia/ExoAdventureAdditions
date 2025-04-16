package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class UnfinishedMacguffinItem extends Item {

    private final int stage;

    public UnfinishedMacguffinItem(int stage) {
        super(new Item.Properties().stacksTo(1));
        this.stage = stage;
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(2.6F * stage);
    }

    @Override
    public int getBarColor(ItemStack itemStack) { return 0x7400c7; }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.macguffin_15_unfinished.info.1", stage),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
    }
}
