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
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

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
public class JsonStringAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.value(value);
    }

    @Override
    public String read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        if (token == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        StringBuilder b = new StringBuilder();
        if (token == JsonToken.BEGIN_OBJECT) {
            in.beginObject();
            b.append("{");
            do {
                token = in.peek();
                if (token == JsonToken.NAME) {
                    b.append("\"");
                    b.append(in.nextName());
                    b.append("\":");
                } else if (token == JsonToken.STRING) {
                    b.append("\"");
                    b.append(in.nextString());
                    b.append("\",");
                } else if (token == JsonToken.BOOLEAN) {
                    b.append(in.nextBoolean());
                } else if (token == JsonToken.NULL) {
                    b.append("null,");
                } else if (token == JsonToken.NUMBER) {
                    b.append(in.nextString());
                    b.append(",");
                }
            } while(token != JsonToken.END_OBJECT);
            in.endObject();
            b.deleteCharAt(b.length()-1);
            b.append("}");
        }
        return b.toString();
    }
}