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
import java.sql.Date;
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
public class JsonDateAdapter extends TypeAdapter<Date>{
    
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public void write(JsonWriter writer, Date date) throws IOException {
        if (date == null) {
            writer.nullValue();
        } else {
            writer.value(sdf.format(date));
        }
    }

    @Override
    public Date read(JsonReader reader) throws IOException {
        String date_text = null;
        try {
            date_text = reader.nextString();
            if (date_text == null) {
                return null;
            }
            return new Date(sdf.parse(date_text).getTime());
        } catch (Exception ex) {
            throw new RuntimeException("Exception: Parsing Json Date '"+date_text+"'", ex);
        }
    }
    
}
