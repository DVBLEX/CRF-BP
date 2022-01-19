package com.crf.server.rest.security;

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

import lombok.Getter;

public class MyUserDetails implements UserDetails {

    private static final long   serialVersionUID = 1L;

    private static final ZoneId currentZone      = ZoneId.systemDefault();

    @Getter
    private final long          id;
    @Getter
    private final String        code;
    @Getter
    private final long          customerId;
    @Getter
    private final String        firstname;
    @Getter
    private final String        lastname;
    private final String        username;
    private final String        password;
    private final boolean       isEnabled;
    private final boolean       isLocked;
    @Getter
    private final int           loginFailureCount;
    @Getter
    private final int           role;
    private final Date          dateLastPassword;
    private final Date          dateLocked;
    private final boolean       isCredentialsExpired;
    @Getter
    private final long          customerType;
    @Getter
    private final int           customerCategory;
    private final int           loginLockHours;
    private final int           passwordValidDays;

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

            var now = LocalDateTime.now();
            var lastPasswordDate = LocalDateTime.ofInstant(this.dateLastPassword.toInstant(), currentZone);

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

            var now = LocalDateTime.now();
            var lockDate = LocalDateTime.ofInstant(this.dateLocked.toInstant(), currentZone);

            return lockDate.isAfter(now.minusHours(loginLockHours));

        } else
            return false;
    }

    public boolean isAdmin() {
        return this.role == ServerConstants.OPERATOR_ROLE_ADMIN;
    }

    @Override
    public int hashCode() {

        final var prime = 31;
        var result = 1;
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
            return other.username == null;
        } else
            return username.equals(other.username);
    }
}
