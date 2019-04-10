package mas.code.core.airspace;

import mas.code.core.misc.BaseSegment;

import java.util.function.Function;

public class Segment extends BaseSegment {
    public String id;
//    public Waypoint start;
//    public Waypoint end;

    public Segment(String id, Waypoint start, Waypoint end) {
        super(start,end);
        this.id = id;

    }
    public String toString(){
        return id + "::" + super.toString();
    }
}
