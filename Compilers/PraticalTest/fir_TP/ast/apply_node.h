#ifndef __FIR_AST_APPLY_NODE_H__
#define __FIR_AST_APPLY_NODE_H__

#include <string>
#include <cdk/ast/expression_node.h>

namespace fir {

  /**
   * Class for teste pratico.
   */
  class apply_node: public cdk::basic_node {
    cdk::expression_node *_vector;
    std::string _func;
    cdk::expression_node *_low;
    cdk::expression_node *_high;

  public:
    inline apply_node(int lineno, cdk::expression_node *vector, std::string func, cdk::expression_node *low, cdk::expression_node *high) :
        basic_node(lineno), _vector(vector), _func(func), _low(low), _high(high) {
    }

  public:
    inline cdk::expression_node *vector() {
      return _vector;
    }
    inline std::string func() {
      return _func;
    }
    inline cdk::expression_node *low() {
      return _low;
    }
    inline cdk::expression_node *high() {
      return _high;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_apply_node(this, level);
    }

  };

} // fir

#endif
