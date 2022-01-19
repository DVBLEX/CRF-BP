package com.crf.server.web.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.crf.server.base.common.ServerConstants;

public class MyUserDetails implements UserDetails {

    private static final long   serialVersionUID = 1L;

    private static final ZoneId currentZone      = ZoneId.systemDefault();

    private long                id;
    private String              code;
    private long                customerId;
    private String              firstname;
    private String              lastname;
    private String              username;
    private String              password;
    private boolean             isEnabled;
    private boolean             isLocked;
    private int                 loginFailureCount;
    private int                 role;
    private Date                dateLastPassword;
    private Date                dateLocked;
    private boolean             isCredentialsExpired;
    private long                customerType;
    private int                 customerCategory;
    private int                 loginLockHours;
    private int                 passwordValidDays;

    public MyUserDetails(long id, String code, long customerId, String firstname, String lastname, String username, String password, boolean enabled, boolean isLocked,
        int loginFailureCount, int role, Date dateLastPassword, boolean isCredentialsExpired, Date dateLocked, long customerType, int customerCategory, int loginLockHours,
        int passwordValidDays) {

        this.id = id;
        this.code = code;
        this.customerId = customerId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.isEnabled = enabled;
        this.isLocked = isLocked;
        this.loginFailureCount = loginFailureCount;
        this.role = role;
        this.dateLastPassword = dateLastPassword;
        this.isCredentialsExpired = isCredentialsExpired;
        this.dateLocked = dateLocked;
        this.customerType = customerType;
        this.customerCategory = customerCategory;
        this.loginLockHours = loginLockHours;
        this.passwordValidDays = passwordValidDays;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(ServerConstants.SPRING_SECURITY_ROLE_PREFIX + String.valueOf(role)));
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getIsLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {

        if (!this.isCredentialsExpired) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastPasswordDate = LocalDateTime.ofInstant(this.dateLastPassword.toInstant(), currentZone);

            return lastPasswordDate.isAfter(now.minusDays(passwordValidDays));

        } else
            return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean getIsLocked() {

        if (isLocked) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lockDate = LocalDateTime.ofInstant(this.dateLocked.toInstant(), currentZone);

            return lockDate.isAfter(now.minusHours(loginLockHours));

        } else
            return false;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getLoginFailureCount() {
        return loginFailureCount;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public long getCustomerId() {
        return customerId;
    }

    public int getRole() {
        return this.role;
    }

    public long getCustomerType() {
        return customerType;
    }

    public int getCustomerCategory() {
        return customerCategory;
    }

    public boolean isAdmin() {
        return this.role == ServerConstants.OPERATOR_ROLE_ADMIN;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyUserDetails other = (MyUserDetails) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
