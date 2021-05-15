package m19.app.users;

import m19.app.exception.UserRegistrationFailedException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * 4.2.1. Register new user.
 */
public class DoRegisterUser extends Command<LibraryManager> {

  private Input<String> _userName;
  private Input<String> _userEmail;

  /**
   * @param receiver
   */
  public DoRegisterUser(LibraryManager receiver) {
    super(Label.REGISTER_USER, receiver);
    _userName = _form.addStringInput(Message.requestUserName());
    _userEmail = _form.addStringInput(Message.requestUserEMail());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    String userName;
    String userEmail;
    int id = 0;
    _form.parse();
    userName = _userName.value();
    userEmail = _userEmail.value();
    try {
      id = _receiver.registerUser(userName, userEmail);
      _display.popup(Message.userRegistrationSuccessful(id));
    } catch (UserRegistrationFailedException urfe) {
      throw new UserRegistrationFailedException(userName, userEmail);
    }
  }
}
