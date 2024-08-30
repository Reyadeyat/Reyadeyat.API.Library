package net.reyadeyat.api.library.time;

public class TimeZone {
    
}

/*
https://stackoverflow.com/questions/39506891/why-is-zoneoffset-utc-zoneid-ofutc
208

The answer comes from the javadoc of ZoneId (emphasis mine) ...

    A ZoneId is used to identify the rules used to convert between an Instant and a LocalDateTime. There are two distinct types of ID:

        Fixed offsets - a fully resolved offset from UTC/Greenwich, that uses the same offset for all local date-times
        Geographical regions - an area where a specific set of rules for finding the offset from UTC/Greenwich apply

    Most fixed offsets are represented by ZoneOffset. Calling normalized() on any ZoneId will ensure that a fixed offset ID will be represented as a ZoneOffset.

... and from the javadoc of ZoneId#of (emphasis mine):

    This method parses the ID producing a ZoneId or ZoneOffset. A ZoneOffset is returned if the ID is 'Z', or starts with '+' or '-'.

The argument id is specified as "UTC", therefore it will return a ZoneId with an offset, which also presented in the string form:

System.out.println(now.withZoneSameInstant(ZoneOffset.UTC));
System.out.println(now.withZoneSameInstant(ZoneId.of("UTC")));

Outputs:

2017-03-10T08:06:28.045Z
2017-03-10T08:06:28.045Z[UTC]

As you use the equals method for comparison, you check for object equivalence. Because of the described difference, the result of the evaluation is false.

When the normalized() method is used as proposed in the documentation, the comparison using equals will return true, as normalized() will return the corresponding ZoneOffset:

    Normalizes the time-zone ID, returning a ZoneOffset where possible.

now.withZoneSameInstant(ZoneOffset.UTC)
    .equals(now.withZoneSameInstant(ZoneId.of("UTC").normalized())); // true

As the documentation states, if you use "Z" or "+0" as input id, of will return the ZoneOffset directly and there is no need to call normalized():

now.withZoneSameInstant(ZoneOffset.UTC).equals(now.withZoneSameInstant(ZoneId.of("Z"))); //true
now.withZoneSameInstant(ZoneOffset.UTC).equals(now.withZoneSameInstant(ZoneId.of("+0"))); //true

To check if they store the same date time, you can use the isEqual method instead:

now.withZoneSameInstant(ZoneOffset.UTC)
    .isEqual(now.withZoneSameInstant(ZoneId.of("UTC"))); // true

Sample

System.out.println("equals - ZoneId.of(\"UTC\"): " + nowZoneOffset
        .equals(now.withZoneSameInstant(ZoneId.of("UTC"))));
System.out.println("equals - ZoneId.of(\"UTC\").normalized(): " + nowZoneOffset
        .equals(now.withZoneSameInstant(ZoneId.of("UTC").normalized())));
System.out.println("equals - ZoneId.of(\"Z\"): " + nowZoneOffset
        .equals(now.withZoneSameInstant(ZoneId.of("Z"))));
System.out.println("equals - ZoneId.of(\"+0\"): " + nowZoneOffset
        .equals(now.withZoneSameInstant(ZoneId.of("+0"))));
System.out.println("isEqual - ZoneId.of(\"UTC\"): "+ nowZoneOffset
        .isEqual(now.withZoneSameInstant(ZoneId.of("UTC"))));

Output:

equals - ZoneId.of("UTC"): false
equals - ZoneId.of("UTC").normalized(): true
equals - ZoneId.of("Z"): true
equals - ZoneId.of("+0"): true
isEqual - ZoneId.of("UTC"): true


*/




