# arrays (Library)

_Jaiva's arrays standard library which is itself, made in Jaiva.  It really just contains convenience methods such that you don't  have to index into or look for things yourself._

## Table of Contents
| Alias | Link |
| --- | --- |
| a_remove | [a_remove](#a_remove) |
| a_pop | [a_pop](#a_pop) |
| a_push | [a_push](#a_push) |
| a_pushAll | [a_pushAll](#a_pushAll) |
| a_unshift | [a_unshift](#a_unshift) |
| a_shift | [a_shift](#a_shift) |
| a_filter | [a_filter](#a_filter) |
| a_replace | [a_replace](#a_replace) |
| a_delete | [a_delete](#a_delete) |
| a_indexOf | [a_indexOf](#a_indexOf) |
| a_reverse | [a_reverse](#a_reverse) |
| a_reduce | [a_reduce](#a_reduce) |
| a_map | [a_map](#a_map) |
| a_from | [a_from](#a_from) |
| a_forEach | [a_forEach](#a_forEach) |
| a_apply | [a_apply](#a_apply) |
| a_findFirst | [a_findFirst](#a_findFirst) |
| a_findLast | [a_findLast](#a_findLast) |
| a_find | [a_find](#a_find) |
## Functions

### a_remove

_**Remove n elements from the end of an array**_


#### Definition

```jaiva
F~a_remove(arr, n)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_n_**  **`<-`** _**number**_
	 - _Amount of elements to remove_

_**Returns:**_ **An array which contains the original array with n amount of elements removed from the end.**

> [!NOTE]
> _Throws if the first argument is not an array or if the second argument is not a non-negative number. Not to be confused with a_delete() which removes elements from a specific index in an array._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_remove(arr, 2))! @ Returns [1, 2, 3]
khuluma(a_remove(arr, 0))! @ Returns [1, 2, 3, 4, 5]
khuluma(a_remove(arr, 5))! @ Returns []
```


---


### a_pop

_**Pops the last element off an array and returns a new array without that element.**_


#### Definition

```jaiva
F~a_pop(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The original array_

_**Returns:**_ **A new array without the last element of the original array.**

> [!WARNING]
> This symbol depends on the following symbols: **a_remove**. If not imported this call may fail!!

> [!NOTE]
> _This is just a wrapper for the _a_remove()_ function, it doesn't matter which one you use Throws if the argument is not an array._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_pop(arr))! @ Returns [1, 2, 3, 4]
```


---


### a_push

> [!WARNING]
> This symbol has been marked as deprecated! Whilst this function works, rather use arrLit + array spreading with ::: which is ultimately cleaner and more expressive syntax. `arrLit(arr:::, element)`


_**Appends an element to the end of an array and returns the new array.**_


#### Definition

```jaiva
F~a_push(arr, element)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_element_**  **`<-`** _**any**_
	 - _Element to add_

_**Returns:**_ **A new array with the element appended at the end of the array.**

> [!NOTE]
> _Throws if the first argument is not an array._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3!
khuluma(a_push(arr, 4))! @ Returns [1, 2, 3, 4]
```


---


### a_pushAll

> [!WARNING]
> This symbol has been marked as deprecated! Whilst this function works, rather use arrLit + array spreading with ::: which is ultimately cleaner and more expressive syntax. `arrLit(arr:::, element1, element2, ...)`


_**Appends multiple elements to the end of an array and returns the new array.**_


#### Definition

```jaiva
F~a_pushAll(<-params)
```
- **_params_**  **`<-`** _**idk**_
	 - _A variable number of arguments where the first argument is the original array and the rest are the elements to add_

_**Returns:**_ **A new array with the elements appended at the end of the array.**

> [!NOTE]
> _Throws if the first argument is not an array._


Since Version: _5.0.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3!
khuluma(a_pushAll(arr, 4, 5, 6))! @ Returns [1, 2, 3, 4, 5, 6]
```


---


### a_unshift

> [!WARNING]
> This symbol has been marked as deprecated! Whilst this function works, rather use arrLit + array spreading with ::: which is ultimately cleaner and more expressive syntax. `arrLit(element, arr:::)`


_**Appends an element to the start of an array and returns the new array.**_


#### Definition

```jaiva
F~a_unshift(arr, element)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_element_**  **`<-`** _**any**_
	 - _Element to add_

_**Returns:**_ **A new array with the element appended at the start of the array.**

> [!NOTE]
> _Throws if the first argument is not an array._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 2, 3, 4!
khuluma(a_unshift(arr, 1))! @ Returns [1, 2, 3, 4]
```


---


### a_shift

_**Removes n elements from the start of an array**_


#### Definition

```jaiva
F~a_shift(arr, n)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_n_**  **`<-`** _**number**_
	 - _Amount of elements to remove_

_**Returns:**_ **An array which contains the original array with n amount of elements removed from the start.**

> [!NOTE]
> _This uses a colonize loop to iterate through the array and skip the first n elements. It may not be the most efficient way to do this, but it works. Throws if the first argument is not an array or if the second argument is not a non-negative number._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_shift(arr, 2))! @ Returns [3, 4, 5]
khuluma(a_shift(arr, 0))! @ Returns [1, 2, 3, 4, 5]
khuluma(a_shift(arr, 5))! @ Returns []
```


---


### a_filter

_**Filter an array based on a predicate function**_


#### Definition

```jaiva
F~a_filter(arr, F~predicate)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_predicate_**  **`<-`** _**F~any**_
	 - _A function that takes an element and additional parameters and returns a boolean._

_**Returns:**_ **A new array with elements that satisfy the predicate function.**

> [!NOTE]
> _The predicate function must take in at least 1 parameter, which is the input element. The function can then further take in an index as parameter 2, and the original array at parameter 3 and should return a boolean. Throws if the first argument is not an array or if the second argument is not a function._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5, 6!
maak isEven <- f~(num) : num % 2 = 0! @ A lambda function to check if a number is even
khuluma(a_filter(arr, isEven))! @ Returns [2, 4, 6]
@ Filter for elements who's index is even.
khuluma(a_filter(arr, f~(num, index) : index % 2 = 0)!
```


---


### a_replace

_**Replace an element at a specific index in an array with a new element**_


#### Definition

```jaiva
F~a_replace(arr, i, el?)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_i_**  **`<-`** _**number**_
	 - _The index to replace_
- **_el_** **`?`** **`<-`** _**any**_
	 - _The new element to insert (Can be idk)_

_**Returns:**_ **A new array with the element at index i replaced with el**

> [!NOTE]
> _Throws if the first argument is not an array or if the index is out of bounds._


Since Version: _3.0.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_replace(arr, 2, 99))! @ Returns [1, 2, 99, 4, 5]
```


---


### a_delete

_**Deletes an element at a specific index in an array and shifts the rest of the elements to the left**_


#### Definition

```jaiva
F~a_delete(arr, i)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_i_**  **`<-`** _**number**_
	 - _The index to delete_

_**Returns:**_ **A new array with the element at index i removed and the rest of the elements shifted to the left**

> [!NOTE]
> _Throws if the first argument is not an array or if the index is out of bounds. Not to be confused with a_remove() which removes elements from the end of an array_


Since Version: _3.0.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_delete(arr, 2))! @ Returns [1, 2, 4, 5]
```


---


### a_indexOf

_**Finds the first occurrence of an element in an array and returns its index**_


#### Definition

```jaiva
F~a_indexOf(arr, el?)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_el_** **`?`** **`<-`** _**any**_
	 - _The element to find_

_**Returns:**_ **The index of the first occurrence of el in arr, or idk if not found**

> [!NOTE]
> _This uses the search first approach, it may not be the most efficient way to do this, but it works. Throws if the input is not an array._


Since Version: _3.0.0_


#### Example: 

```jaiva
maak arr <-| "apple", "banana", "cherry", "date", "banana"!
khuluma(a_indexOf(arr, "banana"))! @ Returns 1
khuluma(a_indexOf(arr, "date"))! @ Returns 3
khuluma(a_indexOf(arr, "fig"))! @ Returns idk
```


---


### a_reverse

_**Reverses the order of elements in an array and returns a new array**_


#### Definition

```jaiva
F~a_reverse(arr)
```
- **_arr_**  **`<-`** _**[]**_
	 - _The original array_

_**Returns:**_ **A new array with the elements in reverse order**

> [!NOTE]
> _This uses a colonize loop to iterate through the array backwards and constructs a new array with the elements in reverse order. Throws if the input is not an array._


Since Version: _3.0.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4, 5!
khuluma(a_reverse(arr))! @ Returns [5, 4, 3, 2, 1]
```


---


### a_reduce

_**Reduces an array to a single value using a reducer function**_


#### Definition

```jaiva
F~a_reduce(arr, F~reducer, initial?)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array_
- **_reducer_**  **`<-`** _**F~any**_
	 - _A function that takes an accumulator and an element and returns a new accumulator_
- **_initial_** **`?`** **`<-`** _**any**_
	 - _An optional initial value for the accumulator_

_**Returns:**_ **The final value of the accumulator after processing all elements**

> [!NOTE]
> _The reducer function must take in 2 parameters, the accumulator and the current element, and return the new value of the accumulator. If no initial value is provided, the first element of the array is used as the initial value and the reduction starts from the second element Throws if the first argument is not an array or if the second argument is not a function._


Since Version: _3.0.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4!
maak sumReducer <- f~(acc, el) : acc + el! @ A lambda function to sum two numbers
a_reduce(arr, sumReducer, 0)! @ Returns 10
a_reduce(arr)!
|> sumReducer! @ Returns 10
```


---


### a_map

_**Transforms every element in an array using a provided function.**_


#### Definition

```jaiva
F~a_map(arr, F~mapperFunction)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array to map over._
- **_mapperFunction_**  **`<-`** _**F~any**_
	 - _A function that takes the element and additional parameters returns its transformed value._

_**Returns:**_ **A new array populated with the results of calling the mapperFunction on every element.**

> [!NOTE]
> _The mapper function must take in at least 1 parameter, which is the input element. The function can then further take in an index as parameter 2, and the original array at parameter 3 and should return a new transformed version. Throws if the first argument is not an array or if the second argument is not a function._


Since Version: _4.1.1_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4!
maak square <- f~(num) : num * num! @ A lambda function to square a number
khuluma(a_map(arr, square))! @ Returns [1, 4, 9, 16]
```


---


### a_from

_**Creates an array from a string, splitting it by a specified delimiter or index.**_


#### Definition

```jaiva
F~a_from(input, split?)
```

- **_input_**  **`<-`** _**string**_
	 - _The input string to convert into an array._
- **_split_** **`?`** **`<-`** _**idk**_
	 - _An optional delimiter string or index number._

_**Returns:**_ **An array created from the input string, split by the specified delimiter or index.**

> [!NOTE]
> _Throws if the input is not a string. Also, this function imports jaiva/types for type conversions. A performance hit may be observed the first time this function is called._


Since Version: _5.0.0_


#### Example: 

```jaiva
khuluma(a_from("hello"))! @ Returns ["h", "e", "l", "l", "o"]
khuluma(a_from("apple$,banana$,cherry", "$,"))! @ Returns ["apple", "banana", "cherry"]
khuluma(a_from("abcdef", 3))! @ Returns ["abc", "def"]
```


---


### a_forEach

_**Applies the given function to each element in the array**_


#### Definition

```jaiva
F~a_forEach(arr, F~function)
```

- **_arr_**  **`<-`** _**[]**_
	 - _The original array to go over._
- **_function_**  **`<-`** _**F~any**_
	 - _A function that takes one element and extra parameters and shouldn't return really._

_**Returns:**_ **idk**

> [!NOTE]
> _The function must take in at least 1 parameter (the element). and can optionally take in at the 2nd parameter an index, and at the third the original array. Throws if the first argument is not an array or if the second argument is not a function._


Since Version: _5.1.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4!
maak square <- f~(num) : khuluma(num * num)! @ A lambda function to print the square a number
a_forEach(arr, square)! @ Will print the squares.
```


---


### a_apply

_**Applies the supplied function(s) to a given input array. In this case, the first element is the input array  and every other subsequent element is expected to be a function reference. If the supplied function returns  an array then the a_apply function will relace the original with that returned array, otherwise it gets  treated as side-effects.**_


#### Definition

```jaiva
F~a_apply(<-params)
```
- **_params_**  **`<-`** _**[]**_
	 - _Var args parameter. Input any amount of parameters, however the first parameter must be an array and everything else a function._

_**Returns:**_ **The transformed array, or the original array if no functions were given or no functions returned any arrays**

> [!NOTE]
> _Just like normal function, it can take a function reference or lambda, which is itself technically a function reference_


Since Version: _5.1.0_


#### Example: 

```jaiva
maak arr <-| 1, 2, 3, 4!
@
a_apply(arr)!
@ Returns [1, 2, 3, 4]
a_apply(arr, f~(a) : a_map(a, f~(num) : num * 3))
@ Returns [3, 6, 9, 12]
a_apply(arr, khuluma)
@ Returns [1, 2, 3, 4] (khuluma returns idk)
a_apply(arr, a_reverse, f~(a) : a_map(a, f~(num) : num * 3))
@ Returns [12, 9, 6, 3]
a_apply(arr)!
|> a_reverse!
|> f~(a) : a_map(a, f~(num) : num * 3)!
@ Previous example but using newer parameter extension
```


---


### a_findFirst


#### Definition

```jaiva
F~a_findFirst(arr, F~predicate)
```

---


### a_findLast


#### Definition

```jaiva
F~a_findLast(arr, F~predicate)
```

---


### a_find


#### Definition

```jaiva
F~a_find(arr, F~predicate)
```

---

## Variables
