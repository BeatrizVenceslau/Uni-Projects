#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include "targets/postfix_target.h"
#include "targets/frame_size_calculator.h"
#include "targets/symbol.h"
#include "ast/all.h"  // all.h is automatically generated


#include "fir_parser.tab.h"

//---------------------------------------------------------------------------

void fir::postfix_writer::do_nil_node(cdk::nil_node * const node, int lvl) {
  // EMPTY
}
void fir::postfix_writer::do_data_node(cdk::data_node * const node, int lvl) {
  // EMPTY
}
void fir::postfix_writer::do_not_node(cdk::not_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl + 2);
  _pf.INT(0);
  _pf.EQ();
}
void fir::postfix_writer::do_and_node(cdk::and_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl = ++_lbl;
  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JZ(mklbl(lbl));

  node->right()->accept(this, lvl + 2);
  _pf.AND();

  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
}
void fir::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl = ++_lbl;
  node->left()->accept(this, lvl + 2);
  _pf.DUP32();
  _pf.JNZ(mklbl(lbl));

  node->right()->accept(this, lvl + 2);
  _pf.OR();

  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl));
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++) {
    node->node(i)->accept(this, lvl);
  }
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
  if (_inBody) {
    _pf.DOUBLE(node->value()); // load number to the stack
  } else {
    _pf.SDOUBLE(node->value());    // double is on the DATA segment
  }
}

void fir::postfix_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
  if (_inBody) {
    _pf.INT(node->value()); // load number to the stack
  } else {
    _pf.SINT(node->value());    // double is on the DATA segment
  }
}

void fir::postfix_writer::do_string_node(cdk::string_node * const node, int lvl) {
  int lbl1;

  /* generate the string */
  _pf.RODATA(); // strings are DATA readonly
  _pf.ALIGN(); // make sure we are aligned
  _pf.LABEL(mklbl(lbl1 = ++_lbl)); // give the string a name
  _pf.SSTRING(node->value()); // output string characters

  /* leave the address on the stack */
  if (_inFunction) {
    // local variable initializer
    _pf.TEXT();
    _pf.ADDR(mklbl(lbl1));
  } else {
    // global variable initializer
    _pf.DATA();
    _pf.SADDR(mklbl(lbl1));
  }
}

void fir::postfix_writer::do_null_node(fir::null_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if (_inBody) {
    _pf.INT(0);
  } else {
    _pf.SINT(0);
  }
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  _pf.NEG(); // 2-complement
}

void fir::postfix_writer::do_identity_node(fir::identity_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.ADD();
}
void fir::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.SUB();
}
void fir::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MUL();
}
void fir::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.DIV();
}
void fir::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}
void fir::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.LT();
}
void fir::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.LE();
}
void fir::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.GE();
}
void fir::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.GT();
}
void fir::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.NE();
}
void fir::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.EQ();
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_variable_node(cdk::variable_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // simplified generation: all variables are global
  _pf.ADDR(node->name());
}

void fir::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);
  _pf.LDINT(); // depends on type size
}

void fir::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->rvalue()->accept(this, lvl); // determine the new value

  if (node->type()->name() == cdk::TYPE_DOUBLE) {
    if (node->rvalue()->type()->name() == cdk::TYPE_INT) 
    _pf.I2D();
    _pf.DUP64();
  } else {
    _pf.DUP32();
  }

  if (new_symbol() == nullptr) {
    node->lvalue()->accept(this, lvl); // where to store the value
  } else {
    _pf.DATA(); // variables are all global and live in DATA
    _pf.ALIGN(); // make sure we are aligned
    _pf.LABEL(new_symbol()->name()); // name variable location
    reset_new_symbol();
    _pf.SINT(0); // initialize it to 0 (zero)
    _pf.TEXT(); // return to the TEXT segment
    node->lvalue()->accept(this, lvl);
  }

  if (node->type()->name() == cdk::TYPE_DOUBLE) {
    _pf.STDOUBLE();
  } else {
    _pf.STINT();
  }
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_evaluation_node(fir::evaluation_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  if (node->argument()->is_typed(cdk::TYPE_INT)) {
    _pf.TRASH(4); // delete the evaluated value
  } else if (node->argument()->is_typed(cdk::TYPE_STRING)) {
    _pf.TRASH(4); // delete the evaluated value's address
  } else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  }
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_read_node(fir::read_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS; 
  if (node->type()->name() == cdk::TYPE_INT){
    _functions_to_declare.insert("readi");
    _pf.CALL("readi");
    _pf.LDFVAL32();
  } else if (node->type()->name() == cdk::TYPE_DOUBLE){
    _functions_to_declare.insert("readd");
    _pf.CALL("readd");
    _pf.LDFVAL64();
  } else {
    std::cerr << "ERROR: Only integer or double allowed!" << std::endl;
    return;
  }
}

