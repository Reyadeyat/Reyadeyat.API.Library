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

import java.util.Arrays;
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
public class SequenceString implements Sequence<String> {

    private class SequenceStringItem {

        char[] sequence_chars;
        Integer[] sequence_chars_cursors;
        Integer sliding_cursor;
        Integer counterCursor;

        public SequenceStringItem() {
            this.sliding_cursor = chars_width - 1;
            this.counterCursor = 0;
            this.sequence_chars = new char[chars_width];
            this.sequence_chars_cursors = new Integer[chars_width];
            for (int i = 0; i < chars_width; i++) {
                this.sequence_chars_cursors[i] = 0;
                this.sequence_chars[i] = padding_char;
            }
        }

        public String nextSequence() throws Exception {
            if (counterCursor <= sequence_base) {
                sequence_chars[chars_width - 1] = ordered_sequence[sequence_chars_cursors[chars_width - 1] = counterCursor++];
            } else {//carry out by 1
                counterCursor = 1;
                Integer carry_out = 1;
                for (int i = chars_width - 1; i > sliding_cursor - carry_out && carry_out == 1; i--) {
                    if (sequence_chars_cursors[i] == sequence_base) {
                        sequence_chars[i] = ordered_sequence[sequence_chars_cursors[i] = 0];
                        carry_out = 1;
                    } else {
                        sequence_chars[i] = ordered_sequence[++sequence_chars_cursors[i]];
                        carry_out = 0;
                    }
                }
                sliding_cursor = sliding_cursor - carry_out;
                if (sliding_cursor == -1) {
                    if (rewind == true) {
                        sliding_cursor = chars_width - 1;
                        counterCursor = 0;
                        for (int i = 0; i < chars_width; i++) {
                            sequence_chars_cursors[i] = 0;
                            sequence_chars[i] = padding_char;
                        }
                        sequence_chars[sliding_cursor] = ordered_sequence[sequence_chars_cursors[sliding_cursor] = counterCursor++];
                    } else {
                        throw new Exception("Buffer Overflow; Sequence is full; rewind is disabled.");
                    }
                } else if (carry_out > 0) {
                    sequence_chars[sliding_cursor] = ordered_sequence[sequence_chars_cursors[sliding_cursor] = 1];
                }
            }
            return new String(sequence_chars);
        }
        
        public void initSequence(String value) throws Exception {
            if (value.length() > chars_width) {
                throw new Exception("Length of initial sequence value '" + value + "' is greater than accepted with[" + chars_width + "]");
            }
            for (int i = 0; i < ordered_sequence.length; i++) {
                if (value.indexOf(ordered_sequence[i]) == -1) {
                    throw new Exception("initial sequence value char[" + i + "]='" + ordered_sequence[i] + "' is not in the set of accepted chars " + new String(ordered_sequence));
                }
            }
            counterCursor = 1;
            Integer carry_out = 1;
            for (int i = chars_width - 1; i > sliding_cursor - carry_out && carry_out == 1; i--) {
                if (sequence_chars_cursors[i] == sequence_base) {
                    sequence_chars[i] = ordered_sequence[sequence_chars_cursors[i] = 0];
                    carry_out = 1;
                } else {
                    sequence_chars[i] = ordered_sequence[++sequence_chars_cursors[i]];
                    carry_out = 0;
                }
            }
        }
        
        public String getLastSequenceIssued() {
            return new String(sequence_chars);
        }
    }

    private HashMap<Class, SequenceStringItem> sequenceClassMap;
    private char[] ordered_sequence;
    private Integer sequence_base;
    private Integer chars_width;
    private char padding_char;
    private Boolean rewind;
    private Boolean synchronize;

    /**
     * ordered_sequence will be sorted by design
     */
    public SequenceString(char[] ordered_sequence, Integer chars_width, char padding_char, Boolean rewind, Boolean synchronize) throws Exception {
        this.ordered_sequence = ordered_sequence;
        this.sequence_base = this.ordered_sequence.length - 1;
        this.chars_width = chars_width;
        Arrays.sort(this.ordered_sequence);
        this.padding_char = padding_char;
        this.rewind = rewind;
        this.synchronize = synchronize;
        sequenceClassMap = new HashMap<Class, SequenceStringItem>();
        for (int i = 0; i < this.ordered_sequence.length; i++) {
            if (this.padding_char > this.ordered_sequence[i]) {
                throw new Exception("padding char must be lower any char in the ordered sequence char array");
            }
            if (i < this.ordered_sequence.length - 1 && this.ordered_sequence[i] == this.ordered_sequence[i + 1]) {
                throw new Exception("Sequence must has unique chars found char[" + i + "] '" + this.ordered_sequence[i] + "' equals char[" + (i + 1) + "] '" + this.ordered_sequence[i + 1] + "' must be lower any char in the ordered sequence char array");
            }
        }
    }

