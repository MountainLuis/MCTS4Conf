package mas.code.core.traffic.ATC;

public interface ATCCmd {
    enum Type{
        Speed,Altitude,Direction
    }
    Type getType();
    int getAssignTime();
    void setAssignTime(int time);
}
