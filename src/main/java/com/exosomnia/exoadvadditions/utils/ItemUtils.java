package com.exosomnia.exoadvadditions.utils;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.UUID;

public class ItemUtils extends Item {

    public static final UUID UUID_ATTACK_DAMAGE = BASE_ATTACK_DAMAGE_UUID;
    public static final UUID UUID_ATTACK_SPEED = BASE_ATTACK_SPEED_UUID;

    public static final UUID UUID_CHEST_ARMOR = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");
    public static final UUID UUID_LEGS_ARMOR = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");

    public ItemUtils(Properties properties) {
        super(properties);
    }
}
