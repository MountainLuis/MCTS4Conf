package mas.code.core.traffic.aircraft;

import kt.mas.core.traffic.aircraft.AircraftType;
import mas.code.core.framework.Scenario;
import mas.code.core.geom.Point2D;
import mas.code.core.misc.LogicPosition;
import mas.code.core.traffic.ATC.ATCCmd;
import mas.code.core.traffic.ATC.AltCmd;
import mas.code.core.traffic.ATC.SpdCmd;
import mas.code.core.traffic.FlightPlan;
import mas.code.core.traffic.Routing;

import java.util.LinkedList;

public class Aircraft {
        enum Stage{
                NotStarted,Running,Done
        }
        public String id;
        public AircraftType type;
        public FlightPlan curFltPlan;
        public TrajectoryEngine trajectoryEngine;
        public LinkedList<AcftState> prediction;

        public AcftState curState;
//        public boolean finished;
        Stage stage = Stage.NotStarted;

        public Aircraft(){
        }
        public Aircraft(String id, AircraftType type){
                this.id = id;
                this.type = type;
//                System.out.println(type);
                trajectoryEngine = new TrajectoryEngine(this);
                prediction = new LinkedList<>();
        }

        public boolean isFinished(){
                return stage == Stage.Done;
        }

        public AcftState lastPredictedState(){
                if(prediction.size() <= 0){
                        return null;
                }else {
                        return prediction.get(prediction.size()-1);
                }
        }
        private void initState(int now){
                FlightPlan curFltPlan = this.curFltPlan;
                Routing routing = curFltPlan.routing;
                curState = new AcftState(this);
                curState.time = now;
                curState.location = new Point2D(routing.getPoints().get(0));

                curState.fltPlan = curFltPlan;
                curState.pos = new LogicPosition(routing,0,curState.location);
                curState.heading = curState.pos.currentSegment.getHeading();
                curState.alt = curFltPlan.RFL;
                curState.performance = this.type.getPerformanceByAlt(curState.alt);
                curState.vState = VerticalState.Cruise;
                curState.hSpdTAS = curState.performance.getNormCruiseTAS();

        }
        private boolean checkStage(){
                if (stage == Stage.Done){
                        return false;
                }
                if(curFltPlan == null){
                        stage = Stage.NotStarted;
                        System.out.println("NO Plan ");
                        return false;
                }
                if (curFltPlan != null){
                        int now = Scenario.clock.getTime();
//                        System.out.println("now " + now);
                        if (stage == Stage.NotStarted){
                                if (now >= curFltPlan.startTime){
                                        stage = Stage.Running;
                                        initState(now);
                                        return true;
                                }
                        }

                }
                return stage == Stage.Running;
        }
        public void doStep(){
                if(!checkStage()){
                        return;
                }
                predict();
//                predictnew();
                if(prediction.size() == 0){
                        return;
                }
                curState = prediction.removeFirst();
                System.out.println("State: " + curState);
                if(curState.finished){
                        stage = Stage.Done;
                }
        }
        private void predict(){
                int dt = Scenario.clock.getTimeStep();
                AcftState last = prediction.size()==0?curState:prediction.getLast();
                int t0 = (int) (Math.max(Scenario.clock.getTime(),last.time)+dt);
                int t1 = Scenario.clock.getTime() + 300 + dt;
                for (int t = t0; t<= t1; t+=dt){
                        if (last.finished){
                                break;
                        }
                        ATCCmd cmd = t==60?new AltCmd(Scenario.clock.getTime(),-600,8400):null;
//                        ATCCmd cmd = t==20?new SpdCmd(Scenario.clock.getTime(),-10,200):null;
//                        ATCCmd cmd =  null;
                        last= trajectoryEngine.doStep(last,cmd,dt);
                        prediction.add(last);
                }
        }
        //A simple function
        private void predictnew(){
                AcftState last = prediction.size()==0?curState:prediction.getLast();
                int dt = Scenario.clock.getTimeStep();
                ATCCmd cmd =  null;
                last = trajectoryEngine.doStep(last,cmd,dt);
                prediction.add(last);
        }


}
