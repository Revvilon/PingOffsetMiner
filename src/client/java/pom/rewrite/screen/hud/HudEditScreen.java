package pom.rewrite.screen.hud;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.DraggableContainer;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pom.rewrite.config.ConfigHandler;
import pom.rewrite.screen.click.ClickGui;

import java.util.List;
import java.util.function.BiConsumer;

public class HudEditScreen extends BaseOwoScreen<FlowLayout> {
    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    private static final List<HudElement> hudElements = HudManager.getTickables();

    private static class Draggable<C extends UIComponent> extends DraggableContainer<C> {

        private @Nullable BiConsumer<Integer, Integer> onMoveCallback = null;

        private static final int SNAP_THRESHOLD = 2;

        public Draggable(C child) {
            super(Sizing.content(), Sizing.content(), child);
            this.foreheadSize(0);
        }

        private int getRelativeX(int currentX, int maxWidth) {
            if (maxWidth <= 0) return 0;
            int pctX = Math.round(((float) currentX / maxWidth) * 100);
            return Math.abs(pctX - 50) <= SNAP_THRESHOLD ? 50 : Math.max(0, Math.min(100, pctX));
        }

        private int getRelativeY(int currentY, int maxHeight) {
            if (maxHeight <= 0) return 0;
            int pctY = Math.round(((float) currentY / maxHeight) * 100);
            return Math.abs(pctY - 50) <= SNAP_THRESHOLD ? 50 : Math.max(0, Math.min(100, pctY));
        }

        @Override
        public @Nullable UIComponent childAt(int x, int y) {
            return this.isInBoundingBox(x, y) ? this : super.childAt(x, y);
        }

        @Override
        public boolean onMouseDrag(MouseButtonEvent click, double deltaX, double deltaY) {
            this.xOffset += deltaX;
            this.yOffset += deltaY;

            var window = Minecraft.getInstance().getWindow();
            int maxWidth = window.getGuiScaledWidth() - this.width;
            int maxHeight = window.getGuiScaledHeight() - this.height;

            int intendedX = (int) (this.baseX + Math.round(this.xOffset));
            int intendedY = (int) (this.baseY + Math.round(this.yOffset));

            int finalX = getRelativeX(intendedX, maxWidth) == 50 ? maxWidth / 2 : intendedX;
            int finalY = getRelativeY(intendedY, maxHeight) == 50 ? maxHeight / 2 : intendedY;

            int actualDeltaX = finalX - this.x;
            int actualDeltaY = finalY - this.y;

            this.x = finalX;
            this.y = finalY;

            this.childView.forEach(child -> {
                child.updateX(child.x() + actualDeltaX);
                child.updateY(child.y() + actualDeltaY);
            });


            if (this.onMoveCallback != null) {
                this.onMoveCallback.accept(getRelativeX(intendedX, maxWidth), getRelativeY(intendedY, maxHeight));
            }


            return true;
        }

        public void onMove(BiConsumer<Integer, Integer> callback) {
            this.onMoveCallback = callback;
        }
    }

    @Override
    protected void build(FlowLayout root) {
        if (hudElements.isEmpty()) onClose();

        root.positioning(Positioning.relative(0, 0));
        root.surface(Surface.VANILLA_TRANSLUCENT);
        root.sizing(Sizing.fill());

        for (HudElement element : hudElements) {
            FlowLayout layout = element.getLayout();
            if (layout == null) continue;

            Draggable<FlowLayout> draggableWrapper = new Draggable<>(layout);
            draggableWrapper.surface(Surface.outline(Color.WHITE.argb()));
            draggableWrapper.padding(Insets.of(10));
            draggableWrapper.positioning(Positioning.relative(element.getSavedX(), element.getSavedY()));
            draggableWrapper.onMove(element::updatePosition);


            root.child(draggableWrapper);
        }
    }

    @Override
    public void onClose() {
        ConfigHandler.saveAsync();
        Minecraft.getInstance().setScreenAndShow(new ClickGui());
    }
}
