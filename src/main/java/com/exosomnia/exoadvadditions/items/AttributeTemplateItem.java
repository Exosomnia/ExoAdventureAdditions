package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class AttributeTemplateItem extends Item {

    private String id;
    private Pair<Attribute, AttributeModifier>[] attributes;
    private Predicate<ItemStack> validation;
    private String langHelp;

    public AttributeTemplateItem(String id, Predicate<ItemStack> validation, String langHelp) {
        super(new Item.Properties().stacksTo(64));
        this.id = id;
        this.validation = validation;
        this.langHelp = langHelp;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.attribute_template.help", I18n.get(langHelp)),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.INFO_HEADER.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
    }

    public void setAttributes(Pair<Attribute, AttributeModifier>... pairs) {
        attributes = pairs;
    }

    public String getId() {
        return this.id;
    }

    public Pair<Attribute, AttributeModifier>[] getModifiers() {
        return this.attributes;
    }

    public boolean validate(ItemStack itemStack) {
        return validation.test(itemStack);
    }

    public static boolean isEtherium(ItemStack itemStack) {
        return itemStack.is(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.bySeparator("exoadvadditions:etherium_armor", ':')));
    }

    public static boolean isReinforceable(ItemStack itemStack) {
        return (itemStack.getItem() instanceof ArmorItem armorItem && !armorItem.getDefaultAttributeModifiers(armorItem.getEquipmentSlot()).containsKey(Attributes.ARMOR_TOUGHNESS));
    }
}
