package com.wide.widebackend.dataobjects.dto;

public class ProgramOutputDTO {

    private String programOutput;

    private int exitCode;

    public ProgramOutputDTO(){}

    public ProgramOutputDTO(String programOutput, int exitCode){
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
