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
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

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
public class JsonDirectElementWriter {
    public JsonWriter writer;
    public Gson gson;
    
    public JsonDirectElementWriter(Gson gson, JsonWriter writer) {
        this.writer = writer;
        this.gson = gson;
    }
    
    public void writeJsonElement(JsonElement json_element) throws Exception {
        gson.toJson(json_element, writer);
    }
    
    public void mergeJsonPrimitive(JsonPrimitive json_primitive) throws Exception {
        mergeJsonPrimitive(null, json_primitive);
    }
    
    public void mergeJsonPrimitive(String name, JsonPrimitive json_primitive) throws Exception {
        if (name != null) {
            name(name);
        }
        if (json_primitive.isBoolean()) {
            writer.value(json_primitive.getAsBoolean());
        } else if (json_primitive.isString()) {
            writer.value(json_primitive.getAsString());
        } else if (json_primitive.isNumber()) {
            writer.value(json_primitive.getAsNumber());
        }
    }
    
    public void writeJsonElement(String name, JsonElement json_element) throws Exception {
        name(name);
        gson.toJson(json_element, writer);
    }
    
    public void beginObject() throws Exception {
        writer.beginObject();
    }
    
    public void name(String name) throws Exception {
        writer.name(name);
    }
    
    public void beginArray() throws Exception {
        writer.beginArray();
    }
    
    public void endArray() throws Exception {
        writer.endArray();
    }
    
    public void endObject() throws Exception {
        writer.endObject();
    }
    
    public void nullValue() throws Exception {
        nullValue(null);
    }
    
    public void nullValue(String name) throws Exception {
        if (name != null) {
            name(name);
        }
        writer.nullValue();
    }
    
    public void value(Boolean value) throws Exception {
        writer.value(value);
    }
    
    public void value(Number value) throws Exception {
        writer.value(value);
    }
    
    public void value(String value) throws Exception {
        writer.value(value);
    }
    
    public void value(boolean value) throws Exception {
        writer.value(value);
    }
    
    public void value(double value) throws Exception {
        writer.value(value);
    }
    
    public void value(float value) throws Exception {
        writer.value(value);
    }
    
    public void value(long value) throws Exception {
        writer.value(value);
    }
    
    public void flush() throws Exception {
        writer.flush();
    }
}

