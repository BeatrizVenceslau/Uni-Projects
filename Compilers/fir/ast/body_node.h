#ifndef __FIR_AST_BODY_NODE_H__
#define __FIR_AST_BODY_NODE_H__



namespace fir {

  /**
   * Class for describing body nodes.
   */
  class body_node: public cdk::basic_node {
    fir::prologue_node *_prologue;
    fir::block_node *_main, *_epilogue;

  public:
    inline body_node(int lineno, fir::prologue_node *prologue, fir::block_node *main, fir::block_node *epilogue) :
        basic_node(lineno), _prologue(prologue), _main(main), _epilogue(epilogue){
    }

  public:
    inline fir::prologue_node *prologue() {
      return _prologue;
    }
    inline fir::block_node *main() {
      return _main;
    }
    inline fir::block_node *epilogue() {
      return _epilogue;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_body_node(this, level);
    }

  };

} // fir

#endif
