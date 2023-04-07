package com.oracle.truffle.jx.nodes.core;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.jx.nodes.JXExpressionNode;
import java.util.List;

public class JXLambdaExecutor extends RootNode {
  private List<JXLambdaArgBindingNode> parameterBindingNodes;
  private JXExpressionNode evalNode;

  protected JXLambdaExecutor(
      TruffleLanguage<?> language,
      FrameDescriptor frameDescriptor,
      List<JXLambdaArgBindingNode> parameterBindingNodes,
      JXExpressionNode evalNode) {
    super(language, frameDescriptor);
    this.parameterBindingNodes = parameterBindingNodes;
    this.evalNode = evalNode;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    for (JXLambdaArgBindingNode bindingNode : parameterBindingNodes) {
      bindingNode.executeVoid(frame);
    }
    return evalNode.executeGeneric(frame);
  }
}
