package gg.scala.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScalaApplicationTests {
    @Autowired
    private RequestMappingHandlerMapping mappings;

    @Test
    void contextLoads() {
    }

    @Test
    void exposesWebsiteMigrationEndpoints() {
        Set<String> patterns = mappings.getHandlerMethods().keySet().stream()
                .flatMap(info -> info.getPatternValues().stream())
                .collect(Collectors.toSet());

        assertThat(patterns).contains(
                "/forum/forum/{id}",
                "/forum/replies/{id}",
                "/forum/friends/{uuid}",
                "/forum/friends/{id}",
                "/rank/{uuid}/members",
                "/rankGroups/{group}",
                "/forum/messages/{participant}",
                "/forum/messages/thread/{id}",
                "/forum/messages",
                "/forum/messages/{id}/replies",
                "/forum/messages/{messageId}/replies/{replyId}",
                "/forum/messages/replies/{replyId}"
        );
    }
}
