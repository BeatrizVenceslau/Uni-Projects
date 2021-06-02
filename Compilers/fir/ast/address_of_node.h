#ifndef __FIR_AST_ADDRESS_OF_NODE_H__
#define __FIR_AST_ADDRESS_OF_NODE_H__

#include <cdk/ast/lvalue_node.h>
#include <cdk/ast/expression_node.h>

namespace fir {

  /**
   * Class for addressing nodes.
   */
  class address_of_node: public cdk::expression_node {
    cdk::lvalue_node *_value;

  public:
    inline address_of_node(int lineno, cdk::lvalue_node *value) :
        cdk::expression_node(lineno), _value(value) {
    }

  public:
    inline cdk::lvalue_node *value() {
      return _value;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_address_of_node(this, level);
    }

  };

} // fir

#endif
