package m19.core;

/**
 * Nao pode requisitar mais que n obras permitidas
 */
public class CheckRequisitionsOverload implements Rules {
    private int _ruleIndex = 4;

    @Override
    public int getIdx(User user, Work work) {
        switch(user.getUserBehaviour()) {
            case NORMAL:
                if(user.getRequisitions().size() < 3)
                    return -1;
                break;
            case CUMPRIDOR:
                if(user.getRequisitions().size() < 5)
                    return -1;
                break;
            case FALTOSO:
                if(user.getRequisitions().size() < 1)
                    return -1;
                break;
        }
        return _ruleIndex;
    }
}