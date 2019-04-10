package mas.code.core.traffic.aircraft;

import demo.spatialindex.rtree.Rect;
import demo.spatialindex.rtree.SpatialData;
import kt.mas.core.traffic.aircraft.FlightPerformance;
import mas.code.core.geom.Point2D;
import mas.code.core.misc.LogicPosition;
import mas.code.core.traffic.ATC.AltCmd;
import mas.code.core.traffic.ATC.SpdCmd;
import mas.code.core.traffic.FlightPlan;
import org.jetbrains.annotations.NotNull;

public class AcftState implements SpatialData {
    public Aircraft aircraft;
    public AcftState(Aircraft aircraft){this.aircraft = aircraft;}

    public long time;
    public Point2D location;
    public FlightPlan fltPlan;
    public double heading;
    public double alt;
    public double vSpd;
    public double hSpdTAS;
    public double hSpdCAS;
    public double hSpdMach;
    public double gSpd;
    public double turnRate;

    public FlightPerformance performance;

    public AltCmd altCmd;
    public SpdCmd spdCmd;

    public VerticalState vState;
    public LogicPosition pos;
    public boolean finished = false;

    public String toString(){
        return aircraft.id + " " + time + " " + location + " " + alt + " spd:" + hSpdTAS;
    }


    @NotNull
    @Override
    public Rect getBounds() {
        return new Rect(location.lng, location.lng, location.lat, location.lat, alt, alt);
    }
}
