package com.wide.widebackend.service;


import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class PythonService {

    Logger logger = LoggerFactory.getLogger(PythonService.class);

    /**
     * runs provided python code on docker instance command line
     * makes use of the python container - py_code_container
     * @param pyCode python code to be executed
     * @return instance of programOutPutDto
     **/
    public ProgramOutputDTO runPythonCode(String pyCode){

        ProgramOutputDTO programOutputDto = new ProgramOutputDTO();

        try {

            StringBuilder programOutput = new StringBuilder();

            //array of commands to be run sequentially
            String[] command = {"docker", "exec", "-i", "py_code_container", "python", "-c",pyCode};

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect error stream to standard output
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Get the input stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read and print the output
            String line;
            while ((line = reader.readLine()) != null) {

                programOutput.append(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
//            System.out.println("Exited with error code: " + exitCode);

            inputStream.close();
            reader.close();
            //setting DAO;
            programOutputDto.setExitCode(exitCode);

            programOutputDto.setProgramOutput(programOutput.toString());

            return programOutputDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * overlaoded variant of runPythonCode
     * expects user input to prevent infinite loop
     * @param pyCode python code to be executed
     * @param userInput expected user input
     * @return instance of programOutPutDto
     **/
    public ProgramOutputDTO runPythonCode(String pyCode, String userInput){

        ProgramOutputDTO programOutputDto = new ProgramOutputDTO();

        try {

            StringBuilder programOutput = new StringBuilder();


            //array of commands to be run sequentially
            String[] command = {"docker", "run", "-i", "--rm", "python", "python", "-c",pyCode};

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect error stream to standard output
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            //access the output stream and add the users input to it
            OutputStream outputStream = process.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            bufferedWriter.write(userInput);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Get the input stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            // Read and print the output
            String line;
            while ((line = reader.readLine()) != null) {

                programOutput.append(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            //close all writers and streams
            inputStream.close();
            reader.close();
            bufferedWriter.close();


            //setting DAO;
            programOutputDto.setExitCode(exitCode);
            programOutputDto.setProgramOutput(programOutput.toString());

            return programOutputDto;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
