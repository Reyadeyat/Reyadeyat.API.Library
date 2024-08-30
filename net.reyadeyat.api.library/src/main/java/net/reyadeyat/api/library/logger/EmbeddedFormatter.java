package net.reyadeyat.api.library.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class EmbeddedFormatter extends Formatter {
    //# EmbeddedFormatter for no break set break=
    //net.reyadeyat.api.library.logging.print_break=~
    //net.reyadeyat.api.library.logging.print_logger=false
    //net.reyadeyat.api.library.logging.print_time=false

    final static private SimpleFormatter simple_formatter = new SimpleFormatter();
    private String print_break;
    private Boolean print_logger;
    private Boolean print_date;
    final static private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSSSSS");
    
    public EmbeddedFormatter(String print_break, Boolean print_logger, Boolean print_date) {
        this.print_break = print_break;
        this.print_logger = print_logger;
        this.print_date = print_date;
        this.print_break = this.print_break == null || this.print_break.length() == 0 ? "\n" : "\n"+this.print_break+"\n";
    }
    
    @Override
    public String format(LogRecord record) {
        if (!print_logger || !print_date) {
            StringBuilder message = new StringBuilder(this.print_break);
            if (print_date == true) {
                ZonedDateTime zdt = ZonedDateTime.ofInstant(
                record.getInstant(), ZoneId.systemDefault());
                message.append(sdf.format(zdt));
            }
            if (message.length() > this.print_break.length()) {
                message.append(" ");
            }
            if (print_logger == true) {
                String source;
                if (record.getSourceClassName() != null) {
                    source = record.getSourceClassName();
                    if (record.getSourceMethodName() != null) {
                       source += " " + record.getSourceMethodName();
                    }
                } else {
                    source = record.getLoggerName();
                }
                message.append(source);
            }
            if (message.length() > this.print_break.length()) {
                message.append("\n");
            }
            message.append(simple_formatter.formatMessage(record));
            if (record.getThrown() != null) {
                message.append("\n").append(simple_formatter.formatMessage(record)).append("\n");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                message.append(sw.toString());
                return message.toString();
            }
            return message.toString();
        }
        return simple_formatter.format(record);
    }
}