void fir::postfix_writer::do_while_node(fir::while_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  _whilecond.push(++_lbl); // condition
  _whileend.push(++_lbl); // end
  
  _pf.ALIGN();
  os() << "        ;; WHILE condition" << std::endl;
  _pf.LABEL(mklbl(_whilecond.top()));

  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(_whileend.top()));
  
  os() << "        ;; WHILE block" << std::endl;
  node->block()->accept(this, lvl + 2);
  _pf.JMP(mklbl(_whilecond.top()));

  _pf.ALIGN();
  os() << "        ;; WHILE end" << std::endl;
  _pf.LABEL(mklbl(_whileend.top()));

  _whilecond.pop();
  _whileend.pop();
}

void fir::postfix_writer::do_while_finally_node(fir::while_finally_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  _whilecond.push(++_lbl); // condition
  _whileend.push(++_lbl); // end

  _pf.ALIGN();
  os() << "        ;; WHILE condition" << std::endl;
  _pf.LABEL(mklbl(_whilecond.top()));

  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(_whileend.top()));
  
  os() << "        ;; WHILE block" << std::endl;
  node->block()->accept(this, lvl + 2);
  _pf.JMP(mklbl(_whilecond.top()));

  _pf.ALIGN();
  os() << "        ;; WHILE end" << std::endl;
  _pf.LABEL(mklbl(_whileend.top()));

  os() << "        ;; WHILE FINALLY block" << std::endl;
  node->finallyBlock()->accept(this, lvl + 2);

  _whilecond.pop();
  _whileend.pop();
}

void fir::postfix_writer::do_leave_node(fir::leave_node * const node, int lvl) {
  long unsigned int ncicle = node->ncicle();
  if (_whilecond.size() >= ncicle) {
    for(int i=1; i < node->ncicle(); i++){
      _whilecond.pop();
      _whileend.pop();
    }
    _pf.JMP(mklbl(_whileend.top())); // jump to end of while cycle
  } else
    error(node->lineno(), "'leave' outside 'while'");
}

void fir::postfix_writer::do_restart_node(fir::restart_node * const node, int lvl) {
  long unsigned int ncicle = node->ncicle();
  if (_whilecond.size() >= ncicle) {
    for(int i=1; i < node->ncicle(); i++){
      _whilecond.pop();
      _whileend.pop();
    }
    _pf.JMP(mklbl(_whilecond.top())); // jump to next while cycle
  } else
    error(node->lineno(), "'restart' outside 'while'");
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_if_node(fir::if_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1));
}

//---------------------------------------------------------------------------

void fir::postfix_writer::do_if_else_node(fir::if_else_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1, lbl2;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->thenblock()->accept(this, lvl + 2);
  _pf.JMP(mklbl(lbl2 = ++_lbl));
  _pf.LABEL(mklbl(lbl1));
  node->elseblock()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1 = lbl2));
}

void fir::postfix_writer::do_write_node(fir::write_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  for (size_t arg = 0; arg < node->argument()->size(); arg++) {
    auto argument = dynamic_cast<cdk::expression_node*>(node->argument()->node(arg));

    std::shared_ptr<cdk::basic_type> arg_type = argument->type();
    argument->accept(this, lvl); // expression to write
    if (arg_type->name() == cdk::TYPE_INT) {
      _functions_to_declare.insert("printi");
      _pf.CALL("printi");
      _pf.TRASH(4); // trash int
    } else if (arg_type->name() == cdk::TYPE_DOUBLE) {
      _functions_to_declare.insert("printd");
      _pf.CALL("printd");
      _pf.TRASH(8); // trash double
    } else if (arg_type->name() == cdk::TYPE_STRING) {
      _functions_to_declare.insert("prints");
      _pf.CALL("prints");
      _pf.TRASH(4); // trash char
    } else {
      std::cerr << "cannot write expression of unknown type" << std::endl;
      return;
    }
  }
  if (node->newline()) {
    _functions_to_declare.insert("println");
    _pf.CALL("println");
  }
}

