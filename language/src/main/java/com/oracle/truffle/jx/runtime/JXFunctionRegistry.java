/*
 * Copyright (c) 2012, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.jx.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.jx.JSONXLang;

import java.util.*;

/** Manages the mapping from function names to {@link JXFunction function objects}. */
public final class JXFunctionRegistry {

  private final JSONXLang language;
  private final FunctionsObject functionsObject = new FunctionsObject();
  private final Map<Map<TruffleString, RootCallTarget>, Void> registeredFunctions =
      new IdentityHashMap<>();

  public JXFunctionRegistry(JSONXLang language) {
    this.language = language;
  }

  /**
   * Returns the canonical {@link JXFunction} object for the given name. If it does not exist yet,
   * it is created.
   */
  @TruffleBoundary
  public JXFunction lookup(TruffleString name, boolean createIfNotPresent) {
    JXFunction result = functionsObject.functions.get(name);
    if (result == null && createIfNotPresent) {
      result = new JXFunction(language, name);
      functionsObject.functions.put(name, result);
    }
    return result;
  }

  /**
   * Associates the {@link JXFunction} with the given name with the given implementation root node.
   * If the function did not exist before, it defines the function. If the function existed before,
   * it redefines the function and the old implementation is discarded.
   */
  JXFunction register(TruffleString name, RootCallTarget callTarget) {
    JXFunction result = functionsObject.functions.get(name);
    if (result == null) {
      result = new JXFunction(name, callTarget);
      functionsObject.functions.put(name, result);
    } else {
      result.setCallTarget(callTarget);
    }
    return result;
  }

  /**
   * Registers a map of functions. The once registered map must not change in order to allow to
   * cache the registration for the entire map. If the map is changed after registration the
   * functions might not get registered.
   */
  @TruffleBoundary
  public void register(Map<TruffleString, RootCallTarget> newFunctions) {
    if (registeredFunctions.containsKey(newFunctions)) {
      return;
    }
    for (Map.Entry<TruffleString, RootCallTarget> entry : newFunctions.entrySet()) {
      register(entry.getKey(), entry.getValue());
    }
    registeredFunctions.put(newFunctions, null);
  }

  public JXFunction getFunction(TruffleString name) {
    return functionsObject.functions.get(name);
  }

  /** Returns the sorted list of all functions, for printing purposes only. */
  public List<JXFunction> getFunctions() {
    List<JXFunction> result = new ArrayList<>(functionsObject.functions.values());
    Collections.sort(
        result,
        new Comparator<JXFunction>() {
          public int compare(JXFunction f1, JXFunction f2) {
            assert JSONXLang.STRING_ENCODING == TruffleString.Encoding.UTF_16
                : "SLLanguage.ENCODING changed, string comparison method must be adjusted accordingly!";
            return f1.getName().compareCharsUTF16Uncached(f2.getName());
          }
        });
    return result;
  }

  public TruffleObject getFunctionsObject() {
    return functionsObject;
  }
}