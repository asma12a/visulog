package up.visulog.config;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.Map;

public class SerializableConfiguration implements Serializable {
    private Path configPath;
    private Path gitPath;
    private Path outputPath;
    private Map<String, PluginConfig> plugins;
    private boolean loadConfig;

	public SerializableConfiguration() {}

	public void fromConfig(Configuration config) {
		configPath = config.getConfigPath();
		gitPath = config.getGitPath();
		outputPath = config.getOutputPath();
		plugins = config.getPluginConfigs();
		loadConfig = config.isLoadable();
	}

	public Configuration toConfig() {
		return new Configuration(
			configPath, gitPath, outputPath, plugins, loadConfig
		);
	}

    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (configPath != null) {
            out.writeUTF(configPath.toString());
        } else {
            out.writeUTF("");
        }

        if (gitPath != null) {
            out.writeUTF(gitPath.toString());
        } else {
            out.writeUTF("");
        }

        if (outputPath != null) {
            out.writeUTF(outputPath.toString());
        } else {
            out.writeUTF("");
        }

        out.writeObject(plugins);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        String configPathRaw = in.readUTF();
        if (configPathRaw.equals("")) {
            configPath = null;
        } else {
            configPath = Paths.get(configPathRaw);
        }

        String gitPathRaw = in.readUTF();
        if (gitPathRaw.equals("")) {
            gitPath = null;
        } else {
            gitPath = Paths.get(gitPathRaw);
        }

        String outputPathRaw = in.readUTF();
        if (outputPathRaw.equals("")) {
            outputPath = null;
        } else {
            outputPath = Paths.get(outputPathRaw);
        }

        plugins = (Map<String, PluginConfig>) in.readObject();
    }	
}