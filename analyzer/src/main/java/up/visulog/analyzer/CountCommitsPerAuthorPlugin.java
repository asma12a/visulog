package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.*;

    /**
     * Classe CountCommitsPerAuthorPlugin qui implémente l'interface AnalyzerPlugin.
     * 
     * Un objet de type CountCommitsPerAuthorPlugin est caractérisé par les
     * informations suivantes :
     * 
     * Une configuration attribuée défnitivement.
     * 
     * Un "résultat" suceptible d'être changé.
     * 
     * @author Asma
     * @version : 1.0
   */

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    /**
     * L'attribut configuration de type Configuration. Cet attribut est
     * inchangeable.
     */
    private final Configuration configuration;
    private final PluginConfig  pluginConfig;
    
    /**
     * L'attribut result de type Resultat.
     * 
     * @see CountCommitsPerAuthorPlugin#getResult()
     */
    private Result result;

    /**
     * Constructeur CountCommitPerAuthorPlugin.
     * 
     * A la construction d'un objet CountCommitPerAuthorPlugin.
     * </p>
     * 
     * @param configuration La configuration unique d'un CountCommitPerAuthorPlugin.
     * @see CountCommitPerAuthorPlugin#configuration.
     */

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration, PluginConfig pluginConfig) {
        this.configuration = generalConfiguration;
        this.pluginConfig  = pluginConfig;
    }

    /**
     * Caclule le nombre de commits que chaque utilisateur a écrit.
     * 
     * @param gitLog ( Liste des commits ).
     * @return result
     *
     */

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        
        // TODO: match emails exactly
        Pattern pattern = Pattern.compile(".*<(.*)>");

        for (var commit : gitLog) {
            String author = commit.author;
            Matcher matcher = pattern.matcher(author);

            String mail;
            if (!matcher.matches()) {
                mail = author;
            } else {
                mail = matcher.group(1);
            }
            
            var nb = result.commitsPerAuthor.getOrDefault(mail, 0);
            result.commitsPerAuthor.put(mail, nb + 1);
        }
        return result;
    }

    /**
     * Change le résultat en lui donnant comme nouvelle valeur l'exécution de la
     * méthode processLog.
     */

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    /**
     * Retourne le résultat Exécute le run() si l'analyses n'a pas été déja fait.
     * 
     * @return result.
     */

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    /**
     * Classe Result qui implémente l'interface AnalyzerPlugin.Result.
     * 
     * Un objet de type Result est caractérisé par les informations suivantes :
     * 
     * Une map attribuée défnitivement.
     * 
     */
    static class Result implements AnalyzerPlugin.Result {

    /**
     * L'attribut commitsPerAuthor de type Map. Cet attribut est inchangeable.
     */
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();

     /**
      * Retourne commitsPerAuthor .
      * 
      * @return commitsPerAuthor.
      *
     */
        Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

     /**
      * Retourne le commitsPerAuthor sous forme d'une chaine de charactères. .
      * 
      * @return commitsPerAuthor.
      *
      */
        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

      /**
       * Retourne une chaine de charactère .
       * 
       * Cette chaine contient des balises html qui forment une liste d'éléments.
       * 
       * Elle associe alors à chaque nombre de commits effectués leurs auteur.
       * 
       * @return html.
       *
      */

        @Override
        public String getResultAsHtmlDiv() {
            Chart graph = new Chart("countCommits", "Count Commits Per Author");
            graph.columnChartString(commitsPerAuthor);
            return graph.toHTML();
        }
    }
}
