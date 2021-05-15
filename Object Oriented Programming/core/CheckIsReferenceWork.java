package m19.core;

/**
 * Nao pode requisitar obras de referencia
 */
public class CheckIsReferenceWork implements Rules {
    private int _ruleIndex = 5;

    @Override
    public int getIdx(User user, Work work) {
        if (work.getCategorys() == Categorys.REFERENCE) 
            return _ruleIndex;
        return -1;
    }
}