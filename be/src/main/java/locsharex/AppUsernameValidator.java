package locsharex;

import static java.util.Objects.nonNull;

import java.util.regex.Pattern;

import javax.inject.Singleton;

@Singleton
public class AppUsernameValidator {

    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9]{3,}$"); // min 3 alpha numeric chars

    public boolean validate(String name) {
        return nonNull(name) && pattern.matcher(name).matches();
    }

    public void validateOrThrow(String name) {
        if (!validate(name))
            throw new IllegalArgumentException("Name not valid, it shall match the pattern: " + pattern.pattern());
    }
}
