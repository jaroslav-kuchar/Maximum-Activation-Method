package cz.cvut.fit.pw.maxactivation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Jaroslav Kuchar
 */
public class PreferencesCapacityFunction implements CapacityFunction {

    private Properties preferences = null;

    public PreferencesCapacityFunction(Properties preferences) {
        this.preferences = preferences;
    }

    @Override
    public void computeCapacity(Graph graph) {
        // if attribute does not exist - create 
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel model = ac.getModel();

        // capacity attribute
        AttributeColumn cap = model.getEdgeTable().getColumn(capacityAttribute);

        if (cap == null) {
            cap = model.getEdgeTable().addColumn(capacityAttribute, org.gephi.data.attributes.api.AttributeType.INT);
        }

        // current time        
        long curTime;
        Date present = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            present = df.parse(preferences.getProperty("AGEING-PRESENT"));
        } catch (ParseException ex) {
            System.out.println(ex);
            present = new Date();
        }
        curTime = present.getTime();

        // compute capacity
        for (Edge e : graph.getEdges()) {

            Node source = e.getSource();
            Node target = e.getTarget();

            NodeType sourceType;
            NodeType targetType;

            // node type by namespace or by type attribute
            if (model.getNodeTable().getColumn("namespace") != null) {
                sourceType = NodeType.getTypeByNS((String) source.getNodeData().getAttributes().getValue("namespace"));
                targetType = NodeType.getTypeByNS((String) target.getNodeData().getAttributes().getValue("namespace"));
            } else {
                sourceType = NodeType.getTypeByType((String) source.getNodeData().getAttributes().getValue("type"));
                targetType = NodeType.getTypeByType((String) target.getNodeData().getAttributes().getValue("type"));
            }

            // set colors
            source.getNodeData().setColor(sourceType.getR(), sourceType.getG(), sourceType.getB());
            target.getNodeData().setColor(targetType.getR(), targetType.getG(), targetType.getB());

            // get preference from properties
            int capacity = Integer.valueOf(preferences.getProperty(sourceType + "-" + targetType, "0"));

            // if time attribute is available and ageing
            if (model.getEdgeTable().getColumn("time_interval") != null && preferences.getProperty("AGEING").equals("1")) {
                TimeInterval ti = (TimeInterval) e.getEdgeData().getAttributes().getValue("time_interval");
                if (ti != null) {
                    double age = curTime - ti.getLow();                    
                    // if adge is in past
                    if (age >= 0) {
                        // convert to days                    
                        age = ((((age / 1000) / 60) / 60) / 24) / Integer.valueOf(preferences.getProperty("AGEING-DAYS"));
                        // ageing function
                        capacity = (int) (capacity * Math.exp(Double.valueOf(preferences.getProperty("AGEING-CONSTANT")) * age));
                    } else {
                        // edge in future
                        capacity = 0;
                    }
                }
            }


            e.getEdgeData().getAttributes().setValue(capacityAttribute, capacity);


        }
    }
}