//https://stackoverflow.com/questions/19023978/should-mysql-have-its-timezone-set-to-utc
/*


It seems that it does not matter what timezone is on the server as long as you have the time set right for the current timezone, know the timezone of the datetime columns that you store, and are aware of the issues with daylight savings time.

On the other hand if you have control of the timezones of the servers you work with then you can have everything set to UTC internally and never worry about timezones and DST.

Here are some notes I collected of how to work with timezones as a form of cheatsheet for myself and others which might influence what timezone the person will choose for his/her server and how he/she will store date and time.
MySQL Timezone Cheatsheet

Notes:

    Changing the timezone will not change the stored datetime or timestamp, but it will select a different datetime from timestamp columns
    Warning! UTC has leap seconds, these look like '2012-06-30 23:59:60' and can be added randomly, with 6 months prior notice, due to the slowing of the earths rotation

    GMT confuses seconds, which is why UTC was invented.

    Warning! different regional timezones might produce the same datetime value due to daylight savings time
    The timestamp column only supports dates 1970-01-01 00:00:01 to 2038-01-19 03:14:07 UTC, due to a limitation.

    Internally a MySQL timestamp column is stored as UTC but when selecting a date MySQL will automatically convert it to the current session timezone.

    When storing a date in a timestamp, MySQL will assume that the date is in the current session timezone and convert it to UTC for storage.
    MySQL can store partial dates in datetime columns, these look like "2013-00-00 04:00:00"
    MySQL stores "0000-00-00 00:00:00" if you set a datetime column as NULL, unless you specifically set the column to allow null when you create it.
    Read this

To select a timestamp column in UTC format

no matter what timezone the current MySQL session is in:

SELECT 
CONVERT_TZ(`timestamp_field`, @@session.time_zone, '+00:00') AS `utc_datetime` 
FROM `table_name`

You can also set the sever or global or current session timezone to UTC and then select the timestamp like so:

SELECT `timestamp_field` FROM `table_name`

To select the current datetime in UTC:

SELECT UTC_TIMESTAMP();
SELECT UTC_TIMESTAMP;
SELECT CONVERT_TZ(NOW(), @@session.time_zone, '+00:00');

Example result: 2015-03-24 17:02:41
To select the current datetime in the session timezone

SELECT NOW();
SELECT CURRENT_TIMESTAMP;
SELECT CURRENT_TIMESTAMP();

To select the timezone that was set when the server launched

SELECT @@system_time_zone;

Returns "MSK" or "+04:00" for Moscow time for example, there is (or was) a MySQL bug where if set to a numerical offset it would not adjust the Daylight savings time
To get the current timezone

SELECT TIMEDIFF(NOW(), UTC_TIMESTAMP);

It will return 02:00:00 if your timezone is +2:00.
To get the current UNIX timestamp (in seconds):

SELECT UNIX_TIMESTAMP(NOW());
SELECT UNIX_TIMESTAMP();

To get the timestamp column as a UNIX timestamp

SELECT UNIX_TIMESTAMP(`timestamp`) FROM `table_name`

To get a UTC datetime column as a UNIX timestamp

SELECT UNIX_TIMESTAMP(CONVERT_TZ(`utc_datetime`, '+00:00', @@session.time_zone)) FROM `table_name`

Get a current timezone datetime from a positive UNIX timestamp integer

SELECT FROM_UNIXTIME(`unix_timestamp_int`) FROM `table_name`

Get a UTC datetime from a UNIX timestamp

SELECT CONVERT_TZ(FROM_UNIXTIME(`unix_timestamp_int`), @@session.time_zone, '+00:00') 
FROM `table_name`

Get a current timezone datetime from a negative UNIX timestamp integer

SELECT DATE_ADD('1970-01-01 00:00:00',INTERVAL -957632400 SECOND) 

There are 3 places where the timezone might be set in MySQL:

Note: A timezone can be set in 2 formats:

    an offset from UTC: '+00:00', '+10:00' or '-6:00'
    as a named time zone: 'Europe/Helsinki', 'US/Eastern', or 'MET'

    Named time zones can be used only if the time zone information tables in the mysql database have been created and populated.

in the file "my.cnf"

default_time_zone='+00:00'

or

timezone='UTC'

@@global.time_zone variable

To see what value they are set to

SELECT @@global.time_zone;

To set a value for it use either one:

SET GLOBAL time_zone = '+8:00';
SET GLOBAL time_zone = 'Europe/Helsinki';
SET @@global.time_zone='+00:00';

@@session.time_zone variable

SELECT @@session.time_zone;

To set it use either one:

SET time_zone = 'Europe/Helsinki';
SET time_zone = "+00:00";
SET @@session.time_zone = "+00:00";

both "@@global.time_zone variable" and "@@session.time_zone variable" might return "SYSTEM" which means that they use the timezone set in "my.cnf".

For timezone names to work (even for default-time-zone) you must setup your timezone information tables need to be populated: http://dev.mysql.com/doc/refman/5.1/en/time-zone-support.html

Note: you can not do this as it will return NULL:

SELECT 
CONVERT_TZ(`timestamp_field`, TIMEDIFF(NOW(), UTC_TIMESTAMP), '+00:00') AS `utc_datetime` 
FROM `table_name`

Setup mysql timezone tables

For CONVERT_TZ to work, you need the timezone tables to be populated

SELECT * FROM mysql.`time_zone` ;
SELECT * FROM mysql.`time_zone_leap_second` ;
SELECT * FROM mysql.`time_zone_name` ;
SELECT * FROM mysql.`time_zone_transition` ;
SELECT * FROM mysql.`time_zone_transition_type` ;

If they are empty, then fill them up by running this command

mysql_tzinfo_to_sql /usr/share/zoneinfo | mysql -u root -p mysql

if this command gives you the error "data too long for column 'abbreviation' at row 1", then it might be caused by a NULL character being appended at the end of the timezone abbreviation

the fix being to run this

mysql_tzinfo_to_sql /usr/share/zoneinfo | mysql -u root -p mysql
(if the above gives error "data too long for column 'abbreviation' at row 1")
mysql_tzinfo_to_sql /usr/share/zoneinfo > /tmp/zut.sql

echo "SET SESSION SQL_MODE = '';" > /tmp/mysql_tzinfo_to.sql
cat /tmp/zut.sql >> /tmp/mysql_tzinfo_to.sql

mysql --defaults-file=/etc/mysql/my.cnf --user=verifiedscratch -p mysql < /tmp/mysql_tzinfo_to.sql

(make sure your servers dst rules are up to date zdump -v Europe/Moscow | grep 2011 https://chrisjean.com/updating-daylight-saving-time-on-linux/)
See the full DST (Daylight Saving Time) transition history for every timezone

SELECT 
tzn.Name AS tz_name,
tztt.Abbreviation AS tz_abbr,
tztt.Is_DST AS is_dst,
tztt.`Offset` AS `offset`,
DATE_ADD('1970-01-01 00:00:00',INTERVAL tzt.Transition_time SECOND)  AS transition_date
FROM mysql.`time_zone_transition` tzt
INNER JOIN mysql.`time_zone_transition_type` tztt USING(Time_zone_id, Transition_type_id)
INNER JOIN mysql.`time_zone_name` tzn USING(Time_zone_id)
-- WHERE tzn.Name LIKE 'Europe/Moscow' -- Moscow has weird DST changes
ORDER BY tzt.Transition_time ASC

CONVERT_TZ also applies any necessary DST changes based on the rules in the above tables and the date that you use.

Note:
According to the docs, the value you set for time_zone does not change, if you set it as "+01:00" for example, then the time_zone will be set as an offset from UTC, which does not follow DST, so it will stay the same all year round.

Only the named timezones will change time during daylight savings time.

Abbreviations like CET will always be a winter time and CEST will be summer time while +01:00 will always be UTC time + 1 hour and both won't change with DST.

The system timezone will be the timezone of the host machine where mysql is installed (unless mysql fails to determine it)

You can read more about working with DST here

related questions:

    How do I set the time zone of MySQL?
    MySql - SELECT TimeStamp Column in UTC format
    How to get Unix timestamp in MySQL from UTC time?
    Converting Server MySQL TimeStamp To UTC
    https://dba.stackexchange.com/questions/20217/mysql-set-utc-time-as-default-timestamp
    How do I get the current time zone of MySQL?
    MySQL datetime fields and daylight savings time -- how do I reference the "extra" hour?
    Converting negative values from FROM_UNIXTIME

Sources:

    https://bugs.mysql.com/bug.php?id=68861
    http://dev.mysql.com/doc/refman/5.0/en/date-and-time-functions.html
    http://dev.mysql.com/doc/refman/5.1/en/datetime.html
    http://en.wikipedia.org/wiki/Coordinated_Universal_Time
    http://shafiqissani.wordpress.com/2010/09/30/how-to-get-the-current-epoch-time-unix-timestamp/
    https://web.ivy.net/~carton/rant/MySQL-timezones.txt


*/