/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


public class TreeUnmarshaller implements UnmarshallingContext {

    private final Object root;
    protected HierarchicalStreamReader reader;
    private final ConverterLookup converterLookup;
    private final Mapper mapper;
    private final FastStack<Class<?>> types = new FastStack<>(16);
    private final FastStack<String> fieldNames = new FastStack<>(16);
    private final FastStack<String> classNames = new FastStack<>(16);
    private DataHolder dataHolder;
    private final PrioritizedList<Runnable> validationList = new PrioritizedList<>();

    public TreeUnmarshaller(
            final Object root, final HierarchicalStreamReader reader, final ConverterLookup converterLookup,
            final Mapper mapper) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
    }

    @Override
    public Object convertAnother(final Object parent, final Class<?> type) {
        return convertAnother(parent, type, null);
    }

    @Override
    public Object convertAnother(final Object parent, Class<?> type, Converter converter) {
        type = mapper.defaultImplementationOf(type);
        if (converter == null) {
            converter = converterLookup.lookupConverterForType(type);
        } else {
            if (!converter.canConvert(type)) {
                final ConversionException e = new ConversionException("Explicitly selected converter cannot handle type");
                e.add("item-type", type.getName());
                e.add("converter-type", converter.getClass().getName());
                throw e;
            }
        }
        return convert(parent, type, converter);
    }

    /* public Object convertAnother0(final Object parent, Class<?> type, Converter converter, Field field) {
        type = mapper.defaultImplementationOf(type);
        if (converter == null) {
            converter = converterLookup.lookupConverterForType(type);
        } else {
            if (!converter.canConvert(type)) {
                final ConversionException e = new ConversionException("Explicitly selected converter cannot handle type");
                e.add("item-type", type.getName());
                e.add("converter-type", converter.getClass().getName());
                throw e;
            }
        }
        return convert0(parent, type, converter, field);
    } */

    protected Object convert(final Object parent, final Class<?> type, final Converter converter) {
        types.push(type);
        try {
            String currentClass = System.getProperty("currentClassInXStream");
            String currentField = System.getProperty("currentFieldInXStream");
            System.out.println("CURRENT0: " + currentClass + " + " + currentField);
            /*
            Class clz = Class.forName(currentClass);
            Field field = clz.getDeclaredField(currentField);
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            Object cur = this.getRequiredObject();
            System.out.println("FIELDNAME IN Tree: " + field.getName());
            Object ob;
            if(parent instanceof Map) {
                ob = converter.unmarshal(reader, this);
            }
            else if (Modifier.isStatic(field.getModifiers())){
                ob = field.getType().cast(field.get(null));
            }
            else {
                ob = field.getType().cast(field.get(cur));
            }
            System.out.println("NEW OBJ" + ob.getClass());
            */
            if(parent instanceof Map) {
                classNames.push("java.util.Map");
                fieldNames.push("entry");
            }
            else{
                classNames.push(currentClass);
                fieldNames.push(currentField);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println("TREEUNMARSHALLER + TPYE(NAME): " + type.getName());
        // System.out.println("TREEUNMARSHALLER + OBJECT(CLASS): " + parent.getClass());
        try {
            return converter.unmarshal(reader, this);
        } catch (final ConversionException conversionException) {
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } catch (final RuntimeException e) {
            final ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } finally {
            types.popSilently();
            classNames.popSilently();
            fieldNames.popSilently();
        }
    }

    /* protected Object convert0(final Object parent, final Class<?> type, final Converter converter, final Field field) {
        types.push(type);
        try {
            field.setAccessible(true);
            Object cur = this.getRequiredObject();
            System.out.println("CUR OBJ: " + cur.getClass());
            System.out.println("FIELDNAME IN Tree: " + field.getName());
            Object ob;
            if (Modifier.isStatic(field.getModifiers())){
                ob = field.get(null);
            }
            else {
                ob = field.get(cur);
            }
            System.out.println("NEW OBJ" + ob.getClass());
            objects.push(ob);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        System.out.println("TREEUNMARSHALLER + TPYE(NAME): " + type.getName());
        // System.out.println("TREEUNMARSHALLER + OBJECT(CLASS): " + parent.getClass());
        try {
            return converter.unmarshal(reader, this);
        } catch (final ConversionException conversionException) {
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } catch (final RuntimeException e) {
            final ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } finally {
            types.popSilently();
            // objects.popSilently();
        }
    } */

    private void addInformationTo(final ErrorWriter errorWriter, final Class<?> type, final Converter converter,
            final Object parent) {
        errorWriter.add("class", type.getName());
        errorWriter.add("required-type", getRequiredType().getName());
        errorWriter.add("converter-type", converter.getClass().getName());
        if (converter instanceof ErrorReporter) {
            ((ErrorReporter)converter).appendErrors(errorWriter);
        }
        if (parent instanceof ErrorReporter) {
            ((ErrorReporter)parent).appendErrors(errorWriter);
        }
        reader.appendErrors(errorWriter);
    }

    @Override
    public void addCompletionCallback(final Runnable work, final int priority) {
        validationList.add(work, priority);
    }

    @Override
    public Object currentObject() {
        return types.size() == 1 ? root : null;
    }

    public FastStack<Class<?>> getTypes() {
        return types;
    }

    public FastStack<String> getFieldNames() {
        return fieldNames;
    }

    public FastStack<String> getClassNames() {
        return classNames;
    }

    @Override
    public Class<?> getRequiredType() {
        return types.peek();
    }

    public String getRequiredFieldName() {
        return fieldNames.peek();
    }

    public String getRequiredClassName() {
        return classNames.peek();
    }

    @Override
    public Object get(final Object key) {
        lazilyCreateDataHolder();
        return dataHolder.get(key);
    }

    @Override
    public void put(final Object key, final Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    @Override
    public Iterator<Object> keys() {
        lazilyCreateDataHolder();
        return dataHolder.keys();
    }

    private void lazilyCreateDataHolder() {
        if (dataHolder == null) {
            dataHolder = new MapBackedDataHolder();
        }
    }

    public Object start(final DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        String attr = HierarchicalStreams.readClassAttribute(reader, mapper);
        System.out.println("TreeUnmarshal attributeName: " + attr);
        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
        // objects.push(this.root);
        final Object result = convertAnother(null, type);
        // objects.popSilently();
        for (final Runnable runnable : validationList) {
            runnable.run();
        }
        return result;
    }

    protected Mapper getMapper() {
        return mapper;
    }

}
