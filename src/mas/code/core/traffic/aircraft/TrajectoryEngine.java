package mas.code.core.traffic.aircraft;

import kt.mas.core.traffic.aircraft.AircraftType;
import kt.mas.core.traffic.aircraft.FlightPerformance;
import mas.code.core.misc.LogicPosition;
import mas.code.core.misc.LogicSegment;
import mas.code.core.traffic.ATC.ATCCmd;
import mas.code.core.traffic.ATC.AltCmd;
import mas.code.core.traffic.ATC.SpdCmd;
import mas.code.util.GeoUtil;
import mas.code.util.Global;

public class TrajectoryEngine {
    public Aircraft aircraft;
    private AircraftType aircraftType;

    public TrajectoryEngine(Aircraft aircraft){
        this.aircraft = aircraft;
        aircraftType = aircraft.type;
    }
        double normVSpdAcc = 100 * Global.FTPM2MPS;
        double normHSpdAcc = 1.5 * Global.KT2MPS;
        double normHSpdDec = normHSpdAcc;
        double TurnThreshold = 0.3;


    public AcftState doStep(AcftState curState, ATCCmd atcCmd,int timeStep){
        AcftState nextState = new AcftState(aircraft);
        nextState.time = curState.time + timeStep;
        nextState.fltPlan = curState.fltPlan;
        assignCmd(curState,nextState,atcCmd);
        verticalMove(curState,nextState,timeStep);
        nextState.performance = aircraftType.getPerformanceByAlt(nextState.alt);
        horizontalMove(curState,nextState,timeStep);

//        System.out.println(curState.pos.getCurrentSegment().getHeading() + " line heading ");
//        System.out.println(curState.location + "   " + curState.pos.distanceToTarget + " distance " + curState.pos.getTarget() + " " + curState.heading + " " + curState.hSpdTAS + " heading to Target" + GeoUtil.calHeading(curState.location,curState.pos.getTarget()));
//        System.out.println(curState.aircraft.id  + " " + curState.location + " alt: " + curState.alt + " time : " + curState.time);
//        System.out.println(curState.aircraft.id  + " " + curState.location + " HSpd: " + curState.hSpdTAS + " time : " + curState.time);

        return nextState;
    }

    private void assignCmd(AcftState curState, AcftState nextState, ATCCmd atcCmd){
        AltCmd altCmd = null;
        SpdCmd spdCmd = null;

        if (atcCmd != null ) {
            if (atcCmd instanceof AltCmd) {
                System.out.println("receive a altitude command " + atcCmd);
                altCmd = (AltCmd) atcCmd;
            } else if (atcCmd instanceof SpdCmd) {
                System.out.println("receive a speed command" + atcCmd);
                spdCmd = (SpdCmd) atcCmd;
            }
        }
        nextState.altCmd = altCmd==null?curState.altCmd : altCmd;
        nextState.spdCmd = spdCmd == null ? curState.spdCmd:spdCmd;
    }
    private double horizontalMove(AcftState curState, AcftState nextState,int timeStep){
        FlightPerformance performance = curState.performance;
        double prevHSpd = curState.hSpdTAS;
        double normHSpd = 0.0;
        double maxHSpd = 0.0;
        double minHSpd = 0.0;
        if(nextState.vState == VerticalState.Climb){
            normHSpd = performance.getNormClimbTAS();
            maxHSpd = performance.getMaxClimbTAS();
            minHSpd = performance.getMinClimbTAS();
        }else if(nextState.vState == VerticalState.Descent){
            normHSpd = performance.getNormDescentTAS();
            maxHSpd = performance.getMaxDescentTAS();
            minHSpd = performance.getMinDescentTAS();
        }else {
            normHSpd = performance.getNormCruiseTAS();
            maxHSpd = performance.getMaxCruiseTAS();
            minHSpd = performance.getMinCruiseTAS();
        }

        double targetSpd = normHSpd;
        if(nextState.spdCmd != null){
            if(nextState.spdCmd.targetTAS > normHSpd){
                targetSpd = Math.min(nextState.spdCmd.targetTAS,maxHSpd);
            }else if(nextState.spdCmd.targetTAS < normHSpd){
                targetSpd = Math.max(nextState.spdCmd.targetTAS,minHSpd);
            }
        }
        double nextHSpd = 0.0;
        if (targetSpd > prevHSpd){
            nextHSpd = Math.min(prevHSpd+normHSpdAcc*timeStep, targetSpd);
        }else if(targetSpd < prevHSpd){
            nextHSpd = Math.max(prevHSpd-normHSpdDec*timeStep,targetSpd);
        }else {
            nextHSpd = prevHSpd;
        }
        nextState.hSpdTAS = nextHSpd;
        double hDist = (prevHSpd + nextHSpd)*timeStep/2.0;
        makeTurn(curState,nextState,hDist,timeStep);

        LogicPosition prevPos = curState.pos;
        nextState.pos = new LogicPosition(prevPos.routing,prevPos.currentSegmentIndex,nextState.location);
        checkPosition(nextState,timeStep);
        return hDist;
    }
    private boolean checkPosition(AcftState nextState,int timeStep){
        LogicPosition pos = nextState.pos;
        if(passCurrentSegment(nextState,pos.getNextSegment(),timeStep)){
            if (pos.isOnFinalSegment()){
                nextState.finished = true;
                return true;
            }else{
                pos.currentSegmentIndex++;
                pos.updateDistanceAndHeading(nextState.location);
            }
        }
        return false;
    }

