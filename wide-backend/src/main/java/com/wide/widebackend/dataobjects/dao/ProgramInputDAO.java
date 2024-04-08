package com.wide.widebackend.dataobjects.dao;

import java.util.Optional;

public class ProgramInputDAO {

    private String programmingLanguage;

    private String program;

    private Optional<String> userInput;

    //used to name the java file that will be created on the docker container
    private Optional<String> fileName;



    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public String getProgram() {
        return this.program;
    }

    public Optional<String> getUserInput() {
        return this.userInput;
    }

    public Optional<String> getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "ProgramInputDAO{" +
                "programmingLanguage='" + programmingLanguage + '\'' +
                ", program='" + program + '\'' +
                ", userInput=" + userInput +
                ", fileName=" + fileName +
                '}';
    }
}
