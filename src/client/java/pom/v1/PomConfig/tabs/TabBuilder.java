package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;

public @FunctionalInterface interface TabBuilder {
    ConfigCategory buildTab();
}
