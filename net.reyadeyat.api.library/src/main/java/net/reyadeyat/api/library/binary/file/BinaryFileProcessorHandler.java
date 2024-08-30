package net.reyadeyat.api.library.binary.file;

import com.google.gson.JsonArray;
import java.nio.file.Path;

public interface BinaryFileProcessorHandler {
    public Boolean processBinaryFile(Path binary_file_path, String upload_location, JsonArray errors) throws Exception;
}
