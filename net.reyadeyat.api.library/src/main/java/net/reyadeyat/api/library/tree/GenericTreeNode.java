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

package net.reyadeyat.api.library.tree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

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
public class GenericTreeNode implements Comparable<GenericTreeNode> {
    Object object_list[];
    
    public GenericTreeNode(Object ... object_list) {
        this.object_list = object_list;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericTreeNode == false) {
            return false;
        }
        Boolean equals = true;
        GenericTreeNode generic_tree_node = (GenericTreeNode) obj;
        for (int i = 0; i < object_list.length; i++) {
            Object object_1 = object_list[i];
            Object object_2 = generic_tree_node.object_list[i];
            if (object_1 == null || object_2 == null) {
                equals = equals && object_1 == object_2;
            } else {
                equals = equals && object_1.equals(object_2);
            }
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Arrays.deepHashCode(this.object_list);
        return hash;
    }

    @Override
    public int compareTo(GenericTreeNode generic_tree_node) {
        for (int i = 0; i < object_list.length; i++) {
            Object object_1 = object_list[i];
            Object object_2 = generic_tree_node.object_list[i];
            if (object_1 != null && object_2 == null) {
                return 1;
            }
            if (object_1 == null && object_2 != null) {
                return -1;
            }
            int compare = compareTwo(object_1, object_2);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
    
    public int compareTwo(Object object_1, Object object_2) {
        if (object_1 != null && object_2 == null) {
            return 1;
        }
        if (object_1 == null && object_2 != null) {
            return -1;
        }
        if (object_1 instanceof String && object_2 instanceof String) {
            return compareTwo((String) object_1, (String) object_2);
        }
        if (object_1 instanceof Number && object_2 instanceof Number) {
            return compareTwo((Number) object_1, (Number) object_2);
        }
        throw new RuntimeException("CompareTwo objects are not compatible");
    }
    
    public int compareTwo(String string_1, String string_2) {
        int min = Math.min(string_1.length(), string_2.length());
        for (int i = 0; i < min; i++) {
            char c_1 = string_1.charAt(i);
            char c_2 = string_2.charAt(i);
            if (c_1 > c_2) {
                return 1;
            } else if (c_1 < c_2) {
                return -1;
            }
        }
        return string_1.length() < string_2.length() ? -1 : 1;
    }
    
    public int compareTwo(Number number_1, Number number_2) {
        return new BigDecimal(number_1.toString()).compareTo(new BigDecimal(number_2.toString()));
    }
}
