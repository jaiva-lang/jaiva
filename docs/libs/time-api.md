# time/api (Library)

_The time api where the relevant things such as parsing dates are._
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| t_now | [t_now](#t_now) |
| t_msToSec | [t_msToSec](#t_msToSec) |
| t_parseDate | [t_parseDate](#t_parseDate) |
| t_maxTime | [t_maxTime](#t_maxTime) |
## Functions

### t_now

This symbol can be reached by the following aliases: _`t_now`_

_**Returns the current time in milliseconds since the Unix epoch.**_


#### Definition

```jaiva
F~t_now()
```

_**Returns:**_ **A number representing the current time in milliseconds.**

Since Version: _5.0.0_


---


### t_msToSec

This symbol can be reached by the following aliases: _`t_msToSec`_

_**Converts milliseconds to seconds.**_


#### Definition

```jaiva
F~t_msToSec(milliseconds)
```
- **_milliseconds_**  **`<-`** _**number**_
	 - _The number of milliseconds to convert._

_**Returns:**_ **A number representing the converted seconds.**

Since Version: _5.0.0_


---


### t_parseDate

This symbol can be reached by the following aliases: _`t_parseDate`_

_**Parses a date string into milliseconds since the Unix epoch.**_


#### Definition

```jaiva
F~t_parseDate(dateString, format?, timezone?)
```

- **_dateString_**  **`<-`** _**string**_
	 - _The date string to parse._
- **_format_** **`?`** **`<-`** _**string**_
	 - _The format of the date string (e.g. "yyyy-MM-dd HH:mm:ss").Defaults to ISO_LOCAL_DATE_TIME,_
- **_timezone_** **`?`** **`<-`** _**string**_
	 - _The timezone to parse this date into. You can either put a magic string yourself or use the constants within "jaiva/time/zone"_

_**Returns:**_ **A number representing the parsed date in milliseconds.**

> [!NOTE]
> _If no timezone is provided, a default timezone of UTC is used._


Since Version: _5.0.0_


#### Example: 

```jaiva
@ Import jaiva/time/zone
tsea "jaiva/time/zone" <- TZ_AfricaJohannesburg!
maak ms <- t_parseDate("2023-10-05 14:30:00", "yyyy-MM-dd HH:mm:ss", TZ_AfricaJohannesburg)!
khuluma(ms)! @ Outputs the milliseconds since epoch for the given date in the specified timezone.
```


---

## Variables

### t_maxTime

This symbol can be reached by the following aliases: _`t_maxTime`_

_**Returns the maximum possible value for a time in milliseconds (Long.MAX_VALUE).**_


#### Definition
```jaiva
maak t_maxTime <- (number)
```

Since Version: _5.0.0_


---

