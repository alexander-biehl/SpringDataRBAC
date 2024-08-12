package com.alexbiehl.demo.config;

import com.alexbiehl.demo.model.Role;
import com.alexbiehl.demo.model.User;
import com.alexbiehl.demo.repository.RoleRepository;
import com.alexbiehl.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Transactional
@TestConfiguration
// explicitly includes this class in tests with profile 'test-no-security-load'
@Profile("test-no-security-load")
public class TestDataSourceLoader implements InitializingBean {


    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataSourceLoader.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Starting Test DB load");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User("user", "password", true);
        User manager = new User("manager", "password", true);
        User admin = new User("admin", "password", true);

        Role userRole = new Role("ROLE_USER");
        userRole = roleRepository.save(userRole);

        Role managerRole = new Role("ROLE_MANAGER", userRole);
        managerRole = roleRepository.save(managerRole);

        Role adminRole = new Role("ROLE_ADMIN", managerRole);
        adminRole = roleRepository.save(adminRole);

        user.setRoles(Collections.singleton(userRole));
        manager.setRoles(Collections.singleton(managerRole));
        admin.setRoles(Collections.singleton(adminRole));

        userRepository.save(user);
        userRepository.save(manager);
        userRepository.save(admin);

        LOGGER.info("DB Test Load Complete");
        SecurityContextHolder.clearContext();
    }
}
