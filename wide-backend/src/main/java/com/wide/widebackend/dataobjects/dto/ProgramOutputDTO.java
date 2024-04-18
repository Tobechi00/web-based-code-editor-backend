package com.wide.widebackend.dataobjects.dto;

public class ProgramOutputDTO {

    private String programOutput;

    private Integer exitCode;

    private Long executionTime;

    public ProgramOutputDTO(){}

    public ProgramOutputDTO(String programOutput, Integer exitCode){
        this.programOutput = programOutput;
        this.exitCode = exitCode;
    }

    public ProgramOutputDTO(String programOutput, Integer exitCode, Long executionTime){
        this.programOutput = programOutput;
        this.exitCode = exitCode;
        this.executionTime = executionTime;
    }

    public String getProgramOutput() {
        return programOutput;
    }

    public void setProgramOutput(String programOutput) {
        this.programOutput = programOutput;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }
}
