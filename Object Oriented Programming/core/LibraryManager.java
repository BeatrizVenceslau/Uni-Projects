package m19.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import m19.app.exception.NoSuchUserException;
import m19.app.exception.NoSuchWorkException;
import m19.app.exception.RuleFailedException;
import m19.app.exception.UserIsActiveException;
import m19.app.exception.UserRegistrationFailedException;
import m19.app.exception.WorkNotBorrowedByUserException;
import m19.core.exception.BadEntrySpecificationException;
import m19.core.exception.ImportFileException;
import m19.core.exception.MissingFileAssociationException;

/**
 * The façade class.
 */
public class LibraryManager {

  private Library _library = new Library(); 
  private String _filename; 

  public LibraryManager() {
    _filename = null;
  }

  /**
   * @return filename already associate
   **/
  public String getFilename() {
    return _filename;
  }
  
  /**
   * Sorts all users in alphabetical order.
   * @return allUsers sorted list of all users
   **/
  public List<User> getAllUsers() {
    List<User> allUsers = new ArrayList<User> (_library.getAllUsers());
    Collections.sort(allUsers, new OrderName());
    return allUsers;
  }

  /**
   * Gets the User with the id received.
   * @param id the id of the specified user
   * @throws NoSuchUserException if the user does not exist
   * @return Description of User
   **/
  public String getUser(int id) throws NoSuchUserException { 
    if (id > getAllUsers().size() || id < 0 || (id == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(id); 
    else {
      return _library.getUser(id).getDescription();   
    }
  }

  /**
   * Sorts all works in id order.
   * @return allWorks sorted list of all works
   **/
  public List<Work> getAllWorks() {
    List<Work> allWorks = new ArrayList<Work> (_library.getAllWorks());
    Collections.sort(allWorks, new OrderId());
    return allWorks;
  }

  /**
   * Gets the Work with the id received.
   * @param id the id of the specified work
   * @throws NoSuchWorkException if the work does not exist
   * @return Description of Work
   **/
  public String getWork(int id) throws NoSuchWorkException{
    if (id > getAllWorks().size() || id < 0 || (id == 0 && getAllWorks().isEmpty()))
      throw new NoSuchWorkException(id);
    else
      return _library.getWork(id).getDiscription();
  }

  /**
   * Gets the state of the user with the id received.
   * @param userId the id of the specified work
   * @return true if the user is active
   **/
  public Boolean getUserState(int userId) {
    return _library.isUserActive(userId);
  }

  /**
   * Gets the Work with the id received.
   * 
   * @param id the id of the specified work
   * @throws UserIsActiveException if the work does not exist
   * @return if user is Active
   * @throws NoSuchUserException
   **/
  public Boolean isUserActive(int id) throws UserIsActiveException, NoSuchUserException {
    if (id > getAllUsers().size() || id < 0 || (id == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(id); 
    else if(_library.isUserActive(id))
      throw new UserIsActiveException(id);
    else
      return _library.isUserActive(id);
  }

  /**
   * Changes the state of the user.
   * @param id the id of the specified work
   **/
  public void paidTheFine(int id, Boolean state) {
    _library.changeUserState(id, state);
    _library.getUser(id).setFine(-_library.getUser(id).getFine());
    if(_library.overDeadLineAllReq(id) > 0)
      _library.getUser(id).setState(false);
  }

  /**
   * Changes the state of the user.
   * @param userId the id of the specified user
   * @return the int value of the user fine
   **/
  public int getFine(int userId) {
    return _library.getUserFine(userId);
  }

  /**
   * @return Current date
   */
  public int getCurrentDate() {
    return _library.getDate().getCurrentDate();
  }

  /**
   * Advances the date by the specified number of days 
   * @param nDay
   */
  public void advanceDays(int nDays) {
    _library.getDate().advanceDay(nDays);
    for (User user: _library.getAllUsers()) {
      if (_library.overDeadLineAllReq(user.getId()) > 0)
        _library.changeUserState(user.getId(), false);
    }
  }

  /**
   * Registers the User with the userName and email received.
   * @param userName the name of the user to be registered
   * @param email the email of the user to be registered
   * @throws UserRegistrationFailedException if the parameters are not acceptable
   * @return userId
   **/
  public int registerUser(String userName, String email) throws UserRegistrationFailedException {
    if ("".equals(userName) || "".equals(email) || userName.equals(email)) 
      throw new UserRegistrationFailedException(userName, email); 
    else {
    User user = new User(userName, email);
    user.setId(_library);
    _library.getAllUsers().add(user);
    _library.getRequest().addRequisition(user);
    return user.getId();
    }
  }

  /**
   * Searches for a sequence of letters in the title and author/director of each work.
   * @param searchWord the sequence of letters to search for
   * @return searchResult
   **/
  public List<Work> searchWorks(String searchWord) {
    List<Work> searchResult = new ArrayList<Work>();
    List<Work> totalWorks = getAllWorks();

    for (Work work : totalWorks) {
      if(work.getsearch(searchWord))
        searchResult.add(work);
    }
    return searchResult;
  }

  /**
   * Makes a request for the User with the idUser received of the Work with the
   * idWork received.
   * 
   * @param idUser the id of the user that wants to make the request
   * @param idWork the id of the work the user wants to request
   * @throws RuleFailedException if any Rule is broken
   * @return the returnDay of the request made
   * @throws NoSuchUserException
   * @throws NoSuchWorkException
   **/
  public int makeRequest(int idUser, int idWork) throws RuleFailedException, NoSuchUserException, NoSuchWorkException {
    if (idUser >= getAllUsers().size() || idUser < 0 || (idUser == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(idUser);

    else if (idWork >= getAllWorks().size() || idWork < 0 || (idWork == 0 && getAllWorks().isEmpty())) {
      throw new NoSuchWorkException(idWork);
    }
    else {
      if (_library.checkRules(idUser, idWork) == 0) {
        return _library.doRequest(idUser, idWork);
      }
      else {
        if (_library.checkRules(idUser, idWork) == 3) {
          return -1;
        }
        else {
          int i = _library.checkRules(idUser, idWork);
          throw new RuleFailedException(idUser, idWork, i);
        }
      }
    } 
  }

  /**
   * Checks to see if any of the known rules are broken.
   * 
   * @param idUser the id of the user that wants to make the request
   * @param idWork the id of the work the user wants to request
   * @throws NoSuchUserException
   * @throws NoSuchWorkException
   **/
  public int checkRules(int idUser, int idWork) throws NoSuchUserException, NoSuchWorkException {
    if (idUser >= getAllUsers().size() || idUser < 0 || (idUser == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(idUser);

    else if (idWork >= getAllWorks().size() || idWork < 0 || (idWork == 0 && getAllWorks().isEmpty())) {
      throw new NoSuchWorkException(idWork);
    }
    else {
      return _library.checkRules(idUser, idWork);
    }
  }

  /**
   * Makes a return of the Work with the idWork received by the User with the
   * idUser received .
   * 
   * @param idUser the id of the user that wants to make the return
   * @param idWork the id of the work the user wants to return
   * @throws WorkNotBorrowedByUserException if the work hasnt been requested by
   *                                        the user
   * @throws NoSuchUserException
   * @throws NoSuchWorkException
   **/
  public int makeReturn(int idUser, int idWork) throws WorkNotBorrowedByUserException, NoSuchUserException, NoSuchWorkException {
    if (idUser >= getAllUsers().size() || idUser < 0 || (idUser == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(idUser);

    else if (idWork >= getAllWorks().size() || idWork < 0 || (idWork == 0 && getAllWorks().isEmpty())) {
      throw new NoSuchWorkException(idWork);
    }
    else {
      if (_library.checkIfRequested(idUser, idWork) < 0) 
        throw new WorkNotBorrowedByUserException(idUser, idWork);
      else {   
        return _library.doReturn(idUser, idWork);   
      }
    }
  }

  /**
   * Asks to be sent a notification.
   * @param idUser the id of the user that wants to receive the notification
   * @param idWork the id of the work the in question
   **/
  public void askNotification(int userId, int workId) {
    _library.askNotification(userId, workId);
  }

  /**
   * Gets all the users notifications.
   * @param idUser the id of the user that wishes to see their notifications
   * @throws NoSuchUserNotification
   * @return the list of notifications of the user in question
   **/
  public List<Notifications> getAllNotifications(int userId) throws NoSuchUserException {
    if (userId > getAllUsers().size() || userId < 0 || (userId == 0 && getAllUsers().isEmpty()))
      throw new NoSuchUserException(userId); 
    else
      return _library.getAllNotifications(userId);
  }

  /**
   * Clears all of the users notifications.
   * @param idUser the id of the user that wants to make the request
   **/
  public void clearNotifications(int userId) {
    _library.clearNotifications(userId);
  }

  /**
   * Serialize the persistent state of this application.
   * 
   * @throws MissingFileAssociationException if the name of the file to store the persistent
   *         state has not been set yet.
   * @throws IOException if some error happen during the serialization of the persistent state

   */
  public void save() throws MissingFileAssociationException, IOException {
    saveAs(_filename);
  }

  /**
   * Serialize the persistent state of this application into the specified file.
   * 
   * @param filename the name of the target file
   *
   * @throws MissingFileAssociationException if the name of the file to store the persistent
   *         is not a valid one.
   * @throws IOException if some error happen during the serialization of the persistent state
   */
  public void saveAs(String filename) throws MissingFileAssociationException, IOException {
    _filename = filename;
    try (ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(filename))) {
      file.writeObject(_library);
    }
  }

  /**
   * Recover the previously serialized persitent state of this application.
   * 
   * @param filename the name of the file containing the perssitente state to recover
   *
   * @throws IOException if there is a reading error while processing the file
   * @throws FileNotFoundException if the file does not exist
   * @throws ClassNotFoundException 
   */
  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
    try (ObjectInputStream file = new ObjectInputStream(new FileInputStream(filename))) {
      _library = (Library)file.readObject();
      _filename = filename;
    }
  }

  /**
   * Set the state of this application from a textual representation stored into a
   * file.
   * 
   * @param datafile the filename of the file with the textual represntation of
   *                 the state of this application.
   * @throws ImportFileException             if it happens some error during the
   *                                         parsing of the textual
   *                                         representation.
   * @throws UserRegistrationFailedException
   */
  public void importFile(String datafile) throws ImportFileException {
    Parser parser;
    try {
      parser = new Parser(_library);
      _library.importFile(datafile, parser);
    } catch (IOException | BadEntrySpecificationException e) {
      throw new ImportFileException(e);
    }
  }
}
