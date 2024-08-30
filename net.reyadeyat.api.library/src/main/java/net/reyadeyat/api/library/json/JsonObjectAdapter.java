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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
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
public class JsonObjectAdapter extends TypeAdapter<JsonObject> {

    @Override
    public JsonObject read(JsonReader json_reader) throws IOException {
        Gson gson = JsonUtil.gson();
        try {
            JsonObject json_object = gson.fromJson(json_reader.nextString(), JsonObject.class);
            JsonUtil.reclaimGson(gson);
            return json_object;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    @Override
    public void write(JsonWriter json_writer, JsonObject json_object) throws IOException {
        Gson gson = JsonUtil.gson();
        try {
            gson.toJson(json_object, json_writer);
            JsonUtil.reclaimGson(gson);
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
}
