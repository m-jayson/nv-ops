package com.mt.token;


import com.mt.common.OpsException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Opv2TokenService {

  Opv2TokenRepository opv2TokenRepository;

  @NonFinal
  @RestClient
  Opv2TokenClientApi opv2TokenClientApi;


  public Uni<Void> updateToken (String token) {

    return this.opv2TokenRepository.saveOrUpdate(token)
        .onItem().transformToUni(t -> {
          var id = t.getId();
          return this.opv2TokenRepository.updateOtherTokensFalse(id);
        }).replaceWithVoid();
  }


  public Uni<String> fetchToken () {

    return this.opv2TokenRepository.findInUse()
        .onItem().ifNotNull()
        .transform(Opv2Token::getBearerToken)
        .onItem().ifNull().continueWith("")
        .onItem().transformToUni(token -> {
          return this.opv2TokenClientApi.testToken("Bearer " + token)
              .onItem().transform(response -> token);
        });
  }

}
