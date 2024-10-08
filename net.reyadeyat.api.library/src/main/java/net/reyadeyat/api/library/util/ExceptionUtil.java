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
public class ExceptionUtil {
 
    static public String getStackTrace(Exception  exception) {
        return ExceptionUtil.getStackTrace(exception, new StringBuilder()).toString();
    }
    
    static public StringBuilder getStackTrace(Exception  exception, StringBuilder string_builder) {
        if (string_builder == null) {
            string_builder = new StringBuilder();
        }
        string_builder.append("Exception [").append(exception.getMessage()).append("]\n");
        StackTraceElement[] ste_list = exception.getStackTrace();
        for (int i = 0; i < ste_list.length; i++) {
            StackTraceElement ste = ste_list[i];
            string_builder.append(ste.toString()).append("\n");
        }
        return string_builder;
    }
}
