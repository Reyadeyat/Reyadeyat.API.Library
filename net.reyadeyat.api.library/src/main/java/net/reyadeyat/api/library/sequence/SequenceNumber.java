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

package net.reyadeyat.api.library.sequence;

import java.util.HashMap;
import java.util.Iterator;

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
public class SequenceNumber implements Sequence<Number> {

    private class SequenceNumberItem {
        private Number sequence_initial_number;
        private Number sequence_value;
        private Number increment;
        private Boolean rewind;
        private Class type;

        public SequenceNumberItem(Number sequence_initial_number, Number sequence_start_value, Number increment) {
            this.sequence_initial_number = sequence_initial_number;
            this.sequence_value = sequence_start_value;
            this.increment = increment;
            this.type = this.sequence_value.getClass();
        }
        
        public Number nextSequence() throws Exception {
            if (this.type.equals(Byte.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Byte.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Byte[" + Byte.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.byteValue() + increment.byteValue();
            } else if (this.type.equals(Short.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Short.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Short[" + Short.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.shortValue() + increment.shortValue();
            } else if (this.type.equals(Integer.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Integer.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Integer[" + Integer.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.intValue() + increment.intValue();
            } else if (this.type.equals(Long.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Long.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Long[" + Long.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.longValue() + increment.longValue();
            } else if (this.type.equals(Double.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Double.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Double[" + Double.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.doubleValue() + increment.doubleValue();
            } else if (this.type.equals(Float.class)) {
                if (sequence_initial_value.byteValue() + increment.byteValue() >= Float.MAX_VALUE) {
                    if (rewind == true) {
                        this.sequence_value = this.sequence_initial_number;
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full Float[" + Float.MAX_VALUE + "]; rewind is disabled.");
                    }
                }
                sequence_value = sequence_value.floatValue() + increment.floatValue();
            }
            return sequence_value;
        }
        
        public void initSequence(Number value) {
            this.sequence_value = value;
        }
        
        public Number getLastSequenceIssued() {
            return this.sequence_value;
        }
    }
    
    private HashMap<Class, SequenceNumberItem> sequenceClassMap;
    private Boolean synchronize;
    private Number sequence_initial_value;
    private Number sequence_increment;
    
    public SequenceNumber(Number sequence_initial_value, Number sequence_increment, Boolean synchronize) {
        this.sequenceClassMap = new HashMap<Class, SequenceNumberItem>();
        this.sequence_initial_value = sequence_initial_value;
        this.sequence_increment = sequence_increment;
        this.synchronize = synchronize;
    }
    
    /*public void addSequence(Class clas, Type sequence_number, Type increment) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        if (synchronize) {
            synchronized(sequenceClassMap) {
                if (sequenceClassMap.containsKey(clas)) {
                    return;
                }
                sequenceClassMap.put(clas, new SequenceNumberItem(sequence_number, sequence_number, increment));
                return;
            }
        }
        if (sequenceClassMap.containsKey(clas)) {
            return;
        }
        sequenceClassMap.put(clas, new SequenceNumberItem(sequence_number, sequence_number, increment));
    }*/
    
    @Override
    public void createSequence(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        /*if (clas.getCanonicalName().equalsIgnoreCase(Boolean.class.getCanonicalName())) {
            clas = clas;
        }*/
        if (synchronize) {
            synchronized(sequenceClassMap) {
                if (sequenceClassMap.containsKey(clas)) {
                    return;
                }
                sequenceClassMap.put(clas, new SequenceNumberItem(sequence_initial_value, sequence_initial_value, sequence_increment));
                return;
            }
        }
        if (sequenceClassMap.containsKey(clas)) {
            return;
        }
        sequenceClassMap.put(clas, new SequenceNumberItem(sequence_initial_value, sequence_initial_value, sequence_increment));
    }
    
    @Override
    public void initSequence(Class clas, Number value) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        if (value == null) {
            throw new Exception("Sequence initial value can not be null value");
        }
        if (synchronize) {
            synchronized(sequenceClassMap) {
                SequenceNumberItem sni = sequenceClassMap.get(clas);
                if (sni == null) {
                    sequenceClassMap.put(clas, new SequenceNumberItem(sequence_initial_value, value, sequence_increment));
                } else {
                    sni.initSequence(value);
                }
            }
        }
        SequenceNumberItem sni = sequenceClassMap.get(clas);
        if (sni == null) {
            sequenceClassMap.put(clas, new SequenceNumberItem(sequence_initial_value, value, sequence_increment));
        } else {
            sni.initSequence(value);
        }
    }
    
    @Override
    public Boolean hasSequence(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        if (synchronize) {
            synchronized(sequenceClassMap) {
                return sequenceClassMap.containsKey(clas);
            }
        }
        return sequenceClassMap.containsKey(clas);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Number> T nextSequence(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        SequenceNumberItem si = sequenceClassMap.get(clas);
        if (si == null) {
            throw new NullPointerException("Class '" + clas + "' is not defined in this sequence");
        }
        if (synchronize) {
            synchronized(si) {
                return (T) si.nextSequence();
            }
        }
        return (T) si.nextSequence();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Number> T getSequenceState(Class clas) throws Exception {
        SequenceNumberItem si = sequenceClassMap.get(clas);
        if (si == null) {
            throw new NullPointerException("Class '" + clas + "' is not defined in this sequence");
        }
        if (synchronize) {
            synchronized(si) {
                return (T) si.getLastSequenceIssued();
            }
        }
        return (T) si.getLastSequenceIssued();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Number> HashMap<Class, T> getSequenceState() throws Exception {
        if (synchronize) {
            synchronized(sequenceClassMap) {
                HashMap<Class, Number> sequenceClassMapState = new HashMap<Class, Number>();
                Iterator<Class> iterator = sequenceClassMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Class key = iterator.next();
                    /*if (key.getCanonicalName().equalsIgnoreCase(Boolean.class.getCanonicalName())) {
                        key = key;
                    }*/
                    sequenceClassMapState.put(key, sequenceClassMap.get(key).getLastSequenceIssued());
                }
                return (HashMap<Class, T>) sequenceClassMapState;
            }
        }
        HashMap<Class, Number> sequenceClassMapState = new HashMap<Class, Number>();
        Iterator<Class> iterator = sequenceClassMap.keySet().iterator();
        while (iterator.hasNext()) {
            Class key = iterator.next();
            /*if (key.getCanonicalName().equalsIgnoreCase(Boolean.class.getCanonicalName())) {
                key = key;
            }*/
            sequenceClassMapState.put(key, sequenceClassMap.get(key).getLastSequenceIssued());
        }
        return (HashMap<Class, T>) sequenceClassMapState;
    }
}
