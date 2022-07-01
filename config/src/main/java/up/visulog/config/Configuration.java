package up.visulog.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;

public class Configuration {
    private final Path configPath;
    private final Path gitPath;
    private final Path outputPath;
    private final Map<String, PluginConfig> plugins;
    private final boolean loadConfig;

    public Configuration(Path configPath, Path gitPath, Path outputPath, Map<String, PluginConfig> plugins, boolean loadConfig) {
        this.configPath = configPath;
        this.gitPath = gitPath;
        this.outputPath = outputPath;
        this.plugins = Map.copyOf(plugins);
        this.loadConfig = loadConfig;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public Path getGitPath() {
        return gitPath;
    }

    public Path getOutputPath() {
        return outputPath;
    }
    
    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }

    public boolean isLoadable() {
        return loadConfig;
    }
}
