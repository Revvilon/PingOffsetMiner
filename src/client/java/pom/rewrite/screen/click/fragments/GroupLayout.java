package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;

import java.util.Arrays;
import java.util.List;

import static pom.rewrite.screen.click.ClickGui.mainColor;

public class GroupLayout extends FlowLayout {
    public String displayName;
    public List<FlowLayout> layouts;

    public GroupLayout(String name, FlowLayout... layouts) {
        this(name, Algorithm.VERTICAL, layouts);
    }

    public GroupLayout(String name, Algorithm alignment, FlowLayout... layouts) {
        super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);
        this.surface(ScreenUtil.customSurface(mainColor));
        this.padding(Insets.of(5));
        this.verticalAlignment(VerticalAlignment.CENTER);
        this.gap(5);


        this.displayName = name;

        this.layouts = Arrays.asList(layouts);

        CollapsibleContainer collapsibleGroup = UIContainers.collapsible(Sizing.fill(), Sizing.content(), Component.literal(name), true);

        alignment.layout(collapsibleGroup);


        this.child(collapsibleGroup);

        for (FlowLayout layout : this.layouts) {
            collapsibleGroup.child(layout);
        }
    }

}
