package m19.app.requests;

import m19.app.exception.NoSuchUserException;
import m19.app.exception.NoSuchWorkException;
import m19.app.exception.RuleFailedException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;


/**
 * 4.4.1. Request work.
 */
public class DoRequestWork extends Command<LibraryManager> {

  Input<Integer> _userId; 
  Input<Integer> _workId;
  Input<String> _answer;

  /**
   * @param receiver
   */
  public DoRequestWork(LibraryManager receiver) {
    super(Label.REQUEST_WORK, receiver);
 
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    int userId;
    int workId;
    int ruleId = -2;
    int nDays;

    _form.clear();
    _userId = _form.addIntegerInput(Message.requestUserId());
    _workId = _form.addIntegerInput(Message.requestWorkId());
    _form.parse();
    userId = _userId.value();
    workId = _workId.value();

    try {
      _form.clear();
      ruleId = _receiver.checkRules(userId, workId);
      nDays = _receiver.makeRequest(userId, workId);
      if(ruleId == 3){
        _answer = _form.addStringInput(Message.requestReturnNotificationPreference());
        _form.parse();
        String s = _answer.value();
        if("s".equals(s) ) {
          _receiver.askNotification(userId, workId);
        }
      }
      else {
        _display.popup(Message.workReturnDay(workId, nDays));
      }
    } catch (NoSuchWorkException wk) {
      throw new NoSuchWorkException(workId);
    } catch (NoSuchUserException us) {
      throw new NoSuchUserException(userId);
    } catch(RuleFailedException rfe) {
      throw new RuleFailedException(userId, workId, ruleId);
    }
  }
}
