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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class GenericTreeModel {
/*
CREATE TABLE `tree_table` (
  `node_id` int unsigned DEFAULT NULL,
  `node_parent_id` int unsigned DEFAULT NULL,
    ABSTRACT DATA
  `node_level` int unsigned DEFAULT NULL,
  `node_order` int unsigned DEFAULT NULL,
  `node_sort` int unsigned DEFAULT NULL,
  `node_is_parental_node` tinyint(1) DEFAULT '0',
  `node_children_count` int unsigned DEFAULT '0',
  `tree_key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
*/
    public JsonArray treeBalancer(Connection connection, String database_name, String database_table_name, String database_table_id_field_name, String database_table_parent_id_field_name, String selected_tree_key, JsonArray tree, JsonArray errors, Boolean update) throws Exception {
        return treeBalancer(connection, database_name, database_table_name, database_table_id_field_name, database_table_parent_id_field_name, selected_tree_key, tree, errors, update, null, null, false);
    }
    
    public JsonArray treeBalancer(Connection connection, String database_name, String database_table_name, String database_table_id_field_name, String database_table_parent_id_field_name, String selected_tree_key, JsonArray tree, JsonArray errors, Boolean update, JsonArray where) throws Exception {
        return treeBalancer(connection, database_name, database_table_name, database_table_id_field_name, database_table_parent_id_field_name, selected_tree_key, tree, errors, update, null, where, false);
    }
    
    public JsonArray treeBalancer(Connection connection, String database_name, String database_table_name, String database_table_id_field_name, String database_table_parent_id_field_name, String selected_tree_key, JsonArray tree, JsonArray errors, Boolean update, String[] extra_select_field_name_list) throws Exception {
        return treeBalancer(connection, database_name, database_table_name, database_table_id_field_name, database_table_parent_id_field_name, selected_tree_key, tree, errors, update, extra_select_field_name_list, null, false);
    }
    
    public JsonArray treeBalancer(Connection connection, String database_name, String database_table_name, String database_table_id_field_name, String database_table_parent_id_field_name, String selected_tree_key, JsonArray tree, JsonArray errors, Boolean update, String[] extra_select_field_name_list, JsonArray where, Boolean create_tree) throws Exception {
        JsonObject tree_node_x = new JsonObject();
        JsonArray flat_tree = new JsonArray();
        HashMap<Integer, JsonObject> tree_map = new HashMap<>();
        //TreeMap<GenericTreeNode, JsonObject> tree_map = new TreeMap<GenericTreeNode, JsonObject>();
        //int debug_round = 0;
        int nodes = 0;
        int affected_rows = 0;
        
        StringBuilder extra_select_field_list_builder = new StringBuilder();
        for (int i = 0; extra_select_field_name_list != null && i < extra_select_field_name_list.length; i++) {
            extra_select_field_list_builder.append(",").append(extra_select_field_name_list[i].trim());
        }
        
        StringBuilder where_builder = new StringBuilder();
        for (int i = 0; where != null && i < where.size(); i++) {
            JsonObject clause = where.get(i).getAsJsonObject();
            where_builder.append(" ").append(clause.get("condition").getAsString()).append(" ").append(clause.get("field").getAsString().trim()).append(" ").append(clause.get("comparison").getAsString()).append(" ").append(clause.get("value").getAsString().trim());
        }

        String select_tree_sql = "SELECT `" + database_table_id_field_name + "` AS `node_id`, `" + database_table_parent_id_field_name + ("` AS `node_parent_id`, `node_order`, `tree_key`, `node_is_parental_node` " + extra_select_field_list_builder.toString() + " FROM `") + database_name + "`.`" + database_table_name + "` WHERE `tree_key`=? " + where_builder.toString() + " ORDER BY `node_level`, `node_order`";
        try ( PreparedStatement tree_statement = connection.prepareStatement(select_tree_sql)) {
            tree_statement.setString(1, selected_tree_key);
            JsonArray resultset = new JsonArray();
            try ( ResultSet rs = tree_statement.executeQuery()) {
                while (rs.next()) {
                    JsonObject node = new JsonObject();
                    Integer node_id = rs.getInt("node_id");
                    Object node_parent_id = rs.getObject("node_parent_id");
                    Boolean node_is_parental_node = rs.getBoolean("node_is_parental_node");
                    Integer node_order = rs.getInt("node_order");
                    String tree_key = rs.getString("tree_key");
                    node.addProperty("node_id", node_id);
                    node.addProperty("node_parent_id", node_parent_id == null ? null : Integer.valueOf(node_parent_id.toString()));
                    node.addProperty("node_is_parental_node", node_is_parental_node);
                    node.addProperty("node_order", node_order);
                    node.addProperty("tree_key", tree_key);
                    for (int i = 0; i < extra_select_field_name_list.length; i++) {
                        String field = extra_select_field_name_list[i].trim();
                        node.addProperty(field, rs.getString(field));
                    }
                    node.add("children", new JsonArray());
                    tree_map.put(node_id, node);
                    //GenericTreeNode generic_tree_node = new GenericTreeNode(id, node_id, node_parent_id);
                    //tree_map.put(generic_tree_node, tree_node);
                    //tree_map.get(generic_tree_node);
                }
            } catch (Exception sqlx) {
                throw sqlx;
            }
            tree_statement.close();
            //Create Tree
            if (create_tree == true) {
                while (nodes != tree_map.size()) {
                    for (Map.Entry<Integer, JsonObject> map_node : tree_map.entrySet()) {
                        //debug_round++;
                        JsonObject node = map_node.getValue();
                        String node_id = node.get("node_id").getAsString();
                        Integer node_parent_id = node.get("node_parent_id").isJsonNull() == true ? null : node.get("node_parent_id").getAsInt();
                        JsonObject parent_node = tree_map.get(node_parent_id);
                        /*if (nodes == 87 || debug_round == 24) {
                                nodes = nodes;
                            }*/
                        if (node_parent_id != null && parent_node == null) {
                            throw new Exception("Parent Node ID '" + node_parent_id + "' is not exist on this tree '" + selected_tree_key + "'");
                        }
                        if (node_parent_id == null && node.has("node_level") == false) {
                            nodes++;
                            node.add("node_parent_id", JsonNull.INSTANCE);
                            node.addProperty("node_level", 0);
                            //node.addProperty("node_order", 0);
                            tree.add(node);
                        } else if (node_parent_id != null && parent_node.has("node_level") == true && node.has("node_level") == false) {
                            nodes++;
                            JsonArray parent_children = parent_node.get("children").getAsJsonArray();
                            Integer parent_id = parent_node.get("node_id").getAsInt();
                            Integer parent_level = parent_node.get("node_level").getAsInt();
                            Integer parent_children_count = parent_children.size();
                            node.addProperty("node_parent_id", parent_id);
                            node.addProperty("node_level", parent_level + 1);
                            //node.addProperty("node_order", parent_children_count);
                            parent_children.add(node);
                        }
                    }
                }
            } else {
                for (Map.Entry<Integer, JsonObject> map_node : tree_map.entrySet()) {
                    JsonObject node = map_node.getValue();
                    tree.add(node);
                }
            }
            //flaten Tree
            flat_tree = new JsonArray();
            ArrayList<JsonElement> stack_list = new ArrayList<>();
            /*for (int i = 0; i < tree.size(); i++) {
                    JsonElement tree_node = tree.get(i);
                    stack_list.add(tree_node);
                }
                int node_sort_sequence = 0;
                //flattenTree(stack_list, flat_tree, node_sort_sequence);
                while (stack_list.size() > 0) {
                  JsonObject child_tree_node = stack_list.remove(0).getAsJsonObject();
                  if (child_tree_node.has("node_sort") == false) {
                      child_tree_node.addProperty("node_sort", ++node_sort_sequence);
                  }
                  flat_tree.add(child_tree_node);
                  JsonArray children = child_tree_node.get("children").getAsJsonArray();
                  if (children != null && children.size() > 0) {
                    for (int c = 0; c < children.size(); c++) {
                        JsonObject child = children.get(c).getAsJsonObject();
                        child.addProperty("node_sort", ++node_sort_sequence);
                        stack_list.add(child);
                    }
                  }
                }*/
            int node_sort_sequence = 0;
            for (int i = 0; i < tree.size(); i++) {
                JsonElement tree_node = tree.get(i);
                stack_list.add(tree_node);
                while (stack_list.size() > 0) {
                    JsonObject child_tree_node = stack_list.remove(0).getAsJsonObject();
                    child_tree_node.addProperty("node_sort", ++node_sort_sequence);
                    flat_tree.add(child_tree_node);
                    JsonArray children = child_tree_node.get("children").getAsJsonArray();
                    if (children != null && children.size() > 0) {
                        //child_tree_node.addProperty("node_is_parental_node", true);
                        child_tree_node.addProperty("node_children_count", children.size());
                        for (int c = children.size() - 1; c > -1; c--) {
                            JsonObject child = children.get(c).getAsJsonObject();
                            stack_list.add(0, child);
                        }
                    } else {
                        //Boolean node_is_parental_node = child_tree_node.get("node_is_parental_node") == null ? false : child_tree_node.get("node_is_parental_node").getAsBoolean();
                        //child_tree_node.addProperty("node_is_parental_node", node_is_parental_node);
                        child_tree_node.addProperty("node_children_count", 0);
                    }
                }
            }
            if (update) {
                //Update Tree
                StringBuilder update_tree_sql = new StringBuilder("UPDATE `").append(database_name).append("`.`").append(database_table_name).append("` SET `node_level`=?, `node_sort`=?, `node_is_parental_node`=?, `node_children_count`=?, `tree_key`=? WHERE `node_id`=?");
                try ( PreparedStatement update_tree_statement = connection.prepareStatement(update_tree_sql.toString())) {
                    for (int i = 0; i < flat_tree.size(); i++) {
                        JsonObject node = flat_tree.get(i).getAsJsonObject();
                        Integer node_level = node.get("node_level").getAsInt();
                        Integer node_sort = node.get("node_sort").getAsInt();
                        String tree_key = node.get("tree_key").getAsString();
                        Integer node_id = node.get("node_id").getAsInt();
                        Boolean node_is_parental_node = node.get("node_is_parental_node").getAsBoolean();
                        Integer node_children_count = node.get("node_children_count").getAsInt();
                        update_tree_statement.setInt(1, node_level);
                        update_tree_statement.setInt(2, node_sort);
                        update_tree_statement.setBoolean(3, node_is_parental_node);
                        update_tree_statement.setInt(4, node_children_count);
                        update_tree_statement.setString(5, tree_key);
                        update_tree_statement.setInt(6, node_id);
                        update_tree_statement.addBatch();
                    }
                    int[] rows = update_tree_statement.executeBatch();
                    for (int i = 0; i < rows.length; i++) {
                        affected_rows += rows[i];
                    }
                } catch (Exception sqlx) {
                    throw sqlx;
                }
            }
            return flat_tree;
        } catch (Exception sqlx) {
            if (connection.getAutoCommit() == false) {
                connection.rollback();
            }
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, tree_node_x.toString() + "\n" + sqlx.getMessage(), sqlx);
            throw sqlx;
        }
    }
}
