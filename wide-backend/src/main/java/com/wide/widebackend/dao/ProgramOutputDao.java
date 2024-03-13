package com.wide.widebackend.dao;

public class ProgramOutputDao {

    private String programOutput;

    private int exitCode;

    public ProgramOutputDao(){

    }

    public ProgramOutputDao(String programOutput,int exitCode){
        this.programOutput = programOutput;
        this.exitCode = exitCode;
    }

    public String getProgramOutput() {
        return programOutput;
    }

    public void setProgramOutput(String programOutput) {
        this.programOutput = programOutput;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
}
