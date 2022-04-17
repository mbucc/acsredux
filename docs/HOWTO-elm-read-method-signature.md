Reading an Elm Method Signature
======================================

April 16, 2022

This is the method you must implement in order to get a validation error
out of a BadStatus (aka 400) response:

    expectStringResponse
        :  (Result x a -> msg) 
        -> (Response String -> Result x a)
        -> Expect msg
    expectStringResponse toMsg toResult =
        ...

Let's break this down piece-by-piece, 
as it turns out to be a good illustration
of how the Elm type system works.

The function is simple enough.
------------------------------------------

The function `expectStringResponse` takes two arguments and returns a 
single value.

1. argument #1 is `(Result x a -> msg)`,
2. argument #2 is `(Response String -> Result x a)`, and
3. the return value is `Expect msg`.


The function's first argument is a function.
------------------------------------------------------

`(Result x a -> msg)`

This function takes one argument and returns a one value.

1. argument #1 is `Result x a` and
2. the return value is `msg`

The argument is the type constructor `Result`.

`Result` is a type constructor because you must provide two arguments
in order to define (aka construct) the type.

This is very common in Elm,
so I'll repeat that statement for emphasis: 
_you provide arguments to define a type._

As a Java programmer, think of a Java list.  By writing `List<String>` 
you define a type.  Using 
Elm terminology, `List` is (kind-of!) a type constructor that takes
one argument.  The type `String` is the argument.

In Elm, the type constructor for a list is `List a`;
* `List Int` defines a type that is a list of integers and
* `List String` a list of strings.

_Result_ is a type constructor that takes two arguments (an error and a value)
and constructs a type that is guaranteed to be one of two variants, 
either a success or a failure:
```
type Result error value
    = Ok value
    | Err error
```

Note how each variant value uses one of the constructor arguments.

The return type is `msg`.  Since 
we are reading the function from left to right, 
at this point
it can have any type.


The function's second argument is also a function.
------------------------------------------------------

`(Response String -> Result x a)`

This function also takes one argument and returns a one value.

1. argument #1 is `Response String` and
2. the return value is `Result x a`

Note that the constructor arguments
to Result (`x` and `a`) match the names used in the function's first 
argument.  This tells Elm that their types must match the types of the 
constructor args that defined the previous Result type.

For example, if the function's first argument takes in a 
`Result Int String` 
and the function's second argument produces a `Result String String`,
it is a compilation error.

Finally, _Response_ is a type constructor defined by Elm as
```
type Response body
  = BadUrl_ String
  | Timeout_
  | NetworkError_
  | BadStatus_ Metadata body
  | GoodStatus_ Metadata body
```

The Metadata is a type alias for a dictionary provided by Elm:
```
type alias Metadata =
  { url : String
  , statusCode : Int
  , statusText : String
  , headers : Dict String String
  }
```

In short, this function transforms an HTTP response to a result.


The function's return type look simple ...
------------------------------------------

The return type `Expect msg`.

This is a type that takes a single argument (msg).

Note that the type of the argument `msg`
is constrained by the 
`(Result x a -> msg)`
expression in the first argument to the function.

But if you dig into how Elm defines `Expect`, 
you will find a type constructor
that ignores its constructor argument!

```
type Expect msg = Expect
```

Elm's Http.get uses Expect's constructor argument in the input 
to define the return type (a Cmd with the same constructor argument).

    get
        : { url : String
          , expect : Expect msg
          }
        -> Cmd msg




Notes
--------------------

For a more in-depth discussion of type constructors see this 
StackOverflow post: https://stackoverflow.com/a/18205862/1789168.

Hat tip to Jeffrey Huang's 2019 blog post on 
[Going Beyond 200 OK: A Guide to Detailed HTTP Responses in Elm](https://jzxhuang.medium.com/going-beyond-200-ok-a-guide-to-detailed-http-responses-in-elm-6ddd02322e).