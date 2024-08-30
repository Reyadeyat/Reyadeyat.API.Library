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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohammad Nabil Mostafa
 * <a href="mailto:code@mnabil.net">code@mnabil.net</a>
 */
public class SecuredWriter extends Writer {
    
    private Writer writer;
    private Security secutiry;
    private String separator;
    private Logger logger;
    private Level log_level;
    
    public SecuredWriter(Writer writer, Security secutiry, String separator) throws Exception {
        this(writer, secutiry, separator, null, null);
    }
    
    public SecuredWriter(Writer writer, Security secutiry, String separator, Logger logger, Level log_level) throws Exception {
        this.writer = writer;
        this.secutiry = secutiry;
        this.separator = separator;
        this.logger = logger;
        this.log_level = log_level;
        if (this.writer == null) {
            throw new Exception("writer is null");
        }
    }

    @Override
    public void write(char[] plain_buffer, int offset, int length) throws IOException {
        try {
            String encrypted_message = new String(plain_buffer, offset, length);
            char[] encrypted_buffer = (this.secutiry.encrypt_text(encrypted_message)+separator).toCharArray();
            writer.write(encrypted_buffer, 0, encrypted_buffer.length);
            if (logger != null) {
                logger.log(log_level, encrypted_message);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
    
    
}
