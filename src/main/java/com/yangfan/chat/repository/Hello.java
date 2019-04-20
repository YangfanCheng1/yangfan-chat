package com.yangfan.chat.repository;

public interface Hello<T extends Hello<T>> {
    T doSomething();
}
