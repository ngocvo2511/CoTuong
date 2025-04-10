package com.example.cotuong.saveservice;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new HashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new HashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    @Override
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }

        final Map<String, TypeAdapter<?>> labelToDelegate = new HashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new HashMap<>();

        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);

                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                jsonObject.addProperty(typeFieldName, label);
                new Gson().toJson(jsonObject, out);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement jsonElement = JsonParser.parseReader(in);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement labelJsonElement = jsonObject.remove(typeFieldName);

                if (labelJsonElement == null) {
                    throw new JsonParseException("Missing type field: " + typeFieldName);
                }

                String label = labelJsonElement.getAsString();
                Class<?> subtype = labelToSubtype.get(label);
                if (subtype == null) {
                    throw new JsonParseException("Unknown type label: " + label);
                }

                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                return delegate.fromJsonTree(jsonObject);
            }
        };
    }
}