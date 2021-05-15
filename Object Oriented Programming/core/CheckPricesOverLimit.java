package m19.core;

/**
 * Nao pode requisitar obras com valor superior a 25 euros
 */
public class CheckPricesOverLimit implements Rules {
    private int _ruleIndex = 6;

    @Override
    public int getIdx(User user, Work work) {
        if (user.getUserBehaviour() != UserBehaviour.CUMPRIDOR && work.getPrice() > 25) 
            return _ruleIndex;
        return -1;
    }
}