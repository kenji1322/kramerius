package cz.incad.kramerius.virtualcollections.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import cz.incad.kramerius.processes.NotReadyException;
import cz.incad.kramerius.utils.database.JDBCQueryTemplate;
import cz.incad.kramerius.utils.database.JDBCUpdateTemplate;
import cz.incad.kramerius.virtualcollections.CDKStateSupport;
import cz.incad.kramerius.virtualcollections.CDKStateSupportException;

public class CDKStateSupportImpl implements CDKStateSupport {

	public static final Logger LOGGER = Logger.getLogger(CDKStateSupportImpl.class.getName());

	@Inject
	@Named("kramerius4")
	Provider<Connection> connectionProvider = null;

	@Override
	public synchronized void insert(String pid) throws CDKStateSupportException {
		Connection connection = null;
		try {
			connection = connectionProvider.get();
			if (connection == null)
				throw new NotReadyException("connection not ready");

			new JDBCUpdateTemplate(connection).executeUpdate("insert into cdk_states(PID, STATE) values(?, ?)", pid,
					CDKState.HARVESTED.name());
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new CDKStateSupportException(e);
		}

	}

	@Override
	public synchronized void remove(String pid) throws CDKStateSupportException {
		Connection connection = null;
		try {
			connection = connectionProvider.get();
			if (connection == null)
				throw new NotReadyException("connection not ready");

			new JDBCUpdateTemplate(connection).executeUpdate("delete from  cdk_states where PID = ?", pid);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new CDKStateSupportException(e);
		}
	}

	@Override
	public synchronized void changeState(String pid, CDKState state) throws CDKStateSupportException {
		Connection connection = null;
		try {
			connection = connectionProvider.get();
			if (connection == null)
				throw new NotReadyException("connection not ready");

			new JDBCUpdateTemplate(connection).executeUpdate("update cdk_states set STATE = ? where PID = ?",
					state.name(), pid);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new CDKStateSupportException(e);
		}
	}

	@Override
	public synchronized CDKState getState(String pid) throws CDKStateSupportException {
		Connection connection = connectionProvider.get();
		if (connection == null)
			throw new NotReadyException("connection not ready");

		List<CDKState> states = new JDBCQueryTemplate<CDKState>(connection) {

			@Override
			public boolean handleRow(ResultSet rs, List<CDKState> returnsList) throws SQLException {
				String string = rs.getString("STATE");
				returnsList.add(CDKState.valueOf(string));
				return super.handleRow(rs, returnsList);
			}

		}.executeQuery("select STATE from cdk_states where PID = ? ", pid);

		if (!states.isEmpty()) {
			return states.get(0);

		} else {
			return null;
		}
	}

	@Override
	public List<String> getPids(CDKState state) throws CDKStateSupportException {
		Connection connection = connectionProvider.get();
		if (connection == null)
			throw new NotReadyException("connection not ready");
		List<String> pids = new JDBCQueryTemplate<String>(connection) {

			@Override
			public boolean handleRow(ResultSet rs, List<String> returnsList) throws SQLException {
				String string = rs.getString("PID");
				returnsList.add(string);
				return super.handleRow(rs, returnsList);
			}

		}.executeQuery("select PID from cdk_states where STATE = ? ", state.name());
		
		return pids;
	}
	
	

}
