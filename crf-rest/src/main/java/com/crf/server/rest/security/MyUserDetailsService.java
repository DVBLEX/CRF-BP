package com.crf.server.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private String       queryString = "";
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT");
        builder.append("     operators.id,");
        builder.append("     operators.code,");
        builder.append("     operators.customer_id,");
        builder.append("     operators.first_name,");
        builder.append("     operators.last_name,");
        builder.append("     operators.username,");
        builder.append("     operators.password,");
        builder.append("     operators.is_active,");
        builder.append("     operators.is_locked,");
        builder.append("     operators.login_failure_count,");
        builder.append("     operators.role_id,");
        builder.append("     operators.date_last_password,");
        builder.append("     operators.is_credentials_expired,");
        builder.append("     operators.date_locked,");
        builder.append("     system_parameters.login_lock_period,");
        builder.append("     system_parameters.login_password_valid_period,");
        builder.append("     customers.type,");
        builder.append("     customers.category");

        builder.append(" FROM operators");
        builder.append(" INNER JOIN system_parameters ON 1=1");
        builder.append(" LEFT JOIN customers ON operators.customer_id = customers.id");

        builder.append(" WHERE");
        builder.append("    operators.username = ? ");

        builder.append(" LIMIT 1");

        queryString = builder.toString();

        MyUserDetails myUserDetails = jdbcTemplate.query(queryString, rs -> {

            MyUserDetails myUserDetails1 = null;

            if (rs.next()) {
                myUserDetails1 = new MyUserDetails(rs.getLong("id"), rs.getString("code"), rs.getLong("customer_id"), rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("username"), rs.getString("password"), rs.getBoolean("is_active"), rs.getBoolean("is_locked"), rs.getInt("login_failure_count"),
                    rs.getInt("role_id"), rs.getTimestamp("date_last_password"), rs.getBoolean("is_credentials_expired"), rs.getTimestamp("date_locked"), rs.getLong("type"),
                    rs.getInt("category"), rs.getInt("login_lock_period"), rs.getInt("login_password_valid_period"));
            }
            return myUserDetails1;
        }, username);

        if (myUserDetails == null)
            throw new UsernameNotFoundException("User not found.");

        return myUserDetails;
    }
}
