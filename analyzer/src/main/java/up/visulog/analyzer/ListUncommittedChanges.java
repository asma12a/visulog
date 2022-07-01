package up.visulog.analyzer;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff.StageState;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

public class ListUncommittedChanges implements AnalyzerPlugin {
    private final Configuration configuration;
    private final PluginConfig  pluginConfig;

    private Result result;

    public ListUncommittedChanges(Configuration generalConfiguration, PluginConfig pluginConfig) {
        this.configuration = generalConfiguration;
        this.pluginConfig  = pluginConfig;
    }

    /**
     * A method listing all the uncommitted changes from git repository
     * @gitPath represents the address of the git repository
     */
    static Result listUncommittedChanges(Path gitPath) {
        try {
            File file = new File(
                Paths.get(gitPath.toString(), ".git/").toString()
            );
            Repository repository = new FileRepositoryBuilder().setGitDir(file).build();
            
            Git git = new Git(repository);
            Status status = git.status().call();
            
            Result result = new Result();
            result.categories.put("Conflicting",        status.getConflicting());
            result.categories.put("Added",              status.getAdded());
            result.categories.put("Changed",            status.getChanged());
            result.categories.put("Missing",            status.getMissing());
            result.categories.put("Modified",           status.getModified());
            result.categories.put("Removed",            status.getRemoved());
            result.categories.put("Uncommitted",        status.getUncommittedChanges());
            result.categories.put("Untracked",          status.getUntracked());
            result.categories.put("Untracked folders",  status.getUntrackedFolders());

            result.conflictingStageState = status.getConflictingStageState();
            return result;
        } catch (IOException e) {
      	    e.printStackTrace();
        } catch (GitAPIException h) {
            h.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        result = listUncommittedChanges(
            configuration.getGitPath()
        );
    }

    @Override
    public Result getResult() {
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        // TODO: use better permissions here
        public Map<String, Set<String>> categories;
        public Map<String, StageState> conflictingStageState;

        public Result() {
            categories = new HashMap<String, Set<String>>();
            conflictingStageState = new HashMap<String, StageState>();
        }

        private String getSetAsString(String name, Set<String> s) {
            String repr = name + ":\n";
            for (String entry : s) {
                repr = repr + "\t-" + entry + "\n"; 
            }
            return repr;
        }

        private String getSetAsHtml(String name, Set<String> s) {
            String repr = "<h2>" + name + "</h2> <ul>";
            for (String entry : s) {
                repr = repr + "<li>" + entry + "</li>";
            }
            repr = repr + "</ul>";
            return repr;
        }

        @Override 
        public String getResultAsString() {
            String res = "";
            for (Map.Entry<String, Set<String>> entry : categories.entrySet()) {
                res = res + getSetAsString(entry.getKey(), entry.getValue());
            }
            res = res + "Conflicting States";
            for (Map.Entry<String, StageState> entry : conflictingStageState.entrySet()) {
                res = res + "\t-" + entry + "\n";
            }
            return res;
        }

        @Override
        public String getResultAsHtmlDiv() {
            // TODO: Maybe make a graph ?
            /*
            StringBuilder html = new StringBuilder();
            html.append("\n<div id='listUncommittedChanges'>\n");
            for (Map.Entry<String, Set<String>> entry : categories.entrySet()) {
                html.append("\t"+getSetAsHtml(entry.getKey(), entry.getValue())+"\n");
            }
            html.append("\t<h2> Conflicting States </h2> <ul>");
            for (Map.Entry<String, StageState> entry : conflictingStageState.entrySet()) {
                html.append("<li>");
                html.append(entry);
                html.append("</li>\n");
            }
            html.append("</ul>\n");
            html.append("</div>\n");
            return html.toString();
            */
            
            Chart graph = new Chart("unCommitted", "List Of Uncommited Changes");
            graph.columnChartStringWithSet(categories);
            return graph.toHTML();

        }
    }
}
