package test;

import kt.mas.core.traffic.aircraft.AircraftType;
import mas.code.core.framework.Scenario;
import mas.code.core.geom.Point2D;
import mas.code.core.misc.BaseSegment;
import mas.code.core.misc.Direction;
import mas.code.core.misc.LogicPosition;
import mas.code.core.misc.LogicSegment;
import mas.code.core.traffic.Routing;
import mas.code.core.traffic.aircraft.Aircraft;
import mas.code.util.DataAccessObject;
import mas.code.util.GeoUtil;
import sun.security.krb5.SCDynamicStoreConfig;

import java.util.LinkedList;
import java.util.List;

public class Test {
    DataAccessObject dao = new DataAccessObject();

    public static void main(String[]args){
        Demo1 demo1 = new Demo1();
//        demo1.loadWayPoints();
        String[] ss = null;
        demo1.main(ss);

        demo1.scenario = new Scenario();
//       Aircraft aircraft0 = new Aircraft("A0",AircraftType.Companion.getALL().get("A320"));
        String s = "P197-UDINO-SATRO-DPX";
        double lng1 = 117.54861111111111;
        double lat1 = 34.060833333333335;
        double lng2 = 114.64166666666667;
        double lat2 = 33.61666666666667;

//        System.out.println(GeoUtil.calDist(lng1,lat1,lng2,lat2));

        BaseSegment segment = new BaseSegment(new Point2D(lng1,lat1),new Point2D(lng2,lat2));
        BaseSegment segment2 = new BaseSegment(new Point2D(lng1,lat1),new Point2D(lng2,lat2));
        LogicSegment l1 = new LogicSegment(segment,Direction.Forward);
        LogicSegment l2 = new LogicSegment(segment2,Direction.Forward);

//        System.out.println(l1.equals(l2));
//        System.out.println(l1.segment == l2.segment);
//        System.out.println(l1.segment.equals(l2.segment));
        Routing r = demo1.makeRouting(s);
        System.out.println(r);
        System.out.println(r.getSegments().size());
//        LogicPosition lp1 = new LogicPosition(r,0,0,0);
//        LogicPosition lp2 = new LogicPosition(r,0,0,0);

//        System.out.println(lp1 == lp2);
    }



}
