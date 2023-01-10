package server.yousong.autoconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.BeanUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serializable;
import java.lang.reflect.Constructor;


/**
 * To be used for user entities instead of a {@link PagingAndSortingRepository}
 * if authentication is required. As a result,
 * <ul>
 *     <li>on authentication with username and password, the user is looked up in this repository;</li>
 *     <li>on a successful OAuth2/OpenID authentication, a corresponding user entity is created
 *     in this repository (unless it exists already).</li>
 * </ul>
 *
 * @author F. Kasper, ferdinand.kasper@modus-operandi.at
 */
@NoRepositoryBean
public interface UserInfoRepository<T extends UserInfo<ID>, ID extends Serializable>
    extends CrudRepository<T, ID>, UserDetailsService {


    /**
     * Returns the user entity for the specified username, or {@code null}.
     */
    @JsonIgnore
    T findByUsernameIgnoreCase(String name);


    /**
     * Instantiates a new user object from a {@link UserInfo} instance.
     */
    default T toUser(UserInfo<ID> userInfo) {
        try {
            // Determine the actual user type
            Class<T> userType =
                (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), UserInfoRepository.class)[0];
            Constructor<T> userConstructor = userType.getConstructor();

            // Instantiate a new user object and copy the UserInfo properties
            T user = userConstructor.newInstance();
            BeanUtils.copyProperties(userInfo, user, userType);

            return user;

        } catch (Exception ex) {
            throw new RuntimeException("Cannot convert UserInfo to target type", ex);
        }
    }


    /**
     * Speichert eine Benutzer-Entity, die aus einem {@link UserInfo}-Objekt erzeugt wurde.
     */
    default T saveUserInfo(UserInfo<ID> userInfo) {
        return save(toUser(userInfo));
    }


    /**
     * Spring Security calls this method when authenticating with username and password
     * in order to find the matching user entity in this repository.
     */
    @Override
    default UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        T user = findByUsernameIgnoreCase(name);

        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException(name);
        }
    }

}
