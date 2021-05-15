package m19.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import m19.core.exception.BadEntrySpecificationException;

/**
 * Class that represents the library as a whole.
 */
public class Library implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201901101348L;

  private int _nextWorkId;
  private int _nextUserId;
  private Set<User> _userAll;
  private Set<Work> _workAll;
  private Date _date;
  private Request _request;
 
  public Library() {
    _nextWorkId = 0;
    _nextUserId = 0;
    _userAll = new HashSet<> ();
    _workAll = new HashSet<> ();
    _date = new Date();
    _request = new Request();
  } 

  /**
   * Generate the next work id
   */
  protected void doNextWorkId() {
    _nextWorkId += 1;
  }

  /**
   * @return the next work id
   */
  protected int getNextWorkId() {
    return _nextWorkId;
  }

  /**
   * Generate the next user id
   */
  protected void doNextUserId() {
    _nextUserId += 1;
  }

  /**
   * @return the next user id
   */
  protected int getNextUserId() {
    return _nextUserId;
  }

  /**
   * Get the user with the id received.
   * @param id the id of the target user
   * @return the User.
   */
  protected User getUser(int id) {
    Iterator<User> iter = _userAll.iterator();

    while (iter.hasNext()) {
      User u = iter.next();
      if (u.getId() == id)
        return u;
    }
    return null;
  }

  /**
   * Get the state of the user.
   * @param id the id of the target user
   */
  protected Boolean isUserActive(int id) {
    return getUser(id).isActive();
  }

  /**
   * Gets the request.
   * @return the request
   */
  protected Request getRequest() {
    return _request;
  }

    /**
   * Chages the state of the user.
   * @param id the id of the target user
   */
  protected void changeUserState(int userId, Boolean state) {
    getUser(userId).setState(state);
  }

  /**
   * Calculates the value of the fine the user has to pay.
   * @param userId of the user that hasnt returned the work in time
   * @param workId of the work that hasnt been returned in time 
   * @return the int value of the fine
   */
  protected int calculateFine(int userId, int workId){
    User user = getUser(userId);
    Work work = getWork(workId);
    Request request = getUserRequest(user, work);
    int daysOver = overDeadLine(userId, request.getWorkId());
    if(daysOver == 0)
      return 0;
    else {
      user.setFine(daysOver * 5);
      return user.getFine();
    }
  }

  /**
   * Returns the number of days the user held the work over the deadline.
   * @param userId of the user that holds the work
   * @return the number of days
   */
  protected int overDeadLineAllReq(int userId){
    User user = getUser(userId);
    int days = 0;
    Iterator<Request> iter = user.getRequisitions().iterator();
    while (iter.hasNext()) {
      Request r = iter.next();
      int daysOver = overDeadLine(userId, r.getWorkId());
      if(daysOver > 0)
        days = daysOver;
    }
    return days;
  }

  /**
   * Returns the number of days the user held the work over the deadline.
   * @param userId of the user that holds the work
   * @param workId of the work that has been held over the deadline
   * @return the number of days
   */
  protected int overDeadLine(int userId, int workId) {
    User user = getUser(userId);
    Iterator<Request> iter = user.getRequisitions().iterator();
    while (iter.hasNext()) {
      Request r = iter.next();
      if(r.getWorkId() == workId){
        int nDaysOut = getDate().getCurrentDate() - r.getDateRequesition();
        if(nDaysOut - r.getDeadline() <= 0) {
          return 0;
        }
        else {
          return nDaysOut - r.getDeadline();
        }
      }
    }
    return 0;  
  }

  /**
   * Returns the request made by the user of the work.
   * @param userId of the user that requested the work
   * @param workId of the work that the user requested
   * @return the request
   */
  protected Request getUserRequest(User user, Work work){
    int id = work.getId();
    Iterator<Request> iter = user.getRequisitions().iterator();

    while (iter.hasNext()) {
        Request request = iter.next();
        if (request.getWorkId() == id)
            return request;
    }
    return null;
  }
    
  /**
   * Get the work with the id received.
   * @param id the id of the target work
   * @return the Work.
   */
  protected Work getWork(int id) {
    Iterator<Work> iter = _workAll.iterator();

    while (iter.hasNext()) {
      Work u = iter.next();
      if (u.getId() == id)
        return u;
    }
    return null;
  }

  /**
   * @return the Set of all the users
   */
  protected Set<User> getAllUsers() {
    return _userAll;
  }

  /**
   * @return the Set of all the works
   */
  protected Set<Work> getAllWorks() {
    return _workAll;
  }

  /**
   * @return the current date
   */
  protected Date getDate() {
    return _date;
  }

  /**
   * Gets the fine of the indicated user.
   * @param idUser the id of the user that has to pay the fine
   * @return the fine of the user in question
   **/
  protected int getUserFine(int userId) {
    return getUser(userId).getFine();
  }

  /**
   * Asks to be sent a notification.
   * @param idUser the id of the user that wants to receive the notification
   * @param idWork the id of the work the in question
   **/
  protected void askNotification(int userId, int workId) {
    User user = getUser(userId);
    _request.addReturn(user, workId);
  }

  /**
   * Gets all the users notifications.
   * @param idUser the id of the user that wishes to see their notifications
   * @throws NoSuchUserNotification
   * @return the list of notifications of the user in question
   **/
  protected List<Notifications> getAllNotifications(int userId) {
    User user = getUser(userId);
    return user.getAllNotifications();
  }

  /**
   * Clears all of the users notifications.
   * @param idUser the id of the user that wants to make the request
   **/
  protected void clearNotifications(int userId) {
    User user = getUser(userId);
    user.clearNotification();
  }

  /**
   * Checks if the request of the user for the work can be made.
   * @param idUser the id of the user that wants to make the request
   * @param idWork the id of the work the user wants to request
   * @return the index of the failed rule or 0 if none fails
   **/
  protected int checkRules(int idUser, int idWork){
    User user = getUser(idUser);
    Work work = getWork(idWork);
    Rules rule1 = new CheckRequestTwice();
    Rules rule2 = new CheckUserIsSuspended();
    Rules rule3 = new CheckNoMoreCopies();
    Rules rule4 = new CheckRequisitionsOverload();
    Rules rule5 = new CheckIsReferenceWork();
    Rules rule6 = new CheckPricesOverLimit();
    if(rule1.getIdx(user, work) > 0)
      return rule1.getIdx(user, work);
    else if(rule2.getIdx(user, work) > 0)
      return rule2.getIdx(user, work);
    else if(rule3.getIdx(user, work) > 0)
      return rule3.getIdx(user, work);
      else if(rule4.getIdx(user, work) > 0)
      return rule4.getIdx(user, work);
    else if(rule5.getIdx(user, work) > 0)
      return rule5.getIdx(user, work);
    else if(rule6.getIdx(user, work) > 0)
      return rule6.getIdx(user, work);
    else
      return 0;
  }

  /**
   * Gets the return day of the request made.
   * @param idUser the id of the user who made the request
   * @param idWork the id of the work requested
   * @return the return day of the request made
   **/
  protected int getReturnDay(User user, Work work){
    int numCopies = work.getNumberOfCopies();
    if(numCopies == 1){
      if(user.getUserBehaviour() == UserBehaviour.CUMPRIDOR)
        return 8;
      else if(user.getUserBehaviour() == UserBehaviour.FALTOSO)
        return 2;
      else
        return 3;
    }
    else if (numCopies <= 5 && numCopies > 1){
      if(user.getUserBehaviour() == UserBehaviour.CUMPRIDOR)
        return 15;
      else if(user.getUserBehaviour() == UserBehaviour.FALTOSO)
        return 2;
      else
        return 8;
    }
    else{
      if(user.getUserBehaviour() == UserBehaviour.CUMPRIDOR)
        return 30;
      else if(user.getUserBehaviour() == UserBehaviour.FALTOSO)
        return 2;
      else
        return 15;
    }
  }

  /**
   * Does the request asked by the user of the work.
   * @param idUser the id of the user who makes the request
   * @param idWork the id of the work to be requested
   * @return the success of the requisition process
   **/
  protected int doRequest(int idUser, int idWork) {
    User user = getUser(idUser);
    Work work = getWork(idWork);
    int returnDay = getReturnDay(user, work);
    
    Request request = new Request();
    request.makeRequest(returnDay, work, _date.getCurrentDate());
    //_request.setRequisition(work);
    user.addRequisitions(request);
    return returnDay + getDate().getCurrentDate();
  }

  /**
   * Checks if the user requested the work in the first place.
   * @param idUser the id of the user
   * @param idWork the id of the work
   * @return true if the request was made
   **/
  protected int checkIfRequested(int idUser, int idWork){
    User user = getUser(idUser);
    Work work = getWork(idWork);
    Request request = getUserRequest(user, work);

    if (request != null)
      return 0;
    return -1;
  }

  /**
   * Gets the return day of the request made.
   * @param idUser the id of the user who is returning the work
   * @param idWork the id of the work to be returned
   * @return the fine the user must pay
   **/
  protected int doReturn(int userId, int workId) {
    Work work = getWork(workId);
    User user = getUser(userId);
    int daysOver = overDeadLine(userId, workId);
    if (daysOver > 0) {
      user.setOverDeadLine();
      user.checkUserBehaviour();
    }
    else {
      user.setRequestRight();
      user.checkUserBehaviour();
    }
    int fine = calculateFine(userId, workId);
    user.removeRequisitions(workId);
    _request.removeRequisition(work);
    work.putAvailableCopies();
    _request.setReturn(work);
    _request.removeReturn(workId);
    return fine;
  }

  /**
   * Read the text input file at the beginning of the program and populates the
   * instances of the various possible types (books, DVDs, users).
   * 
   * @param filename
   *          name of the file to load
   * @throws BadEntrySpecificationException
   * @throws IOException
   */
  public void importFile(String filename, Parser parser) throws BadEntrySpecificationException, IOException {
    parser.parseFile(filename);
  }

}
