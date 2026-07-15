package pom.rewrite.utility.sound;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import pom.rewrite.config.settings.SettingEnum;
import pom.rewrite.config.settings.SettingList;
import pom.rewrite.config.settings.SettingString;
import pom.rewrite.events.clientTick;
import pom.rewrite.events.finishBlockBreak;
import pom.rewrite.features.sound.SoundAlert;
import pom.rewrite.utility.stats.TickStats;

import java.util.Optional;

public class SoundUtil {

    private final SettingEnum<SoundSource> setting = SoundAlert.soundSource;
    private final SettingList<String> soundPath = SoundAlert.soundSet;

    private void playSound() {
        Minecraft mc =  Minecraft.getInstance();

        if (mc.player == null || mc.level == null || !SoundAlert.instance.isEnabled()) return;

        for (String sounds : soundPath.values) {
            Optional<SoundEvent> event = getSoundEvent(sounds);
            event.ifPresent(soundEvent -> mc.getSoundManager().play(getSoundInstance(soundEvent, setting.value())));
        }
    }

    private SoundInstance getSoundInstance(SoundEvent event, SoundSource source) {
        return new SimpleSoundInstance(event.location(), source, 0.25f, 1.0f, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0d, 0.0d, 0.0d, true);
    }

    private Optional<SoundEvent> getSoundEvent(String identifier) {
        return BuiltInRegistries.SOUND_EVENT.getOptional(Identifier.parse(identifier));
    }

    private boolean lastCondition = false;

    private void tick(boolean currentCondition) {
        if (currentCondition && !lastCondition) playSound();

        lastCondition = currentCondition;
    }

    @EventHandler
    private void clientTick(clientTick event) {
        tick(TickStats.instance().timeoutExceeded());
    }
}
