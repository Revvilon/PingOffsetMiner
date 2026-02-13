package pom.v1.modmenu;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class pomConfig {
    public static ConfigClassHandler<pomConfig> HANDLER = ConfigClassHandler.createBuilder(pomConfig.class)
            .id(Identifier.of("ping-offset-miner", "pom_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("pom.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();

    @SerialEntry
    public Color color1 = new Color(255, 0,0);

    @SerialEntry
    public Color color2 = new Color(0, 255, 0);

    @SerialEntry
    public boolean active = true;

    @SerialEntry
    public boolean sound = false;

    @SerialEntry
    public String soundpath = "path";

    @SerialEntry
    public line selectedLine = line.ThinLine;

    public enum line implements NameableEnum {
        ThinLine(RenderLayer.LINES, "Thin lines"),
        StrippedLines(RenderLayer.LINE_STRIP, "Triangular lines"),
        ThickLine(RenderLayer.SECONDARY_BLOCK_OUTLINE, "Thick lines");


        private final Text name;
        private final RenderLayer layer;

        line(RenderLayer layer, String name) {
            this.name = Text.literal(name);
            this.layer = layer;
        }

        public RenderLayer getLayer() {
            return layer;
        }

        @Override
        public Text getDisplayName() {
            return name;
        }
    };

    public static void init() {
        HANDLER.load();
    }
    public static void save() {
        HANDLER.save();
    }
}
