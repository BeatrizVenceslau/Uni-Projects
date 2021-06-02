#ifndef __FIR_AST_WRITE_NODE_H__
#define __FIR_AST_WRITE_NODE_H__

namespace fir {

  /**
   * Class for describing write and writeln nodes.
   if writeln, newline = true
   */
  class write_node: public cdk::basic_node {
    cdk::sequence_node *_argument;
    bool _newline;

    public:
      inline write_node(int lineno, cdk::sequence_node *argument, bool newline) :
          cdk::basic_node(lineno), _argument(argument), _newline(newline) {
      }

    public:
      inline cdk::sequence_node *argument() {
        return _argument;
      }
      inline cdk::expression_node *arg(size_t arg) {
        return dynamic_cast<cdk::expression_node*>(_argument->node(arg));
      }
      inline bool newline() {
        return _newline;
      }

      void accept(basic_ast_visitor *sp, int level) {
        sp->do_write_node(this, level);
      }

  };

} // fir

#endif
