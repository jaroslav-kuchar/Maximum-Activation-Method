package cz.cvut.fit.pw.fordfulkerson;

import org.gephi.graph.api.Edge;

/*************************************************************************
 *  Compilation:  javac FlowEdge.java
 *  Execution:    java FlowEdge
 *
 *  Capacitated edge with a flow in a flow network.
 *
 *************************************************************************/

/**
 *  The <tt>FlowEdge</tt> class represents a capacitated edge with a flow
 *  in a digraph.
 *  <p>
 *  For additional documentation, see <a href="/algs4/74or">Section 7.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 */
public class FlowEdge {
    
    private Edge e = null;
    private int capacity = 1;
    private int flow = 0;
    private int v;
    private int w;    
    
    public FlowEdge(int from, int to, Edge e) {
        this.v = from;
        this.w = to;
        this.e = e;
    }
    public FlowEdge(int from, int to, Edge e, int capacity) {
        this(from, to, e);
        this.capacity = capacity;
    }

    public int from() {
        return v;
    }

    public int to() {
        return w;
    }

    public double flow() {
        return flow;
    }

    public double capacity() {
        return capacity;
    }

    public Edge getE() {
        return e;
    }
    
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new RuntimeException("Illegal endpoint");
    }
    
    public double residualCapacityTo(int vertex) {
        if      (vertex == v) return flow;
        else if (vertex == w) return capacity - flow;
        else throw new RuntimeException("Illegal endpoint");
    }

    public void addResidualFlowTo(int vertex, double delta) {
        if      (vertex == v) flow -= delta;
        else if (vertex == w) flow += delta;
        else throw new RuntimeException("Illegal endpoint");
    }


    public String toString() {
        return v + "->" + w + " " + flow + "/" + capacity;
    }   

}
