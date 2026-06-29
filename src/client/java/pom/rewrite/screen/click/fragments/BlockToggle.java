package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.BlockComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.world.level.block.state.BlockState;

public class BlockToggle extends BlockComponent {
    public BlockToggle(BlockState state) {
        super(state, null);
        this.sizing(Sizing.fixed(50));
        this.cursorStyle(CursorStyle.HAND);
    }
}
