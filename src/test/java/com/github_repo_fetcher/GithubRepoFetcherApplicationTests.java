package com.github_repo_fetcher;

import com.github_repo_fetcher.dto.Branch;
import com.github_repo_fetcher.dto.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@EnableWireMock({
        @ConfigureWireMock(
                name = "github-client",
                filesUnderClasspath = "wiremock/github-client",
                baseUrlProperties = "github.api.base-url"
        )
})
class GithubRepoFetcherApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectWireMock("github-client")
    private WireMockServer wiremock;

    @Test
    public void shouldReturnOnlyNonForkReposWithBranches() throws Exception {
        // Given
        wiremock.stubFor(WireMock.get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                   {
                                     "name": "git-consortium",
                                     "owner": {
                                       "login": "octocat"
                                     },
                                     "fork": false,
                                     "branches": [
                                       {
                                         "name": "master",
                                         "commit": {
                                           "sha": "b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e"
                                         }
                                       }
                                     ]
                                   },
                                   {
                                     "name": "hello-world",
                                     "owner": {
                                       "login": "octocat"
                                     },
                                     "fork": false,
                                     "branches": [
                                       {
                                         "name": "master",
                                         "commit": {
                                           "sha": "7e068727fdb347b685b658d2981f8c85f7bf0585"
                                         }
                                       }
                                     ]
                                   }
                                ]
                                """)));

        wiremock.stubFor(WireMock.get(urlEqualTo("/repos/octocat/git-consortium/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "name": "master",
                                    "commit": { "sha": "b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e" }
                                  }
                                ]
                                """)));

        wiremock.stubFor(WireMock.get(urlEqualTo("/repos/octocat/hello-world/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "name": "master",
                                    "commit": { "sha": "7e068727fdb347b685b658d2981f8c85f7bf0585" }
                                  }
                                ]
                                """)));

        // When
        String responseBody = webTestClient.get()
                .uri("/users/octocat/repos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        List<Repository> repositories = objectMapper.readValue(responseBody, new TypeReference<>() {});

        // Then
        assertThat(repositories)
                .hasSize(2)
                .allSatisfy(repo -> {
                    assertThat(repo.fork()).isFalse();
                    assertThat(repo.branches()).isNotEmpty();
                });

        Repository repo = repositories.getFirst();
        assertThat(repo.name()).isEqualTo("git-consortium");
        assertThat(repo.owner().login()).isEqualTo("octocat");

        List<Branch> branches = repo.branches();
        assertThat(branches).hasSize(1);
        assertThat(branches.getFirst().name()).isEqualTo("master");
        assertThat(branches.getFirst().commit().sha()).isEqualTo("b33a9c7c02ad93f621fa38f0e9fc9e867e12fa0e");
    }
}
