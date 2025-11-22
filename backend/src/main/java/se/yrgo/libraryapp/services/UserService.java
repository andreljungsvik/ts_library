package se.yrgo.libraryapp.services;

import java.util.Optional;
import javax.inject.Inject;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.*;
import se.yrgo.libraryapp.validators.RealName;
import se.yrgo.libraryapp.validators.Username;

public class UserService {
    private UserDao userDao;
    private PasswordEncoder encoder;

    @Inject
    UserService(UserDao userDao, PasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    public Optional<UserId> validate(String username, String password) {
        Optional<LoginInfo> maybeLoginInfo = userDao.getLoginInfo(username);
        if (maybeLoginInfo.isEmpty()) {
            return Optional.empty();
        }

        LoginInfo loginInfo = maybeLoginInfo.get();

        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        if (!encoder.matches(password, loginInfo.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(loginInfo.getUserId());
    }

    public boolean register(String name, String realname, String password) {
        if (Username.validate(name) && RealName.validate(realname)) {
            String passwordHash = encoder.encode(password);

            realname = realname.replace("'", "\\'");

            return userDao.register(name, realname, passwordHash);
        }
        return false;
    }

    public boolean isNameAvailable(String name) {
        if (name == null || name.trim().length() < 3) {
            return false;
        }
        return userDao.isNameAvailable(name);
    }
}
