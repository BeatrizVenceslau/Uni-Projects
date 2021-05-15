package m19.app.works;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

/**
 * 4.3.3. Raises price of works found according to miscellaneous criteria.
 */
public class DoRaisePrice extends Command<LibraryManager> {

  Input<String> _searchTerm;

  /**
   * @param receiver
   */
  public DoRaisePrice(LibraryManager receiver) {
    super("Aumentar Preco", receiver);
    _searchTerm = _form.addStringInput(Message.requestSearchTerm());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    String searchTerm;
    String title;
    int price;
    _form.parse();
    searchTerm = _searchTerm.value();
    
    for (int i = 0; i < _receiver.searchWorks(searchTerm).size(); i++) {
		  _receiver.searchWorks(searchTerm).get(i).setPrice();
		  title = _receiver.searchWorks(searchTerm).get(i).getTitle();
    	_display.addLine(title);
      //_display.addLine(_receiver.searchWorks(searchTerm).size());
    	//_display.addLine(price.toString());
    	_display.display();
    	_display.clear();
    }
  }
  
}
