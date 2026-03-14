package pom.v1.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import pom.v1.PomConfig.PomConfig;
import pom.v1.PomConfig.PomGui;


public class ModMenuIntegration implements ModMenuApi {



    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return PomGui::createScreen;
    }
}
