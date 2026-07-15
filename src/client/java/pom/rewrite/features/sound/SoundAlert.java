package pom.rewrite.features.sound;

import com.google.gson.JsonElement;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingEnum;
import pom.rewrite.config.settings.SettingList;
import pom.rewrite.config.settings.SettingString;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SoundAlert {
    public static final Feature instance = new Feature("soundAlert", false);
    public static final SettingList<String> soundSet = new SettingList<>(new LinkedHashSet<>(), "sounds", instance, JsonElement::getAsString);

    public static final SettingEnum<SoundSource> soundSource = new SettingEnum<>(SoundSource.BLOCKS, SoundSource.class, "soundSource", instance);
}
