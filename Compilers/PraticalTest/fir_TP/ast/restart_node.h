#ifndef __FIR_AST_RESTART_NODE_H__
#define __FIR_AST_RESTART_NODE_H__

#include <cdk/ast/expression_node.h>

namespace fir {

  /**
   * Class for describing restart nodes.
   */
  class restart_node: public cdk::basic_node {
    int _ncicle;

  public:
    inline restart_node(int lineno, int ncicle) :
        cdk::basic_node(lineno), _ncicle(ncicle) {
    }

  public:
    inline int ncicle() {
      return _ncicle;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_restart_node(this, level);
    }

  };

} // fir

#endif
