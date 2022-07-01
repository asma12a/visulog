package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.config.PluginConfig;

import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Importation de la classe Date : 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CountCommitsPerDate implements AnalyzerPlugin {
  private static final DateTimeFormatter fmt = new DateTimeFormatterBuilder().appendPattern("E MMM d HH:mm:ss uuuu Z")
      .toFormatter(Locale.ENGLISH);

  private final Configuration configuration;
  private final PluginConfig pluginConfig;

  private Result result;

  public CountCommitsPerDate(Configuration generalConfiguration, PluginConfig pluginConfig) {
    this.configuration = generalConfiguration;
    this.pluginConfig = pluginConfig;
  }

  // @author : Asma
  // Un méthode qui transforme une date en une chaine de char :

  public static String DateToChar(Date date) {

    String pattern = "E MMM d HH:mm:ss yyyy Z";

    // Create an instance of SimpleDateFormat used for formatting
    // the string representation of date according to the chosen pattern
    DateFormat df = new SimpleDateFormat(pattern);
    // Using DateFormat format method we can create a string
    // representation of a date with the defined format.
    String dateAsString = df.format(date);

    // The result!
    return (dateAsString);
  }

  static Result processLog(List<Commit> gitLog) {
    var result = new Result();
    for (var commit : gitLog) {
      String chaine = DateToChar(commit.date);
      LocalDate date = LocalDate.parse(chaine, fmt);
      var nb = result.commitsPerDate.getOrDefault(date, 0);
      result.commitsPerDate.put(date, nb + 1);
    }
    return result;
  }

  @Override
  public void run() {
    result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
  }

  @Override
  public Result getResult() {
    if (result == null)
      run();
    return result;
  }

  static class Result implements AnalyzerPlugin.Result {
    // On crée une HashMap commitsPerDate qui associe à chaque date
    // le nombre de commits qui y ont été faits
    private final Map<LocalDate, Integer> commitsPerDate = new HashMap<>();

    Map<LocalDate, Integer> commitsPerDate() {
      return commitsPerDate;
    }

    @Override
    // Elle affiche commitsPerDate sous forme de chine de caracteres
    public String getResultAsString() {
      return commitsPerDate.toString();
    }

    @Override
    public String getResultAsHtmlDiv() {
      Chart graph = new Chart("countCommitsPerDate", "Count Commits Per Date");
      graph.columnChartDate(new TreeMap(commitsPerDate));
      return graph.toHTML();
    }
  }
}
