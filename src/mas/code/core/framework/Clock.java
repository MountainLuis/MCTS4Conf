package mas.code.core.framework;

public interface Clock {

    int getStartTime();
    int getTimeStep();
    int getTime();
    void doStep();

}
