package pav.sprykerFileCreator.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "SprykerPluginSettings",
        storages = {
                @Storage("/spryker.xml")
        }
)
public class Settings implements PersistentStateComponent<Settings> {
    public static final String DEFAULT_PROJECT_ROOT = "/";
    public static final String[] DEFAULT_SPRYKER_NAMESPACES = new String[]{
            "Spryker",
            "SprykerMiddleware",
            "SprykerShop",
            "SprykerEco",
            "Spryker"
    };

    public boolean pluginEnabled = true;
    public String projectRoot = Settings.DEFAULT_PROJECT_ROOT;
    public boolean deleteClassContent = false;
    public boolean allowAnyNamespace = false;

    public static Settings getInstance(Project project) {
        return ServiceManager.getService(project, Settings.class);
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }
}
