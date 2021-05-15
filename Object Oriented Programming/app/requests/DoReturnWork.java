package m19.app.requests;

import m19.app.exception.NoSuchUserException;
import m19.app.exception.NoSuchWorkException;
import m19.app.exception.WorkNotBorrowedByUserException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * 4.4.2. Return a work.
 */
public class DoReturnWork extends Command<LibraryManager> {

  Input<Integer> _userId;
  Input<Integer> _workId;
  Input<String> _answer;

  /**
   * @param receiver
   */
  public DoReturnWork(LibraryManager receiver) {
    super(Label.RETURN_WORK, receiver);

  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    int userId;
    int workId;
    String answer;
    Boolean state;

    _form.clear();
    _userId = _form.addIntegerInput(Message.requestUserId());
    _workId = _form.addIntegerInput(Message.requestWorkId());
    _form.parse();
    userId = _userId.value();
    workId = _workId.value();
    try {
      _form.clear();
      int fine = _receiver.makeReturn(userId, workId);
      if(!_receiver.getUserState(userId) && _receiver.getFine(userId) > 0){
        _display.popup(Message.showFine(userId, fine));
        _answer = _form.addStringInput(Message.requestFinePaymentChoice());
        _form.parse();
        answer = _answer.value();
        
        if("s".equals(answer)){
          state = _receiver.getUserState(userId);
          _receiver.paidTheFine(userId, !(state));
        }
      }
    } catch (NoSuchWorkException wk) {
      throw new NoSuchWorkException(workId);
    } catch (NoSuchUserException us) {
      throw new NoSuchUserException(userId);
    } catch(WorkNotBorrowedByUserException rfe) {
      throw new WorkNotBorrowedByUserException(workId, userId);
    }
  }
}