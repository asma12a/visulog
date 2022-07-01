package up.visulog.analyzer;

import java.util.List;

public class AnalyzerResult {
    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    private final List<AnalyzerPlugin.Result> subResults;

    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
        this.subResults = subResults;
    }

    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    public String toHTML() {
        return "<!DOCTYPE html>" + 
            "<html>" + 
                "<head>" +
                    "<script src='https://canvasjs.com/assets/script/canvasjs.min.js'></script>" +
                    "<style>" +
                        "div.chart { width: 100%; height: 400px; }" +
                    "</style>" + 
                "</head>" + 
                "<body>" +
		subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + 
                "</body>" +
            "</html>";

    }
}
