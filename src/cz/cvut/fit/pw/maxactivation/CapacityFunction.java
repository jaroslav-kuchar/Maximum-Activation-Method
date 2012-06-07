package cz.cvut.fit.pw.maxactivation;

import org.gephi.graph.api.Graph;

/**
 *
 * @author Jaroslav Kuchar
 */
public interface CapacityFunction {

    final String capacityAttribute = "Capacity";

    public void computeCapacity(Graph graph);
}
