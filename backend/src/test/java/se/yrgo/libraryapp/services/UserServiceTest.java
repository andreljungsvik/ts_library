package se.yrgo.libraryapp.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.UserId;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Test
    void correctLogin() {
        final String userId = "1";
        final UserId id = UserId.of(userId);
        final String username = "testuser";
        final String password = "password";
        final String passwordHash =
                "$argon2i$v=19$m=16,t=2,p=1$QldXU09Sc2dzOWdUalBKQw$LgKb6x4usOpDLTlXCBVhxA";
        final LoginInfo info = new LoginInfo(id, passwordHash);
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));
        UserService userService = new UserService(userDao, encoder);
        assertThat(userService.validate(username,
                password)).isEqualTo(Optional.of(id));
    }

    @Test
    void register_validUser_returnsTrue() {
        final String name = "test";
        final String realname = "testmannen";
        final String password = "abc123";
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        UserService userService = new UserService(userDao, encoder);

        when(userDao.register(name, realname, password)).thenReturn(true);
        assertThat(userService.register(name, realname, password)).isTrue();
    }

    @Test
    void register_nonValidUser_returnsFalse() {
        final String name = "ba";
        final String realname = "testmannen";
        final String password = "abc123";
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.register(name, realname, password)).isFalse();
    }

    @Test
    void isNameAvailable_availableName_returnsTrue() {
        final String name = "Bosse Bredsladd";
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        UserService userService = new UserService(userDao, encoder);

        when(userDao.isNameAvailable(name)).thenReturn(true);
        assertThat(userService.isNameAvailable(name)).isTrue();
    }

    @Test
    void isNameAvailable_unavailableName_returnsFalse() {
        final String name = "Bo";
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.isNameAvailable(name)).isFalse();
    }
}