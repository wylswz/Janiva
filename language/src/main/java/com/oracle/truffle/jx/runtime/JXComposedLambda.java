package com.oracle.truffle.jx.runtime;

import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import java.util.Arrays;import java.util.Collections;import java.util.List;
import java.util.stream.Collectors;

@ExportLibrary(InteropLibrary.class)
@ExportLibrary(LambdaLibrary.class)
public class JXComposedLambda extends DynamicObject implements TruffleObject {
  private static final Shape SHAPE = Shape.newBuilder().layout(JXComposedLambda.class).build();

  private static final String PROP_MEMBERS = "members";

  @DynamicField private Object _o1;

  private final Object[] members;

  protected JXComposedLambda(Object[] composedLambdas) {
    super(SHAPE);
    this.members = composedLambdas;
  }

  @ExportMessage(library = LambdaLibrary.class)
  public JXComposedLambda cloneLambda(
          @CachedLibrary("this") LambdaLibrary lambdaLibrary
  ) {
    return new JXComposedLambda(
        Arrays.stream(members).map(lambdaLibrary::cloneLambda).toArray(Object[]::new));
  }

  @ExportMessage(library = LambdaLibrary.class)
  public JXComposedLambda mergeArgs(
          Object[] args,
          @CachedLibrary("this") LambdaLibrary lambdaLibrary
          ) {
    // clone, merge args and
    this.members[0] = lambdaLibrary.mergeArgs(lambdaLibrary.cloneLambda(this.members[0]), args);
    return this;
  }

  @ExportMessage
  public Object execute(Object[] args,
                        @CachedLibrary("this") InteropLibrary library,
                        @CachedLibrary("this") LambdaLibrary lambdaLibrary
  ) throws UnsupportedMessageException, UnsupportedTypeException, ArityException {
    Object res = library.execute(this.members[0], args);
    for (int i=1; i<this.members.length; i++) {
      // clone next, merge args and execute
      res = library.execute(lambdaLibrary.mergeArgs(lambdaLibrary.cloneLambda(this.members[i]), new Object[]{res}));
    }
    return res;
  }

  @ExportMessage
  public boolean isExecutable(
          @CachedLibrary("this") InteropLibrary library
  ) {
    return library.isExecutable(members[0]);
  }


}
