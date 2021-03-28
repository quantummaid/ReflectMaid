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
package de.quantummaid.reflectmaid.resolvedtype.resolver

import de.quantummaid.reflectmaid.ReflectMaid
import de.quantummaid.reflectmaid.resolvedtype.ClassType
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter.Companion.resolveParameters
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

data class ResolvedConstructor(val parameters: List<ResolvedParameter>,
                               val constructor: Constructor<*>) {

    val isPublic: Boolean
        get() {
            val modifiers = constructor.modifiers
            return Modifier.isPublic(modifiers)
        }

    fun describe(): String {
        return constructor.toGenericString()
    }

    companion object {
        @JvmStatic
        fun resolveConstructors(reflectMaid: ReflectMaid,
                                fullType: ClassType): List<ResolvedConstructor> {
            return fullType.assignableType().declaredConstructors
                    .filter {  !it.isSynthetic }
                    .map { resolveConstructor(reflectMaid, it, fullType) }
        }

        fun resolveConstructor(reflectMaid: ReflectMaid,
                               constructor: Constructor<*>,
                               fullType: ClassType): ResolvedConstructor {
            val parameters = resolveParameters(reflectMaid, constructor, fullType)
            return ResolvedConstructor(parameters, constructor)
        }
    }
}