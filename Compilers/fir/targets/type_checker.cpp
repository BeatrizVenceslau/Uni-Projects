#include <string>
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated
#include <cdk/types/primitive_type.h>
#include "fir_parser.tab.h"

#define ASSERT_UNSPEC { if (node->type() != nullptr && !node->is_typed(cdk::TYPE_UNSPEC)) return; }

//---------------------------------------------------------------------------

void fir::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl);
}

//---------------------------------------------------------------------------

void fir::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}
void fir::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}
void fir::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);
  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else if (node->argument()->is_typed(cdk::TYPE_UNSPEC)) {
    node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    node->argument()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
  } else {
    throw std::string("wrong type in unary logical expression");
  }
}
void fir::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}

//---------------------------------------------------------------------------

void fir::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
}

void fir::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void fir::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
}

void fir::type_checker::do_null_node(fir::null_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(cdk::reference_type::create(4, nullptr));
}

//---------------------------------------------------------------------------

void fir::type_checker::processUnaryExpression(cdk::unary_operation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
  if (!node->argument()->is_typed(cdk::TYPE_INT)) 
    throw std::string("wrong type in argument of unary expression");

  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void fir::type_checker::do_neg_node(cdk::neg_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl);
  if (node->argument()->is_typed(cdk::TYPE_INT) || node->argument()->is_typed(cdk::TYPE_STRING)) {
    node->type(node->argument()->type());
  } else {
    throw std::string("integer or vector expressions expected");
  }
}

void fir::type_checker::do_identity_node(fir::identity_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl);
  if (node->argument()->is_typed(cdk::TYPE_INT) || node->argument()->is_typed(cdk::TYPE_STRING)) {
    node->type(node->argument()->type());
  } else {
    throw std::string("integer or vector expressions expected");
  }
}

//---------------------------------------------------------------------------

void fir::type_checker::processBinaryExpression(cdk::binary_operation_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);
  if (!node->left()->is_typed(cdk::TYPE_INT)) 
    throw std::string("wrong type in left argument of binary expression");

  node->right()->accept(this, lvl + 2);
  if (!node->right()->is_typed(cdk::TYPE_INT)) 
    throw std::string("wrong type in right argument of binary expression");

  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void fir::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void fir::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
  processBinaryExpression(node, lvl);
}

//---------------------------------------------------------------------------

void fir::type_checker::do_variable_node(cdk::variable_node *const node, int lvl) {
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  std::shared_ptr<fir::symbol> symbol = _symtab.find(id);

  if (symbol != nullptr) {
    node->type(symbol->type());
  } else {
    throw id;
  }
}

void fir::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_UNSPEC;
  try {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  }
}

void fir::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl + 4);
  node->rvalue()->accept(this, lvl + 4);

  if (node->lvalue()->is_typed(cdk::TYPE_INT)) {
    if (node->rvalue()->is_typed(cdk::TYPE_INT)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
    } else {
      throw std::string("wrong assignment to integer");
    }
  } else if (node->lvalue()->is_typed(cdk::TYPE_POINTER)) {

    if (node->rvalue()->is_typed(cdk::TYPE_POINTER)) {
      node->type(node->rvalue()->type());
    } else if (node->rvalue()->is_typed(cdk::TYPE_INT)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_POINTER));
    } else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_ERROR));
      node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_ERROR));
    } else {
      throw std::string("wrong assignment to pointer");
    }

  } else if (node->lvalue()->is_typed(cdk::TYPE_DOUBLE)) {

    if (node->rvalue()->is_typed(cdk::TYPE_DOUBLE) || node->rvalue()->is_typed(cdk::TYPE_INT)) {
      node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
      node->rvalue()->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
    } else {
      throw std::string("wrong assignment to real");
    }

  } else if (node->lvalue()->is_typed(cdk::TYPE_STRING)) {

    if (node->rvalue()->is_typed(cdk::TYPE_STRING)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
    } else if (node->rvalue()->is_typed(cdk::TYPE_UNSPEC)) {
      node->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
      node->rvalue()->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
    } else {
      throw std::string("wrong assignment to string");
    }

  } else {
    throw std::string("wrong types in assignment");
  }
}

//---------------------------------------------------------------------------

void fir::type_checker::do_evaluation_node(fir::evaluation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
}

//---------------------------------------------------------------------------

void fir::type_checker::do_read_node(fir::read_node *const node, int lvl) {
  node->type(cdk::primitive_type::create(0, cdk::TYPE_UNSPEC));
}

//---------------------------------------------------------------------------

