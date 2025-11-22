package se.yrgo.libraryapp.controllers;

import io.jooby.Context;
import io.jooby.Cookie;
import io.jooby.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import se.yrgo.libraryapp.dao.RoleDao;
import se.yrgo.libraryapp.dao.SessionDao;
import se.yrgo.libraryapp.entities.Role;
import se.yrgo.libraryapp.entities.UserId;
import se.yrgo.libraryapp.entities.forms.LoginData;
import se.yrgo.libraryapp.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LoginControllerTest {
    private UserService userService = mock(UserService.class);
    private RoleDao roleDao = mock(RoleDao.class);
    private SessionDao sessionDao = mock(SessionDao.class);
    private LoginController controller;

    @BeforeEach
    void setup() {
        controller = new LoginController(userService, roleDao, sessionDao);
    }

    @Test
    void login_validCredentials_createsSessionAndReturnsRoles() {
        Context ctx = mock(Context.class);

        LoginData login = new LoginData("user", "pass");
        UserId userId = UserId.of("1");
        UUID sessionId = UUID.randomUUID();

        when(userService.validate("user", "pass"))
                .thenReturn(Optional.of(userId));
        when(sessionDao.create(userId))
                .thenReturn(sessionId);
        when(roleDao.get(userId))
                .thenReturn(List.of(Role.ADMIN));

        List<Role> roles = controller.login(ctx, null, login);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(ctx).setResponseCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertThat(cookie.getValue()).isEqualTo(sessionId.toString());
        assertThat(cookie.isHttpOnly()).isTrue();

        assertThat(roles).containsExactly(Role.ADMIN);
    }

    @Test
    void login_invalidCredentials_returnsUnauthorizedAndEmptyList() {
        Context ctx = mock(Context.class);

        LoginData login = new LoginData("user", "wrong");

        when(userService.validate("user", "wrong"))
                .thenReturn(Optional.empty());

        List<Role> roles = controller.login(ctx, null, login);

        verify(ctx).setResponseCode(StatusCode.UNAUTHORIZED);
        assertThat(roles).isEmpty();
    }

    @Test
    void login_validExistingSession_returnsEmptyList() {
        Context ctx = mock(Context.class);
        UUID session = UUID.randomUUID();

        when(sessionDao.validate(session))
                .thenReturn(UserId.of("10"));

        List<Role> roles = controller.login(ctx, session.toString(), new LoginData("u", "p"));

        verifyNoInteractions(userService);
        assertThat(roles).isEmpty();
    }

    @Test
    void isLoggedIn_validSession_returnsRoles() {
        UUID session = UUID.randomUUID();
        UserId userId = UserId.of("1");

        when(sessionDao.validate(session))
                .thenReturn(userId);
        when(roleDao.get(userId))
                .thenReturn(List.of(Role.USER));

        List<Role> roles =
                controller.isLoggedIn(session.toString());

        assertThat(roles).containsExactly(Role.USER);
    }

    @Test
    void isLoggedIn_invalidSession_returnsEmptyList() {
        UUID session = UUID.randomUUID();

        when(sessionDao.validate(session))
                .thenThrow(new IllegalArgumentException("bad session"));

        List<Role> roles =
                controller.isLoggedIn(session.toString());

        assertThat(roles).isEmpty();
    }

    @Test
    void isLoggedIn_nullCookie_returnsEmptyList() {
        List<Role> roles = controller.isLoggedIn(null);
        assertThat(roles).isEmpty();
    }
}
