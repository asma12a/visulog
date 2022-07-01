package up.visulog.analyzer;
import up.visulog.config.PluginConfig;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ShowStatsCommit implements AnalyzerPlugin {
  private final PluginConfig  pluginConfig;
  private final Configuration configuration;
  private static Result result;

  public ShowStatsCommit(Configuration generalConfiguration,PluginConfig pluginConfig) {
      this.configuration = generalConfiguration;
      this.pluginConfig  = pluginConfig;
  }
  static Result stats(Path chemin){

    var Resultat = new Result();
    ArrayList<HashMap<String, Integer>> commetuveux = Commit.pourcentageCommentaires(chemin);
    Resultat.statistiques = commetuveux;
    return Resultat;
  }
  public void run(){
    Path path = configuration.getGitPath();
    String tmp = configuration.getGitPath().toString();
    Path gitpath = Paths.get(tmp);
    this.result = stats(gitpath);
  }
  public Result getResult(){
    if ( this.result == null ) this.run();
    return this.result;
  }
  static class Result implements AnalyzerPlugin.Result {
    private  ArrayList<HashMap<String, Integer>> statistiques = new ArrayList<HashMap<String,Integer>>();
    public ArrayList<HashMap<String,Integer>> getStatistiques(){
      return statistiques;
    }
    public String getResultAsString() {
        return statistiques.toString();
    }
    public String getResultAsHtmlDiv2(HashMap<String, Integer> var,String arg){
      Chart graph = new Chart( arg, arg );
      graph.columnChartString( var );
      return graph.toHTML();
    }

    public String getResultAsHtmlDiv(){
      String str = "";
        str = str +getResultAsHtmlDiv2(statistiques.get(0),"Nombre de lignes ajoutées en fonction des personnes sur tous les commits") + "\n\n";
        str = str +getResultAsHtmlDiv2(statistiques.get(1),"Nombre de lignes supprimées en fonction des personnes sur tous les commits") + "\n\n";
        str = str +getResultAsHtmlDiv2(statistiques.get(2),"Ajout - suppression") + "\n\n";
        str = str +getResultAsHtmlDiv2(statistiques.get(3),"Nombre de commentaires ajoutés en fonction des personnes sur tous les commits") + "\n\n";
        str = str +getResultAsHtmlDiv2(statistiques.get(4),"Nombre de lignes vides ajoutées ") + "\n\n";
        //str = str +getResultAsHtmlDiv2(statistiques.get(3),"Proportion commentaires/lignes  en fonction des personnes sur tous les commits") + "\n\n";
        return str;
    }
  }



}
