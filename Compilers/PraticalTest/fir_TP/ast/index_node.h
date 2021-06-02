#ifndef __FIR_AST_INDEX_NODE_H__
#define __FIR_AST_INDEX_NODE_H__

#include <cdk/ast/lvalue_node.h>
#include <cdk/ast/expression_node.h>
#include <string>

namespace fir {

  /**
   * Class for accessing the indexed position in memory of a pointer
   */
  class index_node: public cdk::lvalue_node {
    cdk::expression_node *_pointer;
    cdk::expression_node *_index;

  public:
    inline index_node(int lineno, cdk::expression_node *pointer, cdk::expression_node *index):
        cdk::lvalue_node(lineno), _pointer(pointer), _index(index){
    }

  public:
    inline cdk::expression_node *pointer() {
      return _pointer;
    }

    inline cdk::expression_node *index() {
      return _index;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_index_node(this, level);
    }

  };

} // fir

#endif
