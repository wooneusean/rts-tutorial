package my.edu.apu.lab8;

import java.io.Serializable;

public class WingFlapPackage implements Serializable {
    public WingFlapPackage(int direction, int altitude) {
        this.direction = direction;
        this.altitude = altitude;
    }

    int direction;
    int altitude;
}
