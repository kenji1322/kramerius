package cz.incad.kramerius.virtualcollections.database;

import static cz.incad.kramerius.database.cond.ConditionsInterpretHelper.versionCondition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import cz.incad.kramerius.database.VersionService;
import cz.incad.kramerius.processes.database.ProcessDatabaseInitializator;
import cz.incad.kramerius.statistics.database.StatisticDatabaseInitializator;
import cz.incad.kramerius.utils.DatabaseUtils;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.database.JDBCUpdateTemplate;

public class CDKProcessingDatabaseInitalizator {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ProcessDatabaseInitializator.class.getName());
    
    public static void initDatabase(Connection connection, VersionService versionService) {
        try {
            String v = versionService.getVersion();
            String version = versionService.getVersion();
            if (version == null || (versionCondition(version, "<", "6.6.7"))) {
            	createStatesTables(connection);
            } 
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
    }
    
    /**
     * @param connection
     * @throws IOException
     * @throws SQLException
     */
    private static void createStatesTables(Connection connection) throws SQLException, IOException {
        InputStream is = CDKProcessingDatabaseInitalizator.class.getResourceAsStream("res/initstatesdb.sql");
        JDBCUpdateTemplate template = new JDBCUpdateTemplate(connection, false);
        template.setUseReturningKeys(false);
        String stringSQL = IOUtils.readAsString(is, Charset.forName("UTF-8"), true);
        template.executeUpdate(stringSQL);
    }

}
