package cn.com.xuxiaowei.nacos.sentinel.repository;

import cn.com.xuxiaowei.nacos.sentinel.entity.Discovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Nacos 注册中心 服务接口 实现类
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Repository
public class NacosDiscoveryRepository {

	private static final String COLUMN_NAMES = "id, service_name, ip, port";

	private static final String TABLE_NAME = "discovery";

	private static final String SAVE = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAMES + ") VALUES (?, ?, ?, ?)";

	private static final String ALL = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME;

	private static final String GET_BY_SERVICE_NAME = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME
			+ " WHERE service_name = ?";

	private static final String GET_BY_INSTANCE = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME
			+ " WHERE service_name = ? AND ip = ? AND port = ?";

	private static final String DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void save(Discovery discovery) {
		jdbcTemplate.update(SAVE, discovery.getId(), discovery.getServiceName(), discovery.getIp(),
				discovery.getPort());
	}

	public List<Discovery> all() {
		return jdbcTemplate.query(ALL, (rs, rowNum) -> {
			Discovery discovery = new Discovery();
			discovery.setId(rs.getString("id"));
			discovery.setServiceName(rs.getString("service_name"));
			discovery.setIp(rs.getString("ip"));
			discovery.setPort(rs.getInt("port"));
			return discovery;
		});
	}

	public List<Discovery> listByServiceName(String serviceName) {
		try {
			return jdbcTemplate.query(GET_BY_SERVICE_NAME, new Object[] { serviceName }, new int[] { Types.VARCHAR },
					(rs, rowNum) -> convert(rs));
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Discovery getByUnique(String serviceName, String ip, int port) {
		try {
			return jdbcTemplate.queryForObject(GET_BY_INSTANCE, new Object[] { serviceName, ip, port },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.INTEGER }, (rs, rowNum) -> convert(rs));
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void deleteById(String id) {
		jdbcTemplate.update(DELETE_BY_ID, id);
	}

	private Discovery convert(ResultSet rs) throws SQLException {
		Discovery discovery = new Discovery();
		discovery.setId(rs.getString("id"));
		discovery.setServiceName(rs.getString("service_name"));
		discovery.setIp(rs.getString("ip"));
		discovery.setPort(rs.getInt("port"));
		return discovery;
	}

}
