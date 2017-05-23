/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util;

import static com.android.internal.util.ArrayUtils.isEmpty;

import android.annotation.NonNull;
import android.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utility methods for dealing with (typically {@link Nullable}) {@link Collection}s
 *
 * Unless a method specifies otherwise, a null value for a collection is treated as an empty
 * collection of that type.
 */
public class CollectionUtils {
    private CollectionUtils() { /* cannot be instantiated */ }

    /**
     * Returns a list of items from the provided list that match the given condition.
     *
     * This is similar to {@link Stream#filter} but without the overhead of creating an intermediate
     * {@link Stream} instance
     */
    public static @NonNull <T> List<T> filter(@Nullable List<T> list,
            java.util.function.Predicate<? super T> predicate) {
        ArrayList<T> result = null;
        for (int i = 0; i < size(list); i++) {
            final T item = list.get(i);
            if (predicate.test(item)) {
                result = ArrayUtils.add(result, item);
            }
        }
        return emptyIfNull(result);
    }

    /**
     * Returns a list of items resulting from applying the given function to each element of the
     * provided list.
     *
     * The resulting list will have the same {@link #size} as the input one.
     *
     * This is similar to {@link Stream#map} but without the overhead of creating an intermediate
     * {@link Stream} instance
     */
    public static @NonNull <I, O> List<O> map(@Nullable List<I> cur,
            Function<? super I, ? extends O> f) {
        if (isEmpty(cur)) return Collections.emptyList();
        final ArrayList<O> result = new ArrayList<>();
        for (int i = 0; i < cur.size(); i++) {
            result.add(f.apply(cur.get(i)));
        }
        return result;
    }

    /**
     * {@link #map(List, Function)} + {@link #filter(List, java.util.function.Predicate)}
     *
     * Calling this is equivalent (but more memory efficient) to:
     *
     * {@code
     *      filter(
     *          map(cur, f),
     *          i -> { i != null })
     * }
     */
    public static @NonNull <I, O> List<O> mapNotNull(@Nullable List<I> cur,
            Function<? super I, ? extends O> f) {
        if (isEmpty(cur)) return Collections.emptyList();
        final ArrayList<O> result = new ArrayList<>();
        for (int i = 0; i < cur.size(); i++) {
            O transformed = f.apply(cur.get(i));
            if (transformed != null) {
                result.add(transformed);
            }
        }
        return result;
    }

    /**
     * Returns the given list, or an immutable empty list if the provided list is null
     *
     * This can be used to guaranty null-safety without paying the price of extra allocations
     *
     * @see Collections#emptyList
     */
    public static @NonNull <T> List<T> emptyIfNull(@Nullable List<T> cur) {
        return cur == null ? Collections.emptyList() : cur;
    }

    /**
     * Returns the size of the given list, or 0 if the list is null
     */
    public static int size(@Nullable Collection<?> cur) {
        return cur != null ? cur.size() : 0;
    }

    /**
     * Returns the elements of the given list that are of type {@code c}
     */
    public static @NonNull <T> List<T> filter(@Nullable List<?> list, Class<T> c) {
        if (isEmpty(list)) return Collections.emptyList();
        ArrayList<T> result = null;
        for (int i = 0; i < list.size(); i++) {
            final Object item = list.get(i);
            if (c.isInstance(item)) {
                result = ArrayUtils.add(result, (T) item);
            }
        }
        return emptyIfNull(result);
    }

    /**
     * Returns whether there exists at least one element in the list for which
     * condition {@code predicate} is true
     */
    public static <T> boolean any(@Nullable List<T> items,
            java.util.function.Predicate<T> predicate) {
        return find(items, predicate) != null;
    }

    /**
     * Returns the first element from the list for which
     * condition {@code predicate} is true, or null if there is no such element
     */
    public static @Nullable <T> T find(@Nullable List<T> items,
            java.util.function.Predicate<T> predicate) {
        if (isEmpty(items)) return null;
        for (int i = 0; i < items.size(); i++) {
            final T item = items.get(i);
            if (predicate.test(item)) return item;
        }
        return null;
    }

    /**
     * Similar to {@link List#add}, but with support for list values of {@code null} and
     * {@link Collections#emptyList}
     */
    public static @NonNull <T> List<T> add(@Nullable List<T> cur, T val) {
        if (cur == null || cur == Collections.emptyList()) {
            cur = new ArrayList<>();
        }
        cur.add(val);
        return cur;
    }

    /**
     * Similar to {@link List#remove}, but with support for list values of {@code null} and
     * {@link Collections#emptyList}
     */
    public static @NonNull <T> List<T> remove(@Nullable List<T> cur, T val) {
        if (isEmpty(cur)) {
            return emptyIfNull(cur);
        }
        cur.remove(val);
        return cur;
    }

    /**
     * @return a list that will not be affected by mutations to the given original list.
     */
    public static @NonNull <T> List<T> copyOf(@Nullable List<T> cur) {
        return isEmpty(cur) ? Collections.emptyList() : new ArrayList<>(cur);
    }
}
