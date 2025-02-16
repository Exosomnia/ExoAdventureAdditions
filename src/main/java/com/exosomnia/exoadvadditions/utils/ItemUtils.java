package com.exosomnia.exoadvadditions.utils;

import net.minecraft.world.item.Item;

import java.util.UUID;

public class ItemUtils extends Item {

    public static final UUID UUID_ATTACK_DAMAGE = BASE_ATTACK_DAMAGE_UUID;
    public static final UUID UUID_ATTACK_SPEED = BASE_ATTACK_SPEED_UUID;

    public ItemUtils(Properties properties) {
        super(properties);
    }
}
