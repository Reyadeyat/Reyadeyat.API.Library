package net.reyadeyat.api.library.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EmbeddedLevel extends Level {
    //# OFF | SEVERE | WARNING | INFO | ( ( NOTE | STEP | DATA | SQL | TRACE ) | CONFIG ) | FINE | FINER | FINEST | ALL
    //log_level=CONFIG
    public final static EmbeddedLevel NOTE = new EmbeddedLevel("NOTE", Level.CONFIG.intValue()+99);
    public final static EmbeddedLevel STEP = new EmbeddedLevel("STEP", Level.CONFIG.intValue()+80);
    public final static EmbeddedLevel DATA = new EmbeddedLevel("DATA", Level.CONFIG.intValue()+50);
    public final static EmbeddedLevel SQL = new EmbeddedLevel("SQL", Level.CONFIG.intValue()+10);
    public final static EmbeddedLevel TRACE = new EmbeddedLevel("TRACE", Level.CONFIG.intValue()+1);
    //Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "Note: Value = X" + X);
    //Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "STEP: STEP [1]");
    //Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "DATA: TRANSACTION DATE " + date_string);
    //Logger.getLogger(getClass().getName()).log(EmbeddedLevel.DATA, "SQL: SQL [8] WHERE employee=" + employee_id);
    //Logger.getLogger(getClass().getName()).log(EmbeddedLevel.TRACE, "TRACE: LARG DATA Dumps " + json_string_builder.toString());
    
    protected EmbeddedLevel(String name, int value) {
        super(name, value);
    }
    
    public final static void init() {}
    
    public final static void print(String message) {
        Logger.getLogger(EmbeddedLevel.class.getName()).log(EmbeddedLevel.INFO, message);
    }
    
    /*
    if (Logger.getLogger(getClass().getName()).isLoggable(EmbeddedLevel.TRACE) == true) {
        synchronized(System.out) {
        synchronized(System.err) {
            Writer writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
            Logger.getLogger(getClass().getName()).log(EmbeddedLevel.TRACE, "Repor file is succefully loaded:\n"+chunked_file.toString());
            gson.toJson(tree_json, writer);
        }
    }
    */
}
