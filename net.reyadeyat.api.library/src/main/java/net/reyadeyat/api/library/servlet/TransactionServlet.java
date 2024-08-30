package net.reyadeyat.api.library.servlet;

import net.reyadeyat.api.library.json.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.TreeMap;

abstract public class TransactionServlet {
    
    protected final TreeMap<String, TreeMap<String, String>> i18n = new TreeMap<String, TreeMap<String, String>>();
    protected String server_name;
    protected boolean successfully_initialized;
    protected Throwable initialization_exception;
    
    public TransactionServlet(String server_name) {
        this.successfully_initialized = true;
        try {
            this.server_name = server_name;
            i18n.put("ar", new TreeMap<>());
            i18n.put("en", new TreeMap<>());
            this.initI18N();
        } catch (Exception ex) {
            this.successfully_initialized = false;
            this.initialization_exception = ex;
        }
    }
    
    protected void initI18N() {
        
    }
    
    protected boolean isValid(String[] parameter_list, HttpServletRequest req, JsonArray error_list) throws Exception {
        Boolean test = true;
        for (String parameter : parameter_list) {
            String paramerer_value = req.getParameter(parameter);
            if (paramerer_value == null) {
                Part part_value = req.getPart(parameter);
                if (part_value == null) {
                    error_list.add("parameter '"+parameter+"' is null");
                    test = false;
                }
            }
        }
        return test;
    }
    
    protected JsonObject createJsonResponseObject(Boolean success, Integer http_response_code, Integer service_response_code, String message) {
        return createJsonResponseObject(success, http_response_code, service_response_code, message, null);
    }
    
    protected JsonObject createJsonResponseObject(Boolean success, Integer http_response_code, Integer service_response_code, String message, JsonArray error_list) {
        JsonObject response = new JsonObject();
        response.addProperty("status", (success == true ? "success" : "error"));
        response.addProperty("http_response_code", http_response_code);
        response.addProperty("service_response_code", service_response_code != 0 ? service_response_code : http_response_code);
        response.addProperty("message", message);
        if (error_list != null) {
            response.add("error_list", error_list);
        }
        return response;
    }
    
    protected void sendReponse(HttpServletRequest req, HttpServletResponse resp, JsonObject response) throws Exception {
        Integer http_response_code = response.get("http_response_code").getAsInt();
        resp.setStatus(http_response_code);
        Writer writer = resp.getWriter();
        Gson gson = JsonUtil.gson();
        gson.toJson(response, writer);
    }
    
    //abstract public void doTransaction(ServletData servlet_data, HttpServletRequest req, HttpServletResponse resp) throws Exception;
    
    protected void saveUploadedFile(HttpServletRequest req, String file_parameter, String temp_file_path, String temp_file_name, StringBuilder uploadPath) throws Exception {
        String savePath = new StringBuilder(temp_file_path).append(temp_file_name).toString();
        Part file_part = req.getPart(file_parameter); // Retrieves <input type="file" name="file_parameter">
        String fileName = Paths.get(file_part.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        uploadPath.append("/").append(fileName);
        Files.copy(Paths.get(uploadPath.toString()), Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
    }

    protected void uploadBinaryFile(HttpServletRequest resp, Part file_part, String save_file_path) throws Exception {
        int read = 0;
        final byte[] bytes = new byte[1024];
        try (InputStream fileInputStream = file_part.getInputStream()) {
            try (BufferedOutputStream fbos = new BufferedOutputStream(new FileOutputStream(save_file_path))) {
                while ((read = fileInputStream.read(bytes)) != -1) {
                    fbos.write(bytes, 0, read);
                }
                fbos.close();
            } catch (Exception x) {
                throw x;
            }
        } catch (Exception x) {
            throw x;
        }
    }

    protected File getUploadedFile(HttpServletRequest req, String file_parameter, StringBuilder uploadPath) throws Exception {
        Part file_part = req.getPart(file_parameter); // Retrieves <input type="file" name="file_parameter">
        String fileName = Paths.get(file_part.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        uploadPath.append("/").append(fileName);
        return new File(uploadPath.toString());
    }

    protected Part getUploadedFilePart(HttpServletRequest req, String file_parameter) throws Exception {
        Part file_part = req.getPart(file_parameter); // Retrieves <input type="file" name="file_parameter">
        if (file_part == null) {
            return null;
        }
        return file_part;
    }
    
    protected InputStream getUploadedFilePartInputStream(HttpServletRequest req, String file_parameter_name) throws Exception {
        Part file_part = req.getPart(file_parameter_name); // Retrieves <input type="file" name="file_parameter">
        if (file_part == null) {
            return null;
        }
        return file_part.getInputStream();
    }
    
    protected byte[] getUploadedFileBuffer(HttpServletRequest req, String file_parameter) throws Exception {
        Part file_part = req.getPart(file_parameter); // Retrieves <input type="file" name="file_parameter">
        if (file_part == null) {
            return null;
        }
        int read = 0;
        byte[] bytes = null;
        try (InputStream ins = file_part.getInputStream()) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024*1024)) {
                bytes = new byte[1024];
                while ((read = ins.read(bytes)) != -1) {
                    baos.write(bytes, 0, read);
                }
                bytes = baos.toByteArray();
            } catch (Exception x) {
                throw x;
            }
        } catch (Exception x) {
            throw x;
        }
        return bytes;
    }
    
