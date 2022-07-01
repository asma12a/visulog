package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.SerializableConfiguration;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Optional;

public class CLILauncher {

    public static void main(String[] args) {
        var optConfig = makeConfigFromCommandLineArgs(args);
        if (optConfig.isPresent()) {
            Configuration config = optConfig.get();

            // If runnable is set to false just save the options
            Path configPath = config.getConfigPath();
            if (configPath != null) {
                if (!config.isLoadable()) {
                    try {
                        FileOutputStream cfgBin = new FileOutputStream(configPath.toString());
                        ObjectOutputStream oos = new ObjectOutputStream(cfgBin);

                        // Serialize configuration and save to file
                        SerializableConfiguration sConfig = new SerializableConfiguration();
                        sConfig.fromConfig(config);

                        oos.writeObject(sConfig);
                        oos.flush();
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Cannot save config.");
                        return;
                    }
                    return;
                } else {
                    try {
                        FileInputStream cfgBin = new FileInputStream(configPath.toString());
                        ObjectInputStream ois = new ObjectInputStream(cfgBin);

                        // Restore saved configuration
                        SerializableConfiguration sConfig = (SerializableConfiguration) ois.readObject();
                        config = sConfig.toConfig();

                        ois.close();
                    } catch (IOException e) {
                        System.out.println("Cannot load config.");
                        return;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Cannot load config.");
                        return;
                    }
                }
            }

            var analyzer = new Analyzer(config);
            var results = analyzer.computeResults();

            Path output = config.getOutputPath();
            String htmlResult = results.toHTML();

            // If a path was supplied in argument
            // write the result to it
            if (output != null) {
                // Write to file and manage utf8 charset to
                // have no problems with specials characters
                try {
                    Files.write(
                        output,
                        htmlResult.getBytes(StandardCharsets.UTF_8)
                    );
                } catch (IOException e) {
                    System.out.println("Cannot write to file " + output);
                    return;
                }
            } else {
                System.out.println(htmlResult);
            }
        } else displayHelpAndExit();
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        boolean runnable = true;
        Path configPath = null;
        // The absolute path start in subdirectory cli/
        Path gitPath = Paths.get(
            FileSystems.getDefault().getPath(".").toAbsolutePath().toString(),
            ".."
        );

        Path outputPath = null;
        HashMap<String, PluginConfig> plugins = new HashMap<String, PluginConfig>();
        boolean loadConfig = false;

        for (var arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig
                            // Let's just trivially do this, before the TODO is fixed:

                            if (pValue.equals("countCommits")) {
                                plugins.put("countCommits", new PluginConfig());
                            } else if (pValue.equals("countCommitsPerDate")) {
                                plugins.put("countCommitsPerDate", new PluginConfig());
                            } else if (pValue.equals("listUncommittedChanges")) {
                                plugins.put("listUncommittedChanges", new PluginConfig());
                            } else if ( pValue.equals("countLinesPerFilePerAuthor")) {
                                plugins.put("countLinesPerFilePerAuthor", new PluginConfig());
                            }
                            else if (pValue.equals("ShowStatsCommit")){
                              plugins.put("ShowStatsCommit", new PluginConfig());
                            } else {
                                System.out.println("Plugin " + pValue + " doesn't exists !");
                            }

                            break;
                        case "--output":
                            outputPath = Paths.get(gitPath.toString(), pValue);
                            break;
                        case "--loadConfigFile":
                            loadConfig = true;
                            configPath = Paths.get(gitPath.toString(), pValue);
                            break;
                        case "--justSaveConfigFile":
                            configPath = Paths.get("..", pValue);
                            break;
                        case "--help":
                            // Print help and exit program
                            displayHelpAndExit();
                            break; // useless but left for clarity
                        default:
                            // Check if we have specific plugin configuration
                            if (pName.indexOf(".") < 0)
                                return Optional.empty();

                            // Skip the "--", and parse option
                            pName = pName.substring(2);
                            String[] nameParts = pName.split("\\.");
                            if (parts.length != 2)
                                return Optional.empty();
                            String targetPlugin = nameParts[0];
                            String pluginOption = nameParts[1];

                            // Check if plugin is loaded
                            if (!plugins.containsKey(targetPlugin)) {
                                System.out.println(targetPlugin + " is not loaded yet.");
                                return Optional.empty();
                            }

                            // If it is, add option
                            PluginConfig cfg = plugins.get(targetPlugin);
                            cfg.setConfigOpt(pluginOption, pValue);
                    }
                }
            } else {
                if (!arg.equals(".")) {
                    gitPath = FileSystems.getDefault().getPath(arg);
                }
            }
        }

        return Optional.of(
            new Configuration(configPath, gitPath, outputPath, plugins, loadConfig)
        );
    }

    private static void displayHelpAndExit() {
        System.out.println("usage: ./gradlew run --args='[args]'");
        System.out.println("Analyze a git project with different plugins");
        System.out.println();

        System.out.println("- Common:");
        System.out.println("\t--help: print this help");
        System.out.println("\t--output=<filename>: write the plugin output to the file");
        System.out.println();

        // Aide à la gestion de la configuration
        System.out.println("- Configuration:");
        System.out.println("\t--loadConfigFile=<filename>: load configuration from <filename> (not implemented)");
        System.out.println("\t--justSaveConfigFile=<filename>: save the current configuration to <filename> (not implemented)");
        System.out.println();

        // Aide à la gestion des plugins
        System.out.println("- Plugins:");
        System.out.println("\t--addPlugin=<name> load the plugin <name>");
        System.out.println();

        // Liste et documente les différents plugins
        System.out.println("- Available plugins:");
        System.out.println("\tcountCommits: count the numbers of commits made by the project contributors");
        System.out.println("\tcountCommitsPerDate: count the numbers of commit made per date");
        System.out.println("\tlistUncommittedChanges: print the uncommitted changes in the project");
        System.out.println("\tcountLinesPerFilePerAuthor: count the numbers of line edited per file per author");

        System.exit(0);
    }
}
