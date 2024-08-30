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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.ArrayList;

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
public class JsonExclusionStrategy implements ExclusionStrategy {
    private transient ArrayList<String> expose_field_list;
    
    public JsonExclusionStrategy (ArrayList<String> expose_field_list) {
        this.expose_field_list = expose_field_list;
    }
    
    @Override
    public boolean shouldSkipField(FieldAttributes fa) {
        //if (fa.getDeclaringClass().equals(Citation.class) && expose_field_list.contains(fa.getName())) {
        if (expose_field_list.contains(fa.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }
}
