package com.mt.token;


import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Opv2TokenService {

  Opv2TokenRepository opv2TokenRepository;


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
        .onItem().ifNull().continueWith("");
  }

}
