package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;

public @FunctionalInterface interface TabBuilder {
    ConfigCategory buildTab();
}
