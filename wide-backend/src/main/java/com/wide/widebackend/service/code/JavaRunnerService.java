package com.wide.widebackend.service.code;

import com.sun.tools.javac.Main;
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

            String[] createFileCommands;
            String[] runClassFileCommands;
            String classFileName;
            int exitCode = 0;

            if (fileName.isEmpty()) {
                classFileName = "Main";
                createFileCommands = createClassFileCommands(
                        "Main.java",
                        code
                );

            }else {
                String file = fileName.get();
                classFileName = file.substring(0,file.lastIndexOf("."));

                createFileCommands =  createClassFileCommands(
                        file,
                        code
                );

            }

            runClassFileCommands = createRunClassFileCommands(
                    classFileName
            );

            ProcessBuilder processBuilder = new ProcessBuilder(createFileCommands);


            Process process = processBuilder.start();

            //waiting for file creation process to be completed

            process.waitFor();

            processBuilder = new ProcessBuilder(runClassFileCommands);
            processBuilder.redirectErrorStream(true);

            //logging process start time;
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

            exitCode = process.waitFor();
            //logging process end time
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

    private String[] createClassFileCommands(String file, String code){
        //command array for creating writing and compiling java file
        return new String[]{
                "docker",                 // Command to run Docker
                "exec",                   // Docker sub-command to execute a command in a running container
                "-i",                     // Option to keep STDIN open even if not attached
                "java_code_container",    // Name of the Docker container to execute the command in
                "sh",                     // Command shell to execute the following command
                "-c",                     // Option to pass a string to the command shell
                "echo '" + code + "' > "+file+" "+" && javac "+file // Command to echo code and pipe it to JShell
        };
    }

    private String[] createRunClassFileCommands(String classFileName){
        //command array for executing java class file
        return new String[]{
                "docker",                 // Command to run Docker
                "exec",                   // Docker sub-command to execute a command in a running container
                "-i",                     // Option to keep STDIN open even if not attached
                "java_code_container",    // Name of the Docker container to execute the command in
                "sh",                     // Command shell to execute the following command
                "-c",
                "java "+classFileName
        };
    }

    private String[] createRunClassFileCommandsWithInput(String classFileName, String input){
        //command array for executing java class file
        return new String[]{
                "docker",                 // Command to run Docker
                "exec",                   // Docker sub-command to execute a command in a running container
                "-i",                     // Option to keep STDIN open even if not attached
                "java_code_container",    // Name of the Docker container to execute the command in
                "sh",                     // Command shell to execute the following command
                "-c",
                "echo "+input+" | java "+classFileName
        };
    }



    @Override
    public ProgramOutputDTO runCodeWithInput(String code, String input,Optional<String> fileName) {
        ProgramOutputDTO programOutputDTO = new ProgramOutputDTO();

        try{
            StringBuilder output = new StringBuilder();

            String classFileName;
            String[] createFileCommands;
            String[] runClassFileCommands;
            if (fileName.isEmpty()) {
                classFileName = "Main";
                createFileCommands = createClassFileCommands(
                        "Main.java",
                        code
                        );
            }else {
                String file = fileName.get();
                classFileName = file.substring(0,file.lastIndexOf("."));
                createFileCommands = createClassFileCommands(
                        fileName.get(),
                        code);
            }

            runClassFileCommands = createRunClassFileCommandsWithInput(
                    classFileName,
                    input
            );

            ProcessBuilder processBuilder = new ProcessBuilder(createFileCommands);


            Process process = processBuilder.start();

            process.waitFor();

            processBuilder = new ProcessBuilder(runClassFileCommands);
            processBuilder.redirectErrorStream(true);

            long startTime = System.nanoTime();

            process = processBuilder.start();

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
            long endTime = System.nanoTime();

            inputStream.close();
            reader.close();

            programOutputDTO.setExitCode(exitCode);
            programOutputDTO.setExecutionTime(endTime - startTime);
            programOutputDTO.setProgramOutput(output.toString());
            return programOutputDTO;

        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
