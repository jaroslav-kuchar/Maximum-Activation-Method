package cz.cvut.fit.pw.fordfulkerson;

import java.util.HashMap;
import java.util.Map;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author Jaroslav Kuchar
 */

/*************************************************************************
 *  Compilation:  javac FlowNetwork.java
 *  Execution:    java FlowNetwork V E
 *  Dependencies: Bag.java FlowEdge.java
 *
 *  A capacitated flow network, implemented using adjacency lists.
 *
 *************************************************************************/
public class FlowNetwork {

    private final int V;
    private int E;
    private Graph dg;
    private Map<Integer, Integer> n = new HashMap<Integer, Integer>();
    private Node[] nodes;
    private Bag<FlowEdge>[] adj;

    public FlowNetwork(Graph dg) {
        this.V = dg.getNodeCount();
        this.E = 0;
        adj = (Bag<FlowEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<FlowEdge>();
        }

        // gephi
        this.dg = dg;
        nodes = dg.getNodes().toArray();
        for (int i = 0; i < nodes.length; i++) {
            n.put(nodes[i].getId(), i);
        }
        // adj
        for (Edge e : dg.getEdges()) {
            int v = n.get(e.getSource().getId());
            int w = n.get(e.getTarget().getId());
            int capacity = 1;
            if(e.getEdgeData().getAttributes().getValue("Capacity")!=null){
                capacity = (Integer)e.getEdgeData().getAttributes().getValue("Capacity");
            } 
            addEdge(new FlowEdge(v, w, e, capacity));

        }
    }

    public int V() {
        return dg.getNodeCount();
    }
    
    // add edge e in both v's and w's adjacency lists
    public void addEdge(FlowEdge e) {
        E++;
        int v = e.from();
        int w = e.to();
        adj[v].add(e);
        adj[w].add(e);
    }

    // return list of edges incident to  v
    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }
}
