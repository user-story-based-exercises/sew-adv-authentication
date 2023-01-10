package server.yousong.autoconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import server.yousong.autoconfig.UserInfo;
import server.yousong.autoconfig.UserInfoRepository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Optional;


/**
 * Serves a much information about the logged-in user as possible (preferably the user entity),
 * or {@link HttpStatus#UNAUTHORIZED} if nobody or only an anonymous user is logged in.
 *
 * @author F. Kasper, ferdinand.kasper@modus-operandi.at
 */
@RestController
@BasePathAwareController
public class Me<T extends UserInfo<ID>, ID extends Serializable> {

    public static final String PATH = "/me";

    private final UserInfoRepository<T, ID> repository;


    public Me(@Autowired(required = false) UserInfoRepository<T, ID> repository) {
        this.repository = repository;
    }


    @RequestMapping(value = PATH, method = { RequestMethod.GET, RequestMethod.POST })
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public EntityModel<?> me(PersistentEntityResourceAssembler assembler) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ((principal instanceof UserInfo) && (repository != null)) {
            // Re-fetch the entity since the transaction is finished which returned the principal.
            // This avoid LazyInitializationExceptions
            Optional<T> userInfo = repository.findById(((UserInfo<ID>) principal).getId());
            if (userInfo.isPresent()) {
                return assembler.toFullResource(userInfo.get());
            }
        }

        return EntityModel.of(principal);
    }

}
