package com.wide.widebackend.controller;

import com.wide.widebackend.Entity.User;
import com.wide.widebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.List;
/*
controller for all file processing and retrieval apis
 */
@RestController
@Slf4j
@RequestMapping("/w-ide/api")
public class FileController {

    Logger logger = LoggerFactory.getLogger(FileController.class);
    UserService userService;

    public FileController(UserService userService){
        this.userService = userService;
    }

    //todo: use records for dtos

    //duplicate for save-As!!!
    @PostMapping(value ="/files/save/{id}")
    public ResponseEntity<String> saveFiles(@PathVariable Long id,@RequestBody SaveFileDTO saveFileDTO) {

        try {
            User user = userService.getUserById(id);
            Path path = Path.of(saveFileDTO.fileName);

            if (user.getFilePaths().contains(path.toString())){
                File file = path.toFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(saveFileDTO.fileContent);
                writer.close();
            }
            return ResponseEntity.ok("File Saved Successfully");

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("An Error Occurred while Saving file");
        }
    }

    //todo: rewrite this monstrosity; use try with resources and close those leaks also change file renaming method https://stackoverflow.com/questions/1158777/rename-a-file-using-java
    @PostMapping(value = "/files/save-as/{id}")
    ResponseEntity<String> saveFilesAs(@PathVariable Long id, @RequestBody SaveAsFileDTO saveAsFileDTO){

        try {
            User user = userService.findUserById(id);
            List<String> userFiles = user.getFilePaths();

            Path newPath = Path.of(saveAsFileDTO.newFilePath);

            ResponseEntity<String> response = ResponseEntity.internalServerError().build();

            //saving a new file
            if (saveAsFileDTO.oldFilePath.isEmpty() && !userFiles.contains(saveAsFileDTO.newFilePath)){
                File file = newPath.toFile();

                if (!file.exists()){
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(saveAsFileDTO.fileContent);
                    userFiles.add(newPath.toString());

                    userService.savePathsToUser(user,userFiles);
                    writer.close();
                    response = ResponseEntity.ok().body("file created successfully");
                }else {
                    throw new FileAlreadyExistsException("file already exists");
                }
            }

            //renaming an already existing file
            else if (!saveAsFileDTO.oldFilePath.isEmpty() && !userFiles.contains(saveAsFileDTO.newFilePath)){

                File oldFile = Path.of(saveAsFileDTO.oldFilePath).toFile();
                File newFile = newPath.toFile();

                if (oldFile.exists() && !newFile.exists()){
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                    writer.write(saveAsFileDTO.fileContent);
                    writer.close();

                    //deleting old file after being sure of content being written to new file
                    if (oldFile.delete()){
                        userFiles.remove(oldFile.getPath());
                        userFiles.add(newFile.getPath());

                        userService.savePathsToUser(user,userFiles);
                        response = ResponseEntity.ok().body("file renamed successfully");
                    }
                }

            }else {
                //throwing error if new file already exists in memory
                throw new FileAlreadyExistsException("provided new file path already exists");
            }

            return response;

        }catch (IOException i){
            logger.error(i.getMessage());
            return ResponseEntity.internalServerError().body("error occurred while handling files");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("an error has occurred");
        }
    }



    //received file DTO
    record SaveFileDTO(String fileName,String fileContent){}

    //change this for the frontend too
    record SaveAsFileDTO(String oldFilePath,String newFilePath,String fileContent){}

    //method to get all files by provided user id
    @GetMapping(value = "/files/getAllFiles/{id}")
    public ResponseEntity<List<String>> getAllFiles(@PathVariable Long id){
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok().body(user.getFilePaths());
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //method to get the contents of a file {file path provided in request parameter ?path = filename}
    @GetMapping(value = "files/getFileContent")
    public ResponseEntity<String> getFileContent(@RequestParam String path){

        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());//for line separation
            }
            return ResponseEntity.ok().body(stringBuilder.toString());

        }catch (FileNotFoundException f){
            logger.error(f.getMessage());

            return ResponseEntity.internalServerError().body("file with name:"+" "+extractFileNameFromPath(path)+" "+"could not be found");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("an error has occurred");
        }
    }

    @PutMapping(value = "files/updateExistingFile")
    public ResponseEntity<String> updateFileByFileName(@RequestBody String filePath, @RequestBody String replacementContent){

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            bufferedWriter.write(replacementContent);
            return ResponseEntity.ok().body("content updated successfully");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("file with name:"+" "+extractFileNameFromPath(filePath)+" "+"could not be found");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("an error occurred while trying to process file");
        }
    }

    public String extractFileNameFromPath(String path){
        return path.substring(path.lastIndexOf("\\")+1);
    }
}
