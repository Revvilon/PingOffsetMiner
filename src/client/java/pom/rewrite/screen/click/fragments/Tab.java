package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;

public class Tab extends FlowLayout {
    public String name;

    public Tab(String tabName, FlowLayout... groups) {
        super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);
        this.name = tabName;

        this.gap(10);


        for (FlowLayout layout : groups) {
            this.child(layout);
        }
    }
}
