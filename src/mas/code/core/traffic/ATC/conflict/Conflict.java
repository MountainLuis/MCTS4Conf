package mas.code.core.traffic.ATC.conflict;

import mas.code.core.traffic.aircraft.AcftState;
import mas.code.core.traffic.aircraft.Aircraft;

public class Conflict {

        public AcftState state1;
        public AcftState state2;
        public LateralType lateralType;
        public double distance;
        public Aircraft acft1;
        public Aircraft acft2;
        public Conflict(AcftState state1, AcftState state2,LateralType type,double distance){
            this.state1 = state1;
            this.state2 = state2;
            this.lateralType = type;
            this.distance = distance;
            acft1 = state1.aircraft;
            acft2 = state2.aircraft;
        }
        public int hashCode(){
            return state1.hashCode() + state2.hashCode();
        }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Conflict){
            Conflict conflict = (Conflict)obj;
            if ((conflict.state1.equals(this.state1)&& conflict.state2.equals(this.state2))
                    ||(conflict.state1.equals(this.state2)&& conflict.state2.equals(this.state1))){
                if (conflict.lateralType.equals(this.lateralType) && conflict.distance == this.distance){
                    return true;
                }
            }
        }
        return false;
    }
}
enum LateralType{
    SameRouting,
    Cross,
    Opposite
}
