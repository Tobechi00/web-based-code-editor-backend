package com.wide.widebackend.service.code;

import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class JavaRunnerService implements CodeRunnerService<ProgramOutputDTO>{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public ProgramOutputDTO runCodeWithoutInput(String code,Optional<String> fileName) {
        ProgramOutputDTO programOutputDTO = new ProgramOutputDTO();

        try{
            StringBuilder output = new StringBuilder();

            String[] commands;

            if (fileName.isEmpty()) {
                commands = new String[]{
                        "docker",                 // Command to run Docker
                        "exec",                   // Docker sub-command to execute a command in a running container
                        "-i",                     // Option to keep STDIN open even if not attached
                        "java_code_container",    // Name of the Docker container to execute the command in
                        "sh",                     // Command shell to execute the following command
                        "-c",                     // Option to pass a string to the command shell
                        "echo '" + code + "' > Main.java && javac Main.java && java Main" // Command to echo code and pipe it to JShell
                };
            }else {
                String file = fileName.get();
                String classFileName = file.substring(0,file.lastIndexOf("."));

                commands = new String[]{
                        "docker",                 // Command to run Docker
                        "exec",                   // Docker sub-command to execute a command in a running container
                        "-i",                     // Option to keep STDIN open even if not attached
                        "java_code_container",    // Name of the Docker container to execute the command in
                        "sh",                     // Command shell to execute the following command
                        "-c",                     // Option to pass a string to the command shell
                        "echo '" + code + "' > "+file+" "+" && javac "+file+" && java "+classFileName // Command to echo code and pipe it to JShell
                };
            }

            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );


            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            inputStream.close();
            reader.close();

            programOutputDTO.setExitCode(exitCode);
            programOutputDTO.setProgramOutput(output.toString());
            return programOutputDTO;
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProgramOutputDTO runCodeWithInput(String code, String input,Optional<String> fileName) {
        ProgramOutputDTO programOutputDTO = new ProgramOutputDTO();

        try{
            StringBuilder output = new StringBuilder();

            String[] commands;
            if (fileName.isEmpty()) {
                commands = new String[]{
                        "docker",                 // Command to run Docker
                        "exec",                   // Docker sub-command to execute a command in a running container
                        "-i",                     // Option to keep STDIN open even if not attached
                        "java_code_container",    // Name of the Docker container to execute the command in
                        "sh",                     // Command shell to execute the following command
                        "-c",                     // Option to pass a string to the command shell
                        "echo '" + code + "' > Main.java && javac Main.java && java Main &&"+input // Command to echo code and pipe it to JShell
                };
            }else {
                String file = fileName.get();
                String classFileName = file.substring(0,file.lastIndexOf("."));

                commands = new String[]{
                        "docker",                 // Command to run Docker
                        "exec",                   // Docker sub-command to execute a command in a running container
                        "-i",                     // Option to keep STDIN open even if not attached
                        "java_code_container",    // Name of the Docker container to execute the command in
                        "sh",                     // Command shell to execute the following command
                        "-c",                     // Option to pass a string to the command shell
                        "echo '" + code + "' > "+file+" "+" && javac "+file+" && java "+classFileName+" && "+input // Command to echo code and pipe it to JShell
                };
            }

            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            OutputStream outputStream = process.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            bufferedWriter.write(input);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );


            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            inputStream.close();
            reader.close();

            programOutputDTO.setExitCode(exitCode);
            programOutputDTO.setProgramOutput(output.toString());
            return programOutputDTO;

        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
