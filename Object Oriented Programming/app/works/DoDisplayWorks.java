package m19.app.works;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
//import pt.tecnico.po.ui.DialogException;


/**
 * 4.3.2. Display all works.
 */
public class DoDisplayWorks extends Command<LibraryManager> {

  /**
   * @param receiver
   */
  public DoDisplayWorks(LibraryManager receiver) {
    super(Label.SHOW_WORKS, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    for (int i = 0; i < _receiver.getAllWorks().size(); i++) {
      _display.addLine(_receiver.getAllWorks().get(i).getDiscription());
      _display.display();
      _display.clear();
    }
  }
}
