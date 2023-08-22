package io.github.clouderhem.legym.model;

import lombok.Data;

/**
 * @author Aaron Yeung
 * @date 9/6/2022 11:07 AM
 */
@Data
public class JobLog {
    private String ak;
    private String date;
    private String name;
    private double mile;
    private String studentId;
    private String schoolName;
    private String organizationName;
}
