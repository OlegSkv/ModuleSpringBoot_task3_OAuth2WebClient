package example.controllers;

import example.model.Student;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class StudentController {

    private final WebClient webClient;

    public StudentController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/students")
    public String showStudentList(Model model) {
        Mono<List<Student>> response =
                webClient.get()
                        .uri("/students")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<>() {
                        });
        List<Student> students = response.block();

        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/addstudent")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());

        return "add-student";
    }

    @PostMapping("/addstudent")
    public String addStudent(@Valid Student student,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "add-student";
        }

        webClient.post()
                .uri("/students")
                .body(Mono.just(student), Student.class)
                .retrieve()
                .bodyToMono(Student.class)
                .block();

        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Mono<Student> response = webClient.get()
                .uri("/students/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Student.class);
        Student student = response.block();

        model.addAttribute("student", student);

        return "update-student";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable("id") long id,
                                @Valid Student student,
                                BindingResult result,
                                Model model)
            throws Exception{
        if (result.hasErrors()) {
            student.setId(id);
            return "update-student";
        }

        webClient.put()
                .uri("/students/" + id)
                .body(Mono.just(student), Student.class)
                .retrieve()
                .bodyToMono(Student.class)
                .block();

        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") long id, Model model) {

        webClient.delete()
                .uri("/students/" +id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        return "redirect:/students";
    }

    @GetMapping("/logmeout") // is not working
    public String logout(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        processLogout(authorizedClient);
        return "redirect:/";

    }

    private void processLogout(OAuth2AuthorizedClient authorizedClient) {
        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", authorizedClient.getClientRegistration().getClientId());
            requestParams.add("client_secret", authorizedClient.getClientRegistration().getClientSecret());
            requestParams.add("refresh_token", authorizedClient.getRefreshToken().getTokenValue());
            String tokenValue = authorizedClient.getAccessToken().getTokenValue();
            logoutUserSession(requestParams, authorizedClient);

        } catch (Exception e) {
            throw e;
        }
    }

    private void logoutUserSession(MultiValueMap<String, String> requestParams, OAuth2AuthorizedClient authorizedClient) {
        WebClient client = WebClient.builder().baseUrl("http://localhost:8442/realms/spring-boot-security-task/protocol/openid-connect/logout").build();

        String response = client
                .post()
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(requestParams))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/count")
    @ResponseBody
    public String getStudentsAmount() {

        String numberOfStudents = webClient.get()
                .uri("/count")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return numberOfStudents;
    }
}
