package net.reyadeyat.api.library.binary.file;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChunkedFile implements Comparable<Object> {
    
    final private FileChannel file;
    final private ChunkedFileHeader chunked_file_header;
    final public Instant now = Instant.now();
    
    public ChunkedFile(FileChannel file) throws Exception {
        this.file = file;
        chunked_file_header = new ChunkedFileHeader(file);
    }
    
    public ChunkedFile(
            FileChannel file,
            String file_key,
            String file_info,
            long file_length,
            int chunk_count
    ) throws Exception {
        this.file = file;
        chunked_file_header = new ChunkedFileHeader(file, file_key, file_info, file_length, chunk_count);
    }
    
    public void appendChunk(int chunk_index, byte[] chunk_blob_bytes) throws Exception {
        long chunk_pointer = file.size();
        ChunkedFileChunk chunked_file_chunk = new ChunkedFileChunk(file, chunk_index, chunk_pointer, chunk_blob_bytes);
        chunked_file_header.updateHeader(file, chunked_file_chunk);
    }
    
    public DataChunk readChunk(int index) throws Exception {
        long chunk_pointer = this.chunked_file_header.getChunkPointer(index);
        int chunk_length = this.chunked_file_header.getChunkLength(index);
        if (chunk_pointer + chunk_length > file.size()) {
            throw new IOException("File '"+chunked_file_header.getFileKey()+"' chunk_end position '"+(chunk_pointer + chunk_length)+"' passes file size '"+file.size()+"'");
        }
        ChunkedFileChunk chunked_file_chunk = new ChunkedFileChunk(this.file, index, chunk_pointer, chunk_length);
        DataChunk data_chunk = chunked_file_chunk.getDataChunk();
        return data_chunk;
    }
    
    public Boolean isCompleteFile() {
        return chunked_file_header.isFileLengthComplete() && chunked_file_header.isChucnkCountComplete();
    }
    
    public long getFileLength() {
        return chunked_file_header.getFileLength();
    }
    
    public int getChunkCount() {
        return chunked_file_header.getChunkCount();
    }
    
    public String getFileKey() {
        return chunked_file_header.getFileKey();
    }
    
    public String getFileInfo() {
        return chunked_file_header.getFileInfo();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof ChunkedFile) {
            ChunkedFile this2 = (ChunkedFile) object;
            return this2.chunked_file_header.getFileKey().equals(chunked_file_header.getFileKey());
        }
        return chunked_file_header.getFileKey().equals((String) object);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.file);
        hash = 41 * hash + Objects.hashCode(chunked_file_header);
        return hash;
    }

    @Override
    public int compareTo(Object object) {
        if (equals(object)) {
            return 0;
        }
        if (object instanceof ChunkedFile) {
            ChunkedFile chunked_file = (ChunkedFile) object;
            return chunked_file_header.getFileKey().compareTo(chunked_file.chunked_file_header.getFileKey());
        }
        return chunked_file_header.getFileKey().compareTo((String) object);
    }
    
    public void printFileStructure() throws Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\nprintFileStructure() - start");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\nchunked_file_header:\n"+chunked_file_header);
        if (isCompleteFile() == false) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "chunked_file.isCompleteFile() == false");
            return;
        }
        //StringBuilder raw_data = new StringBuilder(getFileLength());
        //Base64.Decoder decoderBase64 = Base64.getDecoder();
        Base64.Decoder decoderBase64 = Base64.getDecoder();
        for (int index = 0; index < getChunkCount(); index++) {
            DataChunk data_chunk = readChunk(index);
            //raw_data.setLength(data_chunk.chunk_end > raw_data.length() ? data_chunk.chunk_end : raw_data.length());
            //String string = new String(decoderBase64.decode(data_chunk.chunk_blob_base64), StandardCharsets.UTF_8);
            //raw_data.replace(data_chunk.chunk_start, data_chunk.chunk_end, string);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\nDataChunk :\n"+data_chunk);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\nprintFileStructure() - end.");
    }
    
    public void save() throws Exception {
        file.force(true);
    }
    
    public String toString(Boolean extend) {
        return chunked_file_header.toString(extend);
    }
    
    @Override
    public String toString() {
        return chunked_file_header.toString();
    }
}
