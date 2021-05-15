package m19.core;

/**
 * Holds the enum of the different behaviour types a user can have.
 */
public enum UserBehaviour {
    NORMAL {
        @Override
        public UserBehaviour changeState(int days) {
            if (days == 3)
                return FALTOSO;
            else if(days == 5)
                return CUMPRIDOR;
            return NORMAL;
        }
    },
    CUMPRIDOR {
        @Override
        public UserBehaviour changeState(int days) {
            if (days == 3)
                return FALTOSO;
            else if (days < 3 && days > 0)
                return NORMAL;
            return CUMPRIDOR;
        }
    },
    FALTOSO {
        @Override
        public UserBehaviour changeState(int days) {
            if (days == 3)
                return NORMAL;
            else if(days == 5)
                return CUMPRIDOR;
            return FALTOSO;
        }
    };

    public abstract UserBehaviour changeState(int days);
}