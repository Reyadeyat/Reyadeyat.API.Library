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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import net.reyadeyat.api.library.util.BooleanParser;

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
public class JsonResultset {
 
    static public JsonObject resultsetJson(String[] field_list, ResultSet resultset, JsonObject json) throws Exception {
        JsonArray json_resultset = resultset(resultset);
        if (json == null) {
            json = new JsonObject();
        }
        JsonElement[] fields_group = new JsonElement[field_list.length];
        JsonElement[] fields_group_parent = new JsonElement[field_list.length];
        for (int c = 0; c < fields_group_parent.length; c++) {
            fields_group[c] = new JsonObject();
        }
        JsonArray leaf = null;
        for (int c = 0; c < json_resultset.size(); c++) {
            JsonObject record = json_resultset.get(c).getAsJsonObject();
            for (int f = 0; f < field_list.length; f++) {
                if (record.get(field_list[f]).equals(fields_group[f]) == false) {
                    fields_group[f] = record.get(field_list[f]);
                    if (f < field_list.length-1) {
                        fields_group_parent[f] = new JsonObject();
                    } else {
                        leaf = new JsonArray();
                        fields_group_parent[f] = leaf;
                    }
                    if (f > 0) {
                        ((JsonObject)fields_group_parent[f-1]).add(record.get(field_list[f]).getAsString(), fields_group_parent[f]);
                    } else if (f == 0) {
                        json.add(record.get(field_list[f]).getAsString(), fields_group_parent[f]);
                    }
                }
            }
            leaf.add(record);
        }
        
        return json;
    }
    
    static public <T> void resultset(ResultSet resultset, Class<T> java_class, ArrayList<T> t_list) throws Exception {
        JsonArray json_resultset = resultset(resultset);
        Gson gson = new GsonBuilder()/*.setPrettyPrinting()*/.excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        for (int c = 0; c < json_resultset.size(); c++) {
            T record = gson.fromJson(json_resultset.get(c), java_class);
            t_list.add(record);
        }
    }
    
    static public <T> ArrayList<T> resultset(ResultSet resultset, Class<T> java_class) throws Exception {
        JsonArray json_resultset = resultset(resultset);
        ArrayList<T> t = new ArrayList<T>(json_resultset.size());
        Gson gson = new GsonBuilder()/*.setPrettyPrinting()*/.excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        for (int c = 0; c < json_resultset.size(); c++) {
            T record = gson.fromJson(json_resultset.get(c), java_class);
            t.add(record);
        }
        return t;
    }
    
