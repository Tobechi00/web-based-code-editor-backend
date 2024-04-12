package com.wide.widebackend.service.code;

import java.util.Optional;

public interface CodeRunnerService<T> {

    public T runCodeWithoutInput(String code,Optional<String> fileName);

    public T runCodeWithInput(String code, String input, Optional<String> fileName);
}
