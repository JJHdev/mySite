package com.project.mySite.emp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "EMP")
public class EmpDefinition implements Serializable {
    @Id
    @Column(name = "ENAME")
    private String eName;

    @Id
    @Column(name = "EMPNO")
    private int empno;

    @Id
    @Column(name = "JOB")
    private String job;

    public EmpDefinition() {
    }

    public EmpDefinition(String eName, int empno, String job) {
        this.eName = eName;
        this.empno = empno;
        this.job = job;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int getEmpno() {
        return empno;
    }

    public void setEmpno(int empno) {
        this.empno = empno;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
