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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
public class UTCTimestamp {
    final static SimpleDateFormat sdf = new SimpleDateFormat("EEE-yyyy-MM-dd-hh_mm_ss.SSSSSS", Locale.US);

    static public String getCurrentTimestamp() {
        Instant verification_instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
        return verification_instant.toString().replace("T", " ").replace("Z", "");
    }
    
    static public Long getMinutesBetween(Instant instant_1, Instant instant_2) {
        //LocalDateTime ldt = LocalDateTime.now();
        //ZonedDateTime now = Instant.now();//.atZone(ZoneOffset.UTC);
        //ZonedDateTime sixMinutesBehind = now
        //    .withZoneSameInstant(ZoneId.of("Asia/Singapore"))
        //    .minusMinutes(6);
    
        return ChronoUnit.MINUTES.between(instant_1, instant_2);
        /*long time_1 = instant_1.getTime();
        long time_2 = instant_2.getTime();
        long sign = 1;
        if (time_1 > time_2) {
            sign = -1;
        }
        long diff = date_2.getTime() - date_1.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) * sign;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) * sign; 
        Instant verification_instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
        return verification_instant.toString().replace("T", " ").replace("Z", "");*/
    }
    
    static public Instant now(ZoneOffset zone_offset) {
        return Instant.now().atOffset(ZoneOffset.UTC).toInstant();
    }
    
    static public Instant now(ZoneId zone_id) {
        return Instant.now().atZone(zone_id).toInstant();
    }
    
    static public Instant now(String zone_code) {
        return Instant.now().atZone(ZoneId.of(zone_code)).toInstant();
    }
    
    static public String getCurrentTimeString() {
        return getCurrentTimeString(null);
    }
    
    static public String getCurrentTimeString(Locale locale) {
        synchronized (sdf) {
            if (locale == null) {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf.format(Date.from(Instant.now()));
            }
            SimpleDateFormat new_sdf = new SimpleDateFormat("EEE-yyyy-MM-dd-hh_mm_ss.SSSSSS", Locale.US);
            new_sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new_sdf.format(Date.from(Instant.now()));
        }
    }
}
