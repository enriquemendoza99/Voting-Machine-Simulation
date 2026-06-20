package Drivers;

public class TPSensor {
    private boolean status = false;
    private final LLSensor llSensor;
    private boolean isTampered = false;

    public TPSensor(LLSensor llSensor) {
        this.llSensor = llSensor;
    }

    public boolean isTampered() {
        return isTampered && llSensor.isLatched();
    }

    public boolean failure() {
        return status;
    }

    // Demo functions below
    public void setFailure(boolean status) {
        this.status = status;
    }

    public void setTampered(boolean isTampered) {
        this.isTampered = isTampered;
    }

}
