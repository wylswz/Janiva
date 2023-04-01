package com.oracle.truffle.jx.statics.lambda;

import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.jx.nodes.JXExpressionNode;
import com.oracle.truffle.jx.nodes.core.JXIfNode;
import com.oracle.truffle.jx.parser.exceptions.JXSyntaxError;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum BuiltInLambda implements BuiltInLambdaFactory {

    IF {
        @Override
        public JXExpressionNode create(List<JXExpressionNode> arguments) {
            return new JXIfNode(
                    arguments.get(0),
                    arguments.get(1),
                    arguments.get(2)
            );
        }

        @Override
        public TruffleString lambdaName() {
            return TruffleString.fromJavaStringUncached("if", TruffleString.Encoding.UTF_8);
        }
    };

    static final Map<TruffleString, BuiltInLambda> cache = new ConcurrentHashMap<>();
    public static BuiltInLambda valueOf(TruffleString ts) {
        if (!cache.containsKey(ts)) {
            for (BuiltInLambda bl : BuiltInLambda.values()) {
                if (bl.lambdaName().equals(ts)) {
                    cache.put(bl.lambdaName(), bl);
                }
            }
        }
        return cache.get(ts);
    }
}
