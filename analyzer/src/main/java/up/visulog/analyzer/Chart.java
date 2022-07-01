package up.visulog.analyzer;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

import java.util.Set;

public class Chart {
    private String name;
    private String title;
    private String type;
    private String dataPoints;
    private int chartId;

    private static int globalId;

    public Chart(String n, String t) {
        name = n;
	    title = t;
        chartId = Chart.globalId++;
    }

    public void columnChartDate(Map<LocalDate, Integer> data) {
        type = "column";
        dataPoints = "";

        for (var item: data.entrySet()) {
            LocalDate date = item.getKey();
            int n = item.getValue();

            dataPoints = dataPoints + "{x: new Date(" +
                date.getYear() + "," + 
		(date.getMonthValue() - 1) + "," + date.getDayOfMonth() + 
                "), y: " + Integer.toString(n) + "},";
        }
    }

    public void columnChartString(Map<String, Integer> data) {
        type = "column";
        dataPoints = "";

        for (var item: data.entrySet()) {
            dataPoints = dataPoints + "{label: \"" + item.getKey() + "\","
                + "y: " + item.getValue() + "},"; 
        }
    }
    
    private String getSetAsString(String name, Set<String> s) {
        String repr = name + ":\n";
        for (String entry : s) {
            repr = repr + "\t-" + entry + "\n"; 
        }
        return repr;
    }
        
    public void columnChartStringWithSet(Map<String, Set<String>> data) {
    	type = "column";
    	dataPoints = "";
    	
    	for (var item : data.entrySet()) {
    	    dataPoints = dataPoints + "{label: \"" + item.getKey() + "\","
    	        //+ "y: " + getSetAsString(item.getKey(), item.getValue()) + "}";
    	        + "y: " + Integer.toString(item.getValue().size()) + "},";
    	}
    }

    public String toHTML() {
        String jsCode = "let chart_" + chartId + " = new CanvasJS.Chart(\"" + name + "\", {"
            + "title: { text: \"" + title + "\" },"
            + "data: [{"
                + "type: \"" + type + "\","
                + "dataPoints: [" + dataPoints + "]"
            + "}]"
            + "}); chart_" + chartId + ".render();";
	String html = "<div class=\"chart\" id=\"" + name + "\"></div>"
            + "<br/>"
            + "<script type=\"text/javascript\">" + jsCode + "</script>"; 
        return html;
    }

   /*blic static String codeDeGraphe(Map<String,Integer> commitsPerAuthor){
    	String code = "var chart = new CanvasJS.Chart(\"countCommits\", {\n\n\n"
    			+ "title:{\n"
    			+ "\ttext: \"Commits Per Author\"\n"
    			"},\n"
    			+ "data: [\n"
    			+ "  {\n"
    			+ "\ttype: \"column\",\n"
    			+ "\tdataPoints: [\n";
    	for (var item: commitsPerAuthor.entrySet()){
    	     code += "\t\t{ label: \"" + item.getKey() + "\", y: " 
	item.getValue() + "},\n";
    	}
    	code += "\t]\n" + "  }\n" + "]\n" + "})\n";
        code += "chart.render();";
    	return code;
    }*/
    
}


