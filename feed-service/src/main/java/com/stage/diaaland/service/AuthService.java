package com.stage.diaaland.service;

import com.stage.diaaland.client.AuthServiceClient;
import com.stage.diaaland.config.JwtConfig;
import com.stage.diaaland.exception.UnableToGetAccessTokenException;
import com.stage.diaaland.exception.UnableToGetUsersException;
import com.stage.diaaland.payload.JwtAuthenticationResponse;
import com.stage.diaaland.payload.ServiceLoginRequest;
import com.stage.diaaland.payload.UserSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;


@Service
@Slf4j
public class AuthService {

    @Autowired private AuthServiceClient authClient;
    @Autowired private ServiceLoginRequest serviceLoginRequest;
    @Autowired private JwtConfig jwtConfig;

    public String getAccessToken() {

        ResponseEntity<JwtAuthenticationResponse> response =
                authClient.signin(serviceLoginRequest);

        if(!response.getStatusCode().is2xxSuccessful()) {
            String message = String.format("unable to get access token for service account, %s",
                    response.getStatusCode());

            log.error(message);
            throw new UnableToGetAccessTokenException(message);
        }

        return response.getBody().getAccessToken();
    }

    public Map<String, String> usersProfilePic(String token,
                                               List<String> usernames) {

        ResponseEntity<List<UserSummary>> response =
                authClient.findByUsernameIn(
                        jwtConfig.getPrefix() + token, usernames);

        if(!response.getStatusCode().is2xxSuccessful()) {
            String message = String.format("unable to get user summaries %s",
                    response.getStatusCode());

            log.error(message);
            throw new UnableToGetUsersException(message);
        }

       return response
                .getBody()
                .stream()
                .collect(toMap(UserSummary::getUsername,
                        UserSummary::getProfilePicture));
    }
}
