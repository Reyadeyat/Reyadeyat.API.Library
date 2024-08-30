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

package net.reyadeyat.api.library.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

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
public class Returns {
    HashMap<String, Object> returns;
    
    public Returns() {
        returns = new HashMap<String, Object>();
    }
    
    public Boolean Boolean(String name) {
        Class<Boolean> type = Boolean.class;
        return type.cast(returns.get(name));
    }
    
    public Byte Byte(String name) {
        Class<Byte> type = Byte.class;
        return type.cast(returns.get(name));
    }
    
    public Character Character(String name) {
        Class<Character> type = Character.class;
        return type.cast(returns.get(name));
    }
    
    public Short Short(String name) {
        Class<Short> type = Short.class;
        return type.cast(returns.get(name));
    }
    
    public Integer Integer(String name) {
        Class<Integer> type = Integer.class;
        return type.cast(returns.get(name));
    }
    
    public Long Long(String name) {
        Class<Long> type = Long.class;
        return type.cast(returns.get(name));
    }
    
    public Float Float(String name) {
        Class<Float> type = Float.class;
        return type.cast(returns.get(name));
    }
    
    public Double Double(String name) {
        Class<Double> type = Double.class;
        return type.cast(returns.get(name));
    }
    
    public Date Date(String name) {
        Class<Date> type = Date.class;
        return type.cast(returns.get(name));
    }
    
    public Time Time(String name) {
        Class<Time> type = Time.class;
        return type.cast(returns.get(name));
    }
    
    public Timestamp Timestamp(String name) {
        Class<Timestamp> type = Timestamp.class;
        return type.cast(returns.get(name));
    }
    
    public String String(String name) {
        Class<String> type = String.class;
        return type.cast(returns.get(name));
    }
    
    public Object Object(String name) {
        return returns.get(name);
    }
    
    public <T> T Object(String name, Class<T> type) {
        return type.cast(returns.get(name));
    }
    
    public void Returns(String name, Boolean object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Byte object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Character object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Short object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Integer object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Long object) {
        returns.put(name, object);
    }
    
    public void Float(String name, Float object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Double object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Date object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Time object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Timestamp object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, String object) {
        returns.put(name, object);
    }
    
    public void Returns(String name, Object object) {
        returns.put(name, object);
    }
}
