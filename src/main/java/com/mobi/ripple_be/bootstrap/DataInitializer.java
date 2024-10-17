package com.mobi.ripple_be.bootstrap;

import com.mobi.ripple_be.entity.AppUser;
import com.mobi.ripple_be.exception.BootstrapDataException;
import com.mobi.ripple_be.repository.UserRepository;
import com.mobi.ripple_be.util.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {
//    private final UserRepository userRepository;
    //    private final MessageRepository messageRepository;
    //    private final RoleRepository roleRepository;
    @Value("${ripple.bootstrap.delete-data-on-startup}")
    private Boolean shouldDelete;

    private final UUID role1Id = UUID.fromString("99a9bbbb-b19a-1ba0-b030-7849e48090fd");
    private final UUID role2Id = UUID.fromString("5567cccc-c19a-2ba0-b030-7849e48090fd");
    private final UUID role3Id = UUID.fromString("67a6dddd-d19a-3ba0-b030-7849e48090fd");
    private final UUID role4Id = UUID.fromString("1236eeee-e19a-3ba0-b030-7849e48090fd");
    private final UUID role5Id = UUID.fromString("456affff-f19a-3ba0-b030-7849e48090fd");

    private final UUID user1Id = UUID.fromString("21a6a1ab-c19a-4ba0-b030-7849e48090fd");
    private final UUID user2Id = UUID.fromString("daababe2-036c-47d7-aea5-c52d74fed1dd");
    private final UUID user3Id = UUID.fromString("1a222aea-eeaa-4ac6-b024-a2969ec4f794");

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
//        if (shouldDelete) {
//            log.warn("Current configuration is set to delete all data from the database." +
//                    " To change this behavior set favor.bootstrap.delete-data-on-startup to false");
//            userRepository.deleteAll().block();
//        }
//        log.info("Loading bootstrapped data. This action is performed for 'dev' profile only");
//        loadRoles();
//        messageRepository.save(Message.builder()
//                .eventType(ChatEventType.CHAT_CREATED)
//                .messageContent(ChatOpenedContent.builder()
//                        .userId(UUID.randomUUID().toString())
//                        .chatId(UUID.randomUUID().toString())
//                        .build())
//                .build()).subscribe();
//        if (userRepository.findAll().count().block() == 0L) {
//            loadUsers();
//        }
    }

