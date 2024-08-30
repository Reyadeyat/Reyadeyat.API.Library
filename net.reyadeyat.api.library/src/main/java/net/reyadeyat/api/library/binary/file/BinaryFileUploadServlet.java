package net.reyadeyat.api.library.binary.file;

import net.reyadeyat.api.library.servlet.TransactionServlet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.reyadeyat.api.library.environment.ApiEnvironment;
import net.reyadeyat.api.library.logger.EmbeddedLevel;
import net.reyadeyat.api.library.servlet.ServletData;

public class BinaryFileUploadServlet extends TransactionServlet {

    final private ServletData binary_file_upload_servlet_data;
    final private String temp_binary_file_upload_path;
    final private Map<String, Object> file_map = new ConcurrentHashMap<>();
    
    public BinaryFileUploadServlet() {
        super("Binary File Upload");
        temp_binary_file_upload_path = ApiEnvironment.getProperty("net.reyadeyat.api.library.binary.file.process.temp_binary_file_upload_path");
        File file = new File(temp_binary_file_upload_path);
        if (file.exists() == false) {
            file.mkdirs();
        }
        binary_file_upload_servlet_data = new ServletData("Binary File Upload Servlet");
    }

    protected void binary_file_upload(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        /*if (req.getParameter("xyz") != null) {
                    Logger.getLogger(this.getClass().getName()).log(EmbeddedLevel.TRACE, "binary file debugger\n" + debugger.toString());
        }*/
        String lang = "ar";
        String file_upload_key = null;
        Long file_length = null;
        Integer chunk_count = null;
        String file_key = null;
        String file_extension = null;
        String file_info = null;
        Integer chunk_index = null;
        Integer chunk_legth = null;
        String chunk_blob_base64 = null;
        String[] parameter_list = {"lang", "file_upload_key", "file_key", "file_extension", "file_info", "file_length", "chunk_count", "chunk_index", "chunk_legth", "chunk_blob_base64"};
        JsonArray error_list = new JsonArray();
        if (isValid(parameter_list, req, error_list)) {
            lang = req.getParameter("lang");
            file_upload_key = req.getParameter("file_upload_key");
            file_key = req.getParameter("file_key");
            file_extension = req.getParameter("file_extension");
            file_info = req.getParameter("file_info");
            file_length = Long.valueOf(req.getParameter("file_length"));
            chunk_count = Integer.valueOf(req.getParameter("chunk_count"));
            synchronized (file_map) {
                file_map.put(file_key, new Object());
            }
            chunk_index = Integer.valueOf(req.getParameter("chunk_index"));
            chunk_legth = Integer.valueOf(req.getParameter("chunk_legth"));
            chunk_blob_base64 = getUploadedFileString(req, "chunk_blob_base64");
        } else {
            JsonObject response = createJsonResponseObject(false, 400, 400, "errors", error_list);
            sendReponse(req, resp, response);
            return;
        }
        File temp_report_path_directory = new File(temp_binary_file_upload_path);
        if (temp_report_path_directory.exists() == false) {
            error_list.add("report path is null");
            JsonObject response = createJsonResponseObject(false, 400, 400, "errors", error_list);
            sendReponse(req, resp, response);
            return;
        }
        Object file_instant = file_map.get(file_key);
        synchronized (file_instant) {
            File binary_file = new File(temp_binary_file_upload_path + "/" + file_key + file_extension);
            if (temp_binary_file_upload_path.startsWith(".") == true) {
                binary_file = new File(binary_file.getAbsolutePath().replaceAll("\\.\\/", ""));
            }
            if (!Files.exists(binary_file.toPath())) {
                Files.createFile(binary_file.toPath());
                Logger.getLogger(this.getClass().getName()).log(EmbeddedLevel.TRACE, "Binary File Upload '"+binary_file.toPath().toString()+"' created successfully.");
            }
            try (FileChannel file = FileChannel.open(binary_file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
                ChunkedFile chunked_file;
                Base64.Decoder base64_decoder = Base64.getDecoder();
                if (file.size() == 0) {
                    chunked_file = new ChunkedFile(file, file_key, file_info, file_length, chunk_count);
                } else {
                    chunked_file = new ChunkedFile(file);
                }
                //debugger.append(file_key).append(",").append(chunk_count).append(",").append(chunk_index).append("\n");
                byte[] chunk_blob_bytes = base64_decoder.decode(chunk_blob_base64);
                if (chunk_legth != chunk_blob_bytes.length) {
                    throw new Exception("Chunk[" + chunk_index + "] => chunk_legth[" + chunk_legth + "] != chunk_blob_bytes_length[" + chunk_blob_bytes.length + "]");
                }
                chunked_file.appendChunk(chunk_index, chunk_blob_bytes);
                chunked_file.save();
                Logger.getLogger(this.getClass().getName()).log(EmbeddedLevel.TRACE, "committed binary file '" + file_key + "' chunk_index[" + chunk_index + "] => chunk_legth[" + chunk_legth + "]");
            } catch (Exception ex) {
                error_list.add("Error processing chunk for file '" + file_key + "' => " + ex.getMessage());
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error processing chunk for file '" + file_key + "' => " + ex.getMessage(), ex);
            }
        }
        if (error_list.size() == 0) {
            //con.commit();
            JsonObject response = createJsonResponseObject(true, 200, 200, "sucess");
            response.addProperty("INFO", "Exceution time");
            sendReponse(req, resp, response);
        } else {
            //con.rollback();
            error_list.add("rollback on errors");
            JsonObject response = createJsonResponseObject(false, 400, 400, "errors", error_list);
            sendReponse(req, resp, response);
        }
    }
}
