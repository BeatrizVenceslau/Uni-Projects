#ifndef __FIR_AST_FUNCTION_CALL_NODE_H__
#define __FIR_AST_FUNCTION_CALL_NODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>

namespace fir {

  /**
   * Class for describing function call nodes.
   */
  class function_call_node: public cdk::expression_node {
    std::string _name;
    cdk::sequence_node *_arguments;

  public:
    inline function_call_node(int lineno, std::string name, cdk::sequence_node *arguments):
        expression_node(lineno), _name(name), _arguments(arguments){
    }

  public:
    inline std::string name() {
      return _name;
    }
    inline cdk::sequence_node *arguments() {
      return _arguments;
    }
    inline cdk::expression_node *argument(size_t arg) {
      return dynamic_cast<cdk::expression_node*>(_arguments->node(arg));
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_call_node(this, level);
    }
  };

} // fir

#endif
