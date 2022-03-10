package locsharex;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
class LocsharexTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        assertThat(application.isRunning()).isTrue();
    }

}
