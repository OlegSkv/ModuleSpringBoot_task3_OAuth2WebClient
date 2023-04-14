package example.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WhoAmIController {

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@GetMapping({"/who"})
	public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication, HttpSession session) {
		if(authentication != null) {
			OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);
			model.addAttribute("userName", authentication.getPrincipal().getAttribute("preferred_username"));
			model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
			model.addAttribute("scopes",authorizedClient.getAccessToken().getScopes());
		}
		session.invalidate();
		return "who";
	}

	private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
		return this.authorizedClientService.loadAuthorizedClient(
				authentication.getAuthorizedClientRegistrationId(), authentication.getName());
	}
}
