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
public class Hunter {
    Throwable throwable;
    public void hunt(Throwable throwable) {
        this.throwable = throwable;
    }
    public Throwable throwable() {
        return this.throwable;
    }
    public String message() {
        StringBuilder b = new StringBuilder();
        Throwable t = this.throwable;
        b.append(t.getMessage());
        t = t.getCause();
        while (t != null) {
            b.append(";").append(t.getMessage());
            t = t.getCause();
        }
        return b.toString();
    }
    public Throwable cause() {
        return this.throwable.getCause();
    }
}
