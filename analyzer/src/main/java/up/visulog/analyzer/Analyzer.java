package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe Analyzer.
 *
 * Un objet de type Analyzer est caractérisé par les informations suivantes :
 *
 * Une configuration attribuée défnitivement.
 *
 * Un objet de type Configuration contenant un chemin de @gitPath et une paire
 * de Key-Value indiquant les plugins
 *
 * Un "résultat" suceptible d'être changé.
 *
 * @author Asma
 * @version : 1.0
 */

public class Analyzer {

    /**
     * L'attribut config de type Configuration. Cet attribut est inchangeable.
     */
    private final Configuration config;

    /**
     * L'attribut result de type AnalyzerResult.
     *
     */

    private AnalyzerResult result;

    /**
     * Constructeur Analyzer.
     *
     * A la construction d'un objet Analyzer.
     * </p>
     *
     * @param config La configuration unique d'un Analyser.
     * @see Analyzer#config.
     */

    public Analyzer(Configuration config) {
        this.config = config;
    }

    /**
     * Caclule le nombre de commits effectué par tous les auteurs.
     *
     * @return AnalyzerResult - Une instance de type AnalyzerResult.
     *
     */

    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        /**
         * Récupération de lu nom et de la valeur de chaque plugin :
         */
        for (var pluginConfigEntry : config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey();
            var pluginConfig = pluginConfigEntry.getValue();
        /**
         * Création d'un plugin à partir de son nom et de sa valeur :
         */
            var plugin = makePlugin(pluginName, pluginConfig);
        /**
         * On ajoute le plugin dans la liste des analyses plugin si il n'est pas vide.
         */
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        // TODO: try running them in parallel
        for (var plugin : plugins)
            plugin.run();

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    /**
     * La methode makePlugin cree un plugin qui depend de l'intention de
     * l'utilisateur Par exemple: si @pluginName est "countCommits", la methode
     * retourne la liste des commits crees par un auteur
     */
    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
        switch (pluginName) {
            case "countCommits":
                return Optional.of(new CountCommitsPerAuthorPlugin(config, pluginConfig));
            case "countCommitsPerDate":
                return Optional.of(new CountCommitsPerDate(config, pluginConfig));
            case "listUncommittedChanges" :
                return Optional.of(new ListUncommittedChanges(config, pluginConfig));
            case "ShowStatsCommit":
                return Optional.of(new ShowStatsCommit(config,pluginConfig));
            case "countLinesPerFilePerAuthor":
                return Optional.of(new CountLinesPerFilePerAuthorPlugin(config, pluginConfig));
            default:
                return Optional.empty();
        }
    }

}
