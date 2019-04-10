package test;

import kt.mas.core.traffic.aircraft.AircraftType;
import mas.code.core.airspace.Waypoint;
import mas.code.core.framework.IScenarioBuilder;
import mas.code.core.framework.Scenario;
import mas.code.core.framework.SimEngine;
import mas.code.core.traffic.FlightPlan;
import mas.code.core.traffic.Routing;
import mas.code.core.traffic.aircraft.Aircraft;
import mas.code.util.DataAccessObject;

import java.util.ArrayList;
import java.util.List;

public class Demo1 implements IScenarioBuilder {
    static DataAccessObject dao = new DataAccessObject();
    static SimEngine se;
    public static Scenario scenario;


    public static void main(String[] args){
        Demo1 test = new Demo1();
        scenario = new Scenario();
        se = new SimEngine();
        se.scenarioBuilder = test;
//        System.out.println("run it");
        se.run();
    }
    private FlightPlan makeFltPlan(){
        String path = "P197-UDINO-SATRO-DPX";
        FlightPlan fp = new FlightPlan("FP#0",scenario.aircraft0,makeRouting(path),9000.0,1);
        FlightPlan fp1 = new FlightPlan("FP#1",scenario.aircraft1,makeRouting(path),9000,30);
        scenario.aircraft0.curFltPlan = fp;
        scenario.aircraft1.curFltPlan = fp1;

        return fp;
    }

    public Routing makeRouting(String l){
        String[] ss = l.split("-");
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        for (String s: ss){
            if (s != null) {
                Waypoint p = scenario.waypoints.get(s);
//                System.out.println(p);
                waypoints.add(p);
            }
        }
        Routing ret = new Routing(l,waypoints);
//        System.out.println(ret.getSegments().size() + " segment size");
        return ret;
    }
    public  ArrayList<Waypoint> makeWayPoints(String l){
        String[] ss = l.split("-");
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        for (String s: ss){
            if (s != null) {
                Waypoint tmp =scenario.waypoints.get(s) ;
                waypoints.add(tmp);
            }
        }
        return waypoints;
    }


    private void loadAcft(){
        scenario.aircraft0 = new Aircraft("A0",AircraftType.Companion.getALL().get("A320"));
        scenario.aircraft1 = new Aircraft("B1",AircraftType.Companion.getALL().get("A320"));
        makeFltPlan();
    }


    public void loadWayPoints(){
        for (String pt:dao.allNaipPtMap.keySet()){
//            System.out.println(pt + " " + dao.allNaipPtMap.get(pt)[1]);
            scenario.waypoints.put(pt,new Waypoint(pt,dao.allNaipPtMap.get(pt)[1],dao.allNaipPtMap.get(pt)[0]));
        }
    }

    @Override
    public void build() {
//        System.out.println("do it ");
        loadWayPoints();
        loadAcft();
        se.setScenario(scenario);
    }
}
