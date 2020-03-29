/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.reflectmaid;

import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

public final class UnresolvableTypeVariableException extends RuntimeException {

    private UnresolvableTypeVariableException(final String message) {
        super(message);
    }

    public static UnresolvableTypeVariableException unresolvableTypeVariableException(final TypeVariableName variableName) {
        validateNotNull(variableName, "variableName");
        final String message = format("No type variable with name '%s'", variableName.name());
        return new UnresolvableTypeVariableException(message);
    }
}