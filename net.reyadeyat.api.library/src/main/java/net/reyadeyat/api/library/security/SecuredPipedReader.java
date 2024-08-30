/*
 * Copyright (C) 2023 Reyadeyat
 *
 * Reyadeyat/RELATIONAL.API is licensed under the
 * BSD 3-Clause "New" or "Revised" License
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://reyadeyat.net/LICENSE/RELATIONAL.API.LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reyadeyat.api.library.security;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 *
 * @author Mohammad Nabil Mostafa
 * <a href="mailto:code@mnabil.net">code@mnabil.net</a>
 */
public class SecuredPipedReader extends Reader {
    
    private Reader reader;
    private Writer piped_writer;
    private Security secutiry;
    private String separator;
    private StringBuilder buffer;
    
    /**Reader only*/
    public SecuredPipedReader(Reader reader, Security secutiry, String separator) {
        this(reader, null, secutiry, separator);
    }
    
    /**Piped Reader to Writer*/
    public SecuredPipedReader(Writer writer, Security secutiry, String separator) {
        this(null, writer, secutiry, separator);
    }
    
    /**reader and Piped Reader to Writer*/
    public SecuredPipedReader(Reader reader, Writer piped_writer, Security secutiry, String separator) {
        this.reader = reader;
        this.piped_writer = piped_writer;
        this.secutiry = secutiry;
        this.separator = separator;
        this.buffer = new StringBuilder();
    }

    @Override
    public int read(char[] encypted_buffer, int offset, int length) throws IOException {
        try {
            buffer.append(encypted_buffer, offset, length);
            int index = -1;
            while((index = buffer.indexOf(separator)) > -1) {
                String encrypted_text = buffer.substring(0, index);
                buffer.delete(0, index+separator.length());
                char[] plain_buffer = this.secutiry.decrypt_text(encrypted_text).toCharArray();
                if (this.piped_writer != null) {
                    this.piped_writer.write(plain_buffer, 0, plain_buffer.length);
                    return plain_buffer.length;
                }
                if (reader != null) {
                    return reader.read(plain_buffer, 0, plain_buffer.length);
                }
            }
            return 0;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
    
    public void flush() throws IOException {
        if (this.piped_writer != null) {
            this.piped_writer.flush();
        }
    }
}
