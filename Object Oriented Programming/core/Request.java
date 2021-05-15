package m19.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Request implements Serializable{
    
    private int _deadline;
    private int _dateRequisition;
    private Work _work;
    private List<Users> _userRequisition;
    private Map<Integer, ArrayList<Users>> _userReturn;

    public Request(){
        _userRequisition = new ArrayList<Users>();
        _userReturn = new HashMap<Integer, ArrayList<Users>>();
    }
    
    /**
     * Creates the request of work made by the user
     * @param deadline day user has to return work
     * @param work
     * @param date day the requesition is made
     */
    protected void makeRequest(int deadline, Work work, int date) {
        _deadline = deadline;
        work.setAvailableCopies();
        _work = work;
        _dateRequisition = date;
    }

    /**
     * @return idWork id of work in requisition
     */
    protected int getWorkId() {
        return _work.getId();
    }
    
    /**
     * @return _deadline day user has to return work
     */
    protected int getDeadline() {
        return _deadline;
    }

    /**
     * @return _dateRequisition day the requesition is made
     */
    protected int getDateRequesition() {
        return _dateRequisition;
    }

    /**
     * @return _userReturn contains all Users interested in notifications of return works
     */
    protected Map<Integer, ArrayList<Users>> getReturn() {
        return _userReturn;
    }

    /**
     * Add user interested in receiving notifications of requitions of works
     * @param user
     */
    protected void addRequisition(Users user) {
        _userRequisition.add(user);
    }

    /**
     * Add user interested in receiving notifications the return of a work
     * @param user
     * @param workId
     */
    protected void addReturn(Users user, int workId) {
        if(_userReturn.containsKey(workId)) {
            ArrayList<Users> userTotal = _userReturn.get(workId); 
            userTotal.add(user);
            _userReturn.put(workId, userTotal); 
        }
        else {
            ArrayList<Users> userTotal = new ArrayList<Users>();
            userTotal.add(user);
            _userReturn.put(workId, userTotal);
        }
    }

    /**
     * Sends the notification that a requision was made
     * @param work
     */
    protected void setRequisition(Work work) {
        String not;
        not = "REQUISIÇÃO".concat(": " + work.getDiscription());
        for(Users user: _userRequisition) {
            user.update(not, work.getId());
        }
    }

    /**
     * Remove the notification that a requision was made
     * @param work
     */
    protected void removeRequisition(Work work) {        
        for(Users user: _userRequisition) {
            user.removeNot(work.getId());
        }
    }

    /**
     * Sends the notification that a return was made
     * @param work
     */
    protected void setReturn(Work work) {
        int workId = work.getId();

        if(_userReturn.containsKey(workId)) {
            String not;
            not = "ENTREGA".concat(": " + work.getDiscription());
            ArrayList<Users> userTotal = new ArrayList<Users>();
            userTotal = _userReturn.get(workId);
            for(Users user: userTotal) {
                user.update(not, workId);
            }
        }
    }

    /**
     * Removes the user from the map of users that want to receive notifications about the work
     * @param workId of the work in question
     */
    protected void removeReturn(int workId) {
        Iterator<Map.Entry<Integer,ArrayList<Users>>> it = _userReturn.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,ArrayList<Users>> req = (Map.Entry<Integer,ArrayList<Users>>)it.next();
            if (req.getKey() == workId) {
                it.remove();
            }
        }
    }

}
