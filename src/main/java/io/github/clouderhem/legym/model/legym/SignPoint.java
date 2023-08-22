package io.github.clouderhem.legym.model.legym;


import lombok.Data;

/**
 * @author Aaron Yeung
 */
@Data
public final class SignPoint {

    public static final int NO_PASS = 0;
    public static final int PASS = 1;
    private String signPoint;
    private int state;

}
