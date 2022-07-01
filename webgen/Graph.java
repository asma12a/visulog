
    package up.visulog.webgen;
    import java.util.Map;
    import java.util.HashMap;
    import htmlflow.HtmlView;
    import htmlflow.StaticHtml;
    import java.io.*;


    public class Graph{
        // une fonction qui crée un graphe à partir d'une etiquette(exemple "CommitsPerauthor") ,un type de graphe(String) 
        //et une liste de données data (Map<String,Integer>)
        public static String  createGraph(String etiquette,String type,Map<String,Integer>data){ 
          String label=" var ctx = document.getElementById('canvas').getContext('2d');\n"+
          "var data ={labels: [";
    
          for (var item: data.entrySet()){
        
            label += "\'" + item.getKey() + "\',";
          }
          label=label.substring(0,label.length()-1);
          label +="],";
        
          String Data="datasets: ["+
        
          "{label: [\'"+ etiquette +"\'],"
            
          +"data: [";
            for(var item : data.entrySet()) {
                Data+=item.getValue()+",";
            }
                Data=Data.substring(0,Data.length()-1);
                Data+="]}]};";
                Data+= "var options= { scales: { yAxes:[{ticks:{beginAtZero: true}}]}};\n"
                + "var config={type:\'"+type+"\',data: data, options:options};\n"
                + "var graph1 = new Chart(ctx, config);\n";
                String resultat=label+Data;
               return resultat;
        }
        //HtmlFlow pour generer du code html de la page du graphe
        public static String generateHtml(String etiquette,String type,Map<String,Integer>data){
            String Codegraph=createGraph(etiquette, type, data);
            HtmlView view = StaticHtml.view(v -> v
            .html()
            .head()
                .title().text(" Graphe").__()
                .script().attrSrc("https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js").__()
            .__() //head
            .body()
                .div().attrClass("container")
                .h1().text("graphe").__()
                .p().text("les données du graphe").__()
                .canvas().attrId("canvas").__()
                .script().text(Codegraph)
                .__() //script
                .__() //div
            .__() //body
            .__()); //html et fin view
            String res=view.render();
            return res;
        }

            public static void createHtmlFile(String res) throws IOException {
                File fic = new File("graph.html");
                BufferedWriter bfwriter=new BufferedWriter(new FileWriter(fic));
                bfwriter.write(res);
                bfwriter.close();
            }
        
        
            

        
            }
