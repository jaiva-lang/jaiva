# net/interf/query (Library)

_instead of querying data yourself you can use these helper functions lol_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| net_iName | [net_iName](#net_iName) |
| net_iDisplayName | [net_iDisplayName](#net_iDisplayName) |
| net_iAddrIpv4List | [net_iAddrIpv4List](#net_iAddrIpv4List) |
| net_iAddrIpv6List | [net_iAddrIpv6List](#net_iAddrIpv6List) |
| net_iActive | [net_iActive](#net_iActive) |
| net_iLoopback | [net_iLoopback](#net_iLoopback) |
| net_iVirtual | [net_iVirtual](#net_iVirtual) |
| net_iAddrMac | [net_iAddrMac](#net_iAddrMac) |
## Functions

### net_iName

This symbol can be reached by the following aliases: _`net_iName`_

_**Gets the name of the network interface**_


#### Definition

```jaiva
F~net_iName(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iName(firstInterface)!
```


---


### net_iDisplayName

This symbol can be reached by the following aliases: _`net_iDisplayName`_

_**Get the display name of this network interface. usually a human readable string**_


#### Definition

```jaiva
F~net_iDisplayName(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iDisplayName(firstInterface)!
```


---


### net_iAddrIpv4List

This symbol can be reached by the following aliases: _`net_iAddrIpv4List`_

_**Gets the list of Ipv4 IPs as byte arrays (or strings) associated with the interface**_


#### Definition

```jaiva
F~net_iAddrIpv4List(array, format?)
```

- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_
- **_format_** **`?`** **`<-`** _**boolean**_
	 - _Whether to format each byte array into the human readable IPv4 format_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iAddrIpv4List(firstInterface)!
```


---


### net_iAddrIpv6List

This symbol can be reached by the following aliases: _`net_iAddrIpv6List`_

_**Gets the list of Ipv6 IPs as byte arrays associated with the interface**_


#### Definition

```jaiva
F~net_iAddrIpv6List(array, format?)
```

- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_
- **_format_** **`?`** **`<-`** _**boolean**_
	 - _Whether to format each byte array into the human readable IPv6 format_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iAddrIpv6List(firstInterface)!
```


---


### net_iActive

This symbol can be reached by the following aliases: _`net_iActive`_

_**Gets the boolean indicating whether the interface is up and running**_


#### Definition

```jaiva
F~net_iActive(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iActive(firstInterface)!
```


---


### net_iLoopback

This symbol can be reached by the following aliases: _`net_iLoopback`_

_**Gets the boolean indicating whether the interface is a loopback one.**_


#### Definition

```jaiva
F~net_iLoopback(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iLoopback(firstInterface)!
```


---


### net_iVirtual

This symbol can be reached by the following aliases: _`net_iVirtual`_

_**Gets the boolean indicating whether the interface is a virtual one. (or subinterface)**_


#### Definition

```jaiva
F~net_iVirtual(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iVirtual(firstInterface)!
```


---


### net_iAddrMac

This symbol can be reached by the following aliases: _`net_iAddrMac`_

_**gets the hardware address of this interface (The MAC Address) If there isn't one this returns idk**_


#### Definition

```jaiva
F~net_iAddrMac(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array which to index into and get information of. Represents a network interface_

Since Version: _7.0.0_


#### Example: 

```jaiva
 tsea "jaiva/net/interf"!
 
 maak interfaces <- net_interfs()!
 maak firstInterface <- interfaces[0]!
 maak value <- net_iAddrMac(firstInterface)!
```


---

## Variables
