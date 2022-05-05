package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;

public class ClassServerException extends Exception {
    ClassesDefinitions.ResponseCode responseCode;

    ClassServerException(ClassesDefinitions.ResponseCode responseCode) {
        super();
        setResponseCode(responseCode);
    }

    public ClassesDefinitions.ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ClassesDefinitions.ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
