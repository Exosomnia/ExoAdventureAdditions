package com.exosomnia.exoadvadditions.effects;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WakefulnessEffect extends MobEffect {

    private final static String MOVEMENT_UUID = "00b04e4e-4a4c-4535-8c0d-99ff24df68a7";

    public WakefulnessEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                MOVEMENT_UUID, 0.05, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @SubscribeEvent
    public static void spawnPhantomsEvent(PlayerSpawnPhantomsEvent event) {
        if (event.getEntity().hasEffect(Registry.EFFECT_WAKEFULNESS.get())) { event.setResult(Event.Result.DENY); }
    }
}
