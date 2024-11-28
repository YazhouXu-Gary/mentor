package com.innocomp.demo.sharding.sharding_sphere_demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShardingSphereDemoApplicationTest {
	private static final Logger LOG = LoggerFactory.getLogger(ShardingSphereDemoApplication.class);

	@Autowired
	DataSource dataSource;

	private RandomGeneratorFactory<RandomGenerator> rf = RandomGeneratorFactory.getDefault();


	private static final String LOG_FORMAT = "---------- {} = {} ----------";

	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testSelectWithShardingKey() throws SQLException{
		String sql = "select * from user_request where user_id=1";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			Assertions.assertNotNull(con);
			rba.commit();
		} 
	}

	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testInsertWithSnowFlake() throws SQLException{
		String sql = "insert into user_request(user_id,request_title,request_type) values (?,?,?)";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);) {
			Assertions.assertNotNull(con);
			int parameterIndex = 1;
			ps.setLong(parameterIndex++, 1);
			ps.setString(parameterIndex++, "Make an appointment");
			ps.setByte(parameterIndex++, (byte)1);

			int count = ps.executeUpdate();
			Assertions.assertEquals(1, count);
			rba.commit();
		}		
	}

	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testBatchInsertWithSnowFlake() throws SQLException{
		String sql = "insert into user_request(user_id,request_title,request_type) values (?,?,?)";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);) {
			Assertions.assertNotNull(con);

			long start = System.currentTimeMillis();
			int numOfBatches = 50;
			for(int i = 0;i< numOfBatches;i++){
				rba.batchStart();
				long sizePerBatch = 5000;
				PrimitiveIterator.OfLong iter =	rf.create().longs(sizePerBatch,1,10000).iterator();
				while (iter.hasNext()) {
					int parameterIndex = 1;

					ps.setLong(parameterIndex++, Math.abs(iter.nextLong()));
					ps.setString(parameterIndex++, "Cancel an appointment");
					ps.setByte(parameterIndex++, (byte)rf.create().nextInt(1, 4));
					ps.addBatch();				
				}				
				ps.executeBatch();
				rba.batchCommit();
			}
			
			long duration = System.currentTimeMillis() - start;
			LOG.info(LOG_FORMAT,"Insertion Time",duration);	
		}		
	}	

	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testComplexSelectWithShardingKey() throws SQLException{
		String sql = """
				select user_id,request_type,count(request_id) as dupC from user_request where user_id=5 group by request_type order by dupC desc limit 6
				""";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);
				) {
			Assertions.assertNotNull(con);
			long start = System.currentTimeMillis();
	
			ResultSet rs = ps.executeQuery();
			if(rs!=null){
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (rs.next()) {	
					List<ColumnValue> rsRow = new ArrayList<ColumnValue>();					
					for(int i = 1;i<=columnCount;i++){
						rsRow.add(new ColumnValue(metaData.getColumnName(i), rs.getObject(i)));
					}
			
				}
				rs.close();
			}			
			rba.commit();
			long duration = System.currentTimeMillis() - start;
			LOG.info(LOG_FORMAT,"Query RTT",duration);			
		} 
	}

	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testComplexSelectWithoutShardingKey() throws SQLException{
		String sql = """
				select request_type,count(request_id) as dupC from user_request group by request_type order by dupC desc limit 6
				""";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);
				) {
			Assertions.assertNotNull(con);

			long start = System.currentTimeMillis();

			ResultSet rs = ps.executeQuery();
			if(rs!=null){
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (rs.next()) {	
					List<ColumnValue> rsRow = new ArrayList<ColumnValue>();					
					for(int i = 1;i<=columnCount;i++){
						rsRow.add(new ColumnValue(metaData.getColumnName(i), rs.getObject(i)));
					}
		
				}
				rs.close();
			}			
			rba.commit();
			long duration = System.currentTimeMillis() - start;
			LOG.info(LOG_FORMAT,"Query RTT",duration);			
		} 
	}	

	
	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testSelectWithPK() throws SQLException{
		String sql = "select user_id from user_request where user_id=40 and request_id=1067568994531147777";
	

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);
				) {
			Assertions.assertNotNull(con);
			long start = System.currentTimeMillis();
	
			ResultSet rs = ps.executeQuery();
			
			if(rs!=null){
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (rs.next()) {	
					List<ColumnValue> rsRow = new ArrayList<ColumnValue>();					
					for(int i = 1;i<=columnCount;i++){
						rsRow.add(new ColumnValue(metaData.getColumnName(i), rs.getObject(i)));
					}
			
				}
				rs.close();
			}	
				 	
			rba.commit();
			long duration = System.currentTimeMillis() - start;
			LOG.info(LOG_FORMAT,"Query RTT",duration);	
		} 
	}	


	@Test
	@Disabled("Enable only when environment sets up correctly")
	void testDeleteWithShadingKey() throws SQLException{
		String sql = "delete from user_request";

		try (Connection con = dataSource.getConnection();
				RollBackAware rba = new RollBackAware(con, false);
				PreparedStatement ps = con.prepareStatement(sql);) {
			Assertions.assertNotNull(con);					
			ps.executeUpdate();

			rba.commit();
		}
	}


	public record ColumnValue(String cName, Object cValue) {
		public ColumnValue{
			Objects.requireNonNull(cName);
		}
	} 

	static class RollBackAware implements AutoCloseable{
		private Connection con = null;
		private boolean committed = false;
		private boolean previousCommitMode = true;
	
		public RollBackAware(Connection con, boolean commitMode) throws SQLException{
			this.con = con;
			previousCommitMode = con.getAutoCommit();
			this.con.setAutoCommit(commitMode);
		}

		public void commit() throws SQLException{
			con.commit();
			committed = true;
		}

		public void batchStart(){
			committed = false;
		}

		public void batchCommit() throws SQLException{
			commit();
		}

		@Override
		public void close() throws SQLException {
			if(!committed){
				con.rollback();
			}
			con.setAutoCommit(previousCommitMode);

		}
		
	}

}
