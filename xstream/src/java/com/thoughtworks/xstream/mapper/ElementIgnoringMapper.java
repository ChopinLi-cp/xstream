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
            // System.out.println("PROCESS: " + definedIn.getName() + " - " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);

            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.CodeSource"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("sun.nio.cs.UTF_8$Decoder"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.nio.charset.CharsetEncoder"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.nio.charset.CharsetDecoder"))) {
                return false;
            } 
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("sun.nio.cs.StreamEncoder"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.zip.ZipCoder"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("com.sun.crypto.provider.SunJCE"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.lang.ClassLoader"))) {
                return false;
            }
            if (Class.forName("java.lang.ClassLoader").isAssignableFrom(definedIn.getDeclaredField(fieldName).getType())) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.SecureClassLoader"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.Provider"))) {
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("javax.security.auth.Subject"))) {
                return false;
            }
            /* if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.concurrent.locks.AbstractQueuedSynchronizer"))) {
                System.out.println("IGNORING AbstractQueuedSynchronizer: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("[Ljava.lang.ThreadGroup;"))) {
                System.out.println("IGNORING ThreadGroup: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("[Ljava.lang.Thread;"))) {
                System.out.println("IGNORING Thread: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.concurrent.ScheduledFuture"))) {
                System.out.println("IGNORING ScheduledFuture: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.concurrent.ScheduledExecutorService"))) {
                System.out.println("IGNORING ScheduledExecutorService: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("org.apache.dubbo.remoting.Server"))) {
                System.out.println("IGNORING Server: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            } */
            /* if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.text.DateFormatSymbols"))) {
                System.out.println("IGNORING DateFormatSymbols: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("org.apache.log4j.Logger"))) {
                System.out.println("IGNORING Logger: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.util.Collections$SingletonSet"))) {
                System.out.println("IGNORING java.util.Collections$SingletonSet: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.security.AccessControlContext"))) {
                System.out.println("IGNORING AccessControlContext: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("[Lio.netty.util.concurrent.EventExecutor;"))) {
                System.out.println("IGNORING EventExecutor: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("[Ljava.util.concurrent.RunnableScheduledFuture;"))) {
                System.out.println("IGNORING RunnableScheduledFuture: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("[Ljava.nio.channels.SelectionKey;"))) {
                System.out.println("IGNORING SelectionKey: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("java.lang.Class"))) {
                System.out.println("IGNORING java.lang.Class: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }
            if (definedIn.getDeclaredField(fieldName).getType().equals(Class.forName("org.apache.dubbo.rpc.Protocol$Adaptive"))) {
                System.out.println("IGNORING $Adaptive: " + definedIn.getDeclaredField(fieldName).getType() + " - " + fieldName);
                return false;
            }*/
        } catch (Exception exception) {
            // ignore
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