    private void makeTurn(AcftState curState, AcftState nextState,double hDist,int timeStep){
        LogicPosition pos = curState.pos;
        double headingDiff = GeoUtil.calIntersectionAngle(pos.headingToTarget,curState.heading);
//        System.out.println("Heading diff" + headingDiff + " " + pos.headingToTarget + " - " + curState.heading);
        double normTurnRate = curState.performance.getNormTurnRate();

        double turnAngle = 0.0;
        if (headingDiff > TurnThreshold){
            turnAngle = Math.min(headingDiff,normTurnRate*timeStep);
        }else if(headingDiff < TurnThreshold){
            turnAngle = Math.max(headingDiff,-normTurnRate*timeStep);
        }
        double nextHeading = GeoUtil.fixAngle(curState.heading+turnAngle);
        nextState.heading = nextHeading;
        nextState.turnRate = turnAngle/timeStep;
        if (turnAngle == 0.0){
            nextState.location = GeoUtil.getDestination(curState.location,curState.heading,hDist);//这里需要一个方法，根据location、heading、dist 获得下一个点的Point对象
        }else {
            nextState.location = GeoUtil.getDestination(
                    curState.location,
                    (curState.heading+nextState.heading)/2.0,
                    hDist
            );
        }
    }

    private void verticalMove(AcftState curState, AcftState nextState,int timeStep){
        double targetAlt = nextState.altCmd == null ? nextState.fltPlan.RFL:nextState.altCmd.targetAlt;
        if (curState.alt == targetAlt){
            nextState.alt = curState.alt;
            nextState.vSpd = 0;
        }else if(curState.alt < targetAlt){
            climb(curState,nextState,targetAlt,timeStep);
        }else{
            descent(curState,nextState,targetAlt,timeStep);
        }
        if(nextState.vSpd==0.0){
            nextState.vState = VerticalState.Cruise;
        }else if(nextState.vSpd<0.0){
            nextState.vState = VerticalState.Descent;
        }else {
            nextState.vState = VerticalState.Climb;
        }

    }
    private double climb(AcftState curState, AcftState nextState,double targetAlt,int timeStep){
        if(curState.alt >= targetAlt){
            nextState.alt = curState.alt;
            nextState.vSpd = 0;
            return 0;
        }
        FlightPerformance performance = curState.performance;
        double altDiff = targetAlt - curState.alt;
        double prevVSpd = curState.vSpd;
        double normVSpd = performance.getNormClimbRate();
        double reqVSpd = altDiff * 2.0 * normVSpdAcc/prevVSpd;

        double nextVSpd = 0;
        if(prevVSpd >= reqVSpd){
            nextVSpd = Math.max(0.0,prevVSpd-normVSpdAcc*timeStep);
        }else if(prevVSpd == normVSpd){
            nextVSpd = prevVSpd;
        }else if(prevVSpd < normVSpd){
            nextVSpd = Math.min(normVSpd,prevVSpd+normVSpdAcc*timeStep);
        }else {
            nextVSpd = Math.max(normVSpd, prevVSpd-normVSpdAcc*timeStep);
        }
        double vDist = (prevVSpd+nextVSpd)*timeStep/2.0;
        if(vDist>=altDiff){
            vDist = altDiff;
            nextVSpd = 0.0;
        }
        nextState.vSpd = nextVSpd;
        nextState.alt = curState.alt +vDist;
        return vDist;
    }
    private double descent(AcftState curState, AcftState nextState,double targetAlt,int timeStep){
        if(curState.alt <= targetAlt){
            nextState.alt = curState.alt;
            nextState.vSpd = 0;
            return 0.0;
        }
        FlightPerformance performance = curState.performance;
        double altDiff = targetAlt - curState.alt;
        double prevVSpd = curState.vSpd;
        double normVSpd = -performance.getNormClimbRate();

        double reqVSpd = prevVSpd==0 ? Double.NEGATIVE_INFINITY:altDiff * 2.0 * -normVSpdAcc/prevVSpd;

        double nextVSpd = 0;
        if(prevVSpd <= reqVSpd){
            nextVSpd = Math.min(0.0,prevVSpd+normVSpdAcc*timeStep);
        }else if(prevVSpd == normVSpd){
            nextVSpd = prevVSpd;
        }else if(prevVSpd > normVSpd){
            nextVSpd = Math.max(normVSpd,prevVSpd-normVSpdAcc*timeStep);
        }else {
            nextVSpd = Math.min(normVSpd, prevVSpd+normVSpdAcc*timeStep);
        }
        double vDist = (prevVSpd+nextVSpd)*timeStep/2.0;
        if(vDist<=altDiff){
            vDist = altDiff;
            nextVSpd = 0.0;
        }
        nextState.vSpd = nextVSpd;
        nextState.alt = curState.alt +vDist;
        return vDist;
    }