//    private void loadUsers() {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        Supplier<BootstrapDataException> exceptionSupplier = () ->
//                new BootstrapDataException("Can't find the specified role.");
////        Role userRole = roleRepository.findByRoleType(RoleType.USER).blockOptional().orElseThrow(exceptionSupplier);
////        Role adminRole = roleRepository.findByRoleType(RoleType.ADMIN).blockOptional().orElseThrow(exceptionSupplier);
//
//        log.info("Loading users");
//
//        {
//            AppUser appUser1 = new AppUser();
//            appUser1.setId(user1Id);
//            appUser1.setEmail("ivan4o_gotininq@abv.bg");
//            appUser1.setPassword(passwordEncoder.encode("aA1234!"));
//            appUser1.setUsername("iva4o_gt");
//            appUser1.setFullName("Ivan Gotininq");
//            appUser1.setFollowers(0L);
//            appUser1.setFollowing(0L);
//            appUser1.setPostsCount(0L);
//            appUser1.setRole(Role.USER);
//            appUser1.setLastIssuedTokenRevocation(Instant.now());
////            user1.setFirstName("Ivan");
////            user1.setLastName("Georgiev");
////            user1.setBirthDate(Date.valueOf(LocalDate.of(1990, 10, 5)));
////            user1.setPhoneNumber("359885554545");
////            user1.setCountryCode("359");
////            user1.setFollowers(0L);
////            user1.setFollowing(0L);
////            user1.setIsEmailValidated(true);
////            user1.setIsPhoneNumberValidated(true);
////            user1.setRoles(List.of(userRole));
//            userRepository.save(appUser1)
//                    .subscribe(u -> log.info(">>>>>>>>>>>user1 saved successfully"));
//
////            userRole.getUsers().add(user1);
////            roleRepository.save(userRole);
//        }
//
//        {
//            AppUser appUser2 = new AppUser();
//            appUser2.setId(user2Id);
//            appUser2.setEmail("petar_petrov33@gmail.com");
//            appUser2.setPassword(passwordEncoder.encode("bB1234!"));
//            appUser2.setUsername("petar_p02");
//            appUser2.setFullName("Petar Petrov");
//            appUser2.setFollowers(0L);
//            appUser2.setFollowing(0L);
//            appUser2.setPostsCount(0L);
//            appUser2.setRole(Role.USER);
//            appUser2.setLastIssuedTokenRevocation(Instant.now());
////            user2.setFirstName("Petar");
////            user2.setLastName("Petrov");
////            user2.setBirthDate(Date.valueOf(LocalDate.of(2000, 11, 6)));
////            user2.setPhoneNumber("359883771111");
////            user2.setCountryCode("359");
////            user2.setFollowers(0L);
////            user2.setFollowing(0L);
////            user2.setIsEmailValidated(true);
////            user2.setIsPhoneNumberValidated(false);
////            user2.setRoles(List.of(userRole));
//            userRepository.save(appUser2)
//                    .subscribe(u -> log.info(">>>>>>>>>>>user2 saved successfully"));
//            ;
//
////            userRole.getUsers().add(user2);
////            roleRepository.save(userRole);
//        }
//
//        {
//            AppUser appUser3 = new AppUser();
////            appUser3.setId(user3Id);
//            appUser3.setEmail("izdislav_kostov7@abv.bg");
//            appUser3.setPassword(passwordEncoder.encode("aA1234!"));
//            appUser3.setUsername("iz_dis_lav");
//            appUser3.setFullName("Iz Dislav");
//            appUser3.setFollowers(0L);
//            appUser3.setFollowing(0L);
//            appUser3.setPostsCount(0L);
//            appUser3.setRole(Role.USER);
//            appUser3.setLastIssuedTokenRevocation(Instant.now());
//
////            user3.setFirstName("Izdislav");
////            user3.setLastName("Kostov");
////            user3.setBirthDate(Date.valueOf(LocalDate.of(2002, 12, 7)));
////            user3.setPhoneNumber("359887878877");
////            user3.setCountryCode("359");
////            user3.setFollowers(0L);
////            user3.setFollowing(0L);
////            user3.setIsEmailValidated(false);
////            user3.setIsPhoneNumberValidated(false);
////            user3.setRoles(List.of(adminRole));
//            userRepository.save(appUser3)
//                    .subscribe(u -> log.info(">>>>>>>>>>>user3 saved successfully"));
//            ;
//
////            adminRole.getUsers().add(user3);
////            roleRepository.save(userRole);
//        }
//        log.info("Users loaded successfully");
//    }

//    private void loadRoles() {
//        createRoleIfNotFound(RoleType.ANONYMOUS);
//        createRoleIfNotFound(RoleType.USER);
//        createRoleIfNotFound(RoleType.MODERATOR);
//        createRoleIfNotFound(RoleType.ADMIN);
//        Supplier<BootstrapDataException> t = () ->
//                new BootstrapDataException("Can't find a user for a role.");
//        Role role1 = new Role();
//        role1.setRoleType(RoleType.USER);
//        role1.setUsers(List.of(
//                        userRepository.findById(user1Id).orElseThrow(t),
//                        userRepository.findById(user2Id).orElseThrow(t)
//                )
//        );
//        roleRepository.save(role1);
//
//        Role role2 = new Role();
//        role2.setRoleType(RoleType.ADMIN);
//        role2.setUsers(List.of(
//                        userRepository.findById(user3Id).orElseThrow(t)
//                )
//        );
//        roleRepository.save(role2);
//    }

//    @Transactional
//    void createRoleIfNotFound(RoleType roleType) {
//
//        Role role = roleRepository.findByRoleType(roleType).blockOptional().orElse(null);
//        if (role == null) {
//            role = new Role();
//            role.setRoleType(roleType);
//            roleRepository.save(role);
//        }
//    }
}
