package pom.v1.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import pom.v1.PomConfig.PomGui;


public class ModMenuIntegration implements ModMenuApi {



    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PomGui::createScreen;
    }
}
