package pom.rewrite.screen.click.fragments;


import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;

import static pom.rewrite.screen.click.ClickGui.mainColor;
import static pom.rewrite.screen.click.ClickGui.textColor;

public class Module extends FlowLayout {
    public FlowLayout setting;
    public String description;

    public Module(FlowLayout setting, String description) {
        super(Sizing.fill(), Sizing.content(), Algorithm.HORIZONTAL);
        this.gap(10);
        this.verticalAlignment(VerticalAlignment.CENTER);
        this.horizontalAlignment(HorizontalAlignment.LEFT);
        this.padding(Insets.of(5));
        this.surface(ScreenUtil.customSurface(mainColor));
        this.margins(Insets.bottom(5));

        this.setting = setting;
        this.description = description;

        FlowLayout leftCont = UIContainers.verticalFlow(Sizing.content(), Sizing.content());
        leftCont.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        leftCont.allowOverflow(true);

        FlowLayout rightContainer = UIContainers.ltrTextFlow(Sizing.expand(), Sizing.content());
        rightContainer.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
        rightContainer.padding(Insets.of(5));

        LabelComponent descLabel = UIComponents.label(Component.literal(description).withColor(textColor.rgb()));
        descLabel.sizing(Sizing.fill(100), Sizing.content());


        leftCont.child(setting);
        rightContainer.child(descLabel);

        this.child(leftCont);
        this.child(rightContainer);
    }
}
