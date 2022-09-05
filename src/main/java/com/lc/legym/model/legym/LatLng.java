package com.lc.legym.model.legym;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Aaron Yeung
 */
@Data
@AllArgsConstructor
public final class LatLng {

    private double latitude;
    private double longitude;

    public LatLng() {
    }

    public LatLng(double d2, double d3, boolean z) throws Exception {
        if (z) {
            if (-180.0d > d3 || d3 >= 180.0d) {
                this.longitude = ((((d3 - 180.0d) % 360.0d) + 360.0d) % 360.0d) - 180.0d;
            } else {
                this.longitude = d3;
            }
            if (d2 < -90.0d || d2 > 90.0d) {
                throw new Exception("LatLng error");
            }
            this.latitude = Math.max(-90.0d, Math.min(90.0d, d2));
            return;
        }
        this.latitude = d2;
        this.longitude = d3;
    }

}