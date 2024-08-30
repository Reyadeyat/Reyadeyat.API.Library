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

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import net.reyadeyat.api.library.json.JsonStringAdapter;

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
public class TreeNode {
    public Integer state;
    public Integer node_id;
    public JsonObject name;
    public Integer node_parent_id;
    public String tree_key;
    public Integer node_level;
    public Integer node_order;
    public Integer node_sort;
    public Boolean node_is_parental_node;
    public Integer node_children_count;
    public JsonObject user_data;
    public JsonObject user_data_small;
    @JsonAdapter(JsonStringAdapter.class)
    public String user_data_medium;
    @JsonAdapter(JsonStringAdapter.class)
    public String user_data_long;
    transient TreeNode node_parent;
    
    public Integer getNodeID() {
        return node_id;
    } 
    
}
