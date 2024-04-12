package com.wide.widebackend.service.code;


import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Slf4j
@Service
public class PythonRunnerService implements CodeRunnerService<ProgramOutputDTO> {

    private final Logger logger = LoggerFactory.getLogger(PythonRunnerService.class);

    /**
     * runs provided python code on docker instance command line
     * makes use of the python container - py_code_container
     * @param code python code to be executed
     * @return instance of programOutPutDto
     **/

    @Override
    public ProgramOutputDTO runCodeWithoutInput(String code,Optional<String> fileName) {

        ProgramOutputDTO programOutputDto = new ProgramOutputDTO();

        try {

            StringBuilder programOutput = new StringBuilder();

            //array of commands to be run sequentially
            String[] commands = {"docker", "exec", "-i", "py_code_container", "python", "-c", code};

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(commands);

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

                programOutput.append(line).append("\n");
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProgramOutputDTO runCodeWithInput(String code, String input,Optional<String> fileName) {
        ProgramOutputDTO programOutputDto = new ProgramOutputDTO();

        try {

            StringBuilder programOutput = new StringBuilder();


            //array of commands to be run sequentially
            String[] commands = {"docker", "exec", "-i", "py_code_container", "python", "-c", code, input};

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            // Redirect error stream to standard output
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            //access the output stream and add the users input to it
            OutputStream outputStream = process.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            bufferedWriter.write(input);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Get the input stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            // Read and print the output
            String line;
            while ((line = reader.readLine()) != null) {

                programOutput.append(line).append("\n");
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
