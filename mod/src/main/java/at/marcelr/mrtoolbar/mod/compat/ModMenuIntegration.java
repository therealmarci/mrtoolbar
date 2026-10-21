package at.marcelr.mrtoolbar.mod.compat;

import at.marcelr.mrtoolbar.mod.DesignScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** "Configure" button in Mod Menu's mod list opens the hotbar design. Only loaded if Mod Menu is installed. */
public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DesignScreen::new;
    }
}
