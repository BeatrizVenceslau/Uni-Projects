#ifndef __FIR_AST_FIR_VARIABLE_DECL_NODE_H__
#define __FIR_AST_FIR_VARIABLE_DECL_NODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include <cdk/types/basic_type.h>

namespace fir {

  /**
   * Class for describing variable nodes.
   */
  class variable_decl_node: public cdk::typed_node {
    std::string _qualifier;
    std::string _name;
    cdk::expression_node *_expression;

  public:
    inline variable_decl_node(int lineno, std::shared_ptr<cdk::basic_type> var_type, std::string qualifier, std::string name, cdk::expression_node *expression) :
        typed_node(lineno), _qualifier(qualifier), _name(name), _expression(expression)
    {
          type(var_type);
    }

  public:
    bool constant() {
      return false;  //initializes the variable as global
                     //can change in postfix visitor if declared in function body
    }
    inline std::string qualifier() {
      return _qualifier;
    }
    inline std::string name() {
      return _name;
    }
    inline cdk::expression_node *expression() {
      return _expression;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_variable_decl_node(this, level);
    }

  };

} // fir

#endif
