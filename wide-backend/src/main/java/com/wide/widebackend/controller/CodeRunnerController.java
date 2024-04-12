package com.wide.widebackend.controller;

import com.wide.widebackend.dataobjects.dao.ProgramInputDAO;
import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import com.wide.widebackend.service.code.CodeRunnerService;
import com.wide.widebackend.service.code.GccRunnerService;
import com.wide.widebackend.service.code.JavaRunnerService;
import com.wide.widebackend.service.code.PythonRunnerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping(path = "/w-ide/api")
public class CodeRunnerController {

    private final PythonRunnerService pythonRunnerService;
    private final JavaRunnerService javaRunnerService;

    private final GccRunnerService gccRunnerService;

    public CodeRunnerController(
            PythonRunnerService pythonService,
            JavaRunnerService javaRunnerService,
            GccRunnerService gccRunnerService) {

        this.pythonRunnerService = pythonService;
        this.javaRunnerService = javaRunnerService;
        this.gccRunnerService = gccRunnerService;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int TIME_LIMIT = 20;


    //method for handling python code compilation requests
    @PostMapping(value = "python/exec")
    public ResponseEntity<ProgramOutputDTO> runPythonCode(@RequestBody ProgramInputDAO programInputDao){

        if (programInputDao.getUserInput().isEmpty()){
        try{
            //file name isn't needed for executing python code
            return  runCodeWithoutInput(programInputDao.getProgram(), pythonRunnerService,Optional.empty(), logger);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }else {
            try {
                return runCodeWithInput(programInputDao.getProgram(),programInputDao.getUserInput().get(), pythonRunnerService,Optional.empty(), logger);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

    }

    @PostMapping(value = "java/exec")
    public ResponseEntity<ProgramOutputDTO> runJavaCode(@RequestBody ProgramInputDAO programInputDao){
        if (programInputDao.getUserInput().isEmpty()){
            try{
                return runCodeWithoutInput(
                        programInputDao.getProgram(),
                        javaRunnerService,
                        programInputDao.getFileName(),
                        logger);
            }catch (Exception e){
                logger.error(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }

        }else {
            try {
                return runCodeWithInput(programInputDao.getProgram(),
                        programInputDao.getUserInput().get(),
                        javaRunnerService,
                        programInputDao.getFileName(),
                        logger);

            } catch (Exception e) {
                logger.error(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

    }

    @PostMapping(value = "c/exec")
    public ResponseEntity<ProgramOutputDTO> runGccCode(@RequestBody ProgramInputDAO programInputDao){
        if (programInputDao.getUserInput().isEmpty()){
            try{
                return runCodeWithoutInput(
                        programInputDao.getProgram(),
                        gccRunnerService,
                        programInputDao.getFileName(),
                        logger);
            }catch (Exception e){
                logger.error(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }

        }else {
            try {
                return runCodeWithInput(programInputDao.getProgram(),
                        programInputDao.getUserInput().get(),
                        gccRunnerService,
                        programInputDao.getFileName(),
                        logger);

            } catch (Exception e) {
                logger.error(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }
    }

    //completable future to prevent resource hogging
    private ResponseEntity<ProgramOutputDTO> runCodeWithoutInput(
            String program,
            CodeRunnerService<ProgramOutputDTO> codeRunnerService,
            Optional<String> fileName,
            Logger logger
    ){
        CompletableFuture<ProgramOutputDTO> future = CompletableFuture.supplyAsync(() ->
                codeRunnerService.runCodeWithoutInput(program,fileName)
        );

        ProgramOutputDTO result = future.orTimeout(TIME_LIMIT, TimeUnit.SECONDS) // Set your desired timeout here
                .exceptionally(throwable -> {
                    // Handle timeout exception
                    // You can customize the ProgramOutputDao for timeout if needed
                    logger.warn(throwable.getMessage());
                    return new ProgramOutputDTO("Error: code took too long to execute", HttpStatus.EXPECTATION_FAILED.value());
                }).join();

        return ResponseEntity.ok(result);
    }

    private ResponseEntity<ProgramOutputDTO> runCodeWithInput(
            String program,String input,
            CodeRunnerService<ProgramOutputDTO> codeRunnerService,
            Optional<String> fileName,
            Logger logger
    ){
        CompletableFuture<ProgramOutputDTO> future = CompletableFuture.supplyAsync(() ->
                codeRunnerService.runCodeWithInput(program, input, fileName)
        );

        // Apply a timeout to the CompletableFuture
        ProgramOutputDTO result = future.orTimeout(TIME_LIMIT, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    // Handle timeout exception
                    logger.warn(throwable.getMessage());
                    return new ProgramOutputDTO("Error: code took too long to execute", HttpStatus.EXPECTATION_FAILED.value());
                }).join();

        return ResponseEntity.ok(result);
    }
}
