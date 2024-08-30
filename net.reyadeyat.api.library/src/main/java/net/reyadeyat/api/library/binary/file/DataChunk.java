package net.reyadeyat.api.library.binary.file;

public class DataChunk {
    
    public int chunk_index;
    public byte[] chunk_blob_bytes;
    
    public DataChunk(
            int chunk_index,
            byte[] chunk_blob_bytes) {
        this.chunk_index = chunk_index;
        this.chunk_blob_bytes = chunk_blob_bytes;
    }
    
    public int getIndex() {
        return chunk_index;
    }
    
    public int length() {
        return chunk_blob_bytes.length;
    }
    
    @Override
    public String toString() {
        return ""
        + "\nchunk_index: " + chunk_index
        + "\nchunk_legth: " + chunk_blob_bytes.length;
    }
}
