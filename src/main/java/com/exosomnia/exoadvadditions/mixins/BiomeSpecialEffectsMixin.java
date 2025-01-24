package com.exosomnia.exoadvadditions.mixins;

import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public interface BiomeSpecialEffectsMixin {
    @Accessor("backgroundMusic")
    void setBackgroundMusic(Optional<Music> music);
}