void fir::postfix_writer::do_variable_decl_node(fir::variable_decl_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  auto id = node->name();

  std::cout << "INITIAL OFFSET: " << _offset << std::endl;

  int offset = 0;
  int typesize = node->type()->size();
  std::cout << "ARG: " << id << ", " << typesize << std::endl;
  if (_inBody) {
    std::cout << "IN BODY" << std::endl;
    _offset -= typesize;
    offset = _offset;
  } else if (_inArgs) {
    std::cout << "IN ARGS" << std::endl;
    offset = _offset;
    _offset += typesize;
  } else {
    std::cout << "GLOBAL!" << std::endl;
    offset = 0; // global variable
  }
  std::cout << "OFFSET: " << id << ", " << offset << std::endl;

  auto symbol = new_symbol();
  if (symbol) {
    symbol->set_offset(offset);
    reset_new_symbol();
  }

  if (_inBody) { //in function body
    if (node->expression()) { //initialized
      node->expression()->accept(this, lvl);
      if (node->is_typed(cdk::TYPE_INT) || node->is_typed(cdk::TYPE_STRING) || node->is_typed(cdk::TYPE_POINTER)) {
        _pf.LOCAL(offset);
        _pf.STINT();
      } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
        if (node->expression()->is_typed(cdk::TYPE_INT))
          _pf.I2D();
        _pf.LOCAL(offset);
        _pf.STDOUBLE();
      } else {
        std::cerr << "cannot initialize" << std::endl;
      }
    }
    //if in side a function body and not initialized - do nothing
  } else { //outside function body
    if (!_function) { 
      if (node->expression() == nullptr) { //not initialized
        _pf.BSS();
        _pf.ALIGN();
        _pf.GLOBAL(id, _pf.OBJ());
        _pf.LABEL(id);
        _pf.SALLOC(typesize);

      } else { //initialized
        if (node->is_typed(cdk::TYPE_INT) || node->is_typed(cdk::TYPE_DOUBLE) || node->is_typed(cdk::TYPE_POINTER)) {
          if (node->constant()) {
            _pf.RODATA();
          } else {
            _pf.DATA();
          }
          _pf.ALIGN();
          _pf.LABEL(id);

          if (node->is_typed(cdk::TYPE_INT) || node->is_typed(cdk::TYPE_POINTER)) {
            node->expression()->accept(this, lvl);
          } else if (node->is_typed(cdk::TYPE_DOUBLE)) {
            if (node->expression()->type()->name() == cdk::TYPE_DOUBLE) {
              node->expression()->accept(this, lvl);
            } else if (node->expression()->is_typed(cdk::TYPE_INT)) {
              //create double node from int node
              cdk::integer_node *i = dynamic_cast<cdk::integer_node*>(node->expression());
              cdk::double_node d(i->lineno(), i->value());
              d.accept(this, lvl);
            } else {
              std::cerr << node->lineno() << ": '" << id << "' has bad initializer for real value\n";
            }
          }

        } else if (node->is_typed(cdk::TYPE_STRING)) {
          if (node->constant()) {
            int strlbl;
            _pf.RODATA();
            _pf.ALIGN();
            _pf.LABEL(mklbl(strlbl = ++_lbl));
            _pf.SSTRING(dynamic_cast<cdk::string_node*>(node->expression())->value());
            _pf.ALIGN();
            _pf.LABEL(id);
            _pf.SADDR(mklbl(strlbl));
          } else {
            _pf.DATA();
            _pf.ALIGN();
            _pf.LABEL(id);
            node->expression()->accept(this, lvl);
          }

        } else {
          std::cerr << node->lineno() << ": '" << id << "' has unexpected initializer\n";
        }
      }
    }
  }
}

void fir::postfix_writer::do_return_node(fir::return_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if (_function->type()->name() != cdk::TYPE_VOID) {
    
    if (_function->type()->name() == cdk::TYPE_INT 
        || _function->type()->name() == cdk::TYPE_STRING
        || _function->type()->name() == cdk::TYPE_POINTER) 
    {
      _pf.STFVAL32();
    } else if (_function->type()->name() == cdk::TYPE_DOUBLE) {
      _pf.STFVAL64();
    } else {
      std::cerr << node->lineno() << "unknown return type" << std::endl;
    }
  }

  _returnSeen = true;
  _pf.JMP(_currentBodyLabel);
}

void fir::postfix_writer::do_block_node(fir::block_node * const node, int lvl) {
  _symtab.push();
  if (node->declarations()) 
    node->declarations()->accept(this, lvl + 2);
  if (node->instructions()) 
    node->instructions()->accept(this, lvl + 2);
  _symtab.pop();
}

void fir::postfix_writer::do_prologue_node(fir::prologue_node * const node, int lvl) {
  _symtab.push();
  if (node->block()) {
    _prologue=1;
    node->block()->accept(this, lvl + 2);
  }
  _symtab.pop();
}

