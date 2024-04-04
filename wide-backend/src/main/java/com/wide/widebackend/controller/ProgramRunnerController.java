package com.wide.widebackend.controller;

import com.wide.widebackend.dataobjects.dao.ProgramInputDAO;
import com.wide.widebackend.dataobjects.dto.ProgramOutputDTO;
import com.wide.widebackend.service.PythonService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping(path = "/w-ide/api")
public class ProgramRunnerController {


    private final PythonService pythonService;

    public ProgramRunnerController(PythonService pythonService) {
        this.pythonService = pythonService;
    }

    Logger log = LoggerFactory.getLogger(ProgramRunnerController.class);


    //method for handling python code compilation requests
    @PostMapping(value = "/python/submit")
    public ResponseEntity<ProgramOutputDTO> runCode(@RequestBody ProgramInputDAO programInputDao){

        if (programInputDao.getUserInput().isEmpty()){
        try{
            CompletableFuture<ProgramOutputDTO> future = CompletableFuture.supplyAsync(() ->
                    pythonService.runPythonCode(programInputDao.getProgram())
            );

            ProgramOutputDTO result = future.orTimeout(10, TimeUnit.SECONDS) // Set your desired timeout here
                    .exceptionally(throwable -> {
                        // Handle timeout exception
                        // You can customize the ProgramOutputDao for timeout if needed
                        return new ProgramOutputDTO("Error: code took too long to execute",555);
                    }).join();

            return ResponseEntity.ok(result);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }else {
            try {
                CompletableFuture<ProgramOutputDTO> future = CompletableFuture.supplyAsync(() ->
                        pythonService.runPythonCode(programInputDao.getProgram(), programInputDao.getUserInput().get())
                );

                // Apply a timeout to the CompletableFuture
                ProgramOutputDTO result = future.orTimeout(20, TimeUnit.SECONDS)
                        .exceptionally(throwable -> {
                            // Handle timeout exception
                            // 555 is a custom error
                            return new ProgramOutputDTO("Error: code took too long to execute", HttpStatus.EXPECTATION_FAILED.value());
                        }).join();

                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

    }
}
