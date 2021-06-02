#ifndef __FIR_TARGETS_SYMBOL_H__
#define __FIR_TARGETS_SYMBOL_H__

#include <string>
#include <memory>
#include <cdk/types/basic_type.h>

namespace fir {

  class symbol {
    std::shared_ptr<cdk::basic_type> _type;
    std::string _qualifier;
    std::string _name;
    std::vector<std::shared_ptr<cdk::basic_type>> _args_types;

    bool _isfunction; //is it a function (true) or a variable (false)
    bool _declared;
    bool _defined; //is the function defined or is the variable inicialized?
    bool _funcCall;

    int _offset = 0; //indicates the offset(type_size) of the variables

    public:
      symbol(bool function, std::shared_ptr<cdk::basic_type> type, std::string qualifier, std::string name, bool declared, bool defined) :
        _type(type), _qualifier(qualifier), _name(name), _isfunction(function), _declared(declared), _defined(defined) {
      }

      virtual ~symbol() {
        // EMPTY
      }

      // values
      std::shared_ptr<cdk::basic_type> type() const {
        return _type;
      }
      void set_type(std::shared_ptr<cdk::basic_type> t) {
        _type = t;
      }
      bool is_typed(cdk::typename_type name) const {
        return _type->name() == name;
      }

      const std::string &qualifier() const {
        return _qualifier;
      }

      const std::string &name() const {
        return _name;
      }

      void set_args_types(const std::vector<std::shared_ptr<cdk::basic_type>> &types) {
        _args_types = types;
      }
      bool arg_is_typed(size_t arg, cdk::typename_type name) const {
        return _args_types[arg]->name() == name;
      }
      std::shared_ptr<cdk::basic_type> arg_type(size_t arg) const {
        return _args_types[arg];
      }
      size_t arg_size(size_t arg) const {
        return _args_types[arg]->size();
      }
      size_t number_of_args() const {
        return _args_types.size();
      }

      // offset
      int offset() {
        return _offset;
      }
      void set_offset(int offset) {
        _offset = offset;
      }

      // booleans
      bool function() const {
        return _isfunction;
      }

      bool defined() const {
        return _defined;
      }

      bool declared() const {
        return _declared;
      }

      bool funcCall() const {
        return _funcCall;
      }
      bool funcCall(bool b) {
        return _funcCall = b;
      }
    };

    inline auto make_symbol(bool function, std::shared_ptr<cdk::basic_type> type, std::string qualifier, std::string name, bool declared, bool defined) {
      return std::make_shared<symbol>(function, type, qualifier, name, declared, defined);
    }

} // fir

#endif
