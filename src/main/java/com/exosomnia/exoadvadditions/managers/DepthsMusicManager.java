package com.exosomnia.exoadvadditions.managers;

import com.exosomnia.exoadvadditions.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.concurrent.ThreadLocalRandom;

public class DepthsMusicManager {

    private final Minecraft mc;
    private final SoundManager soundManager;

    private SimpleSoundInstance currentTrack = null;
    private int musicDelay = 0;
    private boolean isPlaying = false;
    private boolean inDimension = false;

    public DepthsMusicManager() {
        mc = Minecraft.getInstance();
        soundManager = mc.getSoundManager();
        MinecraftForge.EVENT_BUS.addListener(this::playerJoinDepths);
    }

    public void tick() {
        if (inDimension) {
            if (mc.player == null || mc.level == null || !mc.level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
                musicDelay = 0;
                return;
            }
            //If the manager says we're playing, but the sound is not active, it's over, reset the delay to start again
            else if (isPlaying && !soundManager.isActive(currentTrack)) {
                currentTrack = null;
                isPlaying = false;
                musicDelay = ThreadLocalRandom.current().nextInt(600, 1200);
            }
            //If the manager says we're not playing and the delay is over, start up the music again
            else if (!isPlaying && musicDelay-- <= 0) {
                this.currentTrack = SimpleSoundInstance.forMusic(Registry.MUSIC_THE_DEPTHS.get());
                isPlaying = true;
                soundManager.play(currentTrack);
            }
            mc.getMusicManager().stopPlaying();
        }
    }

    public void playerJoinDepths(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(Registry.DEPTHS_DIMENSION) && mc.player != null) {
            inDimension = true;
            this.currentTrack = SimpleSoundInstance.forMusic(Registry.MUSIC_THE_DEPTHS.get());
            isPlaying = true;
            soundManager.play(currentTrack);
        }
        else if (event.getFrom().equals(Registry.DEPTHS_DIMENSION)) {
            inDimension = false;
            if (currentTrack != null) { soundManager.stop(currentTrack); }
        }
    }
}