    @Override
    public void createSequence(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        if (synchronize) {
            synchronized(sequenceClassMap) {
                if (sequenceClassMap.containsKey(clas)) {
                    throw new Exception("Sequence Class '" + clas.getCanonicalName() + "' already created");
                }
                sequenceClassMap.put(clas, new SequenceStringItem(/*ordered_sequence, padding_char, rewind*/));
            }
        }
        if (sequenceClassMap.containsKey(clas)) {
            throw new Exception("Sequence Class '" + clas.getCanonicalName() + "' already created");
        }
        sequenceClassMap.put(clas, new SequenceStringItem(/*ordered_sequence, padding_char, rewind*/));
    }
    
    @Override
    public void initSequence(Class clas, String value) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        if (synchronize) {
            synchronized(sequenceClassMap) {
                if (sequenceClassMap.containsKey(clas) == false) {
                    throw new Exception("Sequence Class '" + clas.getCanonicalName() + "' is not exist");
                }
                sequenceClassMap.get(clas).initSequence(value);
            }
        }
        if (sequenceClassMap.containsKey(clas) == false) {
            throw new Exception("Sequence Class '" + clas.getCanonicalName() + "' is not exist");
        }
        sequenceClassMap.get(clas).initSequence(value);
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
    public <T extends String> T nextSequence(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        SequenceStringItem ss = sequenceClassMap.get(clas);
        if (ss == null) {
            throw new NullPointerException("Class '" + clas + "' is not defined in this sequence");
        }
        if (synchronize) {
            synchronized (ss) {
                return (T) ss.nextSequence();
            }
        }
        return (T) ss.nextSequence();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends String> T getSequenceState(Class clas) throws Exception {
        if (clas == null) {
            throw new Exception("Sequence Class can not be null value");
        }
        SequenceStringItem ss = sequenceClassMap.get(clas);
        if (ss == null) {
            throw new NullPointerException("Class '" + clas + "' is not defined in this sequence");
        }
        if (synchronize) {
            synchronized (ss) {
                return (T) ss.getLastSequenceIssued();
            }
        }
        return (T) ss.getLastSequenceIssued();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends String> HashMap<Class, T> getSequenceState() throws Exception {
        if (synchronize) {
            synchronized (sequenceClassMap) {
                HashMap<Class, String> sequenceClassMapState = new HashMap<Class, String>();
                Iterator<Class> iterator = sequenceClassMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Class key = iterator.next();
                    sequenceClassMapState.put(key, sequenceClassMap.get(key).getLastSequenceIssued());
                }
                return (HashMap<Class, T>) sequenceClassMapState;
            }
        }
        HashMap<Class, String> sequenceClassMapState = new HashMap<Class, String>();
        Iterator<Class> iterator = sequenceClassMap.keySet().iterator();
        while (iterator.hasNext()) {
            Class key = iterator.next();
            sequenceClassMapState.put(key, sequenceClassMap.get(key).getLastSequenceIssued());
        }
        return (HashMap<Class, T>) sequenceClassMapState;
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Boolean debug_sequence_number_add_circuite = false;
            char padding_char = '0';//'#';
            char[] numeric_sequence = "0123456789".toCharArray();
            //char[] alphanumeric_sequence = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
            char[] alphanumeric_sequence = "abcdef0123456789".toCharArray();
            //char[] alphanumeric_sequence = "1AB2".toCharArray();
            SequenceString sequenceString = new SequenceString(alphanumeric_sequence, 6, padding_char, false/*true*/, false/*true*/);
            Sequence<String> sequence = sequenceString;
            sequence.createSequence(SequenceString.class);
            Integer counter = 0;
            Integer debug = 0;
            while (true) {
                String id = sequence.nextSequence(SequenceString.class);
                //System.out.println(id);
                System.out.println(counter + "-" + Integer.parseInt(id, 16) + "-" + id);
                //System.out.println("INSERT INTO `zi`.`text`(`text`)VALUES(TRIM('"+id+"'));");
                //if (debug_sequence_number_add_circuite && debug.parseInt(id) != counter) {
                if (debug_sequence_number_add_circuite && Integer.parseInt(id, 16) != counter) {
                    throw new Exception("Failed to debug");
                }
                counter++;
                if (debug_sequence_number_add_circuite) {
                    if (counter == 65535) {
                        counter = counter;
                    }
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
