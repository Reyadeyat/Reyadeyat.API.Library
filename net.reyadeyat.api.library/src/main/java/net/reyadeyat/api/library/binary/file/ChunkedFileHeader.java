package net.reyadeyat.api.library.binary.file;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class ChunkedFileHeader {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
    long file_length;
    long current_file_length;
    int chunk_count;
    int current_chunk_count;
    int file_key_bytes_size;
    int file_info_bytes_size;
    byte[] chunk_header;//[chunk_count][chunk_pointer][cuhnk_length]
    String file_key;
    String file_info;
    private long createion_timestamp, modification_timestamp;
    private transient byte timestamp[];
    int header_size;
    static final private DecimalFormat df = new DecimalFormat("#,##0.00");

    public ChunkedFileHeader(
            FileChannel file,
            String file_key,
            String file_info,
            long file_length,
            int chunk_count
    ) throws Exception {
        this.current_file_length = 0;
        this.chunk_count = chunk_count;
        this.current_chunk_count = 0;
        this.file_key = file_key;
        byte[] file_key_bytes = file_key.getBytes(StandardCharsets.UTF_8);
        file_key_bytes_size = file_key_bytes.length;
        this.file_info = file_info;
        byte[] file_info_bytes = file_info.getBytes(StandardCharsets.UTF_8);
        file_info_bytes_size = file_info_bytes.length;
        chunk_header = new byte[this.chunk_count*(Long.BYTES+Integer.BYTES)];//pointer,length
        timestamp = new byte[Long.BYTES*4];
        createion_timestamp = Instant.now().toEpochMilli();
        modification_timestamp = Instant.now().toEpochMilli();
        MemoryUtils.setLongAt(timestamp, createion_timestamp, Long.BYTES);
        MemoryUtils.setLongAt(timestamp, modification_timestamp, Long.BYTES*3);
        header_size = (2*Long.BYTES)+(4*Integer.BYTES)+(Long.BYTES*4)+(chunk_count*(Long.BYTES+Integer.BYTES))+file_key_bytes_size+file_info_bytes_size;
        ByteBuffer buffer = ByteBuffer.allocate(header_size);
        this.file_length = file_length + header_size;
        buffer.putLong(file_length);
        buffer.putLong(current_file_length);
        buffer.putInt(chunk_count);
        buffer.putInt(current_chunk_count);
        buffer.putInt(file_key_bytes_size);
        buffer.putInt(file_info_bytes_size);
        buffer.put(timestamp);
        buffer.put(chunk_header);
        buffer.put(file_key_bytes);
        buffer.put(file_info_bytes);
        buffer.flip();
        file.write(buffer);
    }

    public ChunkedFileHeader(
            FileChannel file
    ) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate((2*Long.BYTES)+(4*Integer.BYTES)+(Long.BYTES*4));
        file.read(buffer);
        buffer.flip();
        file_length = buffer.getLong();
        current_file_length = buffer.getLong();
        chunk_count = buffer.getInt();
        current_chunk_count = buffer.getInt();
        file_key_bytes_size = buffer.getInt();
        file_info_bytes_size = buffer.getInt();
        timestamp = new byte[Long.BYTES*4];
        buffer.get(timestamp);
        createion_timestamp = MemoryUtils.getLongAt(timestamp, Long.BYTES);
        modification_timestamp = MemoryUtils.getLongAt(timestamp, Long.BYTES*3);
        chunk_header = new byte[chunk_count*(Long.BYTES+Integer.BYTES)];
        header_size = (2*Long.BYTES)+(4*Integer.BYTES)+(Long.BYTES*4)+(chunk_count*(Long.BYTES+Integer.BYTES))+file_key_bytes_size+file_info_bytes_size;
        buffer = ByteBuffer.allocate(chunk_header.length);
        file.read(buffer);
        buffer.flip();
        buffer.get(chunk_header);
        byte[] file_key_bytes = new byte[file_key_bytes_size];
        buffer = ByteBuffer.allocate(file_key_bytes_size);
        file.read(buffer);
        buffer.flip();
        buffer.get(file_key_bytes);
        this.file_key = new String(file_key_bytes, StandardCharsets.UTF_8);
        byte[] file_info_bytes = new byte[file_info_bytes_size];
        buffer = ByteBuffer.allocate(file_info_bytes_size);
        file.read(buffer);
        buffer.flip();
        buffer.get(file_info_bytes);
        this.file_info = new String(file_info_bytes, StandardCharsets.UTF_8);
    }
    
    public int getHeaderSize() {
        return header_size;
    }
    
    public Boolean isFileLengthComplete() {
        return this.file_length == this.current_file_length;
    }
    
    public Boolean isChucnkCountComplete() {
        return this.chunk_count == this.current_chunk_count;
    }
    
    public void updateHeader(FileChannel file, ChunkedFileChunk chunked_file_chunk) throws Exception {
        //update current_chunk_count
        file.position((2*Long.BYTES)+(1*Integer.BYTES));
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(++current_chunk_count);
        buffer.flip();
        file.write(buffer);
        //update modification_timestamp
        file.position((2*Long.BYTES)+(4*Integer.BYTES));
        modification_timestamp = Instant.now().toEpochMilli();
        MemoryUtils.setLongAt(timestamp, modification_timestamp, Long.BYTES*3);
        buffer = ByteBuffer.allocate(Long.BYTES*4);
        buffer.put(timestamp);
        buffer.flip();
        file.write(buffer);
        //update chunk_header
        file.position((2*Long.BYTES)+(4*Integer.BYTES)+(Long.BYTES*4));
        setChunkPointer(chunked_file_chunk.chunk_index, chunked_file_chunk.getChunkPointer());
        setChunkLength(chunked_file_chunk.chunk_index, chunked_file_chunk.getChunkLength());
        buffer = ByteBuffer.allocate(chunk_header.length);
        buffer.put(chunk_header);
        buffer.flip();
        file.write(buffer);
        //update current_file_length
        current_file_length = file.size() - getHeaderSize();
        file.position(1*Long.BYTES);
        buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(current_file_length);
        buffer.flip();
        file.write(buffer);
        //Logger.getLogger(this.getClass().getName()).log(EmbeddedLevel.TRACE, "binary file '" + file_key + "' chunk_index["+chunked_file_chunk.chunk_index+"] [chunk_pointer, chunk_length] received ["+chunked_file_chunk.getChunkPointer()+", "+chunked_file_chunk.getChunkLength()+"] physicaly stored ["+getChunkPointer(chunked_file_chunk.chunk_index)+", "+getChunkLength(chunked_file_chunk.chunk_index)+"]");
    }
    
    public long getFileLength() {
        return file_length;
    }
    
    public int getChunkCount() {
        return chunk_count;
    }
    
    public String getFileKey() {
        return file_key;
    }
    
    public String getFileInfo() {
        return file_info;
    }
    
    private void setChunkPointer(int index, long pointer) throws Exception {
        index = index*(Long.BYTES+Integer.BYTES);
        MemoryUtils.setLongAt(chunk_header, pointer, index);
    }
    
    private void setChunkLength(int index, int length) throws Exception {
        index = index*(Long.BYTES+Integer.BYTES)+Long.BYTES;
        MemoryUtils.setIntegerAt(chunk_header, length, index);
    }
    
    public long getChunkPointer(int index) throws Exception {
        index = (index*(Long.BYTES+Integer.BYTES));
        return MemoryUtils.getLongAt(chunk_header, index);
    }
    
    public int getChunkLength(int index) throws Exception {
        index = (index*(Long.BYTES+Integer.BYTES))+Long.BYTES;
        return MemoryUtils.getIntegerAt(chunk_header, index);
    }
    
    public String toString(Boolean extend) {
        try {
            String chunk_missing = null;
            if (extend == true) {
                StringBuilder string = new StringBuilder();
                int length = chunk_header.length / (Long.BYTES+Integer.BYTES);
                string.append("..... Start Header Dump .....\n");
                for (int i = 0; i < length; i++) {
                    long chunk_pointer = getChunkPointer(i);
                    int chunk_length = getChunkLength(i);
                    if (chunk_pointer == 0l || chunk_length == 0) {
                        string.append(i).append("[").append(chunk_pointer).append(",").append(chunk_length).append("], ..... error .....\n");
                    } else {
                        string.append(i).append("[").append(chunk_pointer).append(",").append(chunk_length).append("],\n");
                    }
                }
                string.append("..... End Header Dump .....\n");
                chunk_missing = string.toString();
                //chunk_missing = string.length() > 0 ? string.deleteCharAt(string.length()-1).toString() : null;
            }
            Boolean is_complete = isFileLengthComplete() && isChucnkCountComplete();
            return new StringBuilder()
            .append("Stats: File is ").append(is_complete == true ? "complete" : "not complete")
            .append("\nCreated: ").append(sdf.format(createion_timestamp)).append(" - Modified: ").append(sdf.format(modification_timestamp))
            .append("\nfile_length: ").append(file_length)
            .append("\ncurrent_file_length: ").append(current_file_length)
            .append("\nchunk_count: ").append(chunk_count)
            .append("\ncurrent_chunk_count: ").append(current_chunk_count)
            .append("\nfile_key_bytes_size: ").append(file_key_bytes_size)
            .append("\nfile_key: ").append(file_key)
            .append("\nfile_info_bytes_size: ").append(file_info_bytes_size)
            .append("\nfile_info: ").append(file_info)
            .append(is_complete == true ? "" : ("\nRemaining chunk count ["+(chunk_count - current_chunk_count)+"]\n"+(chunk_missing == null ? "" : chunk_missing+"\n")+"Remaining File length ["+df.format((file_length - current_file_length)/1024/1024/1024)+"]GB ["+df.format(((file_length - current_file_length)/1024/1024)%1024)+"]MB ["+df.format(((file_length - current_file_length)/1024)%1024*1024)+"]KB ["+df.format(((file_length - current_file_length))%(1024*1024*1024))+"]B"))
                    .toString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    
    @Override
    public String toString() {
        return toString(false);
    }
}
