import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by Andreas on 2015-10-28.
 * MaxFlow lets the user create a graph with connections between "U" and "V", and let
 * the Ford Fulkerson algorithm calculate the maximum flow. Returns/Prints Maximum Flow, used bows and
 * input Graph.
 */
public class MaxFlow {

        private int[] parent;
        private LinkedList<Integer> queue;
        private int size,nodesLeft,nodesRight;
        private boolean[] visited;
        private int[][] graph;

    /**
     * Constructor for MaxFlow. Intialize/Create Graph, Make connections between vertexes, and return
     * Maxflow.
     */
        public MaxFlow() {


            //Number of left and right nodes.
            nodesLeft = Integer.parseInt(JOptionPane.showInputDialog("Ange antal vänster noder."));
            nodesRight = Integer.parseInt(JOptionPane.showInputDialog("Ange antal höger noder."));

            size=nodesLeft + nodesRight + 2;
            //Create Graph and arrays.
            graph = new int[size][size];
            this.queue = new LinkedList<Integer>();
            parent = new int[size ];
            visited = new boolean[size];

            //Intialize Graph
            for (int i = 1; i <= nodesLeft; i++) {
                graph[0][i] = 1;
            }

            //Intialize Graph
            for (int i = nodesLeft + 1; i < graph.length - 1; i++) {
                graph[i][graph.length - 1] = 1;
            }

            boolean input = true;

            //Create connections between left and right nodes/Vertexes.
            while (input) {
                String left = (JOptionPane.showInputDialog("Skapa en connection från en av vänster noderna.\n"
                        + "En siffra från 1-" + nodesLeft + "\nTryck endast OK för att avsluta."));

                String right = (JOptionPane.showInputDialog("Skapa en connection till en av höger noderna.\n"
                        + "En siffra från " + (nodesLeft + 1) + "-" + (nodesLeft + nodesRight) + "\nTryck endast OK för att avsluta."));

                if (left.equals("") || right.equals("")) {
                    input = false;
                } else {
                    int nodeLeft = Integer.parseInt(left);
                    int nodeRight = Integer.parseInt(right);
                    System.out.println("Gör connection från " + nodeLeft + " till " + nodeRight);

                    graph[nodeLeft][nodeRight] = 1;
                }
            }
            System.out.println("InputGraf:");
            //Print input-Graph.
            printGraph(graph);
            //Find out Maxflow!
            fordFulkerson(graph,0,graph.length-1);
        }

    /**
     * This Method tries to find a path from Source to Sink vertex.
     * Recieves a Graph, Source vertex and Sink vertex. Returns Boolean
     * whether there is a path or not.
     * Traverse graph.
     * @param source -source/Start Vertex.
     * @param sink  -Sink/End Vertex.
     * @param graph -Graph to find path in.
     * @return boolean
     */
        public boolean bfs(int source, int sink, int graph[][]) {
            boolean path = false;
            int destination, element;

            //Initialize Parent[] and Visited[]
            for(int i = 0; i < size; i++) {
                parent[i] = -1; //index outside the graph.
                visited[i] = false; // the nodes arent visited yet.
            }

            queue.add(source);  //Add sourcenode/startnode to queue.
            parent[source] = -1;    //index outside the graph.
            visited[source] = true; //Set the source node to visited since we start there.

            //While there is a Queue
            while (!queue.isEmpty()) {
                element = queue.remove();   //Let "element" be the next "row-position to use in the graph. and remove from queue
                destination = 1;    //Set destination to 1 since source is already visited.

                while (destination < size) {    //While there still is nodes in the graph
                    if (graph[element][destination] > 0 &&  !visited[destination]){ //If the node isnt visited and there is a bow.
                        parent[destination] = element;  // set row position to column where there is a bow/path.
                        queue.add(destination); // Add to queue.
                        visited[destination] = true; //Set node to visited.
                    }
                    destination++;  //Update for next position.
                }
            }
            if(visited[sink]){  //If we reach the sink node- There is a path!
                path = true;
            }

            return path;    //Return if there was a path or not.True/False
        }

    /**
     * Ford Fulkersons Algorithm. This method uses a Graph(int [][]), a source vertex and
     * a sink vertex. The algorithm finds matchings between left and right nodes/vertexes.
     * Finds Max Flow.
     * Returns number of matchings.
     * @param graph -graph
     * @param source -source/start vertex
     * @param sink -Sink/End vertex
     * @return int
     */
        public int fordFulkerson(int graph[][], int source, int sink) {

            int u, v;
            int maxFlow = 0;
            int pathFlow;

            //Copy Graph to ResidualGraph.
            int[][] residualGraph = new int[size ][size ];
            for (int i = 0; i < size; i++){
                for (int k = 0; k < size; k++) {
                    residualGraph[i][k] = graph[i][k];
                }
            }
            //While there is a path bfs()-returns boolean
            while (bfs(source ,sink, residualGraph)) {
                pathFlow = 2;   //A value greater than 1 to compare with the bows/path in the graph.


                for (v = sink; v != source; v = parent[v]){     //"walk the found path". (on iterate, v=u from last iteration.

                    u = parent[v];  //Get the node on the other side of the bow. (U-bow-V)(Cordinates for the graph).


                    //Set pathflow to the lowest value.0=no path,1=path (Should be 1 if everything is right).
                    pathFlow = Math.min(pathFlow, residualGraph[u][v]);

                }

                for (v = sink; v != source; v = parent[v]){ //Update Residualgraphs bows/paths to mark used bows. Same loop as the one above.

                    u = parent[v];  //Get the node on the other side of the bow. (U-bow-V)(Cordinates for the graph).
                    residualGraph[u][v] -= pathFlow;    //Reset connection between nodes to 0( Mark path as used).
                    residualGraph[v][u] += pathFlow;    //Show the new connection between the nodes by 1.Show Match/Bow.

                }

                maxFlow += pathFlow;    //Add pathflow's value to maxFlow (+1 more match).

            }
            System.out.println("\nNya grafen med använda bågar:");
            printGraph(residualGraph);  //Print the "new" graph with matches.
            System.out.println();
            printBows(residualGraph);   //tell the user which bows are used, print.
            System.out.println("\nMaxflow är: "+maxFlow);   //Print maxflow
            return maxFlow; //Return Maxflow
        }

    /**
     * Prints the used bows between the nodes.
     * @param graph -graph to find bows in.
     */
        public void printBows(int[][] graph){
            //Loops through the area in the array where the used bows are. If it finds one, print.
            for(int i=nodesLeft+1; i<graph.length-1;i++){
                for(int j=1; j<nodesLeft+1; j++){
                    if(graph[i][j]==1){
                        System.out.println("Båge använd mellan: "+j+"-"+i);
                    }
                }
            }
        }

    /**
     * Prints the Graph and takes a graph int [][] as input.
     * @param graph - graph to be printed.
     */
        public void printGraph(int [][] graph) {
            //Loops through the graph and prints.
            for (int i = 0; i < graph.length; i++) {
                for (int k = 0; k < graph[i].length; k++) {
                    System.out.print(graph[i][k] + " ");
                }
                System.out.println();
            }
        }

    /**
     * Main method.
     * @param args
     */
        public static void main(String...args) {
            new MaxFlow();
        }
    }


