package com.jaiva.interpreter.libs.net;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.net.interfaces.NetInterfaces;
import com.jaiva.interpreter.libs.net.tcp.TcpLib;

@JaivaLibrary(path = "net", description = "Networking")
@Exports({TcpLib.class, NetConst.class, NetInterfaces.class})
public class NetExports extends BaseLibrary {}
