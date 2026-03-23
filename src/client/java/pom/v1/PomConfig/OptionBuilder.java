package pom.v1.PomConfig;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.controller.*;

import java.awt.*;
import java.util.ArrayList;

public class OptionBuilder {


    @SuppressWarnings("Deprecated")
    protected void link(Option<Boolean> master, ArrayList<Option<?>> options) {
        master.addEventListener((option, event) -> {
            if (event == OptionEventListener.Event.STATE_CHANGE) {
                boolean newVal = master.pendingValue();

                for (Option<?> opt : options) {
                    opt.setAvailable(newVal);
                }
            }
        });
    }
}
