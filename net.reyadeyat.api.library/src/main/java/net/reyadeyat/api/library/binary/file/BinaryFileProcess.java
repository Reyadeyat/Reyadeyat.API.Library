package net.reyadeyat.api.library.binary.file;

import net.reyadeyat.api.library.json.JsonUtil;
import net.reyadeyat.api.library.process.BackgroundProcess;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Constructor;
import net.reyadeyat.api.library.environment.ApiEnvironment;
import net.reyadeyat.api.library.process.BackgroundProcessScheduler;

public class BinaryFileProcess extends BackgroundProcess {

    final static private String process_name = "Binary File Process";

    public BinaryFileProcess(BackgroundProcessScheduler background_process_scheduler) throws Exception {
        super(background_process_scheduler);
        startTask(this::buildFileProcess);
    }
    
    public void buildFileProcess() {
        try {
            File temp_binary_file_upload_path = new File(ApiEnvironment.getProperty("net.reyadeyat.api.library.binary.file.process.temp_binary_file_upload_path"));
            if (temp_binary_file_upload_path == null) {
                return;
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, process_name + " doing some work on background thread '" + Thread.currentThread().getName() + "'");
            String file_processor_map_text = ApiEnvironment.getProperty("net.reyadeyat.api.library.binary.file.process.processor");
            JsonObject file_processor_map = JsonUtil.jsonStringToObject(file_processor_map_text, JsonObject.class);
            Files.walkFileTree(temp_binary_file_upload_path.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file_path, BasicFileAttributes attrs) throws IOException {
                    JsonArray errors = new JsonArray();
                    try {
                        String extension = file_path.toString();
                        extension = extension.substring(extension.lastIndexOf("."));
                        if (file_processor_map.has(extension) == false) {
                            return FileVisitResult.CONTINUE;
                        }
                        String processor_class_path = file_processor_map.get(extension).getAsJsonObject().get("processor").getAsString();
                        Class<?> processorClass = Class.forName(processor_class_path);
                        Constructor<?> constructor = processorClass.getConstructor();
                        constructor.setAccessible(true);
                        Object processor = constructor.newInstance();
                        String upload_location = ApiEnvironment.getProperty("net.reyadeyat.api.library.binary.file.process.upload.location");
                        Boolean delete = ((BinaryFileProcessorHandler)processor).processBinaryFile(file_path, upload_location, errors);
                        if (delete) {
                            Files.delete(file_path);
                        }
                    } catch (Exception ex) {
                        StringBuilder error_message = new StringBuilder();
                        for (int i = 0; i < errors.size(); i++) {
                            error_message.append(errors.get(i).getAsString()).append("\n");
                        }
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, process_name + " Runtime Exception on yprocess '" +process_name + "' while processing file '" + file_path.toString() + "'\n"+error_message.toString()+", Contact Administrator\n" + ex.getMessage(), ex);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(process_name + " Process Runtime Exception '" + background_process_scheduler.getDescription() + "." + process_name + "', Contact Administrator\n" + ex.getMessage(), ex);
        }
    }
}