void fir::type_checker::do_while_node(fir::while_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
  if (!node->condition()->is_typed(cdk::TYPE_INT)) 
    throw std::string("expected integer condition");
}

//---------------------------------------------------------------------------

void fir::type_checker::do_if_node(fir::if_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
  if (!node->condition()->is_typed(cdk::TYPE_INT)) 
    throw std::string("expected integer condition");
}

void fir::type_checker::do_if_else_node(fir::if_else_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
  if (!node->condition()->is_typed(cdk::TYPE_INT)) 
    throw std::string("expected integer condition");
}

void fir::type_checker::do_while_finally_node(fir::while_finally_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
  if (!node->condition()->is_typed(cdk::TYPE_INT)) 
    throw std::string("expected integer condition");
}

void fir::type_checker::do_block_node(fir::block_node *const node, int lvl) {
  // EMPTY
}

void fir::type_checker::do_prologue_node(fir::prologue_node *const node, int lvl) {
  // EMPTY
}

void fir::type_checker::do_body_node(fir::body_node *const node, int lvl) {
  // EMPTY
}

void fir::type_checker::do_variable_decl_node(fir::variable_decl_node *const node, int lvl) {
  ASSERT_UNSPEC;

  if (node->expression()) { //initialized
    node->expression()->accept(this, lvl + 2);

    if (node->is_typed(cdk::TYPE_INT)) {
      if (node->expression()->is_typed(cdk::TYPE_UNSPEC))
        node->expression()->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
      else if (!node->expression()->is_typed(cdk::TYPE_INT)) 
        throw std::string("integer type expected for initializer");

    } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
      if (node->expression()->is_typed(cdk::TYPE_UNSPEC))
        node->expression()->type(cdk::primitive_type::create(8, cdk::TYPE_DOUBLE));
      else if (!( node->expression()->is_typed(cdk::TYPE_INT) || node->expression()->is_typed(cdk::TYPE_DOUBLE) )) 
        throw std::string("integer or double type expected for initialization");
      
    } else if (node->is_typed(cdk::TYPE_STRING)) {
      if (node->expression()->is_typed(cdk::TYPE_UNSPEC))
        node->expression()->type(cdk::primitive_type::create(4, cdk::TYPE_STRING));
      else if (!node->expression()->is_typed(cdk::TYPE_STRING))
        throw std::string("string type expected for initialization");

    } else if (node->is_typed(cdk::TYPE_POINTER)) {
      if (node->expression()->is_typed(cdk::TYPE_UNSPEC))
        node->expression()->type(cdk::primitive_type::create(4, cdk::TYPE_POINTER));
      else if (node->expression()->is_typed(cdk::TYPE_POINTER)) {
        std::shared_ptr < cdk::reference_type > vartype = cdk::reference_type::cast(node->type());
        std::shared_ptr < cdk::reference_type > exprtype = cdk::reference_type::cast(node->expression()->type());
        if (vartype->name() == exprtype->name())
          throw std::string("confliting pointer type for variable and initialization");

      } else if (!node->expression()->is_typed(cdk::TYPE_POINTER))
        throw std::string("pointer type expected for initialization");

    } else {
      throw std::string("unknown type for initialization");
    }
  }

  const std::string &id = node->name();
  auto symbol = fir::make_symbol(false, node->type(), node->qualifier(), id, true, (bool)node->expression());
  throw std::string("variable '" + id + "' declared");
  if (_symtab.insert(id, symbol)) {
    _parent->set_new_symbol(symbol);
  } else {
    throw std::string("variable '" + id + "' already declared");
  }
}

void fir::type_checker::do_function_definition_node(fir::function_definition_node *const node, int lvl) {
  ASSERT_UNSPEC;
  std::string id;

  if (node->name() == "fir")
    id = "_main";
  else if (node->name() == "_main")
    id = "._main";
  else
    id = node->name();
  
  if (node->body()) { //declared and defined
    auto function = fir::make_symbol(true, node->type(), node->qualifier(), id, true, true);

    std::vector < std::shared_ptr < cdk::basic_type >> argtypes;
    for (size_t arg = 0; arg < node->variables()->size(); arg++)
      argtypes.push_back(node->var(arg)->type());
    function->set_args_types(argtypes);

    if (node->type()->name() != node->return_value()->type()->name())
      throw std::string("conflicting types for function'" + id + "' and the return value");

    std::shared_ptr<fir::symbol> previous = _symtab.find(id);
    if (previous) {
      if (previous->qualifier() != node->qualifier()) {
        throw std::string("conflicting definition for '" + id + "'");
      } else {
        _symtab.insert(id, function);
        _parent->set_new_symbol(function);
      }
    }
  }
  else { //only declared
    auto function = fir::make_symbol(true, node->type(), node->qualifier(), id, true, false);

    std::vector < std::shared_ptr < cdk::basic_type >> argtypes;
    for (size_t arg = 0; arg < node->variables()->size(); arg++)
      argtypes.push_back(node->var(arg)->type());
    function->set_args_types(argtypes);

    std::shared_ptr<fir::symbol> previous = _symtab.find(id);
    if (previous) {
      if (previous->qualifier() != node->qualifier()) {
        throw std::string("conflicting definition for '" + id + "'");
      } else {
        _symtab.insert(id, function);
        _parent->set_new_symbol(function);
      }
    }
  }
}

