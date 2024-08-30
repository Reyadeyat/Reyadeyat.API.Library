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
public class BooleanParser {
    static public Boolean parse(String booleanString) throws Exception {
        if ("on,1,true,yes".indexOf(booleanString.toLowerCase()) > -1) {
            return true;
        } else if ("off,0,false,no".indexOf(booleanString.toLowerCase()) > -1) {
            return false;
        }
        throw new Exception("Boolean string '" + booleanString + "' is invalid use {0,1,true,false,yes,no,on,off}");
    }
    
    static public Boolean isParsable(String booleanString) throws Exception {
        if ("on,1,true,yes".indexOf(booleanString.toLowerCase()) > -1) {
            return true;
        } else if ("off,0,false,no".indexOf(booleanString.toLowerCase()) > -1) {
            return false;
        }
        throw new Exception("Boolean string '" + booleanString + "' is invalid use {0,1,true,false,yes,no,on,off}");
    }
}
