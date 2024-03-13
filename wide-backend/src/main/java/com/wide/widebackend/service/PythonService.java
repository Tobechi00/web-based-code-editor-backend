package com.wide.widebackend.service;


import com.wide.widebackend.dao.ProgramOutputDao;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class PythonService {

    Logger logger = LoggerFactory.getLogger(PythonService.class);
    public ProgramOutputDao runPythonCode(String pyCode){

        ProgramOutputDao programOutputDao = new ProgramOutputDao();

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
            programOutputDao.setExitCode(exitCode);

            programOutputDao.setProgramOutput(programOutput.toString());

            return programOutputDao;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public ProgramOutputDao runPythonCode(String pyCode,String userInput){

        ProgramOutputDao programOutputDao = new ProgramOutputDao();

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
            logger.warn("line reached");

            // Wait for the process to complete
            int exitCode = process.waitFor();
//            System.out.println("Exited with error code: " + exitCode);

            inputStream.close();
            reader.close();
            bufferedWriter.close();


            //setting DAO;
            programOutputDao.setExitCode(exitCode);

            programOutputDao.setProgramOutput(programOutput.toString());


            return programOutputDao;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
