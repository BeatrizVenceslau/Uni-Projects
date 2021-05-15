package m19.app.works;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

/**
 * 4.3.3. Perform search according to miscellaneous criteria.
 */
public class DoPerformSearch extends Command<LibraryManager> {

  Input<String> _searchTerm;

  /**
   * @param m
   */
  public DoPerformSearch(LibraryManager m) {
    super(Label.PERFORM_SEARCH, m);
    _searchTerm = _form.addStringInput(Message.requestSearchTerm());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    String searchTerm;
    _form.parse();
    searchTerm = _searchTerm.value();
    
    for (int i = 0; i < _receiver.searchWorks(searchTerm).size(); i++) {
      _display.addLine(_receiver.searchWorks(searchTerm).get(i).getDiscription());
      _display.display();
      _display.clear();
    }
  }
  
}
