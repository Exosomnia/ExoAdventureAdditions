package com.exosomnia.exoadvadditions.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SimpleMusicPacket {

    protected ResourceLocation music;

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
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ClientSimpleMusicHandler.handle(packet, context);
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}