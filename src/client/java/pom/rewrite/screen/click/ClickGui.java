package pom.rewrite.screen.click;

import com.google.common.collect.Lists;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import pom.rewrite.config.ConfigHandler;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.*;
import pom.rewrite.features.PingOffsetMiner;
import pom.rewrite.features.debug.CustomStats;
import pom.rewrite.features.debug.HSMModern;
import pom.rewrite.features.debug.Logging;
import pom.rewrite.features.debug.Profiler;
import pom.rewrite.features.gui.EfficiencyDisplay;
import pom.rewrite.features.gui.TickDisplay;
import pom.rewrite.features.render.HighlightRender;
import pom.rewrite.features.render.MsbRender;
import pom.rewrite.features.render.OutlineRender;
import pom.rewrite.features.render.ProgressRender;
import pom.rewrite.features.sound.SoundAlert;
import pom.rewrite.features.toggles.BlockToggles;
import pom.rewrite.features.toggles.IslandToggles;
import pom.rewrite.screen.click.fragments.*;
import pom.rewrite.screen.click.fragments.Module;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.utility.block.BlockObject;
import pom.rewrite.utility.block.BlockUtil;
import pom.rewrite.utility.sound.SoundRegistryManager;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class ClickGui extends BaseOwoScreen<FlowLayout> {

    public static final Color mainColor = Color.ofRgb(0x222831);
    public static final Color accentColor = Color.ofRgb(0x0097fa);
    public static final Color textColor = Color.ofRgb(0xf2faff);
    public static final Color secondaryColor = Color.ofRgb(0x2b3f57);

    private List<Tab> tabs;


    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    public void onClose() {
        ConfigHandler.saveAsync();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void build(FlowLayout root) {
        root.sizing(Sizing.fill());
        root.surface(Surface.VANILLA_TRANSLUCENT);

        tabs = Lists.newArrayList(
                new Tab("Main",
                        new Module(new FeatureToggle("", PingOffsetMiner.instance),
                                "Toggles Ping Offset Miner"
                                ),
                        new GroupLayout("Outline",
                                new Module(
                                        new FeatureToggle("", OutlineRender.instance),
                                        "Outline rendering"
                                ),
                                new Module(
                                        new ColorSelector("", OutlineRender.preMined),
                                        "Block not broken color"
                                ),
                                new Module(
                                        new ColorSelector("", OutlineRender.postMined),
                                        "Block broken color"
                                ),
                                new Module(
                                        new SliderFloat("Line Width", OutlineRender.lineWidth, 0f, 1f, 0.1f),
                                        "Line width"
                                ),
                                new Module(
                                        new Toggle("", OutlineRender.depthToggle),
                                        "Depth testing"
                                )
                        ),
                        new GroupLayout("Highlight",
                                new Module(
                                        new FeatureToggle("", HighlightRender.instance),
                                        "Highlight rendering"
                                ),
                                new Module(
                                        new ColorSelector("", HighlightRender.preMined),
                                        "Block not broken color"
                                ),
                                new Module(
                                        new ColorSelector("", HighlightRender.postMined),
                                        "Block broken color"
                                ),
                                new Module(
                                        new Toggle("", HighlightRender.depthToggle),
                                        "Depth testing"
                                )
                        ),
                        new Module(new FeatureToggle("", ProgressRender.instance), "Toggles custom mining progress rendering"),
                        new GroupLayout("Mining Speed Boost",
                                new Module(
                                        new FeatureToggle("", MsbRender.instance),
                                        "Should check for MSB"
                                ),
                                new Module(
                                        new TextToggle("", MsbRender.msbToggle, "ON", "OFF"),
                                        "When using MSB, toggle:"
                                )
                                ),
                        new GroupLayout("Sound",
                                new Module(
                                        new FeatureToggle("", SoundAlert.instance),
                                        "Sound alert for mining"
                                        ),
                                new Module(
                                        new EnumToggle<>(SoundAlert.soundSource),
                                        "What sound slider should control sound"
                                ),
                                new CollapsibleSelectorSearch(SoundAlert.soundSet, "Sounds"),
                                new Module(
                                        new CleanButton("Click", press -> Util.getPlatform().openUri("https://www.digminecraft.com/lists/sound_list_pc.php")),
                                        "Website with available sounds"
                                )
                                )
                ),
                new Tab("Hud",
                            new Module(new CleanButton("Hud editor", button -> Minecraft.getInstance().setScreen(new HudEditScreen())), ""),
                            new Module(new FeatureToggle("", EfficiencyDisplay.instance), "Toggles Efficiency Hud"),
                            new Module(new FeatureToggle("", Profiler.instance), "Toggles Ping and Tps Hud"),
                            new Module(new FeatureToggle("", TickDisplay.instance), "Toggles tick Hud"),
                            new Module(new FeatureToggle("", Logging.instance), "Toggles debug Hud")
                        ),
                new Tab("Blocks",
                            new BlockToggleUI("Blocks", BlockToggles.blocks)
                        ),
                new Tab("Islands",
                            new IslandTogglesUI("Islands", IslandToggles.islands)
                        ),
                new Tab("Debug",
                        new Module(new FeatureToggle("", Logging.instance), "Toggles logging"),
                        new GroupLayout("Custom stats",
                                new Module(new FeatureToggle("", CustomStats.instance), "Toggles custom stats"),
                                new Module(new SliderInt("Mining Speed", CustomStats.customSpeed, 0, 20000, 1000), "Custom mining speed"),
                                new Module(new SliderFloat("Tps", CustomStats.customTps, 0, 20, 1), "Custom tps"),
                                new Module(new SliderInt("Ping", CustomStats.customPing, 0, 200, 25), "Custom ping")
                        ),
                        new GroupLayout("Miscellaneous",
                                    new Module(new IntInput("", CustomStats.extraSpeed), "Amount of extra speed applied to gemstones"),
                                new Module(new IntInput("", CustomStats.extraSpeedMetal), "Amount of extra speed applied to metals"),
                                new Module(new FeatureToggle("", HSMModern.instance), "Toggles HSM")
                                )
                )
        );

        FlowLayout body = UIContainers.horizontalFlow(Sizing.fill(70), Sizing.fill(70));
        body.surface(ScreenUtil.customSurface(mainColor));
        body.positioning(Positioning.relative(50,50));
        body.padding(Insets.of(10));
        body.gap(5);


        FlowLayout leftContainer = UIContainers.verticalFlow(Sizing.fill(25), Sizing.fill());
        leftContainer.gap(5);
        leftContainer.surface(ScreenUtil.customSurface(mainColor));
        leftContainer.padding(Insets.of(5));

        ImageComponent image = new ImageComponent(Identifier.fromNamespaceAndPath(MOD_ID, "pom128px.png"), 0, 0, 128, 128, 128, 128);
        image.maxHeight(128);

        FlowLayout imageContainer = UIContainers.verticalFlow(Sizing.fill(), Sizing.content());
        imageContainer.surface(ScreenUtil.customSurface(mainColor));
        imageContainer.padding(Insets.of(5));
        imageContainer.child(image);
        imageContainer.horizontalAlignment(HorizontalAlignment.CENTER);

        image.sizing(Sizing.fill(), Sizing.content());

        leftContainer.child(imageContainer);

        FlowLayout leftScroller = UIContainers.verticalFlow(Sizing.fill(), Sizing.content());
        leftScroller.gap(2);
        leftScroller.padding(Insets.of(5));



        ScrollContainer<FlowLayout> scrollContainer = UIContainers.verticalScroll(
                Sizing.fill(),
                Sizing.fill(),
                leftScroller
        );

        scrollContainer.surface(ScreenUtil.customSurface(ScreenUtil.darken(mainColor, 0.7f), true));

        leftContainer.child(scrollContainer);

        FlowLayout tabScroller = UIContainers.verticalFlow(Sizing.fill(), Sizing.content());
        tabScroller.padding(Insets.of(10));
        tabScroller.gap(10);

        ScrollContainer<FlowLayout> tabScrollContainer = UIContainers.verticalScroll(Sizing.fill(75), Sizing.fill(), tabScroller);
        tabScrollContainer.surface(ScreenUtil.customSurface(ScreenUtil.darken(mainColor, 0.7f), true));

        for (Tab tab : tabs) {
            ButtonComponent button = UIComponents.button(Component.literal(tab.name), butt -> {
                tabScroller.clearChildren();
                tabScroller.child(tab);
                tabContainerTemp = tab;
            });

            button.sizing(Sizing.fill(), Sizing.content());
            button.renderer((g, a, e) -> {});

            leftScroller.child(button);
        }
        tabScroller.clearChildren();
        tabScroller.child(tabContainerTemp == null ? tabs.getFirst() : tabContainerTemp);

        body.child(leftContainer);
        body.child(tabScrollContainer);

        root.child(body);
    }

    private FlowLayout tabContainerTemp;

    public static final class Toggle extends FlowLayout {
        public SettingBoolean setting;
        public String name;

        Toggle(String name, SettingBoolean setting) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
            this.name = name;
            this.setting = setting;
            this.verticalAlignment(VerticalAlignment.CENTER);
            this.horizontalAlignment(HorizontalAlignment.LEFT);

            this.gap(5);

            LabelComponent label = UIComponents.label(Component.literal(name));
            label.verticalTextAlignment(VerticalAlignment.CENTER);

            ToggleButton toggleButton = new ToggleButton(name, setting.getBool());
            toggleButton.onToggled().subscribe(setting::set);

            this.child(label);
            this.child(toggleButton);
        }
    }

    public static final class CleanButton extends FlowLayout {
        public CleanButton(String text, Consumer<ButtonComponent> pressAction) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

            ButtonClean button = new ButtonClean(text, pressAction);

            this.child(button);
        }
    }

    public static final class FeatureToggle extends FlowLayout {
        public Feature feature;
        public String name;

        public FeatureToggle(String name, Feature feature) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
            this.verticalAlignment(VerticalAlignment.CENTER);
            this.horizontalAlignment(HorizontalAlignment.LEFT);
            this.gap(5);

            this.name = name;
            this.feature = feature;

            LabelComponent label = UIComponents.label(Component.literal(name));
            label.verticalTextAlignment(VerticalAlignment.CENTER);

            ToggleButton togglebutton = new ToggleButton(name, feature.isEnabled());
            togglebutton.onToggled().subscribe(feature::setEnabled);

            this.child(label);
            this.child(togglebutton);
        }
    }

    public static final class TextSelector extends FlowLayout {
        public SettingString setting;
        public String name;

        public TextSelector(String name, SettingString setting) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

            this.name = name;
            this.setting = setting;

            TextBoxComponent textBox = UIComponents.textBox(Sizing.fill(25));
            textBox.text(setting.getString());

            textBox.onChanged().subscribe(setting::set);

            this.child(textBox);
        }
    }

    public static final class ColorSelector extends FlowLayout {
        public SettingColor color;
        public String name;

        public ColorSelector(String name, SettingColor color) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
            this.verticalAlignment(VerticalAlignment.CENTER);
            this.horizontalAlignment(HorizontalAlignment.LEFT);
            this.gap(5);

            this.name = name;
            this.color = color;

            ToggleButton colorButton = new ToggleButton(" ", false);

            ColorPickerComponent picker = new ColorPickerComponent();
            picker.showAlpha(true);
            picker.sizing(Sizing.fixed(200), Sizing.fixed(100));
            picker.selectedColor(color.getColor());

            OverlayContainer<ColorPickerComponent> overlayPicker = UIContainers.overlay(picker);
            overlayPicker.closeOnClick(true);
            picker.onChanged().subscribe(value -> this.color.set(value));

            colorButton.onPress(button -> {
                if (this.root() instanceof FlowLayout root) {
                    if (!overlayPicker.hasParent()) {
                        root.child(overlayPicker);
                    } else {
                        root.removeChild(overlayPicker);
                    }
                }
            });

            colorButton.renderer((context, button, delta) -> {
                ScreenUtil.fillAndOutline(context, color.getColor(), button.x(), button.y(), button.width(), button.height());
            });


            this.child(colorButton);
        }
    }

    public static final class IntInput extends FlowLayout {
        public SettingInt setting;
        public String name;

        public IntInput(String name, SettingInt setting) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

            this.name = name;
            this.setting = setting;

            TextBoxComponent textBox = UIComponents.textBox(Sizing.fill(25));
            textBox.text(String.valueOf(setting.getInt()));

            textBox.onChanged().subscribe(newVal -> {
                int intVal = Integer.parseInt(newVal);
                setting.set(intVal);
            });

            this.child(textBox);
        }
    }

    public static final class TextToggle extends FlowLayout {
        SettingBoolean setting;
        String name;

        public TextToggle(String name, SettingBoolean setting, String enabled, String disabled) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
            this.setting = setting;
            this.name = name;

            ToggleText toggle = new ToggleText(enabled, disabled, setting.getBool());

            toggle.onToggled().subscribe(setting::set);

            this.child(toggle);
        }
    }

    public static final class EnumToggle<T extends Enum<T>> extends FlowLayout {

        public SettingEnum<T> setting;

        EnumToggle(SettingEnum<T>  setting) {
            super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);

            this.setting = setting;

            EnumClicker<T> enumClicker = new EnumClicker<>(setting.value().name(), setting.defaultValue(), setting.values);

            this.child(enumClicker);

            enumClicker.onChanged().subscribe(setting::set);

        }
    }

    public static class CollapsibleSelectorSearch extends FlowLayout {

        SettingList<String> setting;
        FlowLayout soundsContainer;

        CollapsibleSelectorSearch(SettingList<String> setting, String name) {
            super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);

            this.setting = setting;
            this.padding(Insets.of(5));
            this.surface(ScreenUtil.customSurface(mainColor));


            soundsContainer = UIContainers.verticalFlow(Sizing.fill(), Sizing.content());


            TextBoxComponent addBox = UIComponents.textBox(Sizing.fill(25));
            addBox.setSuggestion("Sound path...");
            addBox.onChanged().subscribe(query -> {
                if (query.isEmpty()) addBox.setSuggestion("Sound path...");
                else addBox.setSuggestion("");
            });
            CleanButton addButton = getCleanButton(addBox);

            ScrollContainer<FlowLayout> scroll = UIContainers.verticalScroll(Sizing.content(), Sizing.fill(), soundsContainer);
            scroll.scrollbarThiccness(10);
            CollapsibleContainer collapsible = UIContainers.collapsible(Sizing.content(), Sizing.content(), Component.literal(name), false);
            collapsible.child(scroll);
            collapsible.titleLayout().child(0, addBox).child(1, addButton);
            collapsible.titleLayout().alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);

            this.child(collapsible);

            updateContainer();
        }

        private @NonNull CleanButton getCleanButton(TextBoxComponent addBox) {
            CleanButton addButton = new CleanButton("Add", _ -> {
                boolean isSound = pom.rewrite.utility.Util.isSoundEvent(addBox.getValue());
                if (isSound) {
                    this.setting.addValue(addBox.getValue());
                    addBox.text("");
                    addBox.setSuggestion("Added!");
                    updateContainer();
                    return;
                }
                addBox.text("");
                addBox.setSuggestion("Invalid sound!");
            });
            addButton.padding(Insets.right(5));
            return addButton;
        }

        private void updateContainer() {
            if (soundsContainer == null) return;

            soundsContainer.clearChildren();
            setting.getSet().forEach(value -> soundsContainer.child(getSoundsContainer(value)));
        }

        private FlowLayout getSoundsContainer(String id) {
            FlowLayout container = UIContainers.horizontalFlow(Sizing.fill(), Sizing.content());
            container.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
            container.gap(5);
            CleanButton cleanButton = new CleanButton("✖", _ -> {
                setting.removeValue(id);
                updateContainer();
            });
            LabelComponent label = UIComponents.label(Component.literal(id));

            container.child(cleanButton).child(label);
            return container;
        }
    }

    public static final class SliderInt extends FlowLayout {

        SettingInt setting;
        String name;

        SliderInt(String name, SettingInt setting, int min, int max, int step) {
            super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);

            this.setting = setting;
            this.name = name;

            SliderComp slider = new SliderComp();

            slider.value(setting.getInt());
            slider.min(min);
            slider.max(max);
            slider.stepSize(step);
            slider.sizing(Sizing.fill(25), Sizing.content());

            FlowLayout container = UIContainers.verticalFlow(Sizing.content(), Sizing.content());
            container.horizontalAlignment(HorizontalAlignment.CENTER);

            LabelComponent label = UIComponents.label(Component.literal(String.valueOf(setting.getInt())));
            label.margins(Insets.bottom(5));

            slider.onChanged().subscribe(value -> {
                this.setting.set(value);
                label.text(Component.literal(String.format(Locale.ROOT, "%.1f", value)));
            });

            container.child(label);
            container.child(slider);
            slider.value(setting.getInt());

            this.child(container);
        }
    }

    public static final class SliderFloat extends FlowLayout {
        SettingNum setting;
        String name;

        SliderFloat(String name, SettingNum setting, float min, float max, float step) {
            super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);

            this.setting = setting;
            this.name = name;
            SliderComp slider = new SliderComp();
            slider.value(setting.getFloat());
            slider.stepSize(step);
            slider.max(max);
            slider.min(min);
            slider.sizing(Sizing.fill(25), Sizing.content());

            FlowLayout container = UIContainers.verticalFlow(Sizing.content(), Sizing.content());
            container.horizontalAlignment(HorizontalAlignment.CENTER);

            LabelComponent label = UIComponents.label(Component.literal(String.format(Locale.ROOT, "%.1f", setting.getFloat())));
            label.margins(Insets.bottom(5));

            slider.onChanged().subscribe(value -> {
                this.setting.set(value);
                label.text(Component.literal(String.format(Locale.ROOT, "%.1f", value)));
            });

            container.child(label);
            container.child(slider);
            slider.value(setting.getFloat());

            this.child(container);
        }
    }

    public static final class IslandTogglesUI extends FlowLayout {
        public SettingHash setting;
        public String name;

        public IslandTogglesUI(String name, SettingHash setting) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

            this.name = name;
            this.setting = setting;

            setting.getMap().forEach((key, value) -> {
                FlowLayout container =  UIContainers.verticalFlow(Sizing.content(), Sizing.content());
                ToggleButton toggle = new ToggleButton("", value);
                toggle.onToggled().subscribe(newVal -> setting.set(key, newVal));
                container.child(toggle);

                Module module = new Module(
                        container,
                        key
                );

                this.child(module);
            });
        }
    }

    public static final class BlockToggleUI extends FlowLayout {
        public SettingHash setting;
        public String name;

        public BlockToggleUI(String name, SettingHash setting) {
            super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);

            this.gap(5);
            this.name = name;
            this.setting = setting;

            FlowLayout gems = UIContainers.ltrTextFlow(Sizing.fill(), Sizing.content());
            gems.gap(5);
            FlowLayout metal = UIContainers.ltrTextFlow(Sizing.fill(), Sizing.content());
            metal.gap(5);
            FlowLayout ores = UIContainers.ltrTextFlow(Sizing.fill(), Sizing.content());
            ores.gap(5);

            setting.getMap().forEach((key, value) -> {
                FlowLayout blockSwitch = UIContainers.verticalFlow(Sizing.content(), Sizing.content());
                blockSwitch.horizontalAlignment(HorizontalAlignment.CENTER);
                blockSwitch.gap(5);
                ToggleButton button = new ToggleButton(" ", value);

                Identifier location = Identifier.parse("minecraft:" + key);
                Block block = BuiltInRegistries.BLOCK.getValue(location);
                BlockState state = block.defaultBlockState();
                BlockToggle blockComponent = new BlockToggle(state);

                blockSwitch.child(blockComponent);
                blockSwitch.child(button);

                blockComponent.mouseDown().subscribe((event, bool) -> {
                    button.setToggle();
                    return bool;
                });
                button.onToggled().subscribe(newVal -> setting.set(key, newVal));

                if (BlockUtil.getBlock(key) instanceof BlockObject object) {
                    if (object.id.contains("gem")) {gems.child(blockSwitch); return;}
                    if (object.id.contains("skyblock")) {metal.child(blockSwitch); return;}
                    ores.child(blockSwitch);
                }
            });

            GroupLayout gemsGroup = new GroupLayout("Gems:", gems);
            GroupLayout metalGroup = new GroupLayout("Metals:", metal);
            GroupLayout oresGroup = new GroupLayout("Ores:", ores);

            this.child(gemsGroup).child(metalGroup).child(oresGroup);

        }
    }
}
