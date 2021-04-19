/*
 * Copyright (C) 2013, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 08. January 2016 by Joerg Schaible, factored out from FieldAliasingMapper.
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.core.util.FastField;


/**
 * Mapper that allows an field of a specific class to be omitted entirely.
 *
 * @author Joerg Schaible
 */
public class ElementIgnoringMapper extends MapperWrapper {

    protected final Set<Pattern> unknownElementsToIgnore = new LinkedHashSet<>();
    protected final Set<FastField> fieldsToOmit = new HashSet<>();

    public ElementIgnoringMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addElementsToIgnore(final Pattern pattern) {
        unknownElementsToIgnore.add(pattern);
    }

    public void omitField(final Class<?> definedIn, final String fieldName) {
        fieldsToOmit.add(key(definedIn, fieldName));
    }

    @Override
    public boolean shouldSerializeMember(final Class<?> definedIn, final String fieldName) {
        if (fieldsToOmit.contains(key(definedIn, fieldName))) {
            return false;
        } else if (definedIn == Object.class && isIgnoredElement(fieldName)) {
            return false;
        }
        try {
            // Hack to ignore field of type CodeSource
            System.out.println("IGNORING: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);

            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.CodeSource"))) {
                System.out.println("IGNORING CodeSource: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("sun.nio.cs.UTF_8$Decoder"))) {
                System.out.println("IGNORING UTF_8$Decoder: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.nio.charset.CharsetEncoder"))) {
                System.out.println("IGNORING CharsetEncoder: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.nio.charset.CharsetDecoder"))) {
                System.out.println("IGNORING CharsetDecoder: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            } 
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("sun.nio.cs.StreamEncoder"))) {
                System.out.println("IGNORING StreamEncoder: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.zip.ZipCoder"))) {
                System.out.println("IGNORING ZipCoder: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("com.sun.crypto.provider.SunJCE"))) {
                System.out.println("IGNORING SunJCE: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.lang.ClassLoader"))){
                System.out.println("IGNORING ClassLoader: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.SecureClassLoader"))){
                System.out.println("IGNORING SecureClassLoader: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.Provider"))) {
                System.out.println("IGNORING java.security.Provider: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
	        if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("javax.security.auth.Subject"))){
                System.out.println("IGNORING auth.Subject: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
        } catch (Exception exception){
            // ignore
            System.out.println("EXCEPTION IN IGNORING:" + fieldName + " - " + exception);
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }

    @Override
    public boolean isIgnoredElement(final String name) {
        if (!unknownElementsToIgnore.isEmpty()) {
            for (final Pattern pattern : unknownElementsToIgnore) {
                if (pattern.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return super.isIgnoredElement(name);
    }

    private FastField key(final Class<?> type, final String name) {
        return new FastField(type, name);
    }
}
