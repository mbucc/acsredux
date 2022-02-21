package com.acsredux.core.auth;

import com.acsredux.core.auth.validation.leaf.as.action.AsAction;
import com.acsredux.core.auth.validation.leaf.as.resource.AsResource;
import com.acsredux.core.auth.validation.leaf.as.user.AsUser;
import com.acsredux.core.auth.values.Entitlement;
import com.acsredux.core.auth.values.Entitlements;
import com.google.gson.JsonElement;
import com.spencerwi.either.Either;
import java.util.List;
import validation.Validatable;
import validation.composite.FastFail;
import validation.composite.bloc.of.nameds.UnnamedBlocOfNameds;
import validation.composite.bloc.of.unnameds.NamedBlocOfUnnameds;
import validation.composite.wellformedjson.WellFormedJson;
import validation.leaf.Unnamed;
import validation.leaf.as.type.AsString;
import validation.leaf.is.IndexedValue;
import validation.leaf.is.of.structure.jsonobject.IsJsonObject;
import validation.leaf.is.required.Required;
import validation.result.Result;
import validation.result.error.Error;
import validation.result.value.Present;
import validation.result.value.Value;

public class SecurityPolicyValidator implements Validatable<SecurityPolicy> {

  private final String json;

  public SecurityPolicyValidator(String json) {
    this.json = json;
  }

  private Validatable<JsonElement> validateThatStringIsWellFormedJSON(String x)
    throws Exception {
    return new WellFormedJson(validateThatStringIsNonNull(x));
  }

  private Validatable<String> validateThatStringIsNonNull(String x) throws Exception {
    return new Unnamed<>(wrapInEitherMonad(x));
  }

  private Either<Error, Value<String>> wrapInEitherMonad(String x) throws Exception {
    return Either.right(isNotNullOrDie(x));
  }

  private Value<String> isNotNullOrDie(String x) throws Exception {
    return new Present<>(x);
  }

  private Validatable<Entitlements> parseEntitlements(JsonElement x) throws Exception {
    return new NamedBlocOfUnnameds<>(
      "acls",
      x,
      acl ->
        new UnnamedBlocOfNameds<>(
          List.of(
            new AsResource(new AsString(new Required(new IndexedValue("resource", acl)))),
            new AsAction(new AsString(new Required(new IndexedValue("action", acl)))),
            new AsUser(new AsString(new Required(new IndexedValue("action", acl))))
          ),
          Entitlement.class
        ),
      Entitlements.class
    );
  }

  private Validatable<SecurityPolicy> parsePolicy(JsonElement x) throws Exception {
    // UnnamedBlocOfNameds
    //   new :: List<Validatable<?>> -> Class<? extends T>
    //          -> UnnamedBlocOfNameds<T> implements Validatable<T>
    //   result :: Result<T>
    return new UnnamedBlocOfNameds<>(
      List.of(
        new FastFail<>(
          new IsJsonObject(new Required(new IndexedValue("acls", x))),
          this::parseEntitlements
        )
      ),
      SecurityPolicy.class
    );
  }

  @Override
  public Result<SecurityPolicy> result() throws Exception {
    return new FastFail<>(
      validateThatStringIsWellFormedJSON(this.json),
      this::parsePolicy
    ).result();
  }
}
