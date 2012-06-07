package cz.cvut.fit.pw.maxactivation;

/**
 * Node Type
 * @author Jaroslav Kuchar
 */
public enum NodeType {

    API(1, 1, 0), MASHUP(0.4f, 0, 0.4f), USER(0, 0.6f, 0), CATEGORY(0, 0, 1), PROFILE(1, 0, 0);
    private float r, g, b; // node color

    /**
     * Node Type 
     * @param r
     * @param g
     * @param b 
     */
    private NodeType(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Get node type by value of namespace attribute
     * @param type namespace
     * @return node type
     */
    public static NodeType getTypeByNS(String type) {
        if (type == null) {
            return NodeType.PROFILE;
        }
        if (type.equals("http://example.com/linked-apis/api")) {
            return NodeType.API;
        } else if (type.equals("http://example.com/linked-apis/mashup")) {
            return NodeType.MASHUP;
        } else if (type.equals("http://example.com/linked-apis/member")) {
            return NodeType.USER;
        } else if (type.equals("http://example.com/linked-apis")) {
            return NodeType.CATEGORY;
        } else {
            return NodeType.PROFILE;
        }
    }

    /**
     * Get node type by value of type attribute
     * @param type type name
     * @return node type
     */
    public static NodeType getTypeByType(String type) {
        if (type == null) {
            return NodeType.PROFILE;
        }
        if (type.equals("api")) {
            return NodeType.API;
        } else if (type.equals("mashup")) {
            return NodeType.MASHUP;
        } else if (type.equals("member")) {
            return NodeType.USER;
        } else if (type.equals("category")) {
            return NodeType.CATEGORY;
        } else {
            return NodeType.PROFILE;
        }
    }
    
    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }

    public float getR() {
        return r;
    }
}
