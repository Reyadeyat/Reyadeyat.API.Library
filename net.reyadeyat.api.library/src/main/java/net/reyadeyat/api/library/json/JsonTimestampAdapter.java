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
package net.reyadeyat.api.library.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 
 * Description
 * 
 *
 * @author Mohammad Nabil Mostafa
 * <a href="mailto:code@reyadeyat.net">code@reyadeyat.net</a>
 * 
 * @since 2023.01.01
 */
public class JsonTimestampAdapter extends TypeAdapter<Timestamp>{
    
    final static SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    @Override
    public void write(JsonWriter writer, Timestamp timestamp) throws IOException {
        if (timestamp == null) {
            writer.nullValue();
        } else {
            writer.value(stf.format(timestamp));
        }
    }

    @Override
    public Timestamp read(JsonReader reader) throws IOException {
        String timestamp_text = null;
        try {
            timestamp_text = reader.nextString();
            if (timestamp_text == null) {
                return null;
            }
            return new Timestamp(stf.parse(timestamp_text).getTime());
        } catch (Exception ex) {
            throw new RuntimeException("Exception: Parsing Json Date '"+timestamp_text+"'", ex);
        }
    }
    
}