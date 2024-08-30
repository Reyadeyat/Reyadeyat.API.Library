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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime unmarshal(String xml) throws Exception {
        //return ZonedDateTime.parse(xml, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ZonedDateTime.parse(xml);
    }

    @Override
    public String marshal(ZonedDateTime object) throws Exception {
        //return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(object);
        return object.toString();
    }

    @Override
    public JsonElement serialize(ZonedDateTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public ZonedDateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();

        // if provided as String - '2011-12-03T10:15:30+01:00[Europe/Paris]'
        if (jsonPrimitive.isString() && typeOfT.getTypeName().contains("ZonedDateTime")) {
            return ZonedDateTime.parse(jsonPrimitive.getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }

        // if provided as Long
        if (jsonPrimitive.isNumber() && typeOfT.getTypeName().contains("ZonedDateTime")) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
        }

        throw new JsonParseException("Unable to parse ZonedDateTime");
    }
    
    public String toJDBCDateTime(ZonedDateTime object) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:SS").format(object);
    }
    
    public String fromJDBCDateTime(ZonedDateTime object) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:SS").format(object);
    }
}

