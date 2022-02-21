February 20, 2022

How to use Validol to validate JSON
===========================================

A toy example that illustrates the strategy.

*I did not end up using this library.*

Notes:
* `FastFail` chains validators and stops processing as soon as one fails.
  * arg1: a validator; for example, `WellFormedJson`
  * arg2: a function that takes the arg1 value and produces another validator.  
Often the output validator has a different type.
* A JSON block is a collection of siblings.  The are four flavors:
  * an unamed block of named: { "x":1, "y":2 }.  **This is typical how
    the parsing  starts.**
  * a named block of named: "pt1": { "x":1, "y":2 }
  * a named block of unnamed: "xs": [ { "x":1 }, {"x": 2 } ]
  * an unamed block of unnamed: [ [1, 2], [2, 3] ]   *?? not sure ...*
* The block validators (for example, `UnnamedBlocOfNameds`) are what you 
use to construct domain objects from the JSON.
  * arg1: `List.of` validators that correspond to constructor args.
  * arg2: the class to construct.

* Validators commonly will to express multiple validation conditions; for example,
`new IsJsonObject(new Required(new IndexedValue(` ... 
* Validoc comes with 53 different implementations of `Validatable<?>`.  For 
example, the previous bullet used
  * `IndexedValue` always returns success---the value is 
    `Present<>` if the key is there and `Absent<>` not,
  * `Required` only returns success if the value is present, and
  * `IsJsonObject` ensures the value is a map and not a list or a scalar.
* The default behavior is to not error if a key is missing.  You need to
explicitly use the `Required` validator for any required attribute.

```java
package com.acsredux.core.auth;

import com.google.gson.JsonElement;
import com.spencerwi.either.Either;
import validation.Validatable;
import validation.composite.FastFail;
import validation.composite.wellformedjson.WellFormedJson;
import validation.leaf.Unnamed;
import validation.result.Result;
import validation.result.error.Error;
import validation.result.value.Present;
import validation.result.value.Value;

public class SecurityPolicyValidator implements Validatable<SecurityPolicy> {

  private final String json;

  public SecurityPolicyValidator(String json) {
    this.json = json;
  }
  
  @Override
  public Result<SecurityPolicy> result() throws Exception {
    // FailFast
    //   new  :: Validatable<T> -> Function<T, Validatable<R>> 
    //           -> FailFast<T, R> implements Validatable<R>
    // FailFast.result :: Result<R>
    //
    // In this example, the types are:
    //      T           JsonElement
    //      R           SecurityPolicy
    //
    return new FastFail<>(
      validateThatStringIsJSON(this.json),
      this::parsePolicy
    ).result();
  }

  private Validatable<JsonElement> validateThatStringIsJSON(String x) throws Exception {
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

  
  private Validatable<SecurityPolicy> parsePolicy(JsonElement x) throws Exception {
    // UnnamedBlocOfNameds
    //   new :: List<Validatable<?>> -> Class<? extends T>
    //          -> UnnamedBlocOfNameds<T> implements Validatable<T>
    //   result :: Result<T>
    return new UnnamedBlocOfNameds<>(
      List.of(
        new FastFail<>(
          new IsJsonObject(
            new Required(
              new IndexedValue("acls", x)
            )
          ),
          this::parseEntitlements
        )),
      SecurityPolicy.class);
  }

  private Validatable<List<Entitlement>> parseEntitlements(JsonElement x) {
    // and so on ...
    return null;
  }
}
```