    static public <K, V> Map<K, V> resultsetMap(Map<K, V> map, String key, ResultSet resultset, Class<V> java_class) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            ResultSetMetaData resultset_metadata = resultset.getMetaData();
            while (resultset.next()) {
                JsonObject record = new JsonObject();
                @SuppressWarnings("unchecked")
                K key_value = (K) resultset.getObject(key);
                for (int c = 1; c <= resultset_metadata.getColumnCount(); c++) {
                    int field_type = resultset_metadata.getColumnType(c);
                    String field_label= resultset_metadata.getColumnLabel(c);
                    Object field_value = resultset.getObject(c);
                    addRecordField(field_type, field_label, field_value, record);
                    
                }
                V java_object = JsonUtil.jsonElementToObject(record, java_class);
                map.put(key_value, java_object);
            }
            JsonUtil.reclaimGson(gson);
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
        return map;
    }
    
    static public JsonArray resultset(ResultSet resultset) throws Exception {
        JsonArray json_resultset = new JsonArray();
        ResultSetMetaData resultset_metadata = resultset.getMetaData();
        int count = resultset_metadata.getColumnCount();
        while (resultset.next()) {
            JsonObject record = new JsonObject();
            json_resultset.add(record);
            for (int c = 1; c <= count; c++) {
                int field_type = resultset_metadata.getColumnType(c);
                String field_label= resultset_metadata.getColumnLabel(c);
                Object field_value = resultset.getObject(c);
                addRecordField(field_type, field_label, field_value, record);
            }
        }
        return json_resultset;
    }
    
    public static void jsonElementStreaming(JsonElement json_element, JsonWriter json_writer) throws Exception {
        JsonResultset.jsonElementStreaming(null, json_element, json_writer);
    }
    
    public static void jsonElementStreaming(String json_property_name, JsonElement json_element, JsonWriter json_writer) throws Exception {
        if (json_element.isJsonObject()) {
            JsonObject json_object = json_element.getAsJsonObject();
            if (json_property_name != null) {
                json_writer.name(json_property_name);
            }
            json_writer.beginObject();
            Set<Map.Entry<String, JsonElement>> entrySet = json_object.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                String json_element_name = entry.getKey();
                JsonElement json_element_value = entry.getValue();
                if (json_element_value.isJsonNull()) {
                    json_writer.name(json_element_name).value((String)null);
                } else if (json_element_value.isJsonPrimitive()) {
                    JsonPrimitive json_primitive = json_element_value.getAsJsonPrimitive();
                    if (json_primitive.isString()) {
                        json_writer.name(json_element_name).value(json_primitive.getAsString());
                    } else if (json_primitive.isNumber()) {
                        json_writer.name(json_element_name).value(json_primitive.getAsNumber());
                    } else if (json_primitive.isBoolean()) {
                        json_writer.name(json_element_name).value(json_primitive.getAsBoolean());
                    }
                } else if (json_element_value.isJsonArray() || json_element_value.isJsonObject()) {
                    jsonElementStreaming(json_element_name, json_element_value, json_writer);
                } else {
                    throw new Exception("jsonObjectStreaming undefined Json structure");
                }
            }
            json_writer.endObject();
        } else if (json_element.isJsonArray()) {
            JsonArray json_array = json_element.getAsJsonArray();
            if (json_property_name == null) {
                json_writer.name(json_property_name);
            }
            json_writer.beginArray();
            for(int i = 0; i < json_array.size(); i++) {
                JsonElement json_element_value = json_array.get(i);
                if (json_element_value.isJsonNull()) {
                    json_writer.value((String)null);
                } else if (json_element_value.isJsonPrimitive()) {
                    JsonPrimitive json_primitive = json_element_value.getAsJsonPrimitive();
                    if (json_primitive.isString()) {
                        json_writer.value(json_primitive.getAsString());
                    } else if (json_primitive.isNumber()) {
                        json_writer.value(json_primitive.getAsNumber());
                    } else if (json_primitive.isBoolean()) {
                        json_writer.value(json_primitive.getAsBoolean());
                    }
                } else if (json_element_value.isJsonArray() || json_element_value.isJsonObject()) {
                    jsonElementStreaming(json_element_value, json_writer);
                } else {
                    throw new Exception("jsonObjectStreaming undefined Json structure");
                }
            }
            json_writer.endArray();
        } else {
            throw new Exception("jsonObjectStreaming undefined Json seymantic");
        }
    }
    
    public static void resultsetStreaming(JsonObject response, Writer writer, ResultSet jdbc_resultset) throws Exception {
        Gson gson = JsonUtil.gson();
        try (JsonWriter json_writer = new JsonWriter(writer)) {
            //{
            json_writer.beginObject();
            //"response": {}
            JsonResultset.jsonElementStreaming("response", response, json_writer);
            //"resultset": []
            json_writer.name("resultset");
            json_writer.beginArray();
            ResultSetMetaData resultset_metadata = jdbc_resultset.getMetaData();
            int count = resultset_metadata.getColumnCount();
            while (jdbc_resultset.next()) {
                JsonObject record = new JsonObject();
                for (int c = 1; c <= count; c++) {
                    int field_type = resultset_metadata.getColumnType(c);
                    String field_label = resultset_metadata.getColumnLabel(c);
                    Object field_value = jdbc_resultset.getObject(c);
                    addRecordField(field_type, field_label, field_value, record);
                }
                gson.toJson(record, JsonObject.class, json_writer);
            }
            json_writer.endArray();
            json_writer.endObject();
        } catch (Exception ex) {
            throw ex;
        }
    }
    
        
    static public JsonObject resultsetCompact(ResultSet resultset) throws Exception {
        JsonObject json_result = new JsonObject();
        ResultSetMetaData resultset_metadata = resultset.getMetaData();
        int count = resultset_metadata.getColumnCount();
        JsonArray header = new JsonArray();
        json_result.add("header", header);
        for (int c = 1; c <= count; c++) {
            int field_type = resultset_metadata.getColumnType(c);
            String field_label= resultset_metadata.getColumnLabel(c);
            JsonObject field = new JsonObject();
            header.add(field);
            field.addProperty("label", field_label);
            field.addProperty("type", getFieldType(field_type));
        }
        JsonArray json_resultset = new JsonArray();
        json_result.add("resultset", json_resultset);
        while (resultset.next()) {
            JsonArray record = new JsonArray();
            json_resultset.add(record);
            for (int c = 1; c <= count; c++) {
                int field_type = resultset_metadata.getColumnType(c);
                Object field_value = resultset.getObject(c);
                putField(field_type, field_value, record);
            }
        }
        return json_result;
    }
    
    static public String getFieldType(int field_type) throws Exception {
        switch (field_type) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
                return "string";
            case Types.TINYINT:
            case Types.BOOLEAN:
            case Types.BIT:
                return "boolean";
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.SMALLINT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
                return "number";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return "TIMESTAMP_WITH_TIMEZONE";
            case Types.TIME_WITH_TIMEZONE:
                return "TIME_WITH_TIMEZONE";
            default:
                throw new Exception("Ubndefined SQL field java.sql.Types '"+field_type+"'");
        }
    }
    
    static public void putField(int field_type, Object field_value, JsonArray record) throws Exception {
        if (field_value == null) {
            record.add(JsonNull.INSTANCE);
            return;
        }
        switch (field_type) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
                record.add((String) field_value);
                break;
            case Types.TINYINT:
            case Types.BOOLEAN:
            case Types.BIT:
                record.add(BooleanParser.parse(field_value.toString()));
                break;
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.SMALLINT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
                record.add((Number) field_value);
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
            case Types.TIME_WITH_TIMEZONE:
                record.add(field_value.toString());
                break;
            default:
                throw new Exception("Ubndefined SQL field java.sql.Types '"+field_type+"'");
        }
    }
    
    static public void addRecordField(int field_type, String field_label, Object field_value, JsonObject record) throws Exception {
        if (field_value == null) {
            record.add(field_label, JsonNull.INSTANCE);
            return;
        }
        switch (field_type) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
                record.addProperty(field_label, (String) field_value);
                break;
            case Types.TINYINT:
            case Types.BOOLEAN:
            case Types.BIT:
                record.addProperty(field_label, BooleanParser.parse(field_value.toString()));
                break;
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.SMALLINT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.BIGINT:
                record.addProperty(field_label, (Number) field_value);
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
            case Types.TIME_WITH_TIMEZONE:
                record.addProperty(field_label, /*(String)*/ field_value.toString());
                break;
            default:
                throw new Exception("Ubndefined SQL field field_label '"+field_label+"' java.sql.Types '"+field_type+"'");
        }
    }
}