void fir::type_checker::do_function_call_node(fir::function_call_node *const node, int lvl) {
  ASSERT_UNSPEC;

  const std::string &id = node->name();
  auto symbol = _symtab.find(id);
  if (symbol == nullptr) 
    throw std::string("symbol '" + id + "' is undeclared.");
  if (!symbol->function()) 
    throw std::string("symbol '" + id + "' is not a function.");

  // set return var
  if (symbol->is_typed(cdk::TYPE_STRUCT)) {
    const std::string returnvar = "return_" + id;
    auto return_symbol = fir::make_symbol(false, symbol->type(), symbol->qualifier(), returnvar, true, true);
    _symtab.insert(returnvar, return_symbol);
  }

  // set type
  node->type(symbol->type());

  // set arguments
  if (node->arguments()->size() == symbol->number_of_args()) {
    node->arguments()->accept(this, lvl + 4);
    for (size_t arg = 0; arg < node->arguments()->size(); arg++) {
      if (!( node->argument(arg)->type()->name() == symbol->arg_type(arg)->name() || 
             (symbol->arg_is_typed(arg, cdk::TYPE_DOUBLE) && node->argument(arg)->is_typed(cdk::TYPE_INT)) )) 
        throw std::string("type mismatch for argument " + std::to_string(arg + 1) + " of '" + id + "'.");
    }
  } else {
    throw std::string("number of arguments in call (" + 
                      std::to_string(node->arguments()->size()) + 
                      ") must match declaration ("
                      + std::to_string(symbol->number_of_args()) + ").");
  }
}

void fir::type_checker::do_return_node(fir::return_node *const node, int lvl) {
  //Empty
}

void fir::type_checker::do_leave_node(fir::leave_node *const node, int lvl) {
  //parser makes sure it only receives integers
}

void fir::type_checker::do_restart_node(fir::restart_node *const node, int lvl) {
  //parser makes sure it only receives integers
}

void fir::type_checker::do_sizeof_node(fir::sizeof_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);
  node->type(cdk::primitive_type::create(4, cdk::TYPE_INT));
}

void fir::type_checker::do_write_node(fir::write_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
  //verificacao se e ponteiro
  for (size_t arg = 0; arg < node->argument()->size(); arg++) {
    if ( !( node->arg(arg)->is_typed(cdk::TYPE_INT) || node->arg(arg)->is_typed(cdk::TYPE_DOUBLE) || node->arg(arg)->is_typed(cdk::TYPE_STRING) ))
      throw std::string("only integer real and sring expressions expected in allocation expression");
  }
}

void fir::type_checker::do_address_of_node(fir::address_of_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->value()->accept(this, lvl + 2);
  node->type(cdk::reference_type::create(4, node->value()->type()));
}

void fir::type_checker::do_alloc_node(fir::alloc_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);
  if (!node->argument()->is_typed(cdk::TYPE_INT)) {
    throw std::string("integer expression expected in allocation expression");
  }
  node->type(cdk::reference_type::create(4, cdk::primitive_type::create(0, cdk::TYPE_UNSPEC)));
}

void fir::type_checker::do_index_node(fir::index_node *const node, int lvl) {
  ASSERT_UNSPEC;
  std::shared_ptr < cdk::reference_type > pointertype;

  node->pointer()->accept(this, lvl + 2);
  node->index()->accept(this, lvl + 2);
  
  pointertype = cdk::reference_type::cast(node->pointer()->type());
  if (!node->pointer()->is_typed(cdk::TYPE_POINTER)) 
    throw std::string("pointer expression expected in index left-value");

  if (!node->index()->is_typed(cdk::TYPE_INT))
    throw std::string("integer expression expected in pointer index expression");

  node->type(pointertype->referenced());
}
