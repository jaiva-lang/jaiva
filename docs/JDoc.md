# Jaiva Comment Documentation (JDoc)

JDoc (like Javadoc or python docstrings) is a way to document your Jaiva code.

JDoc comments can be used to document all symbols (functions and variables), by
just placing the comment right before the symbol's definition

To create a document comment in jaiva use `@*` instead of `@`


```jaiva
@* The control variable.
maak control <- 10!

@* This function clamps a value between a minimum and maximum.
@* param $> value <- number The value to be clamped.
@* param $> min <- number The minimum allowable value.
@* param $> max <- number The maximum allowable value.
@* ret   $> number The clamped value.
@* devn  $> This function uses a nested ternary expression to achieve clamping.
@*       $> Consider using it in scenarios where you do not want to exceed bounds.
@* from  $> v2.1.0
kwenza clamp(value, min, max) ->
    khutla value < min => min however (value > max => max however value)!
<~

khuluma(clamp(control, 0, 5))! @ Outputs: 5
```

(See [Tags](#tags) for more information on tags)

> For actual highlighting, There is a VSCode extension which make use of this (fixing this soon. As of yet it does not work)

## Styling

`{inline code}`
`**bold**`
`_underline_`

Example

```jaiva
@* This is **bold** and this is _underline_ and this is `{inline code}`
maak a <- 10!
```

## Tags

> [!NOTE] The order of tags does not matter.

A tag has the following syntax:
```
@* (tagName) $> (tag properties and/or description)
```

If your tag description is too long and you hate seeing it in one line, 
on the next omit the tagName and it will be added to the previous!

```
@* tagName $> This is a very long description which I
@*         $> would like to continue on the same line.
@*         $> This gets attached to `tagName`.
@* However, this line, (without the tag separator) starts a new paragraph.
```

### Parameter

`@* param $> (paramName)<?> <- (paramType) (description)`

Aliases: `parameter` ,`par`, `p`, `arg`, `argument`, `param`

Used to describe a function parameter. The parameter name must match the actual parameter name of course
but the type can be anything you want. (As long as it is a single word).

Example

```jaiva
@* This function adds two numbers if the second number is provided.
@* param $> a <- number The first number.
@* param $> b? <- number The second number.
kwenza add(a, b?) ->
    khutla b = idk => a however a + b!
<~
```

### Returns

`@* ret $> (description)`

Aliases: `khutla`, `ret`, `returns`, `r`, `rets`, `return`

Used to describe what a function returns.

Example (extended from above)

```jaiva
@* This function adds two numbers if the second number is provided.
@* param $> a <- number The first number.
@* param $> b? <- number The second number.
@* ret   $> The sum of the two numbers, or just the first number
kwenza add(a, b?) ->
    khutla b = idk => a however a + b!
<~
```

### Depends

`@* depends $> function1, function2, function3, ...`

Aliases: `depend`, `deps`, `dependencies`, `requires`

Used to indicate that use of this function depends on other functions being available within 
the same scope.

This is useful for libraries to indicate that a 
function will not work without other functions.

Example

```jaiva
@* This function calculates the area of a rectangle.
@* dep      $> multiplyFunc
@* param    $> length <- number The length of the rectangle.
@* param    $> width  <- number The width of the rectangle.
@* ret      $> The area of the rectangle.
kwenza area(length, width) ->
    khutla multiplyFunc(length, width)!
<~
```

### Developer Notes

`@* devn $> (description)`

Aliases: `devnote`, `note`, `devn`, `dn`

Used to provide additional notes or insights for developers about the function or variable.

Example

```jaiva
@* This function calculates the factorial of a number using recursion.
@* devn     $> This implementation uses recursion, which may lead to stack overflow for large
@*          $> numbers. Consider using an iterative approach for better performance.
@* param    $> n <- number The number to calculate the factorial for.
@* ret      $> The factorial of the number.
kwenza factorial(n) ->
    khutla n <= 1 => 1 however n * factorial(n - 1)!
<~
```

### Deprecared

`@* deprecared $> (description)`

Aliases: `deprec`, `depr`

Used to indicate that a function or variable is deprecated and should not be used in new code.

Is it up to the developer to indicate what to use, and when the function/variable will be removed.

Example

```jaiva
@* Function to double a number. 
@* deprec   $> Use `newFunction` instead. This function will be removed
@*          $> in version 3.0.0.
@* param    $> x <- number The number to be processed.
@* ret      $> The processed number.
kwenza oldFunction(x) ->
    khutla x * 2!
<~
```

### Version

`@* from $> (version)`

Aliases: `version`, `since`, `ver`, `f`

Used to indicate the version of the codebase when the function or variable was introduced.

Example

```jaiva
@* This function calculates the square of a number.
@* from     $> v2.0.0
@* param    $> x <- number The number to be squared.
@* ret      $> The square of the number.
kwenza square(x) ->
    khutla x * x!
<~
```

## Examples

`a_filter` (from the `jaiva/arrays` library)

> ```jaiva
> @* Filter an array based on a predicate function
> @* p        $> arr <- [] The original array
> @* p        $> predicate <- F~any A function that takes an element and returns true
> @* returns  $> Returns a new array with elements that satisfy the predicate function.
> @* devn     $> The predicate function must take in 1 parameter, the element and return a boolean.
> @* since    $> 1.0.0-beta.3
> kwenza *a_filter(arr, F~predicate) ->
>     maak empty <-|!
>     colonize element with arr ->
>        if (predicate(element) = true) ->
>            maak new <-| element!
>            empty <- flat(empty, new)!
>        <~
>    <~
>
>    khutla empty!
><~
>```

`a_remove` (from the `jaiva/arrays` library)

> ```jaiva
> @* Remove n elements from the end of an array
> @* p          $> arr <- [] The original array
> @* p          $> n <- number Amount of elements to remove
> @* returns    $> Returns an array which contains the original array with n amount of elements removed from the end.
> @* since      $> 1.0.0-beta.3
> kwenza *a_remove(arr, n) ->
>   maak returnValue <-|!
>   colonize i <- 0 | i < arr~ - n | + ->
>   maak el <-| arr[i]!
>       returnValue <- flat(returnValue, el)!
>   <~
>   khutla returnValue!
> <~
> ```

`a_pop` (from the `jaiva/arrays` library)

> ```jaiva
> @* Pops the last element off an array and returns a new array without that element.
> @* p          $> array <- [] The original array
> @* devn       $> This is just a wrapper for the _a_remove()_ function, it doesn't matter which one you use
> @* deps       $> a_remove
> @* returns    $> Returns a new array without the last element of the original array.
> @* since      $> 1.0.0-beta.3
> kwenza *a_pop(array) ->
>   khutla a_remove(array, 1)!
> <~
> ```