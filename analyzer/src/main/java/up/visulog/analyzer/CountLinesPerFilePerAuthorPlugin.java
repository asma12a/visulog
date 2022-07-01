package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import up.visulog.gitrawdata.Commit;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.nio.file.Path;

public class CountLinesPerFilePerAuthorPlugin implements AnalyzerPlugin {

  private final Configuration configuration;
  private final PluginConfig  pluginConfig;

  private Result result;

  public CountLinesPerFilePerAuthorPlugin(Configuration generalConfiguration, PluginConfig pluginConfig)
  {
    this.configuration = generalConfiguration;
    this.pluginConfig  = pluginConfig;
  }

  static Result processBlame(List<String> files , Path gitPath )
  {
    var result = new Result();
    for ( var file : files ){
      HashMap<String,Integer> tmp = Commit.parseBlameFromCommand(file , gitPath );
      result.printedLinesPerFilesPerAuthor.put(file,tmp);
    }
    return result;
  }

  @Override
  public void run()
  {
    String tmp = configuration.getGitPath().toString();
    Path gitpath = Paths.get(tmp);

    Map<String, String> options = this.pluginConfig.getPluginConfig();
    LinkedList<String> files;
    if (options.containsKey("file")) {
      String filename = options.get("file");
      files = new LinkedList<String>();
      files.add(filename);
    } else {
      files = Commit.parseLsFilesFromCommand(gitpath);
    }

    this.result = processBlame(files, gitpath);
  }

  @Override
  public Result getResult()
  {
    if ( this.result == null ) this.run();
    return this.result;
  }

  static class Result implements AnalyzerPlugin.Result
   {
    private HashMap< String , HashMap<String,Integer> > printedLinesPerFilesPerAuthor = new HashMap<>();

    HashMap< String , HashMap<String,Integer> > getPrintedLinesPerFilesPerAuthor()
    {
      return this.printedLinesPerFilesPerAuthor;
    }

    @Override
    public String getResultAsString()
    {
      return this.printedLinesPerFilesPerAuthor.toString();
    }

    public String getResultAsHtmlDivBis( HashMap<String,Integer> t , String arg )
    {
      Chart graph = new Chart( arg, arg);
      graph.columnChartString( t );
      return graph.toHTML();
    }

    @Override
    public String getResultAsHtmlDiv()
    {
      String str = "";
      for ( String string : printedLinesPerFilesPerAuthor.keySet() )
      {
        if ( !(  printedLinesPerFilesPerAuthor.get(string).size() == 1   &&   printedLinesPerFilesPerAuthor.get(string).containsKey("Aldric Degorre") ) ) {
          str = str + getResultAsHtmlDivBis( printedLinesPerFilesPerAuthor.get(string) , string);
          str = str + ("\n\n");
        }
      }
      return str;
    }
  }
}
