package com.wide.widebackend.dao;

import java.util.Optional;

public class ProgramInputDao {

    private String programmingLanguage;

    private String program;

    private Optional<String> userInput;


    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Optional<String> getUserInput() {
        return this.userInput;
    }

    public void setUserInput(Optional<String> userInput) {
        this.userInput = userInput;
    }
}
