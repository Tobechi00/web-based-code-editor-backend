package com.wide.widebackend.service.code;

import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class GccRunnerService implements CodeRunnerService<ProgramOutputDTO> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public ProgramOutputDTO runCodeWithoutInput(String code, Optional<String> fileName) {
        ProgramOutputDTO programOutputDTO = new ProgramOutputDTO();

        try{
            StringBuilder output = new StringBuilder();

            String[] fileCreationCommands = new String[]{
                        "docker",                 // Command to run Docker
                        "exec",                   // Docker sub-command to execute a command in a running container
                        "-i",                     // Option to keep STDIN open even if not attached
                        "gcc_code_container",    // Name of the Docker container to execute the command in
                        "sh",                     // Command shell to execute the following command
                        "-c",                     // Option to pass a string to the command shell
                        "echo '" + code + "' > Main.c && gcc Main.c -o Main"
                };

            String[] fileRunCommmands = new String[]{
                    "docker",                 // Command to run Docker
                    "exec",                   // Docker sub-command to execute a command in a running container
                    "-i",                     // Option to keep STDIN open even if not attached
                    "gcc_code_container",    // Name of the Docker container to execute the command in
                    "sh",                     // Command shell to execute the following command
                    "-c",
                    "./Main"
            };

            ProcessBuilder processBuilder = new ProcessBuilder(fileCreationCommands);

            Process process = processBuilder.start();

            process.waitFor();

            processBuilder = new ProcessBuilder(fileRunCommmands);
            processBuilder.redirectErrorStream(true);

            long startTime = System.nanoTime();
            process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );


            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            long endTime = System.nanoTime();

            inputStream.close();
            reader.close();

            programOutputDTO.setExitCode(exitCode);
            programOutputDTO.setProgramOutput(output.toString());
            programOutputDTO.setExecutionTime(endTime - startTime);
            return programOutputDTO;
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Override
    public ProgramOutputDTO runCodeWithInput(String code, String input, Optional<String> fileName) {
        ProgramOutputDTO programOutputDTO = new ProgramOutputDTO();

        try {
            StringBuilder output = new StringBuilder();

            String[] fileCreationCommands = new String[]{
                    "docker",                 // Command to run Docker
                    "exec",                   // Docker sub-command to execute a command in a running container
                    "-i",                     // Option to keep STDIN open even if not attached
                    "gcc_code_container",    // Name of the Docker container to execute the command in
                    "sh",                     // Command shell to execute the following command
                    "-c",                     // Option to pass a string to the command shell
                    "echo '" + code + "' > Main.c && gcc Main.c -o Main"
            };

            String[] fileRunCommmands = new String[]{
                    "docker",                 // Command to run Docker
                    "exec",                   // Docker sub-command to execute a command in a running container
                    "-i",                     // Option to keep STDIN open even if not attached
                    "gcc_code_container",    // Name of the Docker container to execute the command in
                    "sh",                     // Command shell to execute the following command
                    "-c",
                    "echo "+input+" | ./Main"
            };

            ProcessBuilder processBuilder = new ProcessBuilder(fileCreationCommands);

            Process process = processBuilder.start();

            process.waitFor();

            processBuilder = new ProcessBuilder(fileRunCommmands);
            processBuilder.redirectErrorStream(true);

            long startTime = System.nanoTime();
            process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );


            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            long endTime = System.nanoTime();

            inputStream.close();
            reader.close();

            programOutputDTO.setExitCode(exitCode);
            programOutputDTO.setProgramOutput(output.toString());
            programOutputDTO.setExecutionTime(endTime - startTime);
            return programOutputDTO;
        }catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
