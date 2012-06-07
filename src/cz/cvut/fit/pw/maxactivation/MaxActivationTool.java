package cz.cvut.fit.pw.maxactivation;

import cz.cvut.fit.pw.fordfulkerson.FlowEdge;
import cz.cvut.fit.pw.fordfulkerson.FlowNetwork;
import cz.cvut.fit.pw.fordfulkerson.FordFulkerson;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.tools.spi.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jaroslav Kuchar
 */
@ServiceProvider(service = Tool.class)
public class MaxActivationTool implements Tool {

    private boolean sourceNode = false;
    private boolean targetNode = false;
    private Node sNode = null;
    private Node tNode = null;
    private final MaxActivationToolUI ui = new MaxActivationToolUI();

    @Override
    public void select() {
    }

    @Override
    public void unselect() {
    }

    @Override
    public ToolEventListener[] getListeners() {
        return new ToolEventListener[]{
                    new NodeClickEventListener() {

                        @Override
                        public void clickNodes(Node[] nodes) {
                            // select source or target node
                            if (sourceNode) {
                                // first selected node
                                sNode = nodes[0];
                                // node color
                                sNode.getNodeData().setColor(1, 0, 0);
                                // source node selected
                                sourceNode = false;
                            } else if (targetNode) {
                                // first selected node
                                tNode = nodes[0];
                                // node color
                                tNode.getNodeData().setColor(1, 0.6f, 0);
                                // target node selected
                                targetNode = false;
                            }

                        }
                    }};
    }

    @Override
    public ToolUI getUI() {
        return ui;
    }

    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION;
    }

    private class MaxActivationToolUI implements ToolUI {

        @Override
        public JPanel getPropertiesBar(Tool tool) {
            JPanel panel = new JPanel();

            JButton capacity = new JButton("Capacity");
            capacity.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    //Get current graph
                    GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                    Graph graph = gc.getModel().getGraph();

                    // load preferences
                    Properties preferences = new Properties();
                    FileInputStream in;
                    try {
                        in = new FileInputStream("preferences.properties");
                        preferences.load(in);
                        in.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // compute capacity
                    CapacityFunction cf = new PreferencesCapacityFunction(preferences);
                    cf.computeCapacity(graph);
                }
            });

            JButton source = new JButton("Source");
            source.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    // select source
                    sourceNode = true;
                }
            });

            JButton target = new JButton("Target");
            target.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    // select target
                    targetNode = true;
                }
            });

            JButton run = new JButton("Run");

            run.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    //Get current graph
                    GraphController gc = Lookup.getDefault().lookup(GraphController.class);
                    Graph graph = gc.getModel().getGraph();

                    //List columns
                    AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
                    AttributeModel model = ac.getModel();

                    AttributeColumn flow = model.getEdgeTable().getColumn("Flow");
                    AttributeColumn cut = model.getEdgeTable().getColumn("Cut");

                    // if not exists
                    if (flow == null) {
                        flow = model.getEdgeTable().addColumn("Flow", org.gephi.data.attributes.api.AttributeType.INT);
                    }
                    // if not exists
                    if (cut == null) {
                        cut = model.getEdgeTable().addColumn("Cut", org.gephi.data.attributes.api.AttributeType.INT);
                    }

                    // initialize flow network
                    FlowNetwork G = new FlowNetwork(graph);
                    Node[] nodes = graph.getNodes().toArray();
                    int s = 0;
                    int t = nodes.length - 1;

                    // find source and target in Flow network representation (index in array)
                    for (int i = 0; i < nodes.length; i++) {
                        if (nodes[i].getId() == sNode.getId()) {
                            s = i;
                        }
                        if (nodes[i].getId() == tNode.getId()) {
                            t = i;
                        }

                    }

                    StringBuilder sb = new StringBuilder();

                    // compute maximum flow and minimum cut
                    FordFulkerson maxflow = new FordFulkerson(G, s, t);
                    
                    // output
                    System.out.println("Max flow from " + s + " to " + t);
                    sb.append("Max flow from " + s + " to " + t + "\n");
                    for (int v = 0; v < G.V(); v++) {
                        for (FlowEdge e : G.adj(v)) {
                            e.getE().setWeight(2);
                            graph.getEdge(graph.getNode(nodes[e.from()].getId()), graph.getNode(nodes[e.to()].getId())).getEdgeData().getAttributes().setValue("Flow", e.flow());
                            if (e.flow() > 0) {
                                e.getE().setWeight(4);
                            }
                        }
                    }

                    // min cut
                    for (Edge e : graph.getEdges()) {
                        e.getEdgeData().getAttributes().setValue("Cut", 0);
                        e.getEdgeData().setColor(1, 0.8f, 0.8f);
                    }

                    for (int v = 0; v < G.V(); v++) {
                        for (FlowEdge e : G.adj(v)) {
                            Edge edge = e.getE();
                            // if is in mincut ?
                            if ((v == e.from()) && maxflow.inCut(e.from()) && !maxflow.inCut(e.to())) {
                                edge.getEdgeData().getAttributes().setValue("Cut", 1);
                                edge.getEdgeData().setColor(1, 0, 0);
                            }
                        }
                    }


                    /*
                     * // print min-cut System.out.print("Min cut: ");
                     * sb.append("Min cut: "+"\n"); for (int v = 0; v < G.V();
                     * v++) { if (maxflow.inCut(v)) {
                     * //nodes[v].getNodeData().setColor(1, 0, 0);
                     * System.out.print(v + " "); sb.append(v + " "+"\n"); } }
                     * System.out.println(); sb.append("\n");
                     *
                     *
                     * for (int v = 0; v < G.V(); v++) { for (FlowEdge e :
                     * G.adj(v)) { if ((v == e.from()) &&
                     * maxflow.inCut(e.from()) && !maxflow.inCut(e.to())) {
                     * System.out.println(e); sb.append(e+"\n"); } } }
                     *
                     * System.out.println(); sb.append("\n");
                     *
                     */

                    System.out.println("Max flow value = " + maxflow.value());
                    sb.append("Max flow value = " + maxflow.value());
                    JOptionPane.showMessageDialog(null, sb.toString(), "Results", JOptionPane.PLAIN_MESSAGE);

                }
            });


            panel.add(capacity);
            panel.add(source);
            panel.add(target);
            panel.add(run);
            return panel;
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public String getName() {
            return "Max Activation Method";
        }

        @Override
        public String getDescription() {
            return "Max Activation Method";
        }

        @Override
        public int getPosition() {
            return 1000;
        }
    }
}
