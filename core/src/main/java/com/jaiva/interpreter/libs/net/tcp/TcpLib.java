package com.jaiva.interpreter.libs.net.tcp;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;

@JaivaLibrary(
        path = "net/tcp",
        description = "Holds TCP related shiz yknow."
)
public class TcpLib extends BaseLibrary {
    public TcpLib() {
        add(new ServSocketContainerLib());
        add(new SocketContainerLib());
    }
}