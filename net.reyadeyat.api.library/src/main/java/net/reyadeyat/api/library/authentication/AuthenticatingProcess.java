package net.reyadeyat.api.library.authentication;

import net.reyadeyat.api.library.json.JsonResultset;
import com.google.gson.JsonArray;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.reyadeyat.api.library.data.source.DataSourceService;
import net.reyadeyat.api.library.process.BackgroundProcess;
import net.reyadeyat.api.library.process.BackgroundProcessScheduler;

public class AuthenticatingProcess extends BackgroundProcess {

    private final static String process_name = "Authicating Process";

    public AuthenticatingProcess(BackgroundProcessScheduler background_process_scheduler) throws Exception {
        super(background_process_scheduler);
        startTask(this::authenticatingProcess);
    }
    
    public void authenticatingProcess() {
        
        try {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, process_name + " doing some work on background thread '" + Thread.currentThread().getName() + "'");
            String sql = "SELECT * FROM `automation`.`user`";
            try (Connection connection = DataSourceService.getConnection()) {
                try (PreparedStatement prepared_statement = connection.prepareStatement(sql)) {
                    try (ResultSet result_set = prepared_statement.executeQuery()) {
                        JsonArray arr = JsonResultset.resultset(result_set);
                        arr = arr;
                    }
                }
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception ex) {
            throw new RuntimeException(process_name + " Process Runtime Exception '" + background_process_scheduler.getDescription() + "." + process_name + "', Contact Administrator\n" + ex.getMessage(), ex);
        }
    }
}
