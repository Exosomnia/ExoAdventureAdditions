package com.exosomnia.exoadvadditions.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SimpleMusicPacket {

    private ResourceLocation music;

    public SimpleMusicPacket(ResourceLocation music) {
        this.music = music;
    }

    public SimpleMusicPacket(FriendlyByteBuf buffer) {
        music = buffer.readResourceLocation();
    }

    public static void encode(SimpleMusicPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.music);
    }

    public static void handle(SimpleMusicPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkDirection packetDirection = context.get().getDirection();
            if (packetDirection.equals(NetworkDirection.PLAY_TO_CLIENT)) {
                Minecraft.getInstance().getMusicManager().stopPlaying();
                Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
                if (packet.music != null) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forMusic(ForgeRegistries.SOUND_EVENTS.getValue(packet.music)));
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}