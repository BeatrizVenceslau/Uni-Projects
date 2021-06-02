#ifndef __FIR_TARGET_FRAME_SIZE_CALCULATOR_H__
#define __FIR_TARGET_FRAME_SIZE_CALCULATOR_H__

#include "targets/basic_ast_visitor.h"

#include <sstream>
#include <stack>

namespace fir {

  class frame_size_calculator: public basic_ast_visitor {
    cdk::symbol_table<fir::symbol> &_symtab;
    std::shared_ptr<fir::symbol> _function;

    size_t _localsize;

  public:
    frame_size_calculator(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<fir::symbol> &symtab, std::shared_ptr<fir::symbol> function) :
        basic_ast_visitor(compiler), _symtab(symtab), _function(function), _localsize(0) {
    }

  virtual ~frame_size_calculator() {
      //EMPTY
    }

  public:
    size_t localsize() const {
      return _localsize;
    }

  public:
    #define __IN_VISITOR_HEADER__
    #include "ast/visitor_decls.h"
    #undef __IN_VISITOR_HEADER__
  };

} // fir

#endif
