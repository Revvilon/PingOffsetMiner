package pom.v1.PomConfig;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OptionBuilder {


    /**
     * @param name          Option display name
     * @param prop          Option field
     * @param controller    Controller factory
     */
    public static <T> Option.Builder<T> build(
            String name,
            PomConfig.Property<T> prop,
            Function<Option<T>, ControllerBuilder<T>> controller
     ) {

        return Option.<T>createBuilder()
                .name(Component.literal(name))
                .binding(
                        prop.getDefault(),
                        prop::get,
                        prop::set
                )
                .controller(controller);
    }

    public static OptionGroup buildLinked(OptionGroup option) {
        var options = option.options();
        if (options.size() <= 1) return option;

        if (options.getFirst().pendingValue() instanceof Boolean) {
            @SuppressWarnings("unchecked")
            Option<Boolean> master =  (Option<Boolean>) options.getFirst();

            ArrayList<Option<?>> children = new ArrayList<>(options.subList(1, options.size()));

            link(master, children);
        }

        return option;
    }

    public static List<Option<?>> buildLinked(List<Option<?>> options) {
        if (options.size() <= 1) return options;

        if (options.getFirst().pendingValue() instanceof Boolean) {
            @SuppressWarnings("unchecked")
            Option<Boolean> master =  (Option<Boolean>) options.getFirst();

            ArrayList<Option<?>> children = new ArrayList<>(options.subList(1, options.size()));

            link(master, children);
        }
        return options;
    }

    @SuppressWarnings("Deprecated")
    private static void link(Option<Boolean> master, ArrayList<Option<?>> options) {

        options.forEach(option -> option.setAvailable(master.pendingValue()));

        master.addEventListener((opt, event) -> {
            for (Option<?> child: options) {
                if (event == OptionEventListener.Event.STATE_CHANGE) {
                    child.setAvailable(master.pendingValue());
                }
            }
        });

    }
}
