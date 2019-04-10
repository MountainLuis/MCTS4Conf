package mas.code.core.misc;

import mas.code.core.airspace.Waypoint;
import mas.code.core.geom.Point2D;

import java.util.List;

public interface BaseRouting {

    List<? extends Point2D> getPoints();
    List<LogicSegment> getSegments();
    double getDistance();
}
