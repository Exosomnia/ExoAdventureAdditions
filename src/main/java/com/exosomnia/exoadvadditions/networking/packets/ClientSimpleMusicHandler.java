package com.exosomnia.exoadvadditions.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientSimpleMusicHandler {

    public static void handle(SimpleMusicPacket packet, Supplier<NetworkEvent.Context> context) {
        Minecraft.getInstance().getMusicManager().stopPlaying();
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
        if (packet.music != null) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forMusic(ForgeRegistries.SOUND_EVENTS.getValue(packet.music)));
        }
    }
}
