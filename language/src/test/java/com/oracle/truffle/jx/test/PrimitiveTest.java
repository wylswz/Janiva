/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.jx.test;

import com.oracle.truffle.jx.JSONXLang;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class PrimitiveTest {
  private static final Logger logger = LoggerFactory.getLogger(PrimitiveTest.class);
  Context context;

  @Before
  public void initialize() {
    context = Context.create();
  }

  @After
  public void dispose() {
    context.close();
  }

  @Test
  public void testNumber() {
    TestUtil.runWithStackTrace(() -> {
      Value v = context.eval(JSONXLang.ID, "3.5");
      // logger.debug("Value of 3.5 to double is: " + v.asDouble());
      Assert.assertEquals(3.5, v.asDouble(), 0.001);
    });
  }

  @Test
  public void testString() {
    Value v = context.eval(JSONXLang.ID, "\"asd\"");
    Assert.assertEquals("asd", v.asString());
  }

  @Test
  public void testArithmetic() {
    TestUtil.runWithStackTrace(() -> {
      Value v = context.eval(JSONXLang.ID, "1 + 2");
      Assert.assertEquals(3, v.asInt());

      v = context.eval(JSONXLang.ID, "1 + \"a\"");
      Assert.assertEquals("1a", v.asString());

      v = context.eval(JSONXLang.ID, "1 + 2 * 3");
      Assert.assertEquals(7, v.asInt());

      v = context.eval(JSONXLang.ID, "1 + (1 + 2 * 3)");
      Assert.assertEquals(8, v.asInt());
    });
  }

  @Test
  public void testBoolean() {
    TestUtil.runWithStackTrace(
        () -> {
          Value v = context.eval(JSONXLang.ID, "true");
          assertTrue(v.asBoolean());

          v = context.eval(JSONXLang.ID, "false");
          Assert.assertFalse(v.asBoolean());
        });
  }
}
