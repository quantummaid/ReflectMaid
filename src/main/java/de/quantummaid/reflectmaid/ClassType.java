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

import de.quantummaid.reflectmaid.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolver.ResolvedField;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static de.quantummaid.reflectmaid.UnresolvableTypeVariableException.unresolvableTypeVariableException;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassType implements ResolvedType {
    private final Class<?> clazz;
    private final Map<TypeVariableName, ResolvedType> typeParameters;
    @Exclude
    private List<ResolvedMethod> methods;
    @Exclude
    private List<ResolvedConstructor> constructors;
    @Exclude
    private List<ResolvedField> fields;

    public static ClassType fromClassWithoutGenerics(final Class<?> type) {
        NotNullValidator.validateNotNull(type, "type");
        if (type.isArray()) {
            throw new UnsupportedOperationException();
        }
        if (type.getTypeParameters().length != 0) {
            throw new UnsupportedOperationException(
                    format("Type variables of '%s' cannot be resolved", type.getName()));
        }
        return fromClassWithGenerics(type, emptyMap());
    }

    public static ClassType fromClassWithGenerics(final Class<?> type,
                                                  final Map<TypeVariableName, ResolvedType> typeParameters) {
        NotNullValidator.validateNotNull(type, "type");
        NotNullValidator.validateNotNull(typeParameters, "typeParameters");
        if (type.isArray()) {
            throw new UnsupportedOperationException();
        }
        return new ClassType(type, typeParameters);
    }

    public ResolvedType typeParameter(final TypeVariableName name) {
        if (!this.typeParameters.containsKey(name)) {
            throw new IllegalArgumentException("No type parameter with the name: " + name.name());
        }
        return this.typeParameters.get(name);
    }

    @Override
    public List<ResolvedType> typeParameters() {
        return TypeVariableName.typeVariableNamesOf(this.clazz).stream()
                .map(this.typeParameters::get)
                .collect(toList());
    }

    ResolvedType resolveTypeVariable(final TypeVariableName name) {
        if (!this.typeParameters.containsKey(name)) {
            throw unresolvableTypeVariableException(name);
        }
        return this.typeParameters.get(name);
    }

    public List<ResolvedMethod> methods() {
        if (this.methods == null) {
            this.methods = ResolvedMethod.resolveMethodsWithResolvableTypeVariables(this);
        }
        return unmodifiableList(this.methods);
    }

    public List<ResolvedConstructor> constructors() {
        if (this.constructors == null) {
            this.constructors = ResolvedConstructor.resolveConstructors(this);
        }
        return unmodifiableList(this.constructors);
    }

    public List<ResolvedField> fields() {
        if (this.fields == null) {
            this.fields = ResolvedField.resolvedFields(this);
        }
        return unmodifiableList(this.fields);
    }

    @Override
    public String description() {
        if (this.typeParameters.isEmpty()) {
            return this.clazz.getName();
        }
        final String parametersString = this.typeParameters().stream()
                .map(ResolvedType::description)
                .collect(joining(", ", "<", ">"));
        return this.clazz.getName() + parametersString;
    }

    @Override
    public String simpleDescription() {
        if (this.typeParameters.isEmpty()) {
            return this.clazz.getSimpleName();
        }
        final String parametersString = this.typeParameters().stream()
                .map(ResolvedType::simpleDescription)
                .collect(joining(", ", "<", ">"));
        return this.clazz.getSimpleName() + parametersString;
    }

    @Override
    public boolean isPublic() {
        final int modifiers = this.clazz.getModifiers();
        return Modifier.isPublic(modifiers);
    }

    @Override
    public boolean isAbstract() {
        if (this.clazz.isPrimitive()) {
            return false;
        }
        return Modifier.isAbstract(this.clazz.getModifiers());
    }

    @Override
    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    @Override
    public boolean isAnonymousClass() {
        return this.clazz.isAnonymousClass();
    }

    @Override
    public boolean isInnerClass() {
        return this.clazz.getEnclosingClass() != null;
    }

    @Override
    public boolean isLocalClass() {
        return this.clazz.isLocalClass();
    }

    @Override
    public boolean isStatic() {
        final int modifiers = this.clazz.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    @Override
    public boolean isAnnotation() {
        return this.clazz.isAnnotation();
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public Class<?> assignableType() {
        return this.clazz;
    }
}
