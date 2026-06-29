package pom.rewrite.screen.hud;

import io.wispforest.owo.ui.container.FlowLayout;

public interface HudElement {
    void tick();

    FlowLayout getLayout();

    void updatePosition(int x, int y);

    int getSavedX();
    int getSavedY();
}
