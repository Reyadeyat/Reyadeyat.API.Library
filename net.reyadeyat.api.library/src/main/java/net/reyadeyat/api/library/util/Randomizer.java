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

import java.util.Random;

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
public class Randomizer {
    
    final static public void init(Random randoms, Integer random_lower_bound, Integer random_upper_bound) {
        int r = randoms.nextInt(random_upper_bound-random_lower_bound+1)+random_lower_bound;
        for (int i = 0; i < r; i++) {
            randoms.nextInt(random_upper_bound-random_lower_bound+1);
        }
    }
    
    final static public Integer random(Random randoms, Integer random_lower_bound, Integer random_upper_bound) throws Exception {
        return randoms.nextInt(random_upper_bound-random_lower_bound)+random_lower_bound;
    }
    
    final static public <T extends Number> T random(Random randoms, T initial, Class<T> type) throws Exception {
        if (initial instanceof Integer) {
            return type.cast(randoms.nextInt());
        } else if (initial instanceof Long) {
            return type.cast(randoms.nextLong());
        } else if (initial instanceof Float) {
            return type.cast(randoms.nextFloat());
        } else if (initial instanceof Double) {
            return type.cast(randoms.nextDouble());
        }
        throw new Exception("Unimplemented Number type '"+initial.getClass().getCanonicalName()+"'");
    }
}