    protected String getUploadedFileString(HttpServletRequest req, String file_parameter) throws Exception {
        return new String(getUploadedFileBuffer(req, file_parameter), StandardCharsets.UTF_8);
    }
    
    protected ArrayList<Part> getUploadedPartFileList(HttpServletRequest req, String file_parameter) throws Exception {
        ArrayList<Part> file_part_list = new ArrayList<>();
        file_part_list.addAll(req.getParts());
        for (int i = file_part_list.size()-1; i >= 0; i--) {
            Part part = file_part_list.get(i);
            if (part.getName().equalsIgnoreCase(file_parameter) == false) {
                file_part_list.remove(i);
            }
        }
        return file_part_list;
    }

    protected void downloadTextFile(HttpServletResponse resp, String fileName, String fileContentType, String file_content) throws Exception {
        byte[] file_content_bytes = file_content.getBytes(StandardCharsets.UTF_8);
        resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setContentType(fileContentType);
        resp.setContentLength(file_content_bytes.length);
        OutputStream outStream = resp.getOutputStream();
        outStream.write(file_content_bytes);
    }

    protected void downloadBinaryFile(HttpServletResponse resp, String fileName, String fileContentType, ByteArrayOutputStream file_content) throws Exception {
        byte[] file_content_bytes = file_content.toByteArray();
        resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setContentType(fileContentType);
        resp.setContentLength(file_content_bytes.length);
        OutputStream outStream = resp.getOutputStream();
        outStream.write(file_content_bytes);
    }
    
    protected void downloadBinaryFileInline(HttpServletResponse resp, String fileName, String fileContentType, File download_file) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, 1024, false, "inline");
    }

    protected void downloadBinaryFileAttachment(HttpServletResponse resp, String fileName, String fileContentType, File download_file) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, 1024, false, "attachment");
    }

    protected void downloadBinaryFile(HttpServletResponse resp, String fileName, String fileContentType, File download_file, Integer buffer_size, Boolean stream, String download_type) throws Exception {
        if (stream == false) {
            resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            resp.setHeader("Content-Disposition", download_type+"; filename=\"" + fileName + "\";");
        }
        resp.setContentType(fileContentType);
        resp.setContentLengthLong(download_file.length());
        int read = 0;
        final byte[] bytes = new byte[buffer_size];
        try (InputStream fileInputStream = new BufferedInputStream(new FileInputStream(download_file))) {
            try (BufferedOutputStream fbos = new BufferedOutputStream(resp.getOutputStream())) {
                while ((read = fileInputStream.read(bytes)) != -1) {
                    fbos.write(bytes, 0, read);
                }
                fbos.close();
            } catch (Exception x) {
                throw x;
            }
        } catch (Exception x) {
            throw x;
        }
    }
    
    protected String readSFileIntoString(File file) throws Exception {
        return Files.readString(file.toPath());
    }
    
    protected String encodeFileIntoBase64(File file) throws Exception {
        byte[] file_content = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(file_content);
    }
    
    protected void streamBinaryFileInline(HttpServletResponse resp, String fileName, String fileContentType, File download_file) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, 1024, true, "inline");
    }
    
    protected void streamBinaryFileAttachment(HttpServletResponse resp, String fileName, String fileContentType, File download_file) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, 1024, true, "attachment");
    }
    
    protected void streamBinaryFileInline(HttpServletResponse resp, String fileName, String fileContentType, File download_file, Integer buffer_size) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, buffer_size, true, "inline");
    }

    protected void streamBinaryFileAttachment(HttpServletResponse resp, String fileName, String fileContentType, File download_file, Integer buffer_size) throws Exception {
        downloadBinaryFile(resp, fileName, fileContentType, download_file, buffer_size, true, "attachment");
    }
    
    protected String readFileUploadContent(HttpServletRequest req, String filePartName, StringBuilder file_content) throws Exception {
        return readFileUploadContent(req, filePartName, file_content, 1024);
    }

    protected String readFileUploadContent(HttpServletRequest req, String filePartName, StringBuilder file_content, Integer buffer_size) throws Exception {

        Part file_part = req.getPart(filePartName);
        String fileName = file_part.getSubmittedFileName();
        InputStream fileInputStream = file_part.getInputStream();
        int read = 0;
        final byte[] bytes = new byte[buffer_size];
        while ((read = fileInputStream.read(bytes)) != -1) {
            file_content.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
        }
        return fileName;
    }
}
