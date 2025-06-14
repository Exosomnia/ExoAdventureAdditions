package com.exosomnia.exoadvadditions.effects;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exoskills.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FatiguedEffect extends MobEffect {

    private final static String ATTACK_DAMAGE_UUID = "6fa24011-0b7a-47a4-b9be-7ef9515b3363";
    private final static String ATTACK_SPEED_UUID = "09e5dc64-a200-4cad-af8e-78b9fbf07fa4";
    private final static String RANGED_STRENGTH_UUID = "1690ad93-46a6-49bb-97fb-593100ed693b";
    private final static String SPELL_POWER_UUID = "9c3e4b09-7c5b-4cd9-8aa6-1e01e5cae44b";

    public FatiguedEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);

        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                ATTACK_DAMAGE_UUID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ATTACK_SPEED_UUID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ExoArmory.REGISTRY.ATTRIBUTE_RANGED_STRENGTH.get(),
                RANGED_STRENGTH_UUID, -0.2, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static void handleIntegrations(MobEffect effect) {
        effect.addAttributeModifier(Registry.ATTRIBUTE_MAGIC_DAMAGE,
                SPELL_POWER_UUID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
