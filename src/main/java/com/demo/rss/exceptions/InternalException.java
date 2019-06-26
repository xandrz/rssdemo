package com.demo.rss.exceptions;

import org.springframework.remoting.RemoteTimeoutException;

public class InternalException extends RemoteTimeoutException {
    public InternalException(String msg) {
        super(msg);
    }
}
