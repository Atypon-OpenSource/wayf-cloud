package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.data.Institution;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InstitutionDaoMySqlImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoMySqlImpl.class);

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306");
        dataSource.setUsername("root");
        dataSource.setPassword("test");
    }

    // Eventually we'll want to source these from a properties file
    private static final String CREATE_SQL = "INSERT INTO wayf.institution (id, name, description) VALUES (?, ?, ?)";

    public InstitutionDaoMySqlImpl() {}

    @Override
    public Institution create(Institution institution) {
        LOG.debug("Creating institution [{}] in MySQL", institution);
        institution.setId(UUID.randomUUID().toString());

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(CREATE_SQL);

            statement.setString(1, institution.getId());
            statement.setString(2, institution.getName());
            statement.setString(3, institution.getDescription());
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Could not create institution", e);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Could not close connection", e);
                throw new RuntimeException(e);
            }
        }

        return institution;
    }

    @Override
    public Institution read(String id) {
        throw new UnsupportedOperationException("MySQL Read not implemented yet");
    }

    @Override
    public Institution update(Institution institution) {
        throw new UnsupportedOperationException("MySQL Update not implemented yet");
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException("MySQL Delete not implemented yet");
    }
}
