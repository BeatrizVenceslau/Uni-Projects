package m19.app.users;

import m19.app.exception.NoSuchUserException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * 4.2.3. Show notifications of a specific user.
 */
public class DoShowUserNotifications extends Command<LibraryManager> {

  Input<Integer> _userId;

  /** 
   * @param receiver
   */
  public DoShowUserNotifications(LibraryManager receiver) {
    super(Label.SHOW_USER_NOTIFICATIONS, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    int userId;
    _form.parse();
    userId = _userId.value();
    try {
      for (int i = 0; i < _receiver.getAllNotifications(userId).size(); i++) {
        _display.addLine(_receiver.getAllNotifications(userId).get(i).getNotification());
        _display.display();
        _display.clear();
      }
      _receiver.clearNotifications(userId);
    } catch (NoSuchUserException usr) {
      throw new NoSuchUserException(userId);
    }
  }
}
