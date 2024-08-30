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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import redis.clients.jedis.Jedis;

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
public class JsonUtil {
    static private ArrayList<Gson> gson_pool = new ArrayList<Gson>();
    static private ArrayList<Gson> gson_pool_prety = new ArrayList<Gson>();
    
    static public Gson gson() {
        synchronized(gson_pool) {
            if (gson_pool.size() > 0) {
                return gson_pool.remove(0);
            }
        }
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).disableHtmlEscaping().create();
    }
    
    static public Gson gsonPretty() {
        synchronized(gson_pool_prety) {
            if (gson_pool_prety.size() > 0) {
                return gson_pool_prety.remove(0);
            }
        }
        return new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).disableHtmlEscaping().setPrettyPrinting().create();
    }
    
    static public void reclaimGson(Gson gson) {
        if (gson_pool.contains(gson)) {
            return;
        }
        gson_pool.add(gson);
    }
    
    static public void reclaimGsonPretty(Gson gson) {
        gson_pool_prety.add(gson);
    }
    
    static public <T extends Serializable> byte[] serilize(JsonElement json_element, Class<T> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                T object = gson.fromJson(json_element, class_type);
                JsonUtil.reclaimGson(gson);
                oos.writeObject(object);
                return baos.toByteArray();
            } catch (Exception ex) {
                JsonUtil.reclaimGson(gson);
                throw ex;
            }
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public <T extends Serializable> T objectize(byte[] json_serialized, Class<T> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(json_serialized)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object object = ois.readObject();
                JsonElement json_element = gson.toJsonTree(object, class_type);
                T class_object = gson.fromJson(json_element, class_type);
                JsonUtil.reclaimGson(gson);
                if (class_type.getName().equalsIgnoreCase(object.getClass().getName()) == false) {
                    throw new ClassCastException("Required '"+class_type.getName()+"' but fetched '"+object.getClass().getName()+"'");
                }
                return class_object;
            } catch (Exception ex) {
                JsonUtil.reclaimGson(gson);
                throw ex;
            }
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public <T extends Serializable> JsonElement deserilize(byte[] json_serialized, Class<T> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(json_serialized)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object object = ois.readObject();
                JsonElement json_element = gson.toJsonTree(object, class_type);
                JsonUtil.reclaimGson(gson);
                return json_element;
            } catch (Exception ex) {
                JsonUtil.reclaimGson(gson);
                throw ex;
            }
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public String jsonElementToString(JsonElement json_element) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            String string = gson.toJson(json_element, JsonElement.class);
            JsonUtil.reclaimGson(gson);
            return string;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public String jsonElementToButifulString(JsonElement json_element) throws Exception {
        Gson gson = JsonUtil.gsonPretty();
        try {
            String string = gson.toJson(json_element, JsonElement.class);
            JsonUtil.reclaimGsonPretty(gson);
            return string;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public JsonElement jsonStringToJsonElelement(String json_stringized) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            JsonElement json_element = gson.fromJson(json_stringized, JsonElement.class);
            JsonUtil.reclaimGson(gson);
            return json_element;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public <CustomeType> String objectToJsonString(Object java_object, Class<CustomeType> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            String string = gson.toJson(java_object, class_type);
            JsonUtil.reclaimGson(gson);
            return string;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public <CustomeType> CustomeType jsonElementToObject(JsonElement json_element, Class<CustomeType> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            CustomeType custome_type = gson.fromJson(json_element, class_type);
            JsonUtil.reclaimGson(gson);
            return custome_type;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public <CustomeType> CustomeType jsonStringToObject(String json_stringized, Class<CustomeType> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            CustomeType custome_type = gson.fromJson(json_stringized, class_type);
            JsonUtil.reclaimGson(gson);
            return custome_type;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static public JsonElement objectToJsonElement(Object java_object, Class<?> class_type) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            JsonElement json_element = gson.toJsonTree(java_object, class_type);
            JsonUtil.reclaimGson(gson);
            return json_element;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }
    
    static private void isNull(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        if (nullable == false && (json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true)) {
            throw new Exception("Json Object property '"+json_property+"' is null");
        }
    }

    static private void isNull(JsonArray jsonArray, int jsonIndex, Boolean nullable) throws Exception {
        if (nullable == false && (jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true)) {
            throw new Exception("Json Array index '"+jsonIndex+"' is null");
        }
    }
    
    static public Boolean hasProperty(JsonObject json_object, String json_property) throws Exception {
        return json_object.get(json_property) != null;
    }
    
    static public JsonObject getJsonObject(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsJsonObject();
    }
    
    static public JsonArray getJsonArray(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsJsonArray();
    }
    
    static public String getJsonString(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsString();
    }

    static public Boolean getJsonBoolean(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsBoolean();
    }

    static public Integer getJsonInteger(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsInt();
    }

    static public Long getJsonLong(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsLong();
    }

    static public Double getJsonDouble(JsonObject json_object, String json_property, Boolean nullable) throws Exception {
        JsonUtil.isNull(json_object, json_property, nullable);
        return json_object.get(json_property) == null || json_object.get(json_property).isJsonNull() == true ? null : json_object.get(json_property).getAsDouble();
    }
    
    static private void isNull(JsonArray jsonArray, Integer index, Boolean nullable) throws Exception {
        if (nullable == false && (jsonArray.size() < index || jsonArray.get(index) == null || jsonArray.get(index).isJsonNull() == true)) {
            throw new Exception("Json Object index '"+index+"' is null");
        }
    }
    
    static public JsonObject getJsonObject(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsJsonObject();
    }

    static public String getJsonString(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsString();
    }

    static public Boolean getJsonBoolean(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsBoolean();
    }

    static public Integer getJsonInteger(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsInt();
    }

    static public Long getJsonLong(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsLong();
    }

    static public Double getJsonDouble(JsonArray jsonArray, Integer jsonIndex, Boolean nullable) throws Exception {
        JsonUtil.isNull(jsonArray, jsonIndex, nullable);
        return jsonArray.size() < jsonIndex || jsonArray.get(jsonIndex) == null || jsonArray.get(jsonIndex).isJsonNull() == true ? null : jsonArray.get(jsonIndex).getAsDouble();
    }

    static public void addJsonOpenObject(StringBuilder string) throws Exception {
        string.append("{");
    }

    static public void addJsonCloseObject(StringBuilder string) throws Exception {
        string.append("}");
    }

    static public void addJsonOpenArray(StringBuilder string) throws Exception {
        string.append("[");
    }

    static public void addJsonCloseArray(StringBuilder string) throws Exception {
        string.append("]");
    }

    static public void addJsonComma(StringBuilder string) throws Exception {
        string.append(",");
    }

    static public void addJsonColon(StringBuilder string) throws Exception {
        string.append(":");
    }

    static public void addJsonTag(StringBuilder string, String tag) throws Exception {
        string.append("\"").append(tag).append("\":");
    }

    static public void addJsonArrayItemNull(StringBuilder string) throws Exception {
        string.append("null");
    }

    static public void addJsonArrayItemString(StringBuilder string, String content) throws Exception {
        string.append("\"").append(content).append("\"");
    }

    static public void addJsonArrayItemNumber(StringBuilder string, String content) throws Exception {
        string.append(content);
    }

    static public void addJsonPropertyNull(StringBuilder string, String tag) throws Exception {
        string.append("\"").append(tag).append("\":null");
    }

    static public void addJsonPropertyString(StringBuilder string, String tag, String content) throws Exception {
        string.append("\"").append(tag).append("\":\"").append(content).append("\"");
    }

    static public void addJsonPropertyNumber(StringBuilder string, String tag, String content) throws Exception {
        string.append("\"").append(tag).append("\":").append(content);
    }

    static public Object[] javaArray(JsonArray jsonArray) throws Exception {
        String[] javaArray = new String[jsonArray.size()];
        for (int i = 0; i < javaArray.length; i++) {
            javaArray[i] = (jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsString());
        }
        return javaArray;
    }

    static public ArrayList<String> javaStringArrayList(JsonArray jsonArray) throws Exception {
        ArrayList<String> javaArray = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            javaArray.add(jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsString());
        }
        return javaArray;
    }

    static public String[] javaStringArray(JsonArray jsonArray) throws Exception {
        String[] javaArray = new String[jsonArray.size()];
        for (int i = 0; i < javaArray.length; i++) {
            javaArray[i] = (jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsString());
        }
        return javaArray;
    }

    static public Integer[] javaIntegerArray(JsonArray jsonArray) throws Exception {
        Integer[] javaArray = new Integer[jsonArray.size()];
        for (int i = 0; i < javaArray.length; i++) {
            javaArray[i] = (jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsInt());
        }
        return javaArray;
    }

    static public Long[] javaLongArray(JsonArray jsonArray) throws Exception {
        Long[] javaArray = new Long[jsonArray.size()];
        for (int i = 0; i < javaArray.length; i++) {
            javaArray[i] = (jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsLong());
        }
        return javaArray;
    }

    static public Boolean[] javaBooleanArray(JsonArray jsonArray) throws Exception {
        Boolean[] javaArray = new Boolean[jsonArray.size()];
        for (int i = 0; i < javaArray.length; i++) {
            javaArray[i] = (jsonArray.get(i).isJsonNull() ? null : jsonArray.get(i).getAsBoolean());
        }
        return javaArray;
    }
    
    static public <T extends Serializable> void inRedis(Jedis jedis, String key, String field, JsonElement value, Class<T> class_type) throws Exception {
        JsonUtil.inRedis(jedis, key.getBytes(StandardCharsets.UTF_8), field.getBytes(StandardCharsets.UTF_8), value, class_type);
    }
   
    static public <T extends Serializable> void inRedis(Jedis jedis, byte[] key, byte[] field, JsonElement value, Class<T> class_type) throws Exception {
        byte[] json_serialized = JsonUtil.serilize(value, class_type);
        jedis.hset(key, field, json_serialized);
    }
   
    static public <T extends Serializable> JsonElement outRedis(Jedis jedis, String key, String field, Class<T> class_type) throws Exception {
        return JsonUtil.outRedis(jedis, key.getBytes(StandardCharsets.UTF_8), field.getBytes(StandardCharsets.UTF_8), class_type);
    }
   
    static public <T extends Serializable> JsonElement outRedis(Jedis jedis, byte[] key, byte[] field, Class<T> class_type) throws Exception {
        byte[] json_serialized = jedis.hget(key, field);
        if (json_serialized == null) {
            return null;
        }
        return JsonUtil.deserilize(json_serialized, class_type);
    }
    
    static public <T extends Serializable> T outRedisObject(Jedis jedis, byte[] key, byte[] field, Class<T> class_type) throws Exception {
        byte[] json_serialized = jedis.hget(key, field);
        if (json_serialized == null) {
            return null;
        }
        T object = JsonUtil.objectize(json_serialized, class_type);
        return object;
    }
    
    static public Long delRedis(Jedis jedis, String key) throws Exception {
        return jedis.del(key.getBytes(StandardCharsets.UTF_8));
    }
   
    static public Long delRedis(Jedis jedis, String key, String field) throws Exception {
        return jedis.hdel(key.getBytes(StandardCharsets.UTF_8), field.getBytes(StandardCharsets.UTF_8));
    }
   
    static public <T extends Serializable> ArrayList<JsonElement> redisJsonList(Jedis jedis, String key, Class<T> class_type) throws Exception {
        Map<byte[], byte[]> key_byte_list = jedis.hgetAll(key.getBytes(StandardCharsets.UTF_8));
        ArrayList<JsonElement> json_element_list = new ArrayList<JsonElement>();
        for (Map.Entry<byte[], byte[]> key_entry : key_byte_list.entrySet()) {
            byte[] field_key = key_entry.getKey();
            byte[] field_tag = key_entry.getValue();
            JsonElement json_element = outRedis(jedis, field_key, field_tag, class_type);
            json_element_list.add(json_element);
        }
        return json_element_list;
    }
    
    static public <T extends Serializable> ArrayList<T> redisObjectList(Jedis jedis, String key, Class<T> class_type) throws Exception {
        Map<byte[], byte[]> key_byte_list = jedis.hgetAll(key.getBytes(StandardCharsets.UTF_8));
        ArrayList<T> object_list = new ArrayList<T>();
        for (Map.Entry<byte[], byte[]> key_entry : key_byte_list.entrySet()) {
            byte[] field_key = key_entry.getKey();
            byte[] field_tag = key_entry.getValue();
            //T object = outRedisObject(jedis, key.getBytes(), field_key, class_type);
            T object = objectize(field_tag, class_type);
            object_list.add(object);
        }
        return object_list;
    }
    
    static public JsonArray jsonStringArray(String[] javaArray) throws Exception {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < javaArray.length; i++) {
            jsonArray.add(javaArray[i]);
        }
        return jsonArray;
    }

    static public JsonArray jsonIntegerArray(Integer[] javaArray) throws Exception {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < javaArray.length; i++) {
            jsonArray.add(javaArray[i]);
        }
        return jsonArray;
    }

    static public JsonArray jsonLongArray(Long[] javaArray) throws Exception {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < javaArray.length; i++) {
            jsonArray.add(javaArray[i]);
        }
        return jsonArray;
    }

    static public JsonArray jsonBooleanArray(Boolean[] javaArray) throws Exception {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < javaArray.length; i++) {
            jsonArray.add(javaArray[i]);
        }
        return jsonArray;
    }
    
    static public JsonArray jsonObjectArray(Object[] javaArray) throws Exception {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < javaArray.length; i++) {
            Object java_object =  javaArray[i];
            if (java_object instanceof Boolean) {
                jsonArray.add((Boolean) java_object);
            } else if (java_object instanceof Integer) {
                jsonArray.add((Integer) java_object);
            } else if (java_object instanceof Long) {
                jsonArray.add((Long) java_object);
            } else if (java_object instanceof String) {
                jsonArray.add((String) java_object);
            }
        }
        return jsonArray;
    }
    
    /** Json Streams */

    static public void writeStream(OutputStream output_stream, JsonElement json_element) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(output_stream));
            gson.toJson(json_element, writer);
            writer.flush();
            writer.close();
            JsonUtil.reclaimGson(gson);
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public void writeFile(String file_path, Boolean overwrite, JsonElement json_element) throws Exception {
        File file = new File(file_path);
        if (file.exists() == true && overwrite == false) {
            throw new Exception("File Exists and Overwerite = false");
        }
        file.setWritable(true);
        Gson gson = JsonUtil.gson();
        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file)));
            gson.toJson(json_element, writer);
            writer.flush();
            writer.close();
            JsonUtil.reclaimGson(gson);
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public JsonElement readStream(InputStream input_stream) throws Exception {
        Gson gson = JsonUtil.gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(input_stream));
            JsonElement json_element = gson.fromJson(reader, JsonElement.class);
            reader.close();
            JsonUtil.reclaimGson(gson);
            return json_element;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public JsonElement readFile(String file_path, Boolean create_if_not_exists) throws Exception {
        File file = new File(file_path);
        if (file.exists() == false && create_if_not_exists == false) {
            throw new FileNotFoundException("File not exists and create_if_not_exists = false");
        }
        if (file.exists() == false && create_if_not_exists == true) {
            file.createNewFile();
        }
        Gson gson = JsonUtil.gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
            JsonElement json_element = gson.fromJson(reader, JsonElement.class);
            reader.close();
            JsonUtil.reclaimGson(gson);
            return json_element;
        } catch (Exception ex) {
            JsonUtil.reclaimGson(gson);
            throw ex;
        }
    }

    static public void deleteFile(String file_path) throws Exception {
        File file = new File(file_path);
        file.delete();
        if(file.exists()){
            file.getCanonicalFile().delete();
        }
    }

    /** Json Path*/
    public static JsonPrimitive getJsonPrimitive(String path, JsonElement json_element) throws Exception {
        JsonElement element = JsonUtil.getJsonElement(path, json_element);
        return element.getAsJsonPrimitive();
    }

    public static JsonObject getJsonObject(String path, JsonElement json_element) throws Exception {
        JsonElement element = JsonUtil.getJsonElement(path, json_element);
        return element.getAsJsonObject();
    }

    public static JsonArray getJsonArray(String path, JsonElement json_element) throws Exception {
        JsonElement element = JsonUtil.getJsonElement(path, json_element);
        return element.getAsJsonArray();
    }

    public static JsonElement getJsonElement(String path, JsonElement json_element) throws Exception {
        String[] tokens = path.split("\\.");
        JsonElement element = json_element;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (element.isJsonObject()) {
                element = element.getAsJsonObject().get(token);
            } else if (element.isJsonArray()) {
                element = element.getAsJsonArray();
            } else if (element.isJsonPrimitive()) {
                element = element.getAsJsonPrimitive();
            } else if (element.isJsonNull()) {
                element = element.getAsJsonNull();
            } else {
                throw new Exception("Undefined json '"+element.toString()+"' object type");
            }
        }
        if (element == null) {
            throw new Exception("Json Properties path '"+path+"' is null");
        }
        return element;
    }
    
    public static void throwJsonExceptionOnError(String error_title, JsonArray error_list) throws Exception {
        if (error_list.size() > 0) {
            Gson gson = JsonUtil.gsonPretty();
            String error_list_string = gson.toJson(error_list);
            JsonUtil.reclaimGsonPretty(gson);
            throw new Exception(error_title+"\n"+error_list_string);
        }
    }
}
