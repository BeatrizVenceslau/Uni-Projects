#ifndef __FIR_AST_FUNCTION_DEF_NODE_H__
#define __FIR_AST_FUNCTION_DEF_NODE_H__

#include <string>
#include <cdk/ast/expression_node.h>
#include <cdk/types/basic_type.h>

namespace fir {

  /**
   * Class for describing function nodes.
   */
  class function_definition_node: public cdk::typed_node {
    std::string _qualifier;
    std::string _name;
    cdk::sequence_node *_variables;
    cdk::expression_node *_return_value;
    fir::body_node *_body;

  public:
    inline function_definition_node(int lineno, 
                                std::shared_ptr<cdk::basic_type> func_type, 
                                std::string qualifier,
                                std::string name,
                                cdk::sequence_node *variables,
                                cdk::expression_node *return_value,
                                fir::body_node *body) :
        typed_node(lineno), _qualifier(qualifier), _name(name),
        _variables(variables), _return_value(return_value), _body(body){
          type(func_type);
    }

  public:
    inline std::string name() {
      return _name;
    }
    inline std::string qualifier() {
      return _qualifier;
    }
    inline cdk::sequence_node *variables() {
      return _variables;
    }
    inline cdk::typed_node* var(size_t v) {
      return dynamic_cast<cdk::typed_node*>(_variables->node(v));
    }
    inline cdk::expression_node *return_value() {
      return _return_value;
    }
    inline fir::body_node *body() {
      return _body;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_definition_node(this, level);
    }
  };

} // fir

#endif
