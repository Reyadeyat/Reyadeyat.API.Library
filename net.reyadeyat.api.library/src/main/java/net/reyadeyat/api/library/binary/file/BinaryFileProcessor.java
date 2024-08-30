package net.reyadeyat.api.library.binary.file;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import net.reyadeyat.api.library.logger.EmbeddedLevel;

public class BinaryFileProcessor implements BinaryFileProcessorHandler {

    @Override
    public Boolean processBinaryFile(Path binary_file_path, String upload_location, JsonArray errors) throws Exception {
        try ( FileChannel file = FileChannel.open(binary_file_path, StandardOpenOption.READ)) {
            ChunkedFile chunked_file = new ChunkedFile(file);
            if (chunked_file.isCompleteFile() == false) {
                file.close();
                Logger.getLogger(getClass().getName()).log(EmbeddedLevel.NOTE, "Binary file is not valid:\n"+chunked_file.toString());
                return false;
            }
            String chunked_file_key = chunked_file.getFileKey();
            try {
                Gson gson = new Gson();
                String file_key = chunked_file.getFileKey();
                String file_info_json_text = chunked_file.getFileInfo();
                JsonObject file_info_json = gson.fromJson(file_info_json_text, JsonObject.class);
                if (file_info_json.get("file_process").getAsString().equals(getClass().getSimpleName()) == false) {
                    file.close();
                    return false;
                }
                Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "file_key: "+ file_key);
                Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "file_info: "+file_info_json_text);
                String file_name = file_info_json.get("file_name").getAsString();
                File binary_file = new File(upload_location+File.separator+file_key+"."+file_name);
                try (FileOutputStream fos = new FileOutputStream(binary_file)) {
                    for (int i = 0; i < chunked_file.getChunkCount(); i++) {
                        DataChunk data_chunk = chunked_file.readChunk(i);
                        fos.write(data_chunk.chunk_blob_bytes);
                    }
                }
                if (Logger.getLogger(getClass().getName()).isLoggable(EmbeddedLevel.TRACE) == true) {
                    synchronized(System.out) {
                        Logger.getLogger(getClass().getName()).log(EmbeddedLevel.TRACE, "Report file is succefully loaded:\n"+chunked_file.toString());
                        //Writer writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
                        //gson.toJson(tree_json, writer);
                    }
                }
            } catch (Exception ex) {
                errors.add("Error processing chunk for file '" + chunked_file_key + "'");
                throw ex;
            }
        } catch (Exception ex) {
            errors.add("Error processing chunk for file '" + binary_file_path.toString() + "'");
            throw ex;
        }
        return true;
    }
}