void fir::postfix_writer::do_body_node(fir::body_node * const node, int lvl) {
  _symtab.push();
  if (node->prologue()) 
    node->prologue()->accept(this, lvl + 2);
  
  if (node->main())
    node->main()->accept(this, lvl + 2);

  if (node->epilogue()) 
    node->epilogue()->accept(this, lvl + 2);
  _symtab.pop();
}

void fir::postfix_writer::do_function_definition_node(fir::function_definition_node * const node, int lvl) {
  if (_inBody || _inArgs) {
    error(node->lineno(), "cannot define nor declare function in body or in arguments");
    return;
  }

  ASSERT_SAFE_EXPRESSIONS;
  std::string id;

  if (node->body()) { //declared and defined
    _function = new_symbol();
    _functions_to_declare.erase(_function->name());  //declared
    reset_new_symbol();

    _currentBodyLabel = mklbl(++_lbl);
    _currentPrologueLabel = mklbl(++_lbl);

    _offset = 8;
    _symtab.push();

    // arguments
    if (node->variables() != nullptr && node->variables()->size() > 0) {
      _inArgs = true;
      for (size_t arg = 0; arg < node->variables()->size(); arg++) {
        cdk::basic_node *argument = node->variables()->node(arg);
        argument->accept(this, 0);
      }
      _inArgs = false;
    }

    // function definition
    _pf.TEXT();
    _pf.ALIGN();

    if (node->name() == "fir")
      id = "_main";
    else if (node->name() == "_main")
      id = "._main";
    else
      id = node->name();

    if (node->qualifier() == "public") //only declare global if public
      _pf.GLOBAL(id, _pf.FUNC());
    _pf.LABEL(id);

    // calculate the size of the arguments
    frame_size_calculator lsc(_compiler, _symtab, _function);
    node->accept(&lsc, lvl);
    _pf.ENTER(lsc.localsize());

    _offset = 0;

    // body
    _inBody = true;
    os() << "        ;; before body " << std::endl;
    node->body()->accept(this, lvl + 4);
    os() << "        ;; after body " << std::endl;
    _inBody = false;
    _returnSeen = false;

    _pf.LABEL(_currentBodyLabel);
    _pf.LEAVE();
    _pf.RET();

    _symtab.pop(); // arguments

    if (node->name() == "fir") {
      // declare external functions
      for (std::string s : _functions_to_declare)
        _pf.EXTERN(s);
    }

  }
  else { //only declared
    if (!new_symbol()) 
      return;

    auto function = new_symbol();
    _functions_to_declare.insert(function->name());
    reset_new_symbol();
  }
}

void fir::postfix_writer::do_function_call_node(fir::function_call_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  auto symbol = _symtab.find(node->name());

  //arguments
  size_t args = 0;
  if (node->arguments()->size() > 0) {
    for (int arg = node->arguments()->size() - 1; arg >= 0; arg--) {
      cdk::expression_node *argument = dynamic_cast<cdk::expression_node*>(node->arguments()->node(arg));
      argument->accept(this, lvl + 2);
      if (symbol->arg_is_typed(arg, cdk::TYPE_DOUBLE) && argument->is_typed(cdk::TYPE_INT)) {
        _pf.I2D();
      }
      args += symbol->arg_size(arg);
    }
  }

  _pf.CALL(node->name());
  _pf.TRASH(args);

  //waits for RET instruction
  //Pushes retval to stack
  if (symbol->is_typed(cdk::TYPE_INT) || symbol->is_typed(cdk::TYPE_POINTER) || symbol->is_typed(cdk::TYPE_STRING)) {
    _pf.LDFVAL32();
  } else if (symbol->is_typed(cdk::TYPE_DOUBLE))
    _pf.LDFVAL64();
}

void fir::postfix_writer::do_sizeof_node(fir::sizeof_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.INT(node->argument()->type()->size());
}

void fir::postfix_writer::do_address_of_node(fir::address_of_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->value()->accept(this, lvl + 2);
}

void fir::postfix_writer::do_alloc_node(fir::alloc_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
  _pf.INT(2);
  _pf.SHTL();
  _pf.ALLOC(); // allocate
  _pf.SP(); // put base pointer in stack
}

void fir::postfix_writer::do_index_node(fir::index_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->pointer()->accept(this, lvl);
  node->index()->accept(this, lvl);

  if(node->pointer()->is_typed(cdk::TYPE_DOUBLE)) {
    _pf.INT(3);
    _pf.SHTL();
    _pf.ADD();
  } else { //type int or string
    _pf.INT(2);
    _pf.SHTL();
    _pf.ADD();
  }
}
