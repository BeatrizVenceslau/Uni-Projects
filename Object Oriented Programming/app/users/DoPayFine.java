package m19.app.users;

import m19.app.exception.NoSuchUserException;
import m19.app.exception.UserIsActiveException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * 4.2.5. Settle a fine.
 */
public class DoPayFine extends Command<LibraryManager> {

  Input<Integer> _userId;

  /**
   * @param receiver
   */
  public DoPayFine(LibraryManager receiver) {
    super(Label.PAY_FINE, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    Boolean state;
    int userId;
    _form.parse();
    userId = _userId.value();

    try {
      state = _receiver.isUserActive(userId);
      _receiver.paidTheFine(userId, !(state));
    } catch (NoSuchUserException usr) {
      throw new NoSuchUserException(userId);
    } catch (UserIsActiveException urfe) {
      throw new UserIsActiveException(userId);
    }
  }
}
