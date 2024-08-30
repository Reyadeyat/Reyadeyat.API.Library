package net.reyadeyat.api.library.binary.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChunkedFileChunk {
    
    int chunk_index;
    long chunk_pointer;
    byte[] chunk_blob_bytes;
    
    public ChunkedFileChunk(
            FileChannel file,
            int chunk_index,
            long chunk_pointer,
            byte[] chunk_blob_bytes
    ) throws IOException {
        file.position(chunk_pointer);
        this.chunk_index = chunk_index;
        this.chunk_pointer = chunk_pointer;
        this.chunk_blob_bytes = chunk_blob_bytes;
        ByteBuffer buffer = ByteBuffer.allocate(this.chunk_blob_bytes.length);
        buffer.put(this.chunk_blob_bytes);
        buffer.flip();
        file.write(buffer);
    }

    public ChunkedFileChunk(
            FileChannel file,
            int chunk_index,
            long chunk_pointer,
            int chunk_length
    ) throws IOException {
        file.position(chunk_pointer);
        this.chunk_index = chunk_index;
        this.chunk_pointer = chunk_pointer;
        chunk_blob_bytes = new byte[chunk_length];
        ByteBuffer buffer = ByteBuffer.allocate(chunk_length);
        file.read(buffer);
        buffer.flip();
        buffer.get(chunk_blob_bytes);
    }
    
    public int getChunkIndex() {
        return chunk_index;
    }
    
    public long getChunkPointer() {
        return chunk_pointer;
    }
    
    public int getChunkLength() {
        return chunk_blob_bytes.length;
    }
    
    public byte[] getChunkBytes() {
        return chunk_blob_bytes;
    }
    
    public DataChunk getDataChunk() {
        return new DataChunk(chunk_index, chunk_blob_bytes);
    }
    
    @Override
    public String toString() {
        return ""
        + "\nchunk_index: " + chunk_index
        + "\nchunk_pointer: " + chunk_pointer
        + "\nchunk_length: " + chunk_blob_bytes.length;
    }
}
