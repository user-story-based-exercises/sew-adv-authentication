package server.yousong.autoconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


/**
 * Provides user information for authentication with {@link UserDetails},
 * {@link OAuth2User} and {@link OidcUser} and for authorization in
 * {@link PreAuthorize}.
 *
 * @author F. Kasper, ferdinand.kasper@modus-operandi.at
 */
public interface UserInfo<ID extends Serializable> extends UserDetails, OAuth2User, OidcUser {

    /** Returns the primary key (for {@link PreAuthorize}) */
    @JsonIgnore
    ID getId();


    /** Returns the unique username */
    @Override
    String getUsername();

    /** Saves the unique username */
    void setUsername(String name);


    /** BCrypt hash of the passphrase, or {@code null} */
    @Override
    @JsonIgnore
    String getPassword();


    /** Saves the BCrypt hash of the passphrase */
    void setPassword(String password);


    /** Returns the user's display name (does not need to be unique) */
    default String getDisplayName() {
        return getUsername();
    }


    /** Saves the user's display name (does not need to be unique) */
    default void setDisplayName(String displayName) {}


    /** Returns the user's role, or {@code null} */
    default String getRole() { return "ROLE_USER"; }


    /** Saves the user's role */
    default void setRole(String role) {}


    /*------------------ Start of UserDetails implementation -----------------*/


    /**
     * Override this if users can have multiple roles.
     */
    @Override
    @JsonIgnore
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(getRole()));
    }


    @Override
    @JsonIgnore
    default boolean isAccountNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    default boolean isAccountNonLocked() {
        return true;
    }


    @Override
    @JsonIgnore
    default boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    default boolean isEnabled() {
        return true;
    }


    /*------------------- Start of OidcUser implementation -------------------*/

    @Override
    @JsonIgnore
    default Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }


    @Override
    @JsonIgnore
    default String getName() {
        return getUsername();
    }


    @Override
    @JsonIgnore
    default Map<String, Object> getClaims() {
        return Collections.emptyMap();
    }


    @Override
    @JsonIgnore
    default OidcUserInfo getUserInfo() { return null; }


    @Override
    @JsonIgnore
    default OidcIdToken getIdToken() { return null; }

}
