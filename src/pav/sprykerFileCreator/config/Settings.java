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
    public static final String OVERRIDE_CLASS_CONTENT = "OVERRIDE_CLASS_CONTENT";
    public static final String ALLOW_ANY_NAMESPACE = "ALLOW_ANY_NAMESPACE";

    public static String PROJECT_ROOT = "PROJECT_ROOT";
    public static String test = "persistent config test";

    public static final String[] DEFAULT_SPRYKER_NAMESPACES = new String[]{
            "Spryker",
            "SprykerMiddleware",
            "SprykerShop",
            "SprykerEco",
            "Spryker"
    };

    public boolean pluginEnabled = true;

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