    private boolean passCurrentSegment(AcftState state, LogicSegment nextSeg,double timeStep){
        double distanceToTarget = state.pos.distanceToTarget;
//        System.out.println(distanceToTarget + "    to target "  + state.pos.getTarget());
        if(distanceToTarget > 6 *Global.KM2M){
            return false;
        }
        if(distanceToTarget < state.hSpdTAS * timeStep){
            return true;
        }
        if (nextSeg != null) {
            double prediction = calcTurnPrediction(
                    state.hSpdTAS,
                    state.heading,
                    GeoUtil.calHeading(state.location,nextSeg.getEnd()),
                    state.performance.getNormTurnRate());
            if(distanceToTarget < prediction){
                return true;
            }
        }
        double angle = Math.abs(GeoUtil.calIntersectionAngle(state.heading,state.pos.headingToTarget));
        return angle>90.0;

    }
    private double calcTurnPrediction(double spd, double heading, double nextHeading, double turnRate){
        double turnRadianRate = turnRate* Global.Deg2Rad;
        double turnAngle = Math.abs(GeoUtil.calIntersectionAngle(heading,nextHeading));
        double turnRadian = turnAngle * Global.Deg2Rad;
        double turnRadius = spd/turnRadianRate;
        return turnRadius * Math.tan(turnRadian/2)/1.3;
    }





}